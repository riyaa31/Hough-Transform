import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.*;
import javax.print.DocFlavor;

// Canvas for image display
class ImageCanvas extends Canvas {
	BufferedImage image;
	//cirlce
	ArrayList<Integer> horizontal = new ArrayList<>();
	ArrayList<Integer> vertical = new ArrayList<>();
	ArrayList<Integer> radi = new ArrayList<>();



	// initialize the image and mouse control
	public ImageCanvas(BufferedImage input) {
		image = input;
		addMouseListener(new ClickListener());
	}

        public ImageCanvas(int width, int height) {
		image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		addMouseListener(new ClickListener());
	}
	// redraw the canvas
	public void paint(Graphics g) {
		// draw boundary
		g.setColor(Color.gray);
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);
		// compute the offset of the image.
		int xoffset = (getWidth() - image.getWidth()) / 2;
		int yoffset = (getHeight() - image.getHeight()) / 2;
		g.drawImage(image, xoffset, yoffset, this);


		//outlining cirlce
		int circle = horizontal.size();
		for (int i = 0; i < circle; i++) {
			g.setColor(Color.RED);
			g.drawOval(horizontal.get(i), vertical.get(i), radi.get(i)*2, radi.get(i) *2);
		}
		horizontal.clear();
		vertical.clear();
		radi.clear();
		
	}
	// change the image and redraw the canvas
	public void resetImage(Image input) {
		image = new BufferedImage(input.getWidth(null), input.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2D = image.createGraphics();
		g2D.drawImage(input, 0, 0, null);
		repaint();
	}

	//highlighting over circle
	public void outlineCirle(int x, int y, int r)
	{
		int xoff = (getWidth() - image.getWidth()) / 2;
		int yoff = (getHeight() - image.getHeight()) / 2;
		horizontal.add((x + xoff) - r );
		vertical.add((y + yoff) - r );
		radi.add(r);
	}

	// change the image and redraw the canvas
	public void resetBuffer(int width, int height) {
		image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2D = image.createGraphics();
	}


// listen to mouse click
	class ClickListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if ( e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON3 )
				try {
					ImageIO.write(image, "png", new File("saved.png"));
				} catch ( Exception ex ) {
					ex.printStackTrace();
				}
		}
	}
}
