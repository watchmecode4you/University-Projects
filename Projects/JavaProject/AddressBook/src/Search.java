import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.sql.*;

public class Search{
	public Search(){
		
		final JFrame jf = new JFrame("Search");
		jf.setSize(800,800);
		jf.setResizable(false);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		final Background background = new Background();
		background.myFrame();
		jf.add(background);
		
		final JPanel2 pTop = new JPanel2();
		pTop.setPreferredSize(new Dimension(800,100));
		
		final JPanel2 pCenterContainer = new JPanel2();
		pCenterContainer.setPreferredSize(new Dimension(800,300));
		background.add(pCenterContainer,BorderLayout.CENTER);
		
		final JPanel2 pBottom = new JPanel2();
		pBottom.setPreferredSize(new Dimension(800,250));
		
		final JTable table = new JTable(); 
		table.setLayout(new FlowLayout());
		
		final DefaultTableModel model = new DefaultTableModel();
		
		final Object[] columnsNames = new Object[]{"Name","Phone number","Address","Email","BirthDate"};
		
		model.setColumnIdentifiers(columnsNames);
		
		final Object[] data = new Object[5];
		
		final Object[] previousData = new Object[5];
		
		Font f1 = new Font("comic sans ms",Font.BOLD,40);
		JLabel l3 = new JLabel(Login.NAME);
		l3.setBounds(150,10,800,40);
		l3.setFont(f1);
		l3.setForeground(Color.white);
		pTop.add(l3);
		background.add(pTop,BorderLayout.NORTH);
			
		JLabel l = new JLabel ("Name:");
		l.setForeground(Color.white);
		pCenterContainer.add(l);
			
		final JTextField tf = new JTextField();
		tf.setPreferredSize(new Dimension(100,20));
		pCenterContainer.add(tf);			
		
		JButton b1 = new JButton("Search");
		b1.setBackground(new Color(76,85,99));
		b1.setForeground(Color.white);
		b1.setOpaque(false);
		pCenterContainer.add(b1);
		b1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				try{
					String name;																				
				  				
					name = tf.getText();
					
					if(name.isEmpty()){
						JOptionPane optionPane = new JOptionPane("Fields are Empty!Try Again",JOptionPane.WARNING_MESSAGE);
						JDialog dialog = optionPane.createDialog("Warning!");
						dialog.setAlwaysOnTop(true); // to show top of all other application
						dialog.setVisible(true);
						return ; 
					}
				    final Connect makeConn = new Connect();
					makeConn.getConn();
					
					String query = "Select * From addressbook where addressbook.idUser = '"+Login.userId+"'";
					
					Statement st =  makeConn.con.createStatement();
					ResultSet rs = st.executeQuery(query);
					
					System.out.println(query);
					int x=1;
					while(rs.next()){
						System.out.println(x++);
						
						data[0]= rs.getObject(2) ;
						data[1]= rs.getObject(3) ;
						data[2]= rs.getObject(4) ;
						data[3]= rs.getObject(5) ;
						data[4]= rs.getObject(6) ;			
						
						System.out.println("name is " +data[0]+" with a phone number of: " + data[1]);
						
						System.out.println("gotstring");
						
						if(data[0].toString().contains(name) && !data[0].equals(previousData[0])){
						
							System.out.println("good condition");
								
							model.addRow(data);
								
							table.setModel(model);	
							
							JScrollPane pane = new JScrollPane(table);
							
							pane.getViewport().setBackground(Color.WHITE);
							
							pCenterContainer.add(pane);
							
							jf.setContentPane(background);
							
							previousData[0] = data[0];
						} 						
						System.out.println("one last look");
					}
					makeConn.con.close();
					}
					catch(Exception e1){
						System.out.println("ERROR="+e1);
					}
				System.out.println("i'm here");
			}
		});
					
		
		JButton b2 = new JButton("Add instead");
		b2.setBackground(new Color(76,85,99));
		b2.setForeground(Color.white);
		b2.setOpaque(false);
		pCenterContainer.add(b2);
		b2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try{
					Add a = new Add();
				}
				catch(Exception e2){}
			}
		});
		
		
		
		JButton b3 = new JButton("Exit Application");
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

		background.add(pBottom,BorderLayout.SOUTH);
		
		
		
		jf.show();
	}
}