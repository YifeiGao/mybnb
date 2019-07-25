package project;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import java.sql.*;

public class Booking {
	private static SQLController sqlMngr = User.sqlMngr;
	
	private static final String [] booking_column_name = {"booking ID", "list ID", "host user name", "renter user name",  "start date", "end date"};
	private static final String [] booking_column_type = {"INT AUTO_INCREMENT", "INT NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL",  "DATE NOT NULL", "DATE NOT NULL"};
	private static final String booking_primary_key = "booking ID";
	
	public void getHostHistory(){
		String query = "SELECT booking ID, list ID, renter user name, start date, end date FROM booking where host user name = '"+User.getUser()+"'";
		ResultSet resultSet = User.sqlMngr.selectOp(query);
		System.out.println("This is the rental history of all the listings that you owned, notice the history record is grouped by the listing Id");
		sqlMngr.printRecord(resultSet);
	}
	
	public void getRentalHistory(){
		String query = "SELECT booking ID, list ID, renter user name, start date, end date FROM booking where renter user name = '"+User.getUser()+"'";
		ResultSet resultSet = User.sqlMngr.selectOp(query);
		System.out.println("This is the rental history that you have rented, notice that the history is order by listing ID");
		sqlMngr.printRecord(resultSet);
	}
	

	
	public void deletBooking(String type){
		ArrayList<String> result = new ArrayList<String>();
		boolean valid = false;
		String booking_id;
		String update_sql;
		String get_cancel_sql;
		int cancellation;
		do{
			result = this.checkBookingId(type);
			valid = this.checkFuture(result);
		}while(!valid);
		booking_id = result.get(0);
		sqlMngr.deleteOperation("booking", "booking ID = '"+booking_id+"'");
		//update the user's cancellation time
		get_cancel_sql = "SELECT cancellation FROM users WHERE user name = " + User.getUser();
		cancellation = Integer.parseInt(sqlMngr.rsToList(sqlMngr.selectOp(get_cancel_sql)).get(0).get(0));
		cancellation += 1;
		update_sql = "UPDATE users set cancellation = '"+cancellation+"' WHERE user name = " + User.getUser();
		sqlMngr.updateOp(update_sql);
		/*
		 * TO DO:
		 * update availability of the list
		 */
	}
	private ArrayList<String> checkBookingId(String type){
		String get_id_sql;
		String check_type_sql;
		String booking_id;
		boolean check_id;
		String type_result;
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		do{
			System.out.println("Please provide the booking ID of the future booking that you want to cancle" );
			booking_id = CommandLine.sc.nextLine();
			if(type.equals("host")){
				get_id_sql = "SELECT * FROM booking WHERE host user name = '"+User.getUser()+"' and booking id  = '"+booking_id+"'";
			}
			else{
				get_id_sql = "SELECT * FROM booking WHERE renter user name = '"+User.getUser()+"' and booking id  = '"+booking_id+"'";
			}
			result = sqlMngr.rsToList(sqlMngr.selectOp(get_id_sql));
			//check_id is true if the result list is empty which means the booking Id that the user provide is incorrect
			check_id = result.isEmpty();
			if (check_id){
				System.out.println("The booking Id is not found");
			}
		}while (check_id);
		return result.get(0);
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
			booking_date = result_list.get(Arrays.asList(booking_column_name).indexOf("start date"));
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
	


}
