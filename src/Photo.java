import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class Photo {
	File srcFile;
	Rectangle imageRect;
	Rectangle cropImageRect;
	Rectangle adjustedImageRect;
	Rectangle cropAdjustedImageRect;
	boolean copyFlag;
	Image image;
	
    public Photo(File sFile) {
    	srcFile = sFile;
       	imageRect = new Rectangle(0,0,0,0); 	//don't known size until loaded
       	cropImageRect = new Rectangle(0,0,0,0);
    	cropAdjustedImageRect = new Rectangle(0,0,0,0);
    	adjustedImageRect = new Rectangle(0,0,0,0);
    	copyFlag = false;
    	//image = getIntImage();
    	image = null;
    	
    }
    
    public Photo(File sFile, Rectangle srcCrop) {
    	srcFile = sFile;
       	imageRect = new Rectangle(0,0,0,0); 	//don't known size until loaded
       	cropImageRect = new Rectangle(0,0,0,0);
    	cropAdjustedImageRect = srcCrop;
    	adjustedImageRect = new Rectangle(0,0,0,0);
    	copyFlag = false;
    	//image = getImage();
    	image = null;
    }
    
    public String getName() { return srcFile.getAbsolutePath();}
    public File getSrcFile() { return srcFile; }
    public Image getExtImage() {return image; }
    //public Image getImage() {
    //	String ext;
    //	File fileUsed;
    //	//Check to see if the image is a tif file. If yes, convert it to a temporary jpg and display that, since createImage doesn't seem to work with tifs
    //	ext = FilenameUtils.getExtension(srcFile.getName());
    //	if(ext.equals("tif")) {
    //    	File tmpJpgFile = new File("C:\\Temp\\gbs.jpg");
    //       	String[] args = {"D:\\Program Files (x86)\\ImageMagick-6.8.7-Q16\\convert.exe", srcFile.getAbsolutePath(), tmpJpgFile.getAbsolutePath() };
    //       	try {
	//			Process p = Runtime.getRuntime().exec(args);
	//			try {
	//				p.waitFor();
	//			} catch (InterruptedException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			} //wait for the exec to finish
	//			//System.out.println("AAA - '"  + args[1]);
	//    		fileUsed = tmpJpgFile;
	//    		//image = Toolkit.getDefaultToolkit().createImage(tmpJpgFile.getAbsolutePath());
	//		} catch (IOException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//			fileUsed = null;
	//		}
   // 	}
   // 	else {
   // 		fileUsed = srcFile;
   // 	}
 
	//	Image image = Toolkit.getDefaultToolkit().createImage(fileUsed.getAbsolutePath());
   // 	return (image);
   // }
    public Rectangle getImageRect() { return imageRect; }
    public Rectangle getCropImageRect() { return cropImageRect; }
    public Rectangle getCropAdjustedImageRect() { return cropAdjustedImageRect; }
    public Rectangle getAdjustedImageRect() { return adjustedImageRect; }
    public boolean getCopyFlag() { return copyFlag; }
    
    public void setImageRect(int width, int height) { imageRect = new Rectangle(0,0,width, height); }
    public void setCropImageRect(Rectangle cRect) { cropImageRect = new Rectangle(cRect); }
    public void setCropAdjustedImageRect(Rectangle cRect) { cropAdjustedImageRect = new Rectangle(cRect);}
    public void setAdjustedImageRect(Rectangle aRect) { adjustedImageRect = new Rectangle(aRect);}
    public Photo reset() { return( new Photo(srcFile)); }
    public void setCopyFlag(boolean flag) { copyFlag = flag; }
    
    public void copyPhoto(PhotoDirectory photoDir) throws IOException {
    	//System.out.println("Copying file : '" + srcFile.getAbsolutePath() + "' to Folder '" + destDir.getAbsolutePath() + "'");
    		FileUtils.copyFileToDirectory(srcFile, photoDir.getCopyDirectory());
    }
    
    public void backupPhoto(PhotoDirectory photoDir) throws IOException {
    	//System.out.println("Copying file : '" + srcFile.getAbsolutePath() + "' to Folder '" + destDir.getAbsolutePath() + "'");
    		FileUtils.copyFileToDirectory(srcFile, photoDir.getBackupDirectory());
    }
    
    public void cropPhoto(ViewerGUI frame) throws IOException {
		String exePath = "D:\\Program Files (x86)\\ImageMagick-6.8.7-Q16\\convert.exe";
		String command = "-crop";
		Rectangle cropRect = this.getCropImageRect();
		//String srcFileName = srcFile.getName();
		//String destFileName = photoDir.getCropDirectory().getAbsolutePath() + "\\" + srcFileName;
		
    	//System.out.println("Copying file : '" + srcFile.getAbsolutePath() + "' to Folder '" + destDir.getAbsolutePath() + "'");
    	//FileUtils.copyFileToDirectory(srcFile, cropDir);
    		
		// construct the cmdLine
		// first, build up the crop argument (e.g. width x height + ex + wy)
		// the reduction by 100/102 is a hack because photos sized 16:9 still need to be zoomed up to 102% to fill the screen horizontally
		String rectInfo = (int)(cropRect.getWidth()) + "x"+ (int) (cropRect.getHeight()*100/100) + "+" + (int)(cropRect.getX()) + "+" + (int)(cropRect.getY());
		String [] cmdLine = new String [] { exePath, srcFile.getAbsolutePath(), command, rectInfo, srcFile.getAbsolutePath() };
		
		//Print out the cmdLine doing the crop
		//print();
		//System.out.println("cmdLine = ");
		//for(int i = 0 ; i < cmdLine.length ; i++ ) {
		//	System.out.println(cmdLine[i]);
		//}

		try {
			Process p = Runtime.getRuntime().exec(cmdLine);
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //wait for the exec to finish
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void rotatePhoto(boolean rotateLeft) throws IOException {
		String exePath = "D:\\Program Files (x86)\\ImageMagick-6.8.7-Q16\\convert.exe";
		String direction;
		if(rotateLeft == true)
			direction = "-90";
		else
			direction = "90";
    		
		// construct the cmdLine
		String [] cmdLine = new String [] { exePath, "-rotate", direction, srcFile.getAbsolutePath(), srcFile.getAbsolutePath() };
		
		//Print out the cmdLine doing the rotate
		//print();
		//System.out.println("cmdLine = ");
		//for(int i = 0 ; i < cmdLine.length ; i++ ) {
		//	System.out.println(cmdLine[i]);
		//}

		try {
			Process p = Runtime.getRuntime().exec(cmdLine);
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //wait for the exec to finish;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void flipPhoto(boolean flipVert) throws IOException {
		String exePath = "D:\\Program Files (x86)\\ImageMagick-6.8.7-Q16\\convert.exe";
		String direction;
		if(flipVert == true)
			direction = "-flip";
		else
			direction = "-flop";
    		
		// construct the cmdLine
		String [] cmdLine = new String [] { exePath, direction, srcFile.getAbsolutePath(), srcFile.getAbsolutePath() };
		
		//Print out the cmdLine doing the rotate
		//print();
		//System.out.println("cmdLine = ");
		//for(int i = 0 ; i < cmdLine.length ; i++ ) {
		//	System.out.println(cmdLine[i]);
		//}

		try {
			Runtime.getRuntime().exec(cmdLine);
			Process p = Runtime.getRuntime().exec(cmdLine);
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //wait for the exec to finish;
			//srcFile = new File(srcFile.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void print(){
    	System.out.println("Photo: \n\tfileName = " + getName() + 
    			"\n\timageRect = " + imageRect.toString() + 
    			"\n\tcropImageRect = " + cropImageRect.toString() + 
    			"\n\tadjustedImageRect = " + adjustedImageRect.toString() +
				"\n\tcropAdjustedImageRect = " + cropAdjustedImageRect.toString()); 
    }
}
