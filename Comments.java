package project;

import java.util.ArrayList;

import project.CommandLine;

public class Comments {

	private static String[] renter_comments_column_name = {
			"renter_user_name",
			"host_user_name",
			"booking_ID",
			"comment_date",
			"listing_rating",
			"host_rating",
	"comment"};
	private static String[] host_comments_column_name = {
			"renter_user_name",
			"host_user_name",
			"booking_ID",
			"comment_date",
			"renter_rating",
	"comment"};
	private static String[] renter_comments_column_type = {
			"VARCHAR(30) NOT NULL",
			"VARCHAR(30) NOT NULL",
			"INT NOT NULL",
			"DATE NOT NULL",
			"TINYINT(1) NOT NULL",
			"TINYINT(1) NOT NULL",
	"TEXT NOT NULL"};
	private static String[] host_comments_column_type = {
			"VARCHAR(30) NOT NULL",
			"VARCHAR(30) NOT NULL",
			"INT NOT NULL",
			"DATE NOT NULL",
			"TINYINT(1) NOT NULL",
	"TEXT NOT NULL"};

	private static final String host_comments_primary_key = "renter_user_name, host_user_name, booking_id, date";
	String[] column_values = new String[renter_comments_column_name.length];
	String[] column_types = renter_comments_column_type;

	public static SQLController sqlMngr = new SQLController();
	public Comments(){
		try {
			sqlMngr.connect();
		} catch (ClassNotFoundException e) {
			System.err.println("Esception occurs Comments.constructor");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void insertRateAndComments(String type, String user_name){
		boolean valid;
		String booking_id;
		String list_rating;
		String host_rating;
		String renter_rating;
		String renter_comment;
		String host_comment;
		ArrayList<String> result;
		do{
			result = this.checkHistoryBookingId(type, user_name);
			valid = result.isEmpty();
		}while(!valid);
		booking_id = result.get(0);
		System.out.println("the booking ID is " + booking_id);
		if (type.equals("renter")) {
			//case:Renter, needs to complete listing rating, host rating, and comment
			System.out.println("Please rate the listing from 1-5");
			list_rating = CommandLine.sc.nextLine();
			System.out.println("Please rate the host from 1-5");
			host_rating = CommandLine.sc.nextLine();
			System.out.println("Please write your comment here. If not applicable, please write n/a.");
			renter_comment = CommandLine.sc.nextLine();
			// Store data values and types
		} else if (type.equals("host")) {
			//case:Host, needs to complete renter rating, and comment
			System.out.println("Please rate the renter from 1-5");
			renter_rating = CommandLine.sc.nextLine();
			System.out.println("Please write your comment here. If not applicable, please write n/a.");
			host_comment = CommandLine.sc.nextLine();
			// Store data values and types      
			column_values = new String[host_comments_column_name.length];
			column_types = host_comments_column_type;
		}
		sqlMngr.insertOp("comments", column_types, column_values);	
	}

	private ArrayList<String> checkHistoryBookingId(String type, String user_name) {
		String get_id_sql="";
		boolean check_id;
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		do{
			System.out.println("Please enter the booking id that is provided in the list: ");
			String booking_id = CommandLine.sc.nextLine();
			if (type.equals("host")) {
				get_id_sql = "SELECT booking ID, host user name, renter user name, CURDATE() FROM booking WHERE host user name = '"+user_name+"'"+"AND DATEDIFF(CURDATE(), end date) <= 365 AND booking ID = '" + booking_id + "'";
			}	else if (type.equals("renter")) {
				get_id_sql = "SELECT booking ID, host user name, renter user name, CURDATE() FROM booking WHERE renter user name = '"+user_name+"'"+"AND DATEDIFF(CURDATE(), end date) <= 365 AND booking ID = '" + booking_id + "'";
			}
			result = sqlMngr.rsToList(sqlMngr.selectOp(get_id_sql));
			//check_id is true if the result list is empty which means the booking Id that the user provide is incorrect
			check_id = result.isEmpty();
			if (check_id){
				System.out.println("The booking Id is not valid.");
			}
		}while (check_id);
		return result.get(0);
	}

}
