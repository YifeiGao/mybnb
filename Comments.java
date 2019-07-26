package project;

import java.util.ArrayList;

import project.CommandLine;

public class Comments {

  private static String[] renter_comments_column_name = {
    "renter user name",
    "host user name",
    "booking id",
    "comment date",
    "listing rating",
    "host rating",
    "comment"};
  private static String[] host_comments_column_name = {
    "renter user name",
    "host user name",
    "booking id",
    "comment date",
    "renter rating",
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
	
	private static final String host_comments_primary_key = "renter user name, host user name, booking id, date";
    String[] column_values = new String[renter_comments_column_name.length];
    String[] column_types = renter_comments_column_type;
	public void insertRateAndComments(String type){
    boolean valid;
    String booking_id;
    String list_rating;
    String host_rating;
    String renter_rating;
    String renter_comment;
    String host_comment;
    ArrayList<String> result;
    do{
      result = this.checkHistoryBookingId(type);
      valid = result.isEmpty();
		}while(!valid);
    booking_id = result.get(0);
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
    CommandLine.sqlMngr.insertOp("comments", column_types, column_values);	
  }
  
  private ArrayList<String> checkHistoryBookingId(String type) {
    String get_id_sql="";
    boolean check_id;
    ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
    do{
      System.out.println("Please enter the booking id that is provided in the list: ");
			String booking_id = CommandLine.sc.nextLine();
			if (type.equals("host")) {
				get_id_sql = "SELECT booking ID, host user name, renter user name, CURDATE() FROM booking WHERE host user name = '"+User.getUser()+"'"+"AND DATEDIFF(CURDATE(), end date) <= 365 AND booking ID = '" + booking_id + "'";
			}	else if (type.equals("renter")) {
				get_id_sql = "SELECT booking ID, host user name, renter user name, CURDATE() FROM booking WHERE renter user name = '"+User.getUser()+"'"+"AND DATEDIFF(CURDATE(), end date) <= 365 AND booking ID = '" + booking_id + "'";
			}
			result = CommandLine.sqlMngr.rsToList(CommandLine.sqlMngr.selectOp(get_id_sql));
			//check_id is true if the result list is empty which means the booking Id that the user provide is incorrect
			check_id = result.isEmpty();
			if (check_id){
				System.out.println("The booking Id is not valid.");
			}
    }while (check_id);
    return result.get(0);
  }

}
