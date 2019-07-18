package project;

import java.util.ArrayList;
import java.util.Scanner;

public class CommandLine {
	private static String user;
	private static final String[] user_column_name = {"name", "password", "address", "birth", "occup", "SIN", "username", "type"};
	private static final String[] user_column_type = {"VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "INT NOT NULL", "VARCHAR(30) NOT NULL", "INT NOT NULL", "VARCHAR(30) NOT NULL","VARCHAR(30) NOT NULL"};
	private static final String user_primary_key = "SIN";
	private static final String[] renter_column_name = {"SIN", "credit card number"};
	private static final String[] renter_column_type = {"INT NOT NULL", "INT NOT NULL"};
	private static final String renter_primary_key = "credit card number";
	private static final String [] rcomments_column_name = {"list coordinate", "list address"," renterSIN", "DATE", "comment", "hostSIN", "list_ cale", "host scale"};
	private static final String [] rcomments_column_type = {"VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "INT NOT NULL", "date NOT NULL", "text NOT NULL", "INT NOT NULL", "INT NOT NULL", "INT NOT NULL"};
	private static final String rcomments_primary_key = "list coordinate, list address, userSIN";
	private static final String[] hcomments_column_name = {"hostSIN", "renterSIN", "date", "list address", "list coordinate"};
	private static final String[] hcomments_column_type = {"INT NOT NULL", "INT NOT NULL", "DATE", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL"};
	private static final String hcomments_primary_key = null;
	private static final String [] booking_column_name = {"booking ID", "hostSIN", "renterSIN", "list coordibate", "list address"};
	private static final String [] booking_column_type = {"INT AUTO_INCREMENT", "INT NOT NULL", "INT NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL"};
	private static final String booking_primary_key = "booking ID";

	// 'sqlMngr' is the object which interacts directly with MySQL
	private SQLController sqlMngr = null;
	// 'sc' is needed in order to scan the inputs provided by the user
	private Scanner sc = null;

	//Public functions - CommandLine State Functions

	/* Function used for initializing an istance of current
	 * class
	 */
	private boolean startSession() {
		boolean success = true;
		if (sc == null) {
			sc = new Scanner(System.in);
		}
		if (sqlMngr == null) {
			sqlMngr = new SQLController();
		}
		try {
			success = sqlMngr.connect(this.getCredentials());
			this.userStart();
		} catch (ClassNotFoundException e) {
			success = false;
			System.err.println("Establishing connection triggered an exception!");
			e.printStackTrace();
			sc = null;
			sqlMngr = null;
		}
		return success;
	}

	/**
	 * function will be operate after connected successfully, it handle user's login and register
	 * String choice: the use's choice which have 2 possible values: 1 for login and 2 for register
	 * boolean check: a boolean type that check if the user typed the number that are provided
	 */
	private void userStart(){
		String choice;
		boolean check = false;
		choice = sc.nextLine();
		System.out.println("Welcome to Mybnb, if you want to login press 1, if you are not a user yet please press 2 to register");
		while (!check){
			check = choice.equals("1") | choice.equals("2");
		}
		if (choice.equals("1")){
			//login operations
			this.login();
		}
		else if (choice.equals("2")){
			//create a user
			this.createUser();
		}
	}
	/**
	 * the function that handle login
	 * String pass: the password that the user typed in the command line
	 * String sql: the Mysql query that need to be executed
	 */
	private void login(){
		String pass;
		String sql;
		ArrayList <String> result = new ArrayList <String>();
		boolean check = false;
		//check if the password that the user typed is matched according to the database
		while (!check){
			System.out.println("Please type your username below");
			user = sc.nextLine();
			System.out.println("Please type your password below");
			pass = sc.nextLine();
			sql = "SELECT password FROM users WHERE username = user_name";
			result = sqlMngr.selectOp(sql);	
			check = (result.get(0).equals(pass));
			System.out.println("incorrect password");
		}
		//get the user's type once the user logged in
		sql = "SELECT type FROM users WHERE user = username";
		result = sqlMngr.selectOp(sql);
		System.out.println("Logged in , please choose a options provided below");
		if (result.get(0).equals("host")){
			this.hostOp();
		}
		else if (result.get(0).equals("renter")){
			this.renterOp();
		}

	}
	
	/**
	 * operations of the host
	 */
	private void hostOp(){
		this.hostMenu();
	}
	
	/**
	 * operations of the renter, the renter are provided with few options with corresponding numbers
	 * 
	 * String input: it is the option that the renter chosed
	 */
	private void renterOp(){
		this.renterMenu();
		String input;
		int choice;
		input = sc.nextLine();
		choice = Integer.parseInt(input);
		switch(choice){
		case 0:
			//should log out 
			break;
		case 1:
			//book a list
			break;
		case 2:
			//print the rental history of the user
			break;
		case 3:
			//delete the user's future booking , the user should provide the booking id
			break;
		case 4:
			//write the comments
			break;
		}

	}
	/**
	 * it is the operations that the renter can do 
	 */
	private void renterMenu() {
		System.out.println("=========OPERATIONS=========");
		System.out.println("0. Log out.");
		System.out.println("1. Book a list.");
		System.out.println("2. Check booking history.");
		System.out.println("3. Delete a future booking.");
		System.out.println("4. Write a comment and rate a host.");
		System.out.print("Choose one of the previous options [0-4]: ");
	}

	/**
	 * it is the operations that the host can do
	 */

	private void hostMenu(){
		System.out.println("=========OPERATIONS=========");
		System.out.println("0. Exit.");
		System.out.println("1. Get the all your own lists' information.");
		System.out.println("2. Check a list's booking history.");
		System.out.println("3. Delete a future booking of a list.");
		System.out.println("4. Write a comment and rate a renter.");
		System.out.println("5. Update the price of a listing");
		System.out.print("Choose one of the previous options [0-5]: ");
	}

	/**
	 * the function is use to create table
	 * @param table: the name of the table
	 * @param column_name: a string array that stores the name of columns
	 * @param column_type: a string array that stores the type of the columns
	 * @param key: primary key of the table, it is optional , if the key is null then do nothing otherwise concatenate it with the create table query
	 */
	private void createTable(String table, String[] column_name, String[] column_type, String key){
		int counter = 0;
		String sql = "CREATE TABLE IF NOT EXISTS " + table + "(";
		for (counter = 0; counter < column_name.length; counter++) {
			sql = sql.concat("'" + column_name[counter] + "'");
			sql = sql.concat("'" +column_type[counter]+ "',");
		}
		sql = key != null ? sql.concat("PRIMARY KEY ( '" + key + "'));") : sql;
	}

	/**
	 * the function will operate when a people want to create a user
	 * there are two different user type which are host and renter.
	 * the users can operate differently according to their user type.
	 */
	private void createUser(){
		String type = "";
		String[] column_values = new String[7];

		CommandLine c = new CommandLine();
		boolean check_type = false;
		//while loop to check the type is one of the types that provided
		while (!check_type){
			System.out.println("Please choose a user type that you want to create (host or renter): ");
			type = sc.nextLine();
			check_type = (type.equals("host") | type.equals("renter"));
		}
		column_values = this.getInfo(user_column_name, user_column_name.length - 1);
		column_values[column_values.length-1] = type;

		if(type.equals("host")){
			//do the operation of the host
		}
		else if(type.equals("renter")){
			// insert the renter's info into the table renter by calling function inser_renter
			this.addRenter();
		}
		c.insertOperator("users", user_column_name, column_values);

	}

	/**
	 * add the renter info into the table named renter
	 */
	private void addRenter(){
		String [] column_value = new String[2];
		column_value = this.getInfo(renter_column_name, renter_column_name.length);
	}

	/**
	 * get the value that typed form command line
	 * @param column: a string array which stores the column names
	 * @param c: the number of the columns that you want to insert value
	 * @return a string array that stores the value of columns
	 */
	private String[] getInfo(String[] column, int c){
		String [] values = new String[c];
		for (int i = 0; i < c; i ++){
			System.out.println("Please type your " +  column[i] + " below");
			values[i] = sc.nextLine();
		}
		return values;
	}



	/* Function that acts as destructor of an instance of this class.
	 * Performs some housekeeping setting instance's private field
	 * to null
	 */
	private void endSession() {
		if (sqlMngr != null)
			sqlMngr.disconnect();
		if (sc != null) {
			sc.close();
		}
		sqlMngr = null;
		sc = null;
	}

	// Called during the initialization of an instance of the current class
	// in order to retrieve from the user the credentials with which our program
	// is going to establish a connection with MySQL
	private String[] getCredentials() {
		String[] cred = new String[3];
		System.out.print("Username: ");
		cred[0] = sc.nextLine();
		System.out.print("Password: ");
		cred[1] = sc.nextLine();
		System.out.print("Database: ");
		cred[2] = sc.nextLine();
		return cred;
	}

	/**
	 * insert values to the table
	 * @param table: the table's name
	 * @param column: a string array that stores the name of columns
	 * @param values: a string array that stores the values of columns
	 */
	private void insertOperator(String table, String[] column, String[] values) {
		int rowsAff = 0;
		int counter = 0;
		String query = "";
		System.out.print("Table: "+ table);
		//transform the user input into a valid SQL insert statement
		query = "INSERT INTO " + table + " (" + column + ") VALUES("; 
		for (counter = 0; counter < values.length - 1; counter++) {
			query = query.concat("'" + values[counter] + "',");
		}
		query = query.concat("'" + values[counter] + "');");
		System.out.println(query);
		rowsAff = sqlMngr.insertOp(query);
		System.out.println("");
		System.out.println("Rows affected: " + rowsAff);
		System.out.println("");
	}

}
