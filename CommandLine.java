package project;

import java.util.ArrayList;
import java.util.Scanner;

public class CommandLine {
	public static final String[] user_column_name = {"name", "address", "birth", "occup", "SIN", "type"};
	public static final String[] user_column_type = {"VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "INT NOT NULL", "VARCHAR(30) NOT NULL", "INT NOT NULL", "VARCHAR(30) NOT NULL"};
	public static final String user_primary_key = "SIN";
	public static final String[] renter_column_name = {"SIN", "credit_card_number"};
	public static final String[] renter_column_type = {"INT NOT NULL", "INT NOT NULL"};
	public static final String renter_primary_key = "credit_card_number";

	// 'sqlMngr' is the object which interacts directly with MySQL
	private SQLController sqlMngr = null;
	// 'sc' is needed in order to scan the inputs provided by the user
	private Scanner sc = null;

	//Public functions - CommandLine State Functions

	/* Function used for initializing an istance of current
	 * class
	 */
	public boolean startSession() {
		boolean success = true;
		if (sc == null) {
			sc = new Scanner(System.in);
		}
		if (sqlMngr == null) {
			sqlMngr = new SQLController();
		}
		try {
			success = sqlMngr.connect(this.getCredentials());
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
	 * the function is use to create table
	 * @param table: the name of the table
	 * @param column_name: a string array that stores the name of columns
	 * @param column_type: a string array that stores the type of the columns
	 * @param key: primary key of the table
	 */
	public void createTable(String table, String[] column_name, String[] column_type, String key){
		int counter = 0;
		String sql = "CREATE TABLE IF NOT EXISTS " + table + "(";
		for (counter = 0; counter < column_name.length; counter++) {
			sql = sql.concat("'" + column_name[counter] + "'");
			sql = sql.concat("'" +column_type[counter]+ "',");
		}
		sql = sql.concat("PRIMARY KEY ( '" + key + "'));");
	}
	
	/**
	 * the function will operate when a people want to create a user
	 * there are two different user type which are host and renter.
	 * the users can operate differently according to their user type.
	 */
	public void createUser(){
		String type = "";
		String[] column_values = new String[6];

		CommandLine c = new CommandLine();
		boolean check_type = false;
		//while loop to check the type is one of the types that provided
		while (!check_type){
			System.out.println("Please choose a user type that you want to create (host or renter): ");
			type = sc.nextLine();
			check_type = (type.equals("host") | type.equals("renter"));
		}
		column_values = this.getInfo(user_column_name, user_column_name.length - 1);
		column_values[6] = type;
		
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
	public void addRenter(){
		String [] column_value = new String[2];
		column_value = this.getInfo(renter_column_name, renter_column_name.length);
	}

	/**
	 * get the value that typed form command line
	 * @param column: a string array which stores the column names
	 * @param c: the number of the columns that you want to insert value
	 * @return a string array that stores the value of columns
	 */
	public String[] getInfo(String[] column, int c){
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
	public void endSession() {
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