package chatapplication;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;
import javax.swing.JList;
import javax.swing.JPanel;
 
public class MessagePane extends JPanel implements messageListener {
	
	private final Client client;
	private final String login;
    
	
	private DefaultListModel<String> listModel= new DefaultListModel<>();
	private JList<String>messageList =new JList<>();
	private JTextField inputField = new JTextField();
	 //constructor
	public MessagePane(Client client, String login) {
		this.client=client;
		this.login=login;
		client.addMessageListener(this);
		
		setLayout(new BorderLayout());
		add(messageList,BorderLayout.CENTER);
		add(inputField,BorderLayout.SOUTH);
		
		inputField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				try {
					String text = inputField.getText();
					client.msg(login,text);
					listModel.addElement("YOU: "+ text);
					inputField.setText("");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
	public void onMessage(String fromLogin, String msgBody) {
		// TODO Auto-generated method stub
		String line = fromLogin +" :" + msgBody;
		listModel.addElement(line);
	}
}
