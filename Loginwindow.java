package chatapplication;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

public class Loginwindow extends JFrame {
	
	JTextField  loginfield = new JTextField();
	JPasswordField passwordfield = new JPasswordField();
	JButton loginbutton =new JButton("Login");
	private final Client client;
	
	public Loginwindow () {
		super ("Login");
		
		this.client= new Client ("localhost", 6654);
	    client.connect();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
		p.add(loginfield);
		p.add(passwordfield);
		p.add(loginbutton);
		  
		loginbutton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dologin();
			}

		});
		
		getContentPane().add(p,BorderLayout.CENTER);
		pack();
		setVisible(true);
		
	}
	protected void dologin() {
		// TODO Auto-generated method stub


			// TODO Auto-generated method stub
			String login= loginfield.getText();
			String password = passwordfield.getText();
	        
			try {
				if(client.login(login, password))
				{//bring up the user list window 
					UserlistPane ulp =  new UserlistPane(client);
					JFrame frame= new JFrame ("User__List" );
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setSize(400,600);
					
					frame.getContentPane().add(new JScrollPane(ulp),BorderLayout.CENTER);
					frame.setVisible(true);
					setVisible(false);
					}else {//shows the error message
						JOptionPane.showMessageDialog(this, "invalid login");
					    
 					}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public static void main (String args []) {
		Loginwindow loginwindow = new Loginwindow();
		loginwindow.setVisible(true);
		
    }
	}
