import javax.swing.*;
import java.awt.*;
import java.io.File;

@SuppressWarnings("serial")
public class ImageProperties extends JFrame{
	
	static File f;

	public ImageIcon resizeImage(String imagePath , JLabel label ){
		 ImageIcon MyImage = new ImageIcon(imagePath);
	     Image img = MyImage.getImage();
	     Image newImg = img.getScaledInstance(label.getPreferredSize().width, label.getPreferredSize().height, Image.SCALE_SMOOTH);
	     ImageIcon image = new ImageIcon(newImg);
	     return image;
	}
	
	/*
	 * Method responsible for getting A File type field to the Register.java Class 
	 * to able to add image file to the database 
	 */
	

	@SuppressWarnings("static-access")
	public void getFile(File file){
		this.f = file ; 
	}
}
