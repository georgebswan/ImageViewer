
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;


public class FolderGUI extends JFrame {
	static final long serialVersionUID = 4;
	boolean fileSelected = false;
	
	public boolean isFileSelected() { return (fileSelected); }
	
	FolderGUI(PhotoDirectory photoDir) {

		final JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Photos", "jpg", "JPG", "tif", "TIF");		
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
        }
		else {
			fileSelected = false;
        	JOptionPane.showMessageDialog(null, "Warning: No photo was selected - please try again");
        }
	}
}
