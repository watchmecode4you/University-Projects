import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.*;
@SuppressWarnings("serial")
public class Background extends JPanel{
	
	BufferedImage bi ; 
	
	public void myFrame(){
		try{
			bi = ImageIO.read(new File("C:\\Users\\User\\workspace\\AddressBook\\pic2.jpg"));
		}
		catch(IOException ex){}
		setSize(800,800);
		setVisible(true);
		setLayout(new BorderLayout());
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(bi,0,0,getWidth(),getHeight(),null);
	}
}
