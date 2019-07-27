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
			success = sqlMngr.connect();
		} catch (ClassNotFoundException e) {
			success = false;
			System.err.println("Establishing connection triggered an exception!");
			e.printStackTrace();
			sc = null;
			sqlMngr = null;
		}
		return success;
	}

	public boolean execute(){
		if(sc != null && sqlMngr != null){
			User u = new User();
			int choice = -1;
			String input = "";
			boolean log_in;
			log_in = u.userStart();
			boolean con = true;
			do{
				input = sc.nextLine();
				try{
					choice = Integer.parseInt(input);
					switch(choice){
					case 0:
						log_in = false;
						u.logOut();
						break;
					case 1:
						u.makeBooking();
						break;
					case 2:
						u.printHistory(choice);
						break;
					case 3:
						u.deleteFutureBooking(choice);
						break;
					case 4:
						u.writeComments(choice);
						break;
					case 5:
						con = u.hostNotice();
						if(con){
							u.addListing();
						}
						break;
					case 6:
						con = u.hostNotice();
						if(con){
							u.getHostListings();
						}
						break;
					case 7:
						con = u.hostNotice();
						if(con){
							u.printHistory(choice);
						}
						break;
					case 8:
						con = u.hostNotice();
						if(con){
							u.deleteFutureBooking(choice);
						}
						break;
					case 9:
						con = u.hostNotice();
						if(con){
							u.updateAvai();
						}
						break;
					case 10:
						con = u.hostNotice();
						if(con){
							u.writeComments(choice);
						}
						break;
					case 11:
						con = u.hostNotice();
						if(con){
							u.updatePrice();
						}
						break;
					default:
						break;
					}
				}catch (NumberFormatException e){
					input = "-1";
					System.err.println("NumberFormatException occurs in CommandLine.excute");
					e.printStackTrace();
				}
			}while(log_in);
			return true;
		}
		else {
			System.out.println("");
			System.out.println("Connection could not been established! Bye!");
			System.out.println("");
			return false;
		}
	}



	/**
	 * get the value that typed form command line
	 * @param column: a string array which stores the column names
	 * @param c: the number of column that need the user's input value
	 * @return a string array that stores the value of columns
	 */
	public static ArrayList<String> getInfo(String[] column, int start_c, int end_c){
		ArrayList<String> values = new ArrayList<String>(column.length);
		for (int i = start_c; i <= end_c; i ++){
			System.out.println("Please type the " +  column[i] + " below");
			values.add(i, CommandLine.sc.nextLine());
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






}
