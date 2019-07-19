# Messenger

IMPORTANT - MUST READ: This program can be tricky to set up at first. In order for this program to work, two server connections must be established: a MySQL server and a Server for handling
messages in the chat (provided as Server.java). The following is designed for Mac OS users,
but it can be similarly adapted for Windows.

  1) The user must have downloaded the MySQL JDBC driver and inserted it into the classpath.
  Accordingly, the user must start a MySQL server and a MySQL client. Much of the code in the
  DatabaseManager.java file corresponds to my personal SQL server; accordingly, the user must
  create the appropriate table and database names in order for it to function properly. For example,
  "myuser" and "mypassword"  contained on Line 26 of the DatabaseManager.java should be changed to 
  the login information of the MySQL client.
  
  Then, to run the program, the user must enter 
  
  'java -cp .:$HOME/myWebProject/mysql-connector-java-8.0.{xx}/mysql-connector-java-8.0.{xx}.jar LoginPage Server-IP'
  
  into the command line, where "xx" is the driver version and Server-IP is the IP address of the server the user wants 
  to connect to in order to relay messages.
  
  2) The user must run the chat-handling Server file independently of the Client file (LoginPage.java). The program assumes that port 65501 is not in use.
  
  
  
  Messenger is a instant messaging program designed in Java Swing that allows users to communicate with each other 
  through a Server. Through a MySQL database, user history is established, allowing communications from former 
  sessions as well as prior friend histories to be readily accessed. To enhance user security, the BCrypt hashing function is employed along with a user-specific salt to store the user password as a hashcode. 
