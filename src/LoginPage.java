import javax.swing.*;
import bCrypthash.*;
import java.awt.Component;
import java.awt.event.*;
import java.awt.Dimension;

/*GUI that prompts the user for their
 * username and password. Uses the BCrypt
 * hashing algorithm to hash the password 
 * with a salt
 */

public class LoginPage implements ActionListener {
	
	/*instance variables and constants*/
	
	public static final int FIELD_SIZE = 20;
	public static String IP;
	public static JFrame frame;
	public static DatabaseManager manager;
	private String usernameEntry;
	private JButton newUser;
	private JButton login;
	private JButton submit;
	private JButton cancel;
	private JButton register;
	private JTextField usernamefield;
	private JTextField passwordfield;
	private JLabel usernamelabel;
	private JLabel passwordlabel;
	private JLabel message;
	private JPanel panel1;
	private JPanel panel2;
	private JPanel panel3;
	
	public static void main(String[] args) {
		String IP = args[0];
		frame = new JFrame();
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		new LoginPage();
	}
	
	public LoginPage() {
		newUser = new JButton("New user");
		login = new JButton("Login");
		submit = new JButton("Submit");
		cancel = new JButton("Cancel");
		register = new JButton("Register");
		usernamefield = new JTextField(FIELD_SIZE);
		panel3 = new JPanel();
		usernamefield.setMaximumSize(new Dimension(Integer.MAX_VALUE, usernamefield.getPreferredSize().height));
		passwordfield = new JPasswordField(FIELD_SIZE);
		passwordfield.setMaximumSize(new Dimension(Integer.MAX_VALUE, passwordfield.getPreferredSize().height));
		usernamelabel = new JLabel("		Username:");
		passwordlabel = new JLabel("		Password:");
		manager = new DatabaseManager();
		mainPage();
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		usernameEntry = usernamefield.getText();
		String pwEntry = passwordfield.getText();
		if (command.equals("Login")) {
			setUpLogin(submit);
		}
		else if (command.equals("New user")) {
			setUpLogin(register);
		}
		else if (command.equals("Cancel")) {
			mainPage();
		}
		else if ((command.equals("Submit") && usernameEntry.isEmpty() != true && 
				pwEntry.isEmpty() != true) && manager.containsUser(usernameEntry)) {
			clearAlert();
			clearText();
			String hashedPass = BCrypt.hashpw(pwEntry, manager.retrieveSalt(usernameEntry));
			if (manager.correctLogin(usernameEntry, hashedPass)) {
				new MessengerPage(usernameEntry);
				return;
			}
			else {
				alert("Wrong username or password");
			}
		}
		else if ((command.equals("Register") && usernameEntry.isEmpty() != true && 
				pwEntry.isEmpty() != true)) {
			clearAlert();
			clearText();
			if (manager.containsUser(usernameEntry)) {
				alert("This username has already been chosen.");
			} 
			else if (!manager.containsUser(usernameEntry)) {
				String salt = BCrypt.gensalt();
				String hashedpass = BCrypt.hashpw(pwEntry, salt);
				manager.store(usernameEntry, hashedpass, salt);
				manager.createUserList(usernameEntry);
				alert("You have been registered.");
			}
		}
	}
	
	public void mainPage() {
		frame.getContentPane().removeAll();
		panel1 = new JPanel();
		panel1.add(newUser);
		panel1.add(login);
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
		frame.add(panel1);
		frame.pack();
		frame.setVisible(true);
		newUser.addActionListener(this);
		login.addActionListener(this);
	}
	
	public void setUpLogin(JButton selection) {
		frame.getContentPane().removeAll();
		clearText();
		panel1 = new JPanel();
		panel1.add(usernamelabel);
		panel1.add(Box.createVerticalGlue());
		panel1.add(passwordlabel);
		panel1.add(Box.createVerticalGlue());
		panel1.add(cancel);
		panel1.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
		panel2 = new JPanel();
		panel2.add(usernamefield);
		panel2.add(Box.createVerticalGlue());
		panel2.add(passwordfield);
		panel2.add(Box.createVerticalGlue());
		selection.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel2.add(selection);
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
		frame.add(panel1);
		frame.add(panel2);
		frame.pack();
		frame.setVisible(true);
		selection.addActionListener(this);
		cancel.addActionListener(this);
		usernamefield.addActionListener(this);
		passwordfield.addActionListener(this);
	}
	
	/*Displays a warning text on the bottom of the screen*/
	public void alert(String alertmessage) {
		message = new JLabel(alertmessage);
		panel3.add(message);
		panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));
		panel3.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		frame.add(panel3);
		frame.pack();
		frame.setVisible(true);		
	}
	
	public void clearAlert() {
		panel3.removeAll();
		frame.pack();
		frame.setVisible(true);
	}
	
	public void clearText() {
		usernamefield.setText("");
		passwordfield.setText("");
	}
}
