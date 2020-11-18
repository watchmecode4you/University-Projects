import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class Start{ 
	static String profileName ;
	public Start() throws Exception{
		JButton b1,b2,b3, browse ; 

		final JFrame jf =new JFrame("START");
		jf.setSize(799,799);
		jf.setSize(800,800);
		jf.setResizable(false);
		
		final Background background = new Background();
		background.myFrame();
		jf.add(background);
		
		
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(800,800);

		
		JPanel2 pTop = new JPanel2();
		pTop.setPreferredSize(new Dimension(800,100));
		background.add(pTop,BorderLayout.NORTH);
		
		JPanel2 pBigCenter = new JPanel2();
		pBigCenter.setPreferredSize(new Dimension(800,450));
		background.add(pBigCenter,BorderLayout.CENTER);
		
		JPanel2 pCenterContainer = new JPanel2();
		pCenterContainer.setPreferredSize(new Dimension(800,100));
		
		JPanel2 pPicture = new JPanel2();
		pPicture.setPreferredSize(new Dimension(800,350));
				
		JPanel2 pBottom = new JPanel2();
		pBottom.setPreferredSize(new Dimension(800,250));

		Font f1 = new Font("comic sans ms",Font.BOLD,40);
		JLabel l3 = new JLabel(Login.NAME);
		l3.setBounds(150,10,800,40);
		l3.setFont(f1);
		l3.setForeground(Color.white);
		pTop.add(l3);
		background.add(pTop,BorderLayout.NORTH);

		b1 = new JButton("ADD");
		b1.setBackground(new Color(76,85,99));
		b1.setForeground(Color.white);
		b1.setOpaque(false);
		b1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try{
					Add a = new Add();
				}
				catch(Exception e2){}
			}
		});
		pCenterContainer.add(b1);
		
		b2 = new JButton("SEARCH");
		b2.setBackground(new Color(76,85,99));
		b2.setForeground(Color.white);
		b2.setOpaque(false);
		b2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e1) {
				try{
					Search b = new Search();
				}
				catch(Exception e3){}
			}
		});
		pCenterContainer.add(b2);	
		pBigCenter.add(pCenterContainer);
		
		
		
		final JLabel profile = new JLabel();
		profile.setPreferredSize(new Dimension(400,300));
		File selectedFile = new File("C:\\Users\\User\\workspace\\AddressBook\\download.jpg");   
        String path = selectedFile.getAbsolutePath();         
        ImageProperties imgPro = new ImageProperties() ;        
        profile.setIcon(imgPro.resizeImage(path,profile));
		pPicture.add(profile);
		
		pBigCenter.add(pPicture);

		
		b3 = new JButton("Exit Application");
		b3.setBackground(new Color(76,85,99));
		b3.setForeground(Color.white);
		b3.setOpaque(false);
		b3.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try{			
					System.exit(0);
				}
				catch(Exception e4){}
			}
			
		});
		
		pBottom.add(b3);		
		background.add(pBottom, BorderLayout.SOUTH);
		jf.show();
		}
}