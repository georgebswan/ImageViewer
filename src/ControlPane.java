
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import aberscan.Photo;
import aberscan.PhotoList;

public class ControlPane extends JPanel implements ActionListener, FocusListener{
	static final long serialVersionUID = 3;
	ViewerGUI frame;
	PhotoDirectory photoDir;
	PhotoList photoList;
	ImagePane iPane;
	
	ImageIcon prevIcon = new ImageIcon("images/previous.jpg");
	JButton prev = new JButton(prevIcon);
	ImageIcon copyIcon = new ImageIcon("images/copy.jpg");
	JButton copy = new JButton(copyIcon);
	ImageIcon nextIcon = new ImageIcon("images/next.jpg");
	JButton next = new JButton(nextIcon);
	ImageIcon cropallIcon = new ImageIcon("images/cropall.jpg");
	JButton cropAll = new JButton(cropallIcon);
	ImageIcon rotateleftIcon = new ImageIcon("images/rotateleft.jpg");
	JButton rotateLeft = new JButton(rotateleftIcon);
	ImageIcon rotaterightIcon = new ImageIcon("images/rotateright.jpg");
	JButton rotateRight = new JButton(rotaterightIcon);
	ImageIcon flipVertIcon = new ImageIcon("images/flipVert.jpg");
	JButton flipVert = new JButton(flipVertIcon);
	ImageIcon flipHorizIcon = new ImageIcon("images/flipHoriz.jpg");
	JButton flipHoriz = new JButton(flipHorizIcon);
	JButton selectFile = new JButton("Select File");
    //JCheckBox cropCheck;
    //boolean cropCheckStatus = false;

	JTextField width = new JTextField(5);
	JTextField height = new JTextField(5);

	
	public ControlPane (ViewerGUI parent, ImagePane iPane) {
		frame = parent;
		this.iPane = iPane;
            	
		//pane.setPreferredSize(new Dimension(1000,50));
		//setLayout(new FlowLayout(FlowLayout.CENTER));
		
		//JToolBar cropBar = new JToolBar();
		//cropBar.setRollover(true);
		//cropAll.addActionListener(this);
		//cropBar.add(cropAll);
		//add(cropBar, BorderLayout.LINE_START);
		
		//JToolBar selectBar = new JToolBar();
		//selectBar.setRollover(true);
		//ImageIcon dvdWidthIcon = new ImageIcon("images/dvdwidth.jpg");
		//selectFile.addActionListener(this);
		//selectBar.add(selectFile);
		//add(selectBar, BorderLayout.LINE_START);
		
		JToolBar dvdBar = new JToolBar();
		width.setEditable(true);
	    height.setEditable(true);
		
		//Check box for crop
        //cropCheck = new JCheckBox("Crop Photos?");
        //cropCheck.setSelected(false);
        //cropCheck.addItemListener(
    	//    new ItemListener() {
    	//        public void itemStateChanged(ItemEvent e) {
    	//            boolean cropCheckStatus = (e.getStateChange() == ItemEvent.SELECTED);
    	//            // Set "ignore" whenever box is checked or unchecked.
    	//        	((ImagePane) frame.iPane).setCropFlag(cropCheckStatus);

    	            
    	            //if checked, then enable the width and height for editing
    	//            width.setEditable(cropCheckStatus);
    	//            height.setEditable(cropCheckStatus);
    	//        }
    	//    }
    	//);
		dvdBar.setRollover(true);
		//cropAll.addActionListener(this);
		//dvdBar.add(cropAll);
		//dvdBar.add(cropCheck);
		ImageIcon dvdWidthIcon = new ImageIcon("images/dvdwidth.jpg");
		JButton dvdWidth = new JButton(dvdWidthIcon);
		dvdBar.add(dvdWidth);
		width.addFocusListener(this);
		dvdBar.add(width);
		ImageIcon dvdHeightIcon = new ImageIcon("images/dvdheight.jpg");
		JButton dvdHeight = new JButton(dvdHeightIcon);
		dvdBar.add(dvdHeight);
		height.addFocusListener(this);
		dvdBar.add(height);
		add(dvdBar, BorderLayout.LINE_START);
		
		JToolBar actionBar = new JToolBar();
		actionBar.setRollover(true);
		//setPreferredSize(new Dimension(450, 50));
		prev.addActionListener(this);
		prev.setMnemonic(KeyEvent.VK_LEFT);
		prev.setActionCommand("prevKey");
		actionBar.add(prev);
		
		next.addActionListener(this);
		next.setMnemonic(KeyEvent.VK_RIGHT);
		next.setActionCommand("nextKey");
		actionBar.add(next);
		add(actionBar, (BorderLayout.CENTER));
		
		JToolBar rotateBar = new JToolBar();
		rotateBar.setRollover(true);
		//setPreferredSize(new Dimension(450, 50));
		
		copy.addActionListener(this);
		copy.setMnemonic(KeyEvent.VK_UP);
		copy.setActionCommand("copyKey");
		rotateBar.add(copy);
		
		rotateLeft.addActionListener(this);
		rotateLeft.setMnemonic(KeyEvent.VK_LESS);
		rotateLeft.setActionCommand("rotateLeft");
		rotateBar.add(rotateLeft);
		rotateRight.addActionListener(this);
		rotateRight.setMnemonic(KeyEvent.VK_GREATER);
		rotateRight.setActionCommand("rotateRight");
		rotateBar.add(rotateRight);
		flipVert.addActionListener(this);
		flipVert.setMnemonic(KeyEvent.VK_UP);
		flipVert.setActionCommand("flipVert");
		rotateBar.add(flipVert);
		flipHoriz.addActionListener(this);
		flipHoriz.setMnemonic(KeyEvent.VK_UP);
		flipHoriz.setActionCommand("flipHoriz");
		rotateBar.add(flipHoriz);
		add(rotateBar, (BorderLayout.LINE_END));
	
	}
		
	//
	//
	//Here for all the actions as a result of mouse clicks on buttons.
	public void actionPerformed( ActionEvent e ) {
	
		if(e.getSource() == prev) {
			moveToPrevPhoto();
		}
		else if(e.getSource() == copy) {
			//note that at least one photo is to be copied
			((ImagePane) frame.iPane).setCopyFlag(true);
			
			//mark this photo as one to be copied at end
			frame.photoList.getPhoto().setCopyFlag(true);
				//frame.photoList.getPhoto().copyPhoto(frame.photoDir);
			moveToNextPhoto();
		}
		else if(e.getSource() == next) {
			moveToNextPhoto();
		}
		else if(e.getSource() == cropAll) {
			frame.photoList.cropPhotos((ViewerGUI)frame);
			//frame.photoList.printPhotos();
			((ImagePane) frame.iPane).setCroppedAllFlag(true);
		}
		else if(e.getSource() == rotateLeft) {
			rotatePhoto(true);
		}
		else if(e.getSource() == rotateRight) {
			rotatePhoto(false);
		}
		else if(e.getSource() == flipVert) {
			flipPhoto(true);
		}
		else if(e.getSource() == flipHoriz) {
			flipPhoto(false);
		}
		else if(e.getSource() == selectFile) {
			selectFile();
		}
		else
			System.out.println("ControlPanel ERROR - invalid key click. Button not recognized");

			frame.repaint();
	}

	public void focusGained(FocusEvent e) {	}
	
	public void focusLost(FocusEvent e) {
		if(e.getSource() == width) {
			if(width.getText().equals("")) {
				((ImagePane) frame.iPane).resetDvdWidth();
				((ImagePane) frame.iPane).setCropFlag(false);
			}
			else {
				((ImagePane) frame.iPane).setDvdWidth(Integer.parseInt(width.getText()));
				((ImagePane) frame.iPane).setCropFlag(true);
				//System.out.println("focusLost - Set width to :" + width.getText());
			}
		}
		else if(e.getSource() == height) {
			if(height.getText().equals("")) {
				((ImagePane) frame.iPane).resetDvdHeight();
				((ImagePane) frame.iPane).setCropFlag(false);
			}
			else {
				((ImagePane) frame.iPane).setDvdHeight(Integer.parseInt(height.getText()));
				((ImagePane) frame.iPane).setCropFlag(true);
				repaint();
				//System.out.println("focusLost - Set height to :" + height.getText());
			}
		}		
		else
			System.out.println("FocusEvent ERROR - textField not recognized");
	}
	
	private void rotatePhoto(boolean rotateLeft) {
		try {
			frame.photoList.getPhoto().rotatePhoto(rotateLeft);
			frame.photoList.resetCurPhoto();
			((ImagePane) frame.iPane).setScreenImage(frame.photoList.getPhoto());
			
			//this is a hack, because I can't get the rotated image to redraw, but if I go to next, then previous, I do see the proper image
			//moveToNextPhoto();
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
	}
	
	private void flipPhoto(boolean flipVert) {
		try {
			frame.photoList.getPhoto().flipPhoto(flipVert);
			frame.photoList.resetCurPhoto();
			((ImagePane) frame.iPane).setScreenImage(frame.photoList.getPhoto());
			
			//this is a hack, because I can't get the flipped image to redraw, but if I go to next, then previous, I do see the proper image
			//moveToNextPhoto();
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
	}
	
	private void moveToNextPhoto () {
		
		if(frame.photoList.atEnd()) {
			JOptionPane.showMessageDialog(frame, "At the end");
		}
		else {
			
			//was there a crop box? if yes, then store adjusted and crop Rect before continuing
			if(((ImagePane) frame.iPane).getCropFlag() == true) {
					Rectangle rect = ((ImagePane) frame.iPane).getCropAdjustedRect();
					frame.photoList.getPhoto().setCropAdjustedImageRect(rect);
					rect = ((ImagePane) frame.iPane).getAdjustedImage();
					frame.photoList.getPhoto().setAdjustedImageRect(rect);
			}
			
			Photo photo = frame.photoList.getNextPhoto();
			
			// now move to the next photo
			((ImagePane) frame.iPane).setScreenImage(photo);
			frame.setTitle(photo.getName());
			
			//if the crop rect is landscape, and this photo is portrait view, automatically crop it and move on, since we can't do any
			//adjustment of the cropping box
			//System.out.println("photo" + photo.getName() + ": cropWidth = " + ((ImagePane) frame.iPane).getCropAdjustedRect().getWidth() + ", cropHeight = " + ((ImagePane) frame.iPane).getCropAdjustedRect().getWidth() );
			//if((((ImagePane) frame.iPane).getCropFlag() == true) && (((ImagePane) frame.iPane).getCropAdjustedRect().getWidth() > ((ImagePane) frame.iPane).getCropAdjustedRect().getHeight() )) {
			//	System.out.println("photo" + photo.getName() + ": width = " + photo.getImageRect().getWidth() + ", height = " + photo.getImageRect().getHeight() );
			//	if(photo.getImageRect().getWidth() < photo.getImageRect().getHeight() ) {
			//		moveToNextPhoto();
			//	}
			//}
		}
		
	}
	
	private void moveToPrevPhoto () {
		if(frame.photoList.atBeginning()) {
			JOptionPane.showMessageDialog(frame, "At the beginning");
		}
		else {
			
			//was there a crop box? if yes, then store adjusted and crop Rect before continuing
			if(((ImagePane) frame.iPane).getCropFlag() == true) {
				Rectangle rect = ((ImagePane) frame.iPane).getCropAdjustedRect();
				frame.photoList.getPhoto().setCropAdjustedImageRect(rect);
				rect = ((ImagePane) frame.iPane).getAdjustedImage();
				frame.photoList.getPhoto().setAdjustedImageRect(rect);
			}
			
			// now move to the previous photo
			Photo photo = frame.photoList.getPrevPhoto();
			((ImagePane) frame.iPane).setScreenImage(photo);
			frame.setTitle(photo.getName());
		}
	}
	
	private boolean selectFile() {
		boolean fileSelected = false;
		photoDir = new PhotoDirectory();
		
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG Photos", "jpg", "JPG");		
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);
		
		chooser.setMultiSelectionEnabled(false);
		chooser.setCurrentDirectory(photoDir.getRootDirectory());
		chooser.setDialogTitle("Select File to View First");
		int retVal = chooser.showOpenDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			fileSelected = true;
            File dir = chooser.getCurrentDirectory();
            File startFile = chooser.getSelectedFile();
            String imageDir = dir.getAbsolutePath();
            photoDir.setupDirectories(new File(imageDir), startFile );
            
            Photo startPhoto = photoDir.getStartPhoto();
	    	//System.out.println("StartPhoto = " + startPhoto.getName());
	        
	        // find all the photos in the selected Directory
	        photoList = new PhotoList();
	        photoList.loadImages(photoDir);
	        
	        //photoList.printPhotos();
	        //find the file to start the viewing at
	        photoList.setStartPhoto(startPhoto);
	        
	        //draw the first image
			Photo photo = photoList.getFirstPhoto();
	        ((ImagePane) iPane).setScreenImage(photo);
	        System.out.println("AAA");
            
        }
		else {
			fileSelected = false;
        	JOptionPane.showMessageDialog(null, "Warning: No photo was selected - please try again");
        }
		
		return(fileSelected);
	}
}
