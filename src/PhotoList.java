import java.awt.Rectangle;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

public class PhotoList {
	ArrayList <Photo> photos;
	int curIndex = 0;
	
    public PhotoList() {
    	curIndex = 0;
    	photos = new ArrayList<Photo>();
    }
    
    public Photo getPhoto() { return photos.get(curIndex); }
    public Photo getFirstPhoto() { return photos.get(curIndex); }
    public Photo getNextPhoto() { return photos.get(++curIndex); }
    public Photo getPrevPhoto() { return photos.get(--curIndex); }
    public boolean atBeginning() {if(curIndex == 0) return (true); else return(false); }
    public boolean atEnd() {if(curIndex == (photos.size() - 1)) return (true); else return(false); }
    
    public void resetCurPhoto() {
    	//add a new photo into the photolist after the current one, then go back and remove the old one
    	photos.add(curIndex, getPhoto().reset()); 
    	photos.remove(curIndex);
	}
    
    public void setStartPhoto(Photo photo) {
    	//Goto find where that Photo is in the list of Photos
    	String name = photo.getName();
    	//System.out.println("name = " + name);
    	for(int i = 0; i < photos.size(); i++){
    		//System.out.println("photos" + "[" + i + "] = " + photos[i].getName());
    		if(name.equals(photos.get(i).getName())) {
    			curIndex = i;
    			return;
    		}
    	}
    	System.out.println("PhotoList ERROR: Can't find match for Photo name");
    	System.exit(0);
    	
    }
    
    public void loadImages(PhotoDirectory photoDir){
    	File fileDir = photoDir.getImageDirectory();
    	File [] fileList;
    	Photo photo;
    	
    	//System.out.println("imageDirectory = " + fileDir.getAbsolutePath());
 
    	//get all the files from a directory
       fileList = fileDir.listFiles(new ImageFileFilter());
       
       for (File file : fileList) {
    	   // store the file, image, and image size in the photo, then add to Photolist
    	   photo = new Photo(file);

    	   
    	   photos.add(photo);
       }

    }
    
    public class ImageFileFilter implements FileFilter
    {
      private final String[] okFileExtensions = 
        new String[] {"jpg", "JPG", "tif", "TIF"};

      public boolean accept(File file)
      {
        for (String extension : okFileExtensions)
        {
          if (file.getName().toLowerCase().endsWith(extension))
          {
            return true;
          }
        }
        return false;
      }
    }
    
    public void cropPhotos(ViewerGUI frame) {
    	
    	//first of all, copy the original photos off to another dir so that they are protected
    	//make sure the Backup Directory exists
    	try {
			FileUtils.forceMkdir(frame.photoDir.getBackupDirectory());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
    	//copy the originals into this backup Directory
		for (Photo photo : photos){
			try {
				//source file comes from a different place based on whether photos were cropped or not
				photo.backupPhoto(frame.photoDir);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
    	
    	
		// go through each photo, work out the crop rectangle based on orig size, then do the crop
		for (Photo photo : photos){
			//Is there a crop Rectangle defined for this photo?
			if(photo.getCropAdjustedImageRect().getWidth() != 0 && photo.getCropAdjustedImageRect().getHeight() != 0 ) {
				
				// calculate the cropRectangle back in the original image coordinate system
				// note that to do the calculation, you have to assume the adjustedImageRect (x,y) is (0,0). 
				// so the crop x.y calculation is relative to the adjustedImage, not to the actual orgin
				
				double widthRatio = photo.getImageRect().getWidth()/photo.getAdjustedImageRect().getWidth();
				double heightRatio = photo.getImageRect().getHeight()/photo.getAdjustedImageRect().getHeight();
				
				Rectangle cropImageRect = new Rectangle((int) ((photo.getCropAdjustedImageRect().getX() - photo.getAdjustedImageRect().getX())  * widthRatio),
										(int) ((photo.getCropAdjustedImageRect().getY() - photo.getAdjustedImageRect().getY()) * heightRatio),
										(int) (photo.getCropAdjustedImageRect().getWidth() * widthRatio),
										(int) (photo.getCropAdjustedImageRect().getHeight() * heightRatio));
				photo.setCropImageRect(cropImageRect);
				
				//System.out.println("Width Ratio = " + widthRatio + "\n\tVertical Ratio = "+ heightRatio); 
				//System.out.println("Crop Rect = " + cropImageRect.toString());
				
				//Now do the crop
				try {
					photo.cropPhoto(frame);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		}
		JOptionPane.showMessageDialog(frame, "Photos all cropped");
    }
    
    public void copyPhotos(ViewerGUI frame) {
    	
    	//first of all, make sure the Copy Directory exists
    	try {
			FileUtils.forceMkdir(frame.photoDir.getCopyDirectory());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
    	
		// go through each photo. If flag set, then do the copy
		for (Photo photo : photos){
			//Is there a copy flag set?
			if(photo.getCopyFlag() == true) {
				try {
					//source file comes from a different place based on whether photos were cropped or not
					photo.copyPhoto(frame.photoDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		}
		JOptionPane.showMessageDialog(frame, "Photos all copied");
    }
    
    
    public void printPhotos(){
    	System.out.println("Number of photos is " + photos.size());
        for (Photo photo : photos){
            photo.print(); 
        }
    }
}

