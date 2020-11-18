import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Register {
	
	@SuppressWarnings("deprecation")
	public Register() throws Exception,SQLException {		
		
		final JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(800,800);
		f.setResizable(false);
		
		
		final Background background = new Background();
		background.myFrame();
		f.add(background);
		
		JFileChooser filechooser ;
		
		final JLabel title, instruction, username, password, confirmPassword, name, image, imageSpace; 
		
		final JTextField user , pass , confPass , nameT ; 
		
		final JButton register, cancel, browse ; 
		
		final JPanel2 pTop = new JPanel2();
		pTop.setPreferredSize(new Dimension(800,250));
		
		final JPanel2 pCenterContainer = new JPanel2();
		pCenterContainer.setLayout(new BoxLayout( pCenterContainer,BoxLayout.X_AXIS));
		pCenterContainer.setPreferredSize(new Dimension(800,250));	
		
		final JPanel2 registration = new JPanel2();
		registration.setLayout(new BoxLayout(registration,BoxLayout.Y_AXIS));
		registration.setPreferredSize(new Dimension(400,300));	
		pCenterContainer.add(registration);
		
		final JPanel2 pPicture = new JPanel2();
		pPicture.setPreferredSize(new Dimension(400,300));
		pCenterContainer.add(pPicture);
		
		final JPanel2 pBottom = new JPanel2();	
		pBottom.setPreferredSize(new Dimension(800,300));
		
		
		Font f1 = new Font("comic sans ms",Font.BOLD,40);
		title = new JLabel("Registration");
		title.setForeground(Color.white);
		title.setFont(f1);
		pTop.add(title);
		background.add(pTop,BorderLayout.NORTH);
			
		Font f2 = new Font("times new roman",Font.BOLD,18);
		instruction = new JLabel("Please fill the following fields in order to register:");
		instruction.setFont(f2);
		instruction.setForeground(Color.white);
		registration.add(instruction);
		
		name = new JLabel("Name :");
		name.setForeground(Color.white);
		registration.add(name);
		
		nameT = new JTextField();
		registration.add(nameT);
		
		username = new JLabel("Username :");
		username.setForeground(Color.white);
		registration.add(username);
		
		user = new JTextField();
		registration.add(user);
		
		password = new JLabel("Password :");
		password.setForeground(Color.white);
		registration.add(password);
		
		pass = new JPasswordField();
		registration.add(pass);
		
		confirmPassword = new JLabel("Confirm password :");
		confirmPassword.setForeground(Color.white);
		registration.add(confirmPassword);
		
		confPass = new JPasswordField();
		registration.add(confPass);
		
		image = new JLabel("Insert image:");
		image.setForeground(Color.white);
		registration.add(image);
		
		imageSpace = new JLabel();
		imageSpace.setPreferredSize(new Dimension(400,300));
		pPicture.add(imageSpace);
		
		browse = new JButton("Browse");
		browse.setBackground(new Color(174,105,55));
		browse.setForeground(Color.white);
		browse.setOpaque(false);
		registration.add(browse);
		browse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				String userDir = System.getProperty("user.home");
				
				 //JFileChooser file = new JFileChooser(userDir+"/Desktop/Editing Folder/emojis");
				 JFileChooser file = new JFileChooser(userDir+"/workspace/AddressBook");
		         
		          //filter the files
		         FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg","gif","png");
		         file.addChoosableFileFilter(filter);
		         int result = file.showSaveDialog(null);
		         
		           //if the user click on save in Jfilechooser
		         if(result == JFileChooser.APPROVE_OPTION){
		            File selectedFile = file.getSelectedFile();
		            	             
		            String path = selectedFile.getAbsolutePath();
		             
		            ImageProperties imgPro = new ImageProperties() ;
		             
		            imageSpace.setIcon(imgPro.resizeImage(path,imageSpace));
		             
		            imgPro.getFile(selectedFile);
		         }
		           //if the user click on save in Jfilechooser


		         else if(result == JFileChooser.CANCEL_OPTION){
		            System.out.println("No File Select");
		            JOptionPane optionPane = new JOptionPane("No File Select",JOptionPane.PLAIN_MESSAGE);
					JDialog dialog = optionPane.createDialog("Notification");
					dialog.setAlwaysOnTop(true); // to show top of all other application
					dialog.setVisible(true);
		         }
		       }
			});
		
		background.add(pCenterContainer, BorderLayout.CENTER);
		
		register = new JButton("register");
		register.setBackground(new Color(76,85,99));
		register.setForeground(Color.white);
		register.setOpaque(false);
		pBottom.add(register);
		register.addActionListener(new ActionListener(){

			@SuppressWarnings({ "deprecation", "unused" })
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try{
					final Connect makeConn = new Connect();
					makeConn.getConn();
					
					CheckUps checkups = new CheckUps();
					String Name = nameT.getText();
					String Username = user.getText();
					String Password = pass.getText();
					String ConfPass = confPass.getText();
					
					Date date = new Date(System.currentTimeMillis());
					String dateCreated = new SimpleDateFormat("yyyy-MM-dd").format(date);
					
					ImageProperties img = new ImageProperties();
					File file = img.f ; 
					
					System.out.println(file);
					
					FileInputStream fis = new FileInputStream(file);
					
					System.out.println(fis);
					
					if(Name.isEmpty() && Username.isEmpty() && Password.isEmpty() && ConfPass.isEmpty()){
						JOptionPane optionPane = new JOptionPane("Some fields are left Empty",JOptionPane.WARNING_MESSAGE);
						JDialog dialog = optionPane.createDialog("Warning!");
						dialog.setAlwaysOnTop(true); // to show top of all other application
						dialog.setVisible(true);
					}
					else if(!checkups.isValidPassword(Password,ConfPass)){
						JOptionPane optionPane = new JOptionPane("Passord/Confirmation mismatch or bad Password format. Password should at least contain 8 characters,"
								+ " 1 special character, 1 numeric number, special cases as well as lower cases",JOptionPane.WARNING_MESSAGE);
						JDialog dialog = optionPane.createDialog("Warning!");
						dialog.setAlwaysOnTop(true); // to show top of all other application
						dialog.setVisible(true);
					}
					else {
						String query = "INSERT INTO user (name,username,password,dateCreated,image) VALUES ('"+ Name +"','"+ Username +"','"+ 
								Password +"','"+dateCreated+"', '"+fis+"')";
						
						System.out.println(query);
						try{
							PreparedStatement ps = makeConn.con.prepareStatement(query);
							int i = ps.executeUpdate();
							System.out.println("Row Updated:"+i);
							
							if(i>0){		
								JOptionPane optionPane = new JOptionPane("User added successfully",JOptionPane.PLAIN_MESSAGE);
								JDialog dialog = optionPane.createDialog("Notification");
								dialog.setAlwaysOnTop(true); // to show top of all other application
								dialog.setVisible(true);
								
								nameT.setText("");
								user.setText("");
								pass.setText("");
								confPass.setText("");
								
								
								Login l = new Login();
								f.hide(); 						
							}
							else{
								JOptionPane optionPane = new JOptionPane("User already exists",JOptionPane.PLAIN_MESSAGE);
								JDialog dialog = optionPane.createDialog("Notification");
								dialog.setAlwaysOnTop(true); // to show top of all other application
								dialog.setVisible(true);
							}
						}catch(Exception e3){
							e3.printStackTrace();
						}
					}	
				}
				catch(Exception e3){
					
				}
				
			}
			
		});
		
		cancel = new JButton("cancel");
		cancel.setBackground(new Color(76,85,99));
		cancel.setForeground(Color.white);
		cancel.setOpaque(false);
		pBottom.add(cancel);
		cancel.addActionListener(new ActionListener(){

			@SuppressWarnings({ "deprecation", "unused" })
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Login l = new Login();
			}
			
		});
		
		background.add(pBottom,BorderLayout.SOUTH);
		
		f.setVisible(true);
		f.show();
	}
}
