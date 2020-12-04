//CS 3301, ASSIGNMENT 4
//DANIEL GRILLO, RIYA SHAH, IBRAYYM KOR



import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.ArrayList;

// Main class
public class HoughTransform extends Frame implements ActionListener {
	BufferedImage input;
	int width, height, diagonal;
	ImageCanvas source, target;
	TextField texRad, texThres;
	// Constructor
	public HoughTransform(String name) {
		super("Hough Transform");
		// load image
		try {
			input = ImageIO.read(new File(name));
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
		width = input.getWidth();
		height = input.getHeight();
		diagonal = (int)Math.sqrt(width * width + height * height);
		// prepare the panel for two images.
		Panel main = new Panel();
		source = new ImageCanvas(input);
		target = new ImageCanvas(input);
		main.setLayout(new GridLayout(1, 2, 10, 10));
		main.add(source);
		main.add(target);
		// prepare the panel for buttons.
		Panel controls = new Panel();
		Button button = new Button("Line Transform");
		button.addActionListener(this);
		controls.add(button);
		controls.add(new Label("Radius:"));
		texRad = new TextField("10", 3);
		controls.add(texRad);
		button = new Button("Circle Transform");
		button.addActionListener(this);
		controls.add(button);
		controls.add(new Label("Threshold:"));
		texThres = new TextField("25", 3);
		controls.add(texThres);
		button = new Button("Search");
		button.addActionListener(this);
		controls.add(button);
		// add two panels
		add("Center", main);
		add("South", controls);
		addWindowListener(new ExitListener());
		setSize(diagonal*2+100, Math.max(height,360)+100);
		setVisible(true);
	}
	class ExitListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
	// Action listener
	public void actionPerformed(ActionEvent e) {
		// perform one of the Hough transforms if the button is clicked.
		if ( ((Button)e.getSource()).getLabel().equals("Line Transform") ) {
			ArrayList<int[]> pointsPxl = new ArrayList<>();
            int[][] g = new int[360][diagonal];

            int threshold = Integer.parseInt(texThres.getText());

            for ( int y = 0 , i=0; y < height; y++ )

                for ( int x = 0; x < width; x++)

                {
                    int pxl = source.image.getRGB(x, y);

                    if(pxl != -1){pointsPxl.add(new int[]{x,y});}
                }

            g = HoughLines(pointsPxl);

            DisplayTransform(diagonal, 360, g);

            highlightLines(width, height, g, threshold);
		}
		else if ( ((Button)e.getSource()).getLabel().equals("Circle Transform") ) {
			int[][] g = new int[height][width];
			int radius = Integer.parseInt(texRad.getText());
			// insert your implementation for circle here.
			int red, green, blue;
			int x,y;
			int degree = 360;

			//gathering image pixels
			int pixels[][] = new int[height][width];
			for (int i = 0; i < height; i++) {			// i is the y access
				for (int j = 0; j < width; j++) {		// j is the x access
					pixels[j][i] =  (new Color(source.image.getRGB(j, i))).getRed()<<16 | (new Color(source.image.getRGB(j, i))).getGreen()<<8 | (new Color(source.image.getRGB(j, i))).getBlue();
				}
			}

			//implementation of draw circle
			int redline = Integer.parseInt(texThres.getText());



			//calculating for the hough transform
			for (int i = 0; i < height; i++) { 			//i = y-access
				for (int j = 0; j < width; j++) { 		//j = x-access
					Color clr = new Color(source.image.getRGB(j, i));
					red = clr.getRed();
					green = clr.getGreen();
					blue = clr.getBlue();
					if ( (pixels[j][i] & 0x0000ff) < 255)
					//if ( red == 0 && green == 0 && blue == 0)
					{
						for (int k = 0; k < degree; k++) {
							x = (int)Math.round(j+radius*Math.cos(Math.toRadians(k)));
							y = (int)Math.round(i+radius*Math.sin(Math.toRadians(k)));
							if( x < width && x >= 0 && y < height && y >= 0 )
							{
								//reminder that y = height and x = width
								g[y][x]++;
							}
						}
					}
				}
			}
			//outlining the cirlces
			for (int i = 0; i < width; i++) { 			// i is y-access
				for (int j = 0; j < height; j++) {		// j is the x-access
					if(g[i][j] > redline)
					{
						source.outlineCirle(i,j, radius);
					}
				}
			}
			DisplayTransform(width, height, g);
			source.repaint();
		}
	}
	public int[][] HoughLines(ArrayList<int[]> points)
    {
        int midX = width / 2;
        int midY = height / 2;

        int[][] g = new int[360][diagonal];

        for(int[] point : points)
        {
            int x = point[0];
            int y = point[1];

            for(int theta = 0; theta < 360; theta++)
            {
                double rad = ((theta)*Math.PI)/180;
                double ro = ((x-midX)*Math.cos(rad)+(y-midY)*Math.sin(rad));

                int scaled_ro = (int) (ro+diagonal/2);

                if(scaled_ro >= diagonal || scaled_ro < 0) continue;

                g[theta][scaled_ro]++;
            }
        }
        
        return scaleG(g);
    }

    public int[][] scaleG(int[][] d)
    {
        int max = 0;

        for(int i=0; i<d.length;i++){

            for(int j=0; j<d[0].length; j++){

                if( d[i][j]>max ) max = d[i][j];
            }
        }

        for(int i=0; i<d.length;i++){

            for(int j=0; j<d[0].length; j++){

                d[i][j] = (int) (d[i][j]*(255.0/max));
            }
        }
        return d;
    }

    public void highlightLines(int wdt, int hgt, int[][] g, int thres)
    {

        for(int i=0; i<g.length; i++){

            for(int j=0; j<g[1].length; j++){

                if(g[i][j] > thres ) {drawLine(i, j-diagonal/2);}
            }
        }

    }

    public void drawLine(int t, int r)
    {
        double rad = t*Math.PI/180;

        for(int y=0; y<height; y++)
        {
            int x = (int) ((r-(y-height/2)*Math.sin(rad))/Math.cos(rad)+width/2);
            if(x<0 || x>=width) continue;
            source.image.setRGB(x, y, Color.RED.getRGB());
        }

        for(int x=0; x<width; x++)
        {
            int y = (int) ((r-(x-width/2)*Math.cos(rad))/Math.sin(rad)+height/2);
            if(y<0 || y>=height) continue;
            source.image.setRGB(x, y, Color.RED.getRGB());
        }
        source.repaint();
    }

		
	
	// display the spectrum of the transform.
	public void DisplayTransform(int wid, int hgt, int[][] g) {
		target.resetBuffer(wid, hgt);
		for ( int y=0, i=0 ; y<hgt ; y++ )
			for ( int x=0 ; x<wid ; x++, i++ )
			{
				int value = g[y][x] > 255 ? 255 : g[y][x];
				target.image.setRGB(x, y, new Color(value, value, value).getRGB());
			}
		target.repaint();
	}

	public static void main(String[] args) {
		new HoughTransform(args.length==1 ? args[0] : "HoughCircles3.png");
	}
}
