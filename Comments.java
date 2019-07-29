package project;

import java.sql.ResultSet;
import java.util.ArrayList;

import project.CommandLine;
import project.SQLController;

public class Comments {

	private static String[] renter_comments_column_name = {
		"renter_user_name",
		"host_user_name",
		"booking_ID",
		"comment_date_time",
		"listing_rating",
		"host_rating",
		"comment"};
	private static String[] host_comments_column_name = {
		"renter_user_name",
		"host_user_name",
		"booking_ID",
		"comment_date_time",
		"renter_rating",
		"comment"};
	private static String[] renter_comments_column_type = {
		"VARCHAR(30) NOT NULL",
		"VARCHAR(30) NOT NULL",
		"INT NOT NULL",
		"DATETIME NOT NULL",
		"TINYINT(1) NOT NULL",
		"TINYINT(1) NOT NULL",
		"VARCHAR(45) NOT NULL"};
	private static String[] host_comments_column_type = {
		"VARCHAR(30) NOT NULL",
		"VARCHAR(30) NOT NULL",
		"INT NOT NULL",
		"DATETIME NOT NULL",
		"TINYINT(1) NOT NULL",
		"VARCHAR(45) NOT NULL"};

	private static final String host_comments_primary_key = "host_user_name, comment_date_time";
	private static final String renter_comments_primary_key = "renter_user_name, comment_date_time";
	String[] column_values = new String[renter_comments_column_name.length];
	String[] column_types = renter_comments_column_type;

	public static SQLController sqlMngr = new SQLController();
	
	public static String[] getRenComColumn(){
		return renter_comments_column_name;
	}
	
	public static String[] getRenComColumnType(){
		return renter_comments_column_type;
	}
	
	public static String getRenComKey(){
		return renter_comments_primary_key;
	}
	public static String[] getHostComColumn(){
		return host_comments_column_name;
	}
	
	public static String[] getHostComColumnType(){
		return host_comments_column_type;
	}
	
	public static String getHostComKey(){
		return host_comments_primary_key;
	}

	public Comments(){
		sqlMngr = CommandLine.sqlMngr;
	}

	//[updated]
	public void insertRateAndComments(String type, String user_name){
		boolean valid;
		String booking_ID;
		String list_rating;	String host_rating; String comment_host;	//type = renter
		String renter_rating;	String comment_renter;	//type = host
		String column_names[] = null; String column_types[] = null;
		ArrayList<String> result;
		String table_name = null;
		String query = null;
		ArrayList<ArrayList<String>> r = null;
		if (type.equals("host")) {
			query = "SELECT booking_ID, list_ID, renter_user_name, start_date, end_date FROM booking WHERE host_user_name = '"+user_name+"'"+"AND DATEDIFF(CURDATE(), end_date) <= 365 AND DATEDIFF(CURDATE(), end_date) >= 0;";
		} else if (type.equals("renter")) {
			query = "SELECT booking_ID, list_ID, host_user_name, start_date, end_date FROM booking WHERE renter_user_name = '"+user_name+"'"+"AND DATEDIFF(CURDATE(), end_date) <= 365 AND DATEDIFF(CURDATE(), end_date) >= 0;";
		}
		r = sqlMngr.rsToList(sqlMngr.selectOp(query));
		if (r.isEmpty()) {
			System.out.println("No record found");
			return;
		}

		// Ask user to enter booking id and verify
		result = this.checkRecentHistoryBookingId(type, user_name);
		booking_ID = result.get(0);
		// System.out.println("the booking ID is " + booking_ID);
		if (type.equals("renter")) {
			table_name = "renter_comments";
			//case:Renter, needs to complete listing rating, host rating, and comment
			do {
				valid = true;
				System.out.println("Please rate the listing from 1-5");
				list_rating = CommandLine.sc.nextLine();
				System.out.println(list_rating);
				if (!list_rating.equals("1") && !list_rating.equals("2") && !list_rating.equals("3") && !list_rating.equals("4") && !list_rating.equals("5")) {
					valid = false;
				}
			} while(!valid);
			do {
				valid = true;
				System.out.println("Please rate the host from 1-5");
				host_rating = CommandLine.sc.nextLine();
				if (!host_rating.equals("1") && !host_rating.equals("2") && !host_rating.equals("3") && !host_rating.equals("4") && !host_rating.equals("5")) {
					valid = false;
				}
			} while(!valid);
			System.out.println("Please write your comment here. If not applicable, please write n/a.");
			comment_host = CommandLine.sc.nextLine();
			// Store data values and types
			// result: booking_ID, host_user_name, renter_user_name, SYSDATE()
			//column: "renter_user_name","host_user_name","booking_ID","comment_date_time","listing_rating","host_rating","comment"
			String renter_column_values[] = {user_name, result.get(1), booking_ID, result.get(3), list_rating, host_rating, comment_host};
			column_values = renter_column_values;
			column_names = renter_comments_column_name;
		} else if (type.equals("host")) {
			table_name = "host_comments";
			//case:Host, needs to complete renter rating, and comment
			do {
				valid = true;
				System.out.println("Please rate the renter from 1-5");
				renter_rating = CommandLine.sc.nextLine();
				if (!renter_rating.equals("1") && !renter_rating.equals("2") && !renter_rating.equals("3") && !renter_rating.equals("4") && !renter_rating.equals("5")) {
					valid = false;
				}
			} while(!valid);
			System.out.println("Please write your comment here. If not applicable, please write n/a.");
			comment_renter = CommandLine.sc.nextLine();
			// Store data values and types
			// result: booking_ID, host_user_name, renter_user_name, SYSDATE()
			//column: "renter_user_name","host_user_name","booking_ID","comment_date_time","renter_rating","comment"
			String host_column_values[] = {result.get(2), user_name, result.get(0), result.get(3), renter_rating, comment_renter};
			column_values = host_column_values;
			column_names = host_comments_column_name;
		}
		sqlMngr.insertOp(table_name, column_names, column_values, false);	
	}

	//[updated]
	private ArrayList<String> checkRecentHistoryBookingId(String type, String user_name) {
		String get_id_sql="";
		boolean valid_id;
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		do{
			System.out.println("Please enter the booking id that is provided in the list: ");
			String booking_ID = CommandLine.sc.nextLine();
			if (type.equals("host")) {
				get_id_sql = "SELECT booking_ID, host_user_name, renter_user_name, SYSDATE() FROM booking WHERE host_user_name = '"+user_name+"'"+"AND DATEDIFF(CURDATE(), end_date) <= 365 AND DATEDIFF(CURDATE(), end_date) >= 0 AND booking_ID = '" + booking_ID + "';";
			}	else if (type.equals("renter")) {
				get_id_sql = "SELECT booking_ID, host_user_name, renter_user_name, SYSDATE() FROM booking WHERE renter_user_name = '"+user_name+"'"+"AND DATEDIFF(CURDATE(), end_date) <= 365 AND DATEDIFF(CURDATE(), end_date) >=0 AND booking_ID = '" + booking_ID + "';";
			}
			result = sqlMngr.rsToList(sqlMngr.selectOp(get_id_sql));
			//check_id is true if the result list is empty which means the booking Id that the user provide is incorrect
			valid_id = !result.isEmpty();
			if (!valid_id){
				System.out.println("The booking Id is not valid.");
			}
		}while (!valid_id);
		return result.get(0);
	}

}
