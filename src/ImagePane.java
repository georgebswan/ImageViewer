import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.ImageObserver;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import aberscan.Photo;

public class ImagePane extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

	ViewerGUI frame;
	static final long serialVersionUID = 2;
    Image origImage;
    Image curImage;   
    Rectangle imageRect = new Rectangle(0,0,0,0); 	//info about the image
    Rectangle dvd = new Rectangle(0,0,0,0); 	//info about the dvd screen used to show slideshow
    Rectangle screen = new Rectangle(0,0,0,0);	//info about the image computer screen
    Rectangle adjustedImage = new Rectangle(0,0,0,0); //info about the size of the adjusted image
    Rectangle cropAdjustedRect = new Rectangle(0,0,0,0); // rectangle to hold all cropping box info
    double startX = 0;
    double startY = 0;
    boolean cropFlag = false;
    boolean croppedAllFlag = false;
    boolean copyFlag = false;
    boolean dragFlag = false;
    int cornerSize = 50;
    final int MOVE = 1;
    final int TOP_LEFT_CORNER = 2;
    final int TOP_RIGHT_CORNER = 3;
    final int BOTTOM_RIGHT_CORNER = 4;
    final int BOTTOM_LEFT_CORNER = 5;
    boolean shiftPressed = false;
    final int NOT_SET= 0;
    int mode = NOT_SET;
	    
    public boolean getCropFlag() { return cropFlag; }
    public boolean getCroppedAllFlag() { return croppedAllFlag; }
    public boolean getCopyFlag() { return copyFlag; }
    
    public void setDvdWidth(int w) { dvd.setSize(w, (int)dvd.getHeight()); }
    public void setDvdHeight(int h) { dvd.setSize((int)dvd.getWidth(), h); }
    public void resetDvdWidth() { dvd.setSize(frame.getWidth(), (int)dvd.getHeight()); }
    public void resetDvdHeight() { dvd.setSize((int)dvd.getWidth(), frame.getHeight()); }
    public Rectangle getCropAdjustedRect() { return cropAdjustedRect; }
    public Rectangle getAdjustedImage() { return adjustedImage; }
    
    public void setCropFlag(boolean flag) { cropFlag = flag; }
    public void setCroppedAllFlag(boolean flag) { croppedAllFlag = flag; }
    public void setCopyFlag(boolean flag) { copyFlag = flag; }
    
	public ImagePane(ViewerGUI parent) throws IOException{
	    frame = parent;
	    if (dvd.getWidth() == 0) {resetDvdWidth(); }
	    if (dvd.getHeight() == 0) {resetDvdHeight(); }
	    screen.setSize((int) (dvd.getWidth() - 18), (int)(dvd.getHeight() - 88)); // don't ask why 18 and 8, but that is the visible area within the frame
	    addMouseListener(this);
	    addMouseMotionListener(this);
	    addMouseWheelListener(this);
        this.setBackground(Color.LIGHT_GRAY);
	}
	    
	public void setScreenImage(Photo photo) {
		curImage = photo.getImage();
	    rightSize();
	    //System.out.println("Screen = (" + screen.getWidth() + "," + screen.getHeight() + ")");
	    //System.out.println("OrigImage = (" + origImage.getWidth() + "," + image.getHeight() + ")");
		frame.setTitle(photo.getName());
	}
	
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		if ((infoflags & ImageObserver.ERROR) != 0) {
		    System.out.println("ImagePane ERROR : Error loading image!");
		    System.exit(-1);
		}
		if ((infoflags & ImageObserver.WIDTH) != 0 && (infoflags & ImageObserver.HEIGHT) != 0)
		    rightSize();
		if ((infoflags & ImageObserver.SOMEBITS) != 0)
		    repaint();
		if ((infoflags & ImageObserver.ALLBITS) != 0) {
		    rightSize();
		    repaint();
		    return false;
		}
		return true;
	}
	
	private void rightSize() { 
    	int width = 0;		// original width of the image
    	int height = 0;	// original height
	    width = curImage.getWidth(this);
	    height = curImage.getHeight(this);
	    if (width == -1 || height == -1)
	      return;
	    addNotify();
	    imageRect.setSize(width, height);
	    
	    // write the image size back into the current photo, since we will need this later for cropping
	    Photo photo = frame.photoList.getPhoto();
	    photo.setImageRect( width,  height);
	    
	    //System.out.println("Actual Image = " + imageRect.toString());
	    

		//adjust the height. Note that adjWidth/adjHeight = imageWidth/imageHeight. Therefore if adjHeight = screenHeight, then adjWidth = screenHeight*imageWidth/imageHeight
		height = (int) screen.getHeight();
		width = (int)(scaleWidth(height, imageRect));
	    adjustedImage.setRect(positionEx(width), positionWy(height), width, height);
		//System.out.println("Image after height adjustment = " + adjustedImage.toString());
		
		//if still too wide, then scale down. Use the same ratio as above
		if(width > screen.getWidth()) {
			width = (int)screen.getWidth();
			height = (int)(scaleHeight(width, adjustedImage));
			adjustedImage.setRect(positionEx(width), positionWy(height), width, height);
		}
		
		//now just make it a little smaller so that I can see the border
		//adjustedImage.setSize((int) (adjustedImage.getWidth() - 20), (int) (adjustedImage.getHeight() - 20));
		//System.out.println("Image after width adjustment = (" + adjustedImage.getWidth() + "," + adjustedImage.getHeight() + ")");
		
		// now figure out the cropped size of the image so that it will properly fill the screen in a slideshow
		// Here the ratio is that the cropWidth/cropHeight = screenWidth/screenHeight. 
		// Therefore if the cropWidth is adjWidth, then cropHeight = adjWidth*screenHeight/screenWidth
		// adjust the width if image is horizontal
		if(cropFlag == true && (imageRect.getWidth() > imageRect.getHeight() || imageRect.getWidth() <= imageRect.getHeight() )) {
			// is the cropBox a set size, or the whole image?
			if(dvd.getWidth() == 0 || dvd.getHeight() == 0) {
				width = (int) adjustedImage.getWidth();
				height = (int) adjustedImage.getHeight();
				cropAdjustedRect.setRect(screen.getWidth()/2 - width/2, screen.getHeight()/2 - height/2, width, height);
			}
			else {
				width = (int) adjustedImage.getWidth();
				height = (int) scaleHeight(width, dvd); 
				// here if horizontal
				cropAdjustedRect.setRect(screen.getWidth()/2 - width/2, screen.getHeight()/2 - height/2, width, height);
				//System.out.println("height = " + height + ", Crop Rectangle1 = " + cropAdjustedRect.toString());
				
				//if the croppedHeight is still too high, then reduce the width
				if(cropAdjustedRect.getHeight() > adjustedImage.getHeight()) {
					height = (int) adjustedImage.getHeight();
					width = (int) scaleWidth(height, dvd);
	
					cropAdjustedRect.setRect(screen.getWidth()/2 - width/2, screen.getHeight()/2 - height/2, width, height);
				}
			}
			
			//System.out.println("height = " + height + ", Crop Rectangle2 = " + cropAdjustedRect.toString());
		}
		//else {
		//	height = (int) adjustedImage.getHeight();
		//	width = (int) scaleWidth(height, dvd);
		//	//here for vertical or square
		//	cropAdjustedRect.setRect(screen.getWidth()/2 - width/2, screen.getHeight()/2 - height/2, width, height);
			
			//if the croppedWidth is still too wide, then reduce the height
		//	if(cropAdjustedRect.getHeight() > adjustedImage.getHeight()) {
		//		width = (int) adjustedImage.getWidth();
		//		height = (int) scaleHeight(width, dvd);

		//		cropAdjustedRect.setRect(screen.getWidth()/2 - width/2, screen.getHeight()/2 - height/2, width, height);
		//	}
		//}
		
		//System.out.println("Crop Rectangle = " + cropAdjustedRect.toString());
	}
	
	private double scaleHeight(double width, Rectangle rect) {
		return (width * rect.getHeight()/rect.getWidth());
	}
	
	private double scaleWidth(double height, Rectangle rect) {
		return( height * rect.getWidth()/rect.getHeight());
	}
	private double positionEx(double width) {
		return (screen.getWidth()/2 - width/2);
	}
	
	private double positionWy(double height) {
		return (screen.getHeight()/2 - height/2);
	}
	    
	public void paintComponent(Graphics g){
	    super.paintComponent(g);
	    if(curImage != null){
	    	//System.out.println("AdjustedImageRect = " + adjustedImage.toString());
	        g.drawImage(curImage, (int) adjustedImage.getX(), (int) adjustedImage.getY(), (int) adjustedImage.getWidth(), (int) adjustedImage.getHeight(), this);
	        //g.drawImage(curImage, (int) (screen.getWidth()/2 - adjustedImage.getWidth()/2), (int) (screen.getHeight()/2 - adjustedImage.getHeight()/2), (int) adjustedImage.getWidth(), (int) adjustedImage.getHeight(), this);
	    }
	    
	    //draw the actual screen boundary
	    //drawBoundingScreenRectangle(g);
	    
		//draw the cropping rectangle
	    if (cropFlag == true) {
	    	drawCropRectangle (g);
	    }
	}
	
	private void drawCropRectangle(Graphics g) {
		 Graphics2D g2 = (Graphics2D) g;
	     g2.setColor(Color.RED);
	     Stroke oldStroke = g2.getStroke();
	     g2.setStroke(new BasicStroke(4));

	     //System.out.println("Draw Rect = " + cropAdjustedRect.toString());
	     g2.draw(cropAdjustedRect);
	     
	     g2.setStroke(new BasicStroke(1));
	     g2.setStroke(oldStroke);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		//System.out.println("Mouse Moved");
		
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		//System.out.println("Mouse Clicked");
		
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		//System.out.println("Mouse Entered");
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		//System.out.println("Mouse Exited");
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// see where the mouse is in respect to the crop box
		int x = e.getX();
		int y = e.getY();
		int cornerSize = 50;
		
		//was the SHIFT key also held down when mouse was pressed ? 
		if((e.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK) {
			shiftPressed = true;
		}
		else
			shiftPressed = false;
		
		// first, is it inside and we are going to move the box. Set the selectionRect to be within the crop Rect
		Rectangle selRect = new Rectangle((int) (cropAdjustedRect.getX() + cornerSize), (int) (cropAdjustedRect.getY() + cornerSize), (int) (cropAdjustedRect.getWidth() - cornerSize*2), (int) (cropAdjustedRect.getHeight() - cornerSize*2));
		
		if(selRect.contains(x, y) == true) {
			//System.out.println("Inside box");
			mode = MOVE;
			repaint();
			startX = x;
			startY = y;
		}
		else {
			// am I trying to resize the crop box by clicking near one of the corners?
			// are we near top left corner?
			selRect.setRect((int) (cropAdjustedRect.getX() - cornerSize), (int) (cropAdjustedRect.getY() - cornerSize),cornerSize * 2, cornerSize * 2);
			if (selRect.contains(x, y) == true) {
				//Here for top left corner
				//System.out.println("Found top Left corner");
				mode = TOP_LEFT_CORNER;
				repaint();
				startX = x;
				startY = y;
			}
			else {
				// are we near top right corner?
				selRect.setLocation((int) (cropAdjustedRect.getX() + cropAdjustedRect.getWidth() - cornerSize), 
						(int) (cropAdjustedRect.getY() - cornerSize));
				if (selRect.contains(x, y) == true) {
					//System.out.println("Found top right corner");
					mode = TOP_RIGHT_CORNER;
				}
				else {
					// are we near bottom right corner?
					selRect.setLocation((int) (cropAdjustedRect.getX() + cropAdjustedRect.getWidth() - cornerSize), 
							(int) (cropAdjustedRect.getY() + cropAdjustedRect.getHeight() - cornerSize));
					if (selRect.contains(x, y) == true) {
						//System.out.println("Found bottom right corner");
						mode = BOTTOM_RIGHT_CORNER;
					}
					else {
						// are we near bottom left corner?
						selRect.setLocation((int) (cropAdjustedRect.getX() - cornerSize), 
								(int) (cropAdjustedRect.getY() + cropAdjustedRect.getHeight() - cornerSize));
						if (selRect.contains(x, y) == true) {
							//System.out.println("Found bottom left corner");
							mode = BOTTOM_LEFT_CORNER;
						}
						else {
							//System.out.println("Outside box");
						}
					}
				}
			}
		}
		
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(dragFlag == true) { 
			adjustCropBox(e.getX(), e.getY());
		} 
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		dragFlag = true; 
		adjustCropBox(e.getX(), e.getY());
	}
	
	private void adjustCropBox(int ex, int wy) {
		
		switch(mode) {
		case MOVE:
			// Move the x and y location of the rectangle
			cropAdjustedRect.setLocation((int) (cropAdjustedRect.getX() + (ex - startX)), (int) (cropAdjustedRect.getY() + (wy - startY)));
			
			reset(ex, wy);
			break;

		case TOP_LEFT_CORNER:
			moveTopLeft(ex, wy, shiftPressed);
			reset(ex, wy);
			break;
			
		case TOP_RIGHT_CORNER:
			moveTopRight(ex, wy, shiftPressed);
			reset(ex, wy);
			break;
			
		case BOTTOM_RIGHT_CORNER:
			moveBottomRight(ex, wy, shiftPressed);
			reset(ex, wy);
			break;
			
		case BOTTOM_LEFT_CORNER:
			moveBottomLeft(ex, wy, shiftPressed);
			reset(ex, wy);
			break;
		}	
	}
	
	private void moveBottomLeft(int ex, int wy, boolean shiftPressed) {
		int newX, newY, newWidth, newHeight, deltaX, deltaY;
		
		//System.out.println( "Before : (" + ex + "," + wy + ") :" + cropAdjustedRect.toString());
		
		//what is the delta between the current bottom left corner and the mouse location
		deltaX = (int) (cropAdjustedRect.getX() - ex);
		deltaY = (int) (wy - (cropAdjustedRect.getY() + cropAdjustedRect.getHeight()));
		
		//calculate the new rectangle. Anchor the top right corner, by keeping the newY coord the same as the oldY
		newX = (int) (cropAdjustedRect.getX() - deltaX);
		newY = (int) (cropAdjustedRect.getY());
		//newWidth is the oldWidth +/- the delta in x values
		newWidth = (int) (cropAdjustedRect.getWidth() + deltaX);
		if(dvd.getHeight() == 0) {
			newHeight = (int) scaleHeight(newWidth, imageRect);
		}
		else {
			newHeight = (int) scaleHeight(newWidth, dvd);
		}
		
		//save away this calculation before doing the topRight adjustment
		cropAdjustedRect.setRect(newX, newY, newWidth, newHeight);
		
		//System.out.println( "After : " + cropAdjustedRect.toString());
		
		//adjust the topRight corner if needed
		if(shiftPressed == true) {
			//calculate the mouse coordinates as if I had manually dragged the corner
			ex = (int) (cropAdjustedRect.getX() + cropAdjustedRect.getWidth() + deltaX);
			wy = (int) (cropAdjustedRect.getY() - deltaY);
			moveTopRight(ex, wy, false);
		}
	}
	
	private void moveBottomRight(int ex, int wy, boolean shiftPressed) {
		int newX, newY, newWidth, newHeight, deltaX, deltaY;

		//what is the delta between the current bottom right corner and the mouse location
		deltaX = (int) (ex - (cropAdjustedRect.getX() + cropAdjustedRect.getWidth()));
		deltaY = (int) (wy - (cropAdjustedRect.getY() + cropAdjustedRect.getHeight()));
		
		//calculate the new rectangle. Anchor the top left corner, by keeping the both the newX/newY coords the same as the oldX/oldY
		newX = (int) cropAdjustedRect.getX();
		newY = (int) cropAdjustedRect.getY();
		//newWidth is the oldWidth +/- the difference in the topright x values
		newWidth = (int) (cropAdjustedRect.getWidth() + deltaX);
		if(dvd.getHeight() == 0) {
			newHeight = (int) scaleHeight(newWidth, imageRect);
		}
		else {
			newHeight = (int) scaleHeight(newWidth, dvd);
		}
		
		//save away this calculation before doing the topRight adjustment
		cropAdjustedRect.setRect(newX, newY, newWidth, newHeight);
		
		//adjust the topLeft corner if needed
		if(shiftPressed == true) {
			//calculate the mouse coordinates as if I had manually dragged the corner
			ex = (int) (cropAdjustedRect.getX() - deltaX);
			wy = (int) (cropAdjustedRect.getY() - deltaY);
			moveTopLeft(ex, wy, false);
		}
	}
	
	private void moveTopLeft(int ex, int wy, boolean shiftPressed) {
		int newX, newY, newWidth, newHeight, deltaX, deltaY;

		//what is the delta between the current top corner and the mouse location
		deltaX = (int) (cropAdjustedRect.getX() - ex);
		deltaY = (int) (cropAdjustedRect.getY() - wy);
		
		//calculate the new rectangle. Anchor the bottom right corner by keeping the newY the same as the oldY
		newX = ex;
		//newWidth is the oldWidth +/- the delta in x values
		newWidth = (int) (cropAdjustedRect.getWidth() + deltaX);
		if(dvd.getHeight() == 0) {
			newHeight = (int) scaleHeight(newWidth, imageRect);
		}
		else {
			newHeight = (int) scaleHeight(newWidth, dvd);
		}
		newY = (int) (cropAdjustedRect.getY() + (cropAdjustedRect.getHeight() - newHeight));

		cropAdjustedRect.setRect(newX, newY, newWidth, newHeight);
		
		//adjust the bottomRight corner if needed
		if(shiftPressed == true) {
			//calculate the mouse coordinates as if I had manually dragged the corner
			ex = (int) (cropAdjustedRect.getX() + cropAdjustedRect.getWidth() + deltaX);
			wy = (int) (cropAdjustedRect.getY() + cropAdjustedRect.getHeight() + deltaY);
			moveBottomRight(ex, wy, false);
		}
	}
	
	private void moveTopRight(int ex, int wy, boolean shiftPressed) {
		int newX, newY, newWidth, newHeight, deltaX, deltaY;
		
		//anchor the bottom left corner, and resize the rectangle while keeping the same aspect ratio

		//what is the delta between the current top corner and the mouse location
		deltaX = (int) (ex - (cropAdjustedRect.getX() + cropAdjustedRect.getWidth()));
		deltaY = (int) (cropAdjustedRect.getY() - wy);
		
		//calculate the new rectangle. Anchor the bottom left corner
		newX = (int) cropAdjustedRect.getX();
		//newWidth is the oldWidth +/- the difference in the topright x values
		newWidth = (int) (cropAdjustedRect.getWidth() + deltaX);
		if(dvd.getHeight() == 0) {
			newHeight = (int) scaleHeight(newWidth, imageRect);
		}
		else {
			newHeight = (int) scaleHeight(newWidth, dvd);
		}
		//newY is the oldY +/- the delta in heights
		newY = (int) (cropAdjustedRect.getY() + (cropAdjustedRect.getHeight() - newHeight));
		//anchor the bottom right corner, and resize the rectangle while keeping the same aspect ratio
		
		cropAdjustedRect.setRect(newX, newY, newWidth, newHeight);
		
		//adjust the bottomLeft corner if needed
		if(shiftPressed == true) {
			//calculate the mouse coordinates as if I had manually dragged the corner
			ex = (int) (cropAdjustedRect.getX() - deltaX);
			wy = (int) (cropAdjustedRect.getY() + cropAdjustedRect.getHeight() + deltaY);
			moveBottomLeft(ex, wy, false);
		}
	}
	
	private void reset(int ex, int wy) {
		repaint(); 
		// reset the starting point now the the rectangle has moved
		startX = ex;
		startY = wy;
	}
	

	public void mouseWheelMoved(MouseWheelEvent e) {
	    int diffY = e.getUnitsToScroll() * 8;
	    int wy;
	    
		mode = MOVE;
		wy = (int) (startY + diffY);
		//System.out.println("Scroll Units = " + e.getUnitsToScroll() + "Rotation = " + e.getWheelRotation() + "DiffY = " + diffY);
		adjustCropBox((int)startX, wy);
	}
}