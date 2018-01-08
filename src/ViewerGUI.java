import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;

import aberscan.Photo;
import aberscan.PhotoList;
import aberscan.PreloadImages;

//Stuff still to do
//1. More clean up of ImagePane and use of scaleWidth and scaleHeight
//2. Actually crop original photos
//3. Change createFinalFolder to look in the edit folder before looking in enhancedScans


public class ViewerGUI extends JFrame {
	static final long serialVersionUID = 1;
	ViewerGUI frame;
	ImagePane iPane;
	JPanel cPane;
	PhotoDirectory photoDir;
	PhotoList photoList;
	FolderGUI viewer;
	
	protected  ImagePane getIPane() { return(iPane); }
	protected  PhotoList getPhotoList() { return(photoList); }
		
	ViewerGUI() throws IOException {
        //Set up the main frame 
        super("ImageViewer");
        setSize(1920, 1040);
        frame = this;
        
        // to start, find out the image dir and starting photo
		photoDir = new PhotoDirectory();
		photoList = new PhotoList();
		viewer = new FolderGUI(photoDir);
		if(viewer.isFileSelected() == false)
			System.exit(1);
        
        Photo startPhoto = photoDir.getStartPhoto();
    	System.out.println("StartPhoto = " + startPhoto.getName());
		
    	photoList.loadPhotos(photoDir.getImageDirectory());
    	
    	preloadImages("gbs-before");			//run as a background thread
        
        //System.out.println("\n\nAAAA");
        //photoList.printPhotos();
        //find the file to start the viewing at
        photoList.setStartPhoto(startPhoto);
        
        

        
        // find all the photos in the selected Directory
        //photoList = new PhotoList();
        //photoList.loadImages(photoDir.getImageDirectory());
        
        //photoList.printPhotos();
        //find the file to start the viewing at
       // photoList.setStartPhoto(startPhoto);
        
   
        //Set up the main window
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        
        //Add the image pane
        iPane = new ImagePane(frame);
        pane.add("Center", iPane);
        
        //draw the first image
		Photo photo = photoList.getFirstPhoto();
		//photo.print();
        ((ImagePane) iPane).setScreenImage(photo);
        
        //Add the control pane
        cPane = new ControlPane(frame, iPane);
        pane.add("South", cPane);
        
        setContentPane(pane);
    }
	
    private void preloadImages(String str) {
    	Thread preloadThread;
    	
    	preloadThread = new PreloadImages("Preload of Images", str, photoList);
    	preloadThread.start();
    	
    }

	
    public static void main(String[] arguments) throws IOException {
    	
    	// Show the photo viewer
    	final ViewerGUI vFrame = new ViewerGUI();
    	
        //ExitWindow exit = new ExitWindow();
        vFrame.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				// check to see if the photos were cropped before exiting
				if((vFrame.getIPane().getCropFlag() == true) && (vFrame.getIPane().getCroppedAllFlag() == false)) {
					if(JOptionPane.showConfirmDialog(null, "Question : Before exiting, do you want to crop all photos?" , "GBS" , JOptionPane.YES_NO_OPTION) == 0) { 
						vFrame.getPhotoList().cropPhotos(); 
						JOptionPane.showMessageDialog(vFrame, "Photos all cropped");
					} 
				}
				
				//now write out the copied photos. Need to do this after the cropping step
				if(vFrame.getIPane().getCopyFlag() == true) {
					if(JOptionPane.showConfirmDialog(null, "Question : Before exiting, do you want to copy selected photos?" , "GBS" , JOptionPane.YES_NO_OPTION) == 0) { 
				    	
				    	//first of all, make sure the Copy Directory exists
				    	try {
							FileUtils.forceMkdir(vFrame.photoDir.getCopyDirectory());
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						
						vFrame.getPhotoList().copyPhotos(vFrame.photoDir.getCopyDirectory() ); 
						JOptionPane.showMessageDialog(vFrame, "Photos all copied");
					} 
				}
				System.exit(0);
			}
		});
        vFrame.setVisible(true);
    }
}
