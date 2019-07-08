import java.util.Map;
import java.util.HashMap;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

/*Interfaces with a MySQL database to permit
 * access to stored information such as user 
 * chat history, friendlists, as well as the 
 * hashcode corresponding to the user's password. 
 */
public class DatabaseManager {
	
	/*instance variables*/
	private Connection connection;
	private Statement statement;
	
	/*Constructor
	 * Connects to the MySQL database
	 */
	public DatabaseManager() {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
					"myuser", "mypassword");
			statement = connection.createStatement();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/* Checks whether the username and hashed password
	 * entered by the user correspond to those stored
	 * in the database
	 */
	public boolean correctLogin(String username, String password) {
		boolean check = false;
		String select = "select username, hashpass from pwbank where username = '" + username + "' and hashpass = '" 
				+ password + "'";
		ResultSet rset;
		try {
			rset = statement.executeQuery(select);
			if (rset.next()) {
				check = true;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return check;
	}
	
	/*For first time users, stores the the new user's
	 * username, hashed password, as well as a salt.
	 */
	public void store(String user, String password, String salt) {
		try {
		String insert = "insert into pwbank values ('" + user + "', '" + password + "', '" + salt + "')";
		statement.executeUpdate(insert);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*Obtains the salt for specified user*/
	public String retrieveSalt(String user) {
		String salt = "";
		try {
			String select = "select salt from pwbank where username = '" + user + "'";
			ResultSet rset = statement.executeQuery(select);
			if (rset.next()) {
				salt = rset.getString("salt");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return salt;
	}
	
	/*Obtains the friendlist for specified user*/
	public Set<String> retrieveFriendList(String user) throws SQLException {
		Set<String> friendlist = new HashSet<>();
		try {
			String select = "select friendlist from friendlists where user = '" + user + "'";
			ResultSet rset = statement.executeQuery(select);
			if (rset.next() && rset.getCharacterStream("friendlist") != null) {
				Scanner scanner = new Scanner(rset.getCharacterStream("friendlist"));
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					friendlist.add(line);
				}
				scanner.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return friendlist;
	}
	
	/*Generates the chat history for specified user.
	  Returns a map wherein the key equals to
	  the user's friend and the value equals the
	  chatlog between the user and said friend
	 */
	public Map<String, ArrayList<String>> retrieveLog(String user) throws SQLException {
		Map<String, ArrayList<String>> friendhistory = new HashMap<>();
		try {
			String select = "select * from chatlog where (user1 = '" + user + "') or"
					+ " (user2 = '" + user + "')";
			ResultSet rset = statement.executeQuery(select);
			while (rset.next()) {
				String user1 = rset.getString("user1");
				String user2 = rset.getString("user2");
				Scanner scanner = new Scanner(rset.getCharacterStream("log"));
				ArrayList<String> conversation = new ArrayList<String>();
				while (scanner.hasNext()) {
					String post = scanner.nextLine() + "\n";
					conversation.add(post);
				}
				if (user1.equals(user)) {
					friendhistory.put(user2, conversation);
				}
				else if (user2.equals(user)) {
					friendhistory.put(user1,  conversation);
				}
				scanner.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return friendhistory;
	}
	
	/*Adds new message to the chatlog*/
	public void updateLog(String msg, String user1, String user2) {
		try {
			String update = "update chatlog set log = CONCAT(log, '" + msg + "') where (user1 = '" + user1 + "' and user2 = '" + user2 + "') or"
					+ " (user1 = '" + user2 + "' and user2 = '" + user1 + "')";
			statement.executeUpdate(update);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*Creates a new log for new friends*/
	public void createLog(String user1, String user2) {
		try {
			if (!containsLog(user1, user2)) {
				String insert = "insert into chatlog (user1, user2, log) values ('" + user1 + "', '" + user2 + "', '')";
				statement.executeUpdate(insert);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*Checks whether two users have a chat history*/
	public boolean containsLog(String user1, String user2) {
		boolean check = false;
		try {
			String select = "select * from chatlog where (user1 = '" + user1 + "' and user2 = '" + user2 + "') or"
					+ " (user1 = '" + user2 + "' and user2 = '" + user1 + "')";
			ResultSet rset = statement.executeQuery(select);
			if (rset.next()) {
				check = true;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return check;
	}
	
	/*Creates a friendlist for new users*/
	public void createUserList(String user) {
		try {
			String insert = "insert into friendlists (user, friendlist) values ('" + user + "', '" + "" + "')";
			statement.executeUpdate(insert);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*Adds new friend to a user's friendlist*/
	public void updateFriendList(String mainuser, String friend) {
		try {
			String update = "update friendlists set friendlist = CONCAT(friendlist, '" + friend + "') where user = '" + mainuser + "'";
			statement.executeUpdate(update);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*Checks whether the user is in the database*/
	public boolean containsUser(String username) {
		boolean check = false;
		String select = "select username from pwbank where username = '" + username + "'";
		ResultSet rset;
		try {
			rset = statement.executeQuery(select);
			if (rset.next()) {
				String x = rset.getString("username");
				System.out.println(x);
				check = true;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}	
		return check;
	}
}
