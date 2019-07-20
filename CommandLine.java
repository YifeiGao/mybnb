package project;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

public class CommandLine {
	private static String user;
	private static final String[] listing_column_name = {"list type", "list coordinate", "Country", "Province", "City", "house number and Street","host user name"};
	private static final String[] listing_column_type = {"VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL","VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL"};
	private static final String listing_primary_key = "list coordinate, list address";
	private static final String[] amenites_column_name = {"kitchen","heating","washer", "wifi", "indoor fireplace", "iron", "Laptop-friendly workspace", "crib", "self check_in", "carbon monoxide detector", "shampoo", "air conditioning", "dryer", "breakfast", "hangers", "hair dryer", "TV", "hight chair", "smoke detector", "private bathroom", "Country", "Province","City", "house number and Street"};
	private static final String[] amenites_column_type = {"TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL"};
	private static final String amenites_primary_key = "Country, Province, City, house number and Street";
	private static final String[] user_column_name = {"name", "password", "list address", "birth", "occup", "SIN", "user name", "canclellationc", "type",};
	private static final String[] user_column_type = {"VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "INT NOT NULL", "VARCHAR(30) NOT NULL", "INT NOT NULL", "VARCHAR(30) NOT NULL","INT DEFAULT 0", "VARCHAR(30) NOT NULL"};
	private static final String user_primary_key = "user name";
	private static final String[] renter_column_name = {"user name", "credit card number"};
	private static final String[] renter_column_type = {"VARCHAR(30) NOT NULL", "INT NOT NULL"};
	private static final String renter_primary_key = "credit card number";
	private static final String [] rcomments_column_name = {"list coordinate", "list address"," renter user name", "comment date", "comment", "host user name", "list scale", "host scale"};
	private static final String [] rcomments_column_type = {"VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "DATE NOT NULL", "TEXT NOT NULL", "VARCHAR(30) NOT NULL", "INT NOT NULL", "INT NOT NULL"};
	private static final String rcomments_primary_key = "list coordinate, list address, renter user name, comment date";
	private static final String[] hcomments_column_name = {"list coordinate", "list address"," renter user name", "comment date", "comment", "host user name", "renter scale"};
	private static final String[] hcomments_column_type = {"VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "DATE NOT NULL", "TEXT NOT NULL", "VARCHAR(30) NOT NULL", "INT NOT NULL"};
	private static final String hcomments_primary_key = null;
	private static final String [] booking_column_name = {"booking ID", "host user name", "renter user name", "list coordibate", "list address", "start date", "end date"};
	private static final String [] booking_column_type = {"INT AUTO_INCREMENT", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "DATE NOT NULL", "DATE NOT NULL"};
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
		boolean check;
		//check if the password that the user typed is matched according to the database
		do{
			System.out.println("Please type your username below");
			user = sc.nextLine();
			System.out.println("Please type your password below");
			pass = sc.nextLine();
			sql = "SELECT password FROM users WHERE user name = '"+user+"'";
			result = sqlMngr.selectOp(sql);	
			check = (result.get(0).equals(pass));
			if(!check){
				System.out.println("incorrect password");
			}
		}while (!check);
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
		String input;
		int choice;
		input = sc.nextLine();
		choice = Integer.parseInt(input);
		switch(choice){
		case 0:
			//should log out 
			break;
		case 1:
			//get the host's lists' info
			break;
		case 2:
			//get a list's rental history
			break;
		case 3:
			//Delete a future booking of a list
			this.deletBooking();
			break;
		case 4:
			//update availability
		case 5:
			//write the comments
			break;
		case 6:
			// update the price of a listing
			break;
		case 7:
			// Add a listing
			break;
		}
	}
	/**
	 * handle the host's case 2: print out a list's rental history
	 */
	private void getHostL(){
		ArrayList <String> result = new ArrayList<String>();


	}

	private void addListing(){
		int choice;
		boolean check = false;
		do{
			this.listTypeMenu();
			choice = Integer.parseInt(sc.nextLine());
			check = this.checkInRange(choice, 0, 15);
			if(!check){
				System.out.println("Please choose a type that provided");
			}
		}while(!check);
	}

	private void listTypeMenu(){
		System.out.println("=========LISTING TYPES=========");
		System.out.println("0. House.");
		System.out.println("1. Bed adn breakfast.");
		System.out.println("2. Bungalow.");
		System.out.println("3. Chalet.");
		System.out.println("4. Guest suite.");
		System.out.println("5. Hostel");
		System.out.println("6. Loft.");
		System.out.println("7. Townhouse.");
		System.out.println("8. Apartment.");
		System.out.println("9. Boutique hotel");
		System.out.println("10.Cabin.");
		System.out.println("11.Cottage");
		System.out.println("12. Guesthouse");
		System.out.println("13, Hotel");
		System.out.println("14. Resort");
		System.out.println("15. Villa");
		System.out.print("Please select the type of your listing [0-15]: ");
	}

	private boolean checkInRange(int input, int min, int max){
		boolean check;
		check = input >= min || input <= max;
		return check;
	}

	/**
	 * operations of the renter, the renter are provided with few options with corresponding numbers
	 * 
	 * String input: it is the option that the renter chose
	 * @throws ParseException 
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
			//delete the future booking
		case 3:
			this.deletBooking();
			break;
		case 4:
			//write the comments
			break;
		}

	}
	private ArrayList<String> checkBookingId(){
		String get_id_sql;
		String check_type_sql;
		String booking_id;
		boolean check_id;
		String type;
		String type_result;
		ArrayList<String> result = new ArrayList<String>();
		do{
			System.out.println("Please provide the booking ID of the future booking that you want to cancle" );
			booking_id = sc.nextLine();
			check_type_sql = "SELECT type FROM users WHERE user name = '"+user+"'"; 
			type_result = sqlMngr.selectOp(check_type_sql).get(0);
			if(type_result.equals("host")){
				get_id_sql = "SELECT * FROM booking WHERE host user name = '"+user+"' and booking id  = '"+booking_id+"'";
			}
			else{
				get_id_sql = "SELECT * FROM booking WHERE renter user name = '"+user+"' and booking id  = '"+booking_id+"'";
			}
			result = sqlMngr.selectOp(get_id_sql);
			//check_id is true if the result list is empty which means the booking Id that the user provide is incorrect
			check_id = result.isEmpty();
			if (check_id){
				System.out.println("The booking Id is not correct");
			}
		}while (check_id);
		return result;
	}

	private boolean checkFuture(ArrayList<String> result_list){
		boolean check_date = false;
		//get the current date which named curr, the time zone is GMT
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date curr = new Date();
		sdf.format(curr);
		String booking_date;
		try{
			//check if it is a future booking
			//get the booking date according to the booking id that provided by user
			//the booking start date stores in the index 5 of the result list, the result list is returned by the select operation
			booking_date = result_list.get(5);
			Date start_date = sdf.parse(booking_date);
			check_date = start_date.compareTo(curr) > 0;
			if (!check_date){
				System.out.println("According to our record, the booking is not a future booking");
			}
		}catch(ParseException e){
			System.err.println("Exception triggered during date parse execution!");
			e.printStackTrace();
		}
		return check_date;
	}

	private void insertBooking(){

	}

	private void printHistory(){

	}

	private void deletBooking(){
		ArrayList <String> result = new ArrayList<String>();
		boolean valid = false;
		String booking_id;
		String update_sql;
		String get_cancel_sql;
		int cancellation;
		do{
			result = this.checkBookingId();
			valid = this.checkFuture(result);
		}while(!valid);
		booking_id = result.get(0);
		this.deleteOperation("booking", "booking ID = '"+booking_id+"'");
		//update the user's cancellation time
		get_cancel_sql = "SELECT cancellation FROM users WHERE user name = '"+user+"'";
		cancellation = Integer.parseInt(sqlMngr.selectOp(get_cancel_sql).get(0));
		cancellation += 1;
		update_sql = "UPDATE users set cancellation = '"+cancellation+"' WHERE user name = '"+user+"'";
		sqlMngr.updateOp(update_sql);
		/*
		 * TO DO:
		 * update availability of the list
		 */
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
		System.out.println("0. Log out.");
		System.out.println("1. Get the all your own lists' information.");
		System.out.println("2. Check a list's booking history.");
		System.out.println("3. Delete a future booking of a list.");
		System.out.println("4. Update the availability of the list");
		System.out.println("5. Write a comment and rate a renter.");
		System.out.println("6. Update the price of a listing");
		System.out.println("7. Add a list");
		System.out.print("Choose one of the previous options [0-6]: ");
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
		column_values = this.getInfo(user_column_name, 0, user_column_name.length - 1);
		column_values[column_values.length-1] = type;
		c.insertOperator("users", user_column_name, column_values);
		user = column_values[6];
		if(type.equals("host")){
			/*TO DO:
			 * do the operation of the host
			 */
			this.hostOp();
		}
		else if(type.equals("renter")){
			// insert the renter's info into the table renter by calling function inser_renter
			this.addRenter();
			/*
			 * TO DO: do the operation of the renter
			 */
			this.renterOp();
		}


	}

	/**
	 * add the renter info into the table named renter
	 */
	private void addRenter(){
		String [] column_value = new String[2];
		column_value = this.getInfo(renter_column_name, 1, renter_column_name.length);
		this.insertOperator("renter", renter_column_name, column_value);
	}

	/**
	 * get the value that typed form command line
	 * @param column: a string array which stores the column names
	 * @param c: the number of the columns that you want to insert value
	 * @return a string array that stores the value of columns
	 */
	private String[] getInfo(String[] column, int s, int e){
		String [] values = new String[e - s + 1];
		for (int i = s; i <= e; i ++){
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


	private void deleteOperation(String table, String where_condition){
		System.out.println(table);
		String query = "";
		query = "DELETE FROM" + table + "WHERE ";
		query = query.concat(where_condition);
		System.out.println(query);
		sqlMngr.deleteOp(query);

	}

}
