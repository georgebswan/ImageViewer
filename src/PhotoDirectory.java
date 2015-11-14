import java.io.File;
import aberscan.Photo;


public class PhotoDirectory {
	File imageDirectory;
	File copyDirectory;
	File backupDirectory;
	File rootDirectory;
	Photo startPhoto;
	
	public PhotoDirectory() { 
		rootDirectory = new File("C:\\AberscanInProgress");
	}
	
	public File getImageDirectory() { return imageDirectory; }
	public File getCopyDirectory() { return copyDirectory; }
	public File getBackupDirectory() { return backupDirectory; }
	public File getRootDirectory() { return rootDirectory; }
	public Photo getStartPhoto() { return startPhoto; }
	
	public void setupDirectories(File imageDir, File startFile) { 
		imageDirectory = imageDir; 
		copyDirectory = new File (imageDir.getAbsolutePath() + "\\ToBeEdited");
		backupDirectory = new File (imageDir.getAbsolutePath() + "\\SavedOriginals"); 
		startPhoto = new Photo(startFile);
	}
}
