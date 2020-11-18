import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import javazoom.jlgui.basicplayer.BasicPlayerException;

import java.sql.*;
public class Login {
	public static String userId ; 
	public static String NAME ; 
	public static void main(String args[]){
			final JTextField tf1 ;
			final JPasswordField tf2;
			JLabel l1,l2,l3;
			final JButton b1 , b2 ; 
						
			final JFrame f = new JFrame();
			f.setSize(799,799);
			f.setSize(800,800);
			f.setResizable(false);
				
			final Background background = new Background();
			background.myFrame();
			f.add(background);
					
			BackgroundMusic backgroundmusic = new BackgroundMusic();
			backgroundmusic.musicLoop();
			
			System.out.println("i'm here!");
			
			final JPanel2 pTop = new JPanel2();
			pTop.setPreferredSize(new Dimension(800, 250));
			
			final JPanel2 pCenter = new JPanel2();
			pCenter.setPreferredSize(new Dimension(800, 300));
			
			final JPanel2 pUser_Pass = new JPanel2();
			pUser_Pass.setLayout(new BoxLayout(pUser_Pass, BoxLayout.Y_AXIS));
			pCenter.add(pUser_Pass);
			
			final JPanel2 pBottom = new JPanel2();
			pBottom.setPreferredSize(new Dimension(800, 250));		

			Font f1 = new Font("comic sans ms",Font.BOLD,40);
			l3 = new JLabel("ADDRESS BOOK");
			l3.setFont(f1);
			l3.setForeground(Color.white);
			pTop.add(l3);			
			background.add(pTop,BorderLayout.NORTH);
		
		
			l1= new JLabel("Username :");
			l1.setForeground(Color.white);
			pUser_Pass.add(l1);
		
			 tf1 = new JTextField();
			 pUser_Pass.add(tf1);
		
			l2= new JLabel("Password :");
			l2.setForeground(Color.white);
			pUser_Pass.add(l2);
		
			tf2 = new JPasswordField();
			pUser_Pass.add(tf2);
			
			background.add(pCenter, BorderLayout.CENTER);
			
			
			b1 = new JButton("LOGIN");
			b1.setBackground(new Color(76,85,99));
			b1.setForeground(Color.white);
			b1.setOpaque(false);
			pBottom.add(b1);
			b1.addActionListener( new ActionListener(){
				public void actionPerformed(ActionEvent e){
					try{
						final Connect makeConn = new Connect();
						makeConn.getConn();
						final Statement st = makeConn.con.createStatement();
						
						String username = tf1.getText();
						@SuppressWarnings("deprecation")
						String password = tf2.getText();
						
						if(username.isEmpty() || password.isEmpty()){
							JOptionPane optionPane = new JOptionPane("Some fields are left Empty",JOptionPane.WARNING_MESSAGE);
							JDialog dialog = optionPane.createDialog("Warning!");
							dialog.setAlwaysOnTop(true); // to show top of all other application
							dialog.setVisible(true);
						}
						
						System.out.println("got data ");
						ResultSet rs = st.executeQuery("Select * From user");
						System.out.println("table select");
						while(rs.next()){
							NAME = rs.getString(2);
							String dbUsername = rs.getString(3);
							String dbPassword = rs.getString(4);
							System.out.println("data entered"+username+"	"+password);
							System.out.println("inside while"+dbUsername+"	"+dbPassword);
							if((!username.isEmpty() || !password.isEmpty()) && dbUsername.equals(username) && dbPassword.equals(password)){ 
								try{
									userId = rs.getString(1);
									System.out.println(userId);
									f.hide();
									Start s = new Start();
									}
								catch(Exception e3){
								}
								break ; 
							}		
					    }
						tf1.setText("");
						tf2.setText("");
						makeConn.con.close();
					}
					catch(Exception e1){System.out.println("ERROR 1"+e1);}
				}
			});
			b2 = new JButton("REGISTER");
			b2.setBackground(new Color(76,85,99));
			b2.setForeground(Color.white);
			b2.setOpaque(false);
			pBottom.add(b2);
			b2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					try {
						@SuppressWarnings("unused")
						Register r = new Register();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			
			background.add(pBottom, BorderLayout.SOUTH);	
			
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
			f.show();
		}
	
}
		
