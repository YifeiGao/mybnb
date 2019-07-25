package project;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

public class CommandLine {



	private static final String[] renter_column_name = {"credit card number", "user name"};
	private static final String[] renter_column_type = {"INT NOT NULL", "VARCHAR(30) NOT NULL"};
	private static final String renter_primary_key = "credit card number";
	private static final String [] rcomments_column_name = {"list coordinate", "list address"," renter user name", "comment date", "comment", "host user name", "list scale", "host scale"};
	private static final String [] rcomments_column_type = {"VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "DATE NOT NULL", "TEXT NOT NULL", "VARCHAR(30) NOT NULL", "INT NOT NULL", "INT NOT NULL"};
	private static final String rcomments_primary_key = "list coordinate, list address, renter user name, comment date";
	private static final String[] hcomments_column_name = {"list coordinate", "list address"," renter user name", "comment date", "comment", "host user name", "renter scale"};
	private static final String[] hcomments_column_type = {"VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "DATE NOT NULL", "TEXT NOT NULL", "VARCHAR(30) NOT NULL", "INT NOT NULL"};
	private static final String hcomments_primary_key = null;


	// 'sqlMngr' is the object which interacts directly with MySQL
	public static SQLController sqlMngr = null;
	// 'sc' is needed in order to scan the inputs provided by the user
	public static Scanner sc = null;

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
			User user = new User();
			user.userStart(sqlMngr);
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
	 * get the value that typed form command line
	 * @param column: a string array which stores the column names
	 * @param c: the number of column that need the user's input value
	 * @return a string array that stores the value of columns
	 */
	public static String[] getInfo(String[] column, int c){
		String [] values = new String[c];
		for (int i = 0; i < c; i ++){
			System.out.println("Please type the " +  column[i] + " below");
			values[i] = CommandLine.sc.nextLine();
		}
		return values;
	}






	public static boolean checkInRange(int input, int min, int max){
		boolean check;
		check = input >= min || input <= max;
		return check;
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
}
