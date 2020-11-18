import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.*;

public class Add {
	public Add() throws Exception,SQLException {

		final JTextField tf1,tf2,tf3,tf4,tf5;   
		JLabel l1,l2,l3,l4,l5,l6 , instruction;
		JButton b1,b2,b3;
		final JFrame f;
	
		f= new JFrame("ADDRESS BOOK");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(800,800);

		
		final Background background = new Background();
		background.myFrame();
		f.add(background);
		
		final JPanel2 pTop = new JPanel2();
		pTop.setPreferredSize(new Dimension(800,250));
		
		final JPanel2 pCenterContainer = new JPanel2();
		pCenterContainer.setPreferredSize(new Dimension(500,200));	
		
		
		final JPanel2 pBottom = new JPanel2();	
		pBottom.setPreferredSize(new Dimension(800, 250));
		
		final JPanel2 p_ButtonsAndAddPanel_Container = new JPanel2();
		p_ButtonsAndAddPanel_Container.setLayout(new BoxLayout(p_ButtonsAndAddPanel_Container,BoxLayout.Y_AXIS));
		pCenterContainer.add(p_ButtonsAndAddPanel_Container);
		
		final JPanel2 addPanel = new JPanel2();
		addPanel.setLayout(new BoxLayout(addPanel,BoxLayout.Y_AXIS));
		addPanel.setPreferredSize(new Dimension(500,200));
		p_ButtonsAndAddPanel_Container.add(addPanel);
		
		final JPanel2 pButtons = new JPanel2();
		p_ButtonsAndAddPanel_Container.add(pButtons);

		
		
		final Connect makeConn = new Connect() ;
		makeConn.getConn();
		
		Font f1 = new Font("comic sans ms",Font.BOLD,40);
		l6 = new JLabel(Login.NAME);
		l6.setFont(f1);
		l6.setForeground(Color.white);
		pTop.add(l6);
		background.add(pTop,BorderLayout.NORTH);
		
		Font f2 = new Font("times new roman",Font.BOLD,18);
		instruction = new JLabel("Please fill the following fields in order to add a contact: ");
		instruction.setFont(f2);
		instruction.setForeground(Color.white);
		addPanel.add(instruction);
	
		l1= new JLabel("Name :");
		l1.setForeground(Color.white);
		addPanel.add(l1);
	
		tf1 = new JTextField();
		addPanel.add(tf1);
	
		l2= new JLabel("Phone number :");	
		l2.setForeground(Color.white);
		addPanel.add(l2);
	
		tf2 = new JTextField();
		addPanel.add(tf2);
	
		l3= new JLabel("Address :");
		l3.setForeground(Color.white);
		addPanel.add(l3);
	
		tf3 = new JTextField();
		addPanel.add(tf3);
	
		l4= new JLabel("Email id:");
		l4.setForeground(Color.white);
		addPanel.add(l4);
	
		tf4 = new JTextField();
		addPanel.add(tf4);
	
		l5= new JLabel("Birth date:");
		l5.setForeground(Color.white);
		addPanel.add(l5);
	
		tf5 = new JTextField();
		addPanel.add(tf5);
		
		b1 = new JButton("Enter");
		b1.setBackground(new Color(76,85,99));
		b1.setForeground(Color.white);
		b1.setOpaque(false);
		pButtons.add(b1);
		b1.addActionListener( new ActionListener(){ 
			public void actionPerformed(ActionEvent e){
				try{
					
					CheckUps checkups = new CheckUps();
					String name,phoneNumber,address,email,birthdate;
					name = tf1.getText();
					phoneNumber = tf2.getText();
					address = tf3.getText();
					email = tf4.getText();
					birthdate = tf5.getText();
					
					if(name.isEmpty() || phoneNumber.isEmpty() || address.isEmpty() || email.isEmpty() || birthdate.isEmpty()){
						JOptionPane optionPane = new JOptionPane("Some fields are left empty",JOptionPane.PLAIN_MESSAGE);
						JDialog dialog = optionPane.createDialog("Notification");
						dialog.setAlwaysOnTop(true); // to show top of all other application
						dialog.setVisible(true);
						return; 
					}
					else if(!checkups.isValidEmailAddress(email)){
						JOptionPane optionPane = new JOptionPane("Bad email format",JOptionPane.WARNING_MESSAGE);
						JDialog dialog = optionPane.createDialog("Warning!");
						dialog.setAlwaysOnTop(true); // to show top of all other application
						dialog.setVisible(true);
						return; 
					}
					
					String query = "insert into addressbook (name, phoneNumber, address, email, birthDate, idUser) "
							+ "values ('"+ name +"','"+ phoneNumber +"','"+ address +"','"+ email +"','"+ birthdate +"','"+ Login.userId +"')";
					System.out.println(query) ; 
					PreparedStatement ps = makeConn.con.prepareStatement(query);
					int i = ps.executeUpdate();
					System.out.println("Row Updated:"+i);
					if(i>0){
						JOptionPane optionPane = new JOptionPane("Contact added successfully",JOptionPane.PLAIN_MESSAGE);
						JDialog dialog = optionPane.createDialog("Notification");
						dialog.setAlwaysOnTop(true); // to show top of all other application
						dialog.setVisible(true);
					}
					else{
						JOptionPane optionPane = new JOptionPane("Contact not added",JOptionPane.PLAIN_MESSAGE);
						JDialog dialog = optionPane.createDialog("Notification");
						dialog.setAlwaysOnTop(true); // to show top of all other application
						dialog.setVisible(true);
					}
					tf1.setText("");
					tf2.setText("");
					tf3.setText("");
					tf4.setText("");
					tf5.setText("");
				}
					catch(Exception e1){
						System.out.println("ERROR:"+e1);
					}
			}
		});
		
		b2 = new JButton("Search instead");
		b2.setBackground(new Color(76,85,99));
		b2.setForeground(Color.white);
		b2.setOpaque(false);
		pButtons.add(b2);
		b2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e2) {
				try{
					Search b = new Search();
				}
					catch(Exception e3){}
			}
		});
		
			
		background.add(pCenterContainer,BorderLayout.CENTER);
		
		b3 = new JButton("Exit application");
		b3.setBackground(new Color(76,85,99));
		b3.setForeground(Color.white);
		b3.setOpaque(false);
		pBottom.add(b3);
		b3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e2) {
				try{
					System.exit(0);
				}
				catch(Exception e3){}
			}
		});
	
		
		background.add(pBottom, BorderLayout.SOUTH);	
		
		
		
		f.show();
	}
	}