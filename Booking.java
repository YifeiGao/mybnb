package project;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import java.sql.*;

public class Booking {

	private static final String [] booking_column_name = {"booking_ID", "list_ID", "host_user_name", "renter_user_name",  "start_date", "end_date"};
	private static final String [] booking_column_type = {"INT AUTO_INCREMENT", "INT NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL",  "DATE NOT NULL", "DATE NOT NULL"};
	private static final String booking_primary_key = "booking_ID";

	public void getHostHistory(){
		String query = "SELECT booking_ID, list_ID, renter_user_name, start_date, end_date FROM booking where host_user_name = '"+User.getUser()+"';";
		ResultSet resultSet = CommandLine.sqlMngr.selectOp(query);
		System.out.println("This is the rental history of all the listings that you owned, notice the history record is grouped by the listing Id");
		CommandLine.sqlMngr.printRecord(resultSet);
	}

	public void getRentalHistory(){
		String query = "SELECT booking_ID, list_ID, host_user_name, start_date, end_date FROM booking where renter_user_name = '"+User.getUser()+"';";
		ResultSet resultSet = CommandLine.sqlMngr.selectOp(query);
		System.out.println("This is the rental history that you have rented, notice that the history is order by listing ID");
		CommandLine.sqlMngr.printRecord(resultSet);
	}
	//print out a list of recent booking histories, by default recent: 1year=365days
	//this list does not include any future bookings
	public void getRecentCompletedHostHistory(){
		String query = "SELECT booking_ID, list_ID, host_user_name, start_date, end_date FROM booking WHERE host_user_name = '"+User.getUser()+"'"+"AND DATEDIFF(CURDATE(), end_date) <= 365;";
		ResultSet resultSet = CommandLine.sqlMngr.selectOp(query);
		System.out.println("Here is your recent renting histories within a year. (NOTE: Any ongoing renting records won't be shown in this list.");
		CommandLine.sqlMngr.printRecord(resultSet);
	}

	public void getRecentCompletedRentalHistory(){
		String query = "SELECT booking_ID, list_ID, renter_user_name, start_date, end_date FROM booking WHERE host_user_name = '"+User.getUser()+"'"+"AND DATEDIFF(CURDATE(), end_date) <= 365;";
		ResultSet resultSet = CommandLine.sqlMngr.selectOp(query);
		System.out.println("Here is your recent renting histories within a year. (NOTE: Any ongoing renting records won't be shown in this list.");
		CommandLine.sqlMngr.printRecord(resultSet);
	}



	public void deletBooking(String type){
		ArrayList<String> result = new ArrayList<String>();
		boolean valid = false;
		String booking_id;
		String update_sql;
		String get_cancel_sql;
		int cancellation;
		String start_date;
		String end_date;
		int listing_ID;
		do{
			result = this.checkBookingId(type);
			valid = this.checkFuture(result);
		}while(!valid);
		booking_id = result.get(0);
		CommandLine.sqlMngr.deleteOperation("booking", "booking_ID = '"+booking_id+"'");
		//update the user's cancellation time
		this.updateCancellation(type);
		//update availability of the list
		listing_ID = Integer.parseInt(result.get(1));
		start_date = result.get(4);
		end_date = result.get(5);
		System.out.println("test: start_date is " + start_date +" end date is " + end_date);
		try {
			Date start =  new SimpleDateFormat("yyyy-MM-dd").parse(start_date);
			Date end = new SimpleDateFormat("yyyy-MM-dd").parse(end_date);
			int duration = (int)((end.getTime() - start.getTime()) / (1000*60*60*24) + 1);
			ZoneId defaultZoneId = ZoneId.systemDefault();
			Instant instant = start.toInstant();
			LocalDate change_start = instant.atZone(defaultZoneId).toLocalDate();
			for(int i = 0;i < duration; i++){
				LocalDate date = change_start.plusDays(i);
				new ListCalendar().updateAva(listing_ID, date.toString(), "a");
			}
		} catch (ParseException e) {
			System.out.println("Exception occurs in Booking.deleteBooking");
			e.printStackTrace();
		}
	}


	private ArrayList<String> checkBookingId(String type){
		String get_id_sql;
		String booking_id;
		boolean check_id;
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		do{
			System.out.println("Please provide the booking ID of the future booking that you want to cancle" );
			booking_id = CommandLine.sc.nextLine();
			if(type.equals("host")){
				get_id_sql = "SELECT * FROM booking WHERE host user name = '"+User.getUser()+"' and booking id  = '"+booking_id+"';";
			}
			else{
				get_id_sql = "SELECT * FROM booking WHERE renter user name = '"+User.getUser()+"' and booking id  = '"+booking_id+"';";
			}
			result = CommandLine.sqlMngr.rsToList(CommandLine.sqlMngr.selectOp(get_id_sql));
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

	public void updateCancellation(String type){
		String get_cancel_sql;
		if(type.equals("host")){
			get_cancel_sql = "SELECT host_cancellation FROM users WHERE user_name = " +User.getUser()+ ";";
		}
		else{
			get_cancel_sql = "SELECT renter_cancellation FROM users WHERE user_name = " +User.getUser() + ";";
		}
		int cancellation = Integer.parseInt(CommandLine.sqlMngr.rsToList(CommandLine.sqlMngr.selectOp(get_cancel_sql)).get(0).get(0));
		cancellation += 1;
		String update_sql = "UPDATE users set cancellation = '"+cancellation+"' WHERE user_name = " + User.getUser();
		CommandLine.sqlMngr.updateOp(update_sql);
	}



}
