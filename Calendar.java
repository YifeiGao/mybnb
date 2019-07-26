package project;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Calendar {

	private static String[] lists_calendar_name = {"date", "listing_id", "price", "available","start_date", "last_date"};
	//available column has 3 types of value. 'a' rep available, 'b' rep booked, 'u' rep unavailable.
	private static String[] lists_calendar_type = {"DATE NOT NULL", "INT NULL", "FLOAT NULL,", "VARCHAR(1) NULL", "DATE NOT NULL"};
	private static String lists_calendar_primary_key = "listing_id, date";

	private static String[] ints = {"i"};
	private static String[] ints_type = {"TINYINT"};

	public static void initialInts(){
		for (int j = 0; j <= 9 ; j++){
			CommandLine.sqlMngr.insertOp("ints", new String[] {"i"} , new String[] {Integer.toString(j)});
		}
	}

	//call this function when the host wants to add a new listing, it will open future 2 month's calendar and initial the status as 'a'(available)
	public void insertListCalendar(int listing_ID, float price){
		String insert_date = "INSERT INTO lists_calendar (date)"
				+ "SELECT DATE(CURDATE()) + INTERVAL a.i*10000 + b.i*1000 + c.i*100 + d.i*10 + e.i DAY"
				+ "FROM ints a JOIN ints b JOIN ints c JOIN ints d JOIN ints e"
				+ "WHERE(a.i*10000 + b.i*1000 + c.i*100 + d.i*10 + e.i) <= 61"
				+ "ORDER BY 1;";
		CommandLine.sqlMngr.excuteSql(insert_date);
		String update_other_column = "UPDATE lists_calendar"
				+ "SET listing_id = '"+listing_ID+"',"
				+ "price = '"+price+"',"
				+ "available = 'a',"
				+ "start_date = CURDATE(),"
				+ "last_date =  DATE_ADD(CURDATE(), INTERVAL 61 DAY) ;";
		CommandLine.sqlMngr.excuteSql(update_other_column);	
	}

	public String checkStatus(int listing_ID, String date){
		String sql = "SELECT available FROM lists_calendar WHERE date = '"+date+"';";
		String status = "";
		try {
			ResultSet rs = CommandLine.sqlMngr.selectOp(sql);
			status = rs.getString("available");
		} catch (SQLException e) {
			System.out.println("Exception occurs in checkStatus in class Calendar");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}

	//call this function when renter deletes a future booking, set status as 'a'
	//call this function when host wants to change the availability if the listing
	public void updateAva(int listing_ID, String date, String status){
		String update_ava = "UPDATE lists_calendar SET available = '"+status+"' WHERE date = '"+date+"' and listing_id = '"+listing_ID+"';";
		CommandLine.sqlMngr.updateOp(update_ava);
	}

	//call this function when host wants to update the listing's price of a certain day
	public void updatePrice(int listing_ID, String date, float price){
		String update_price = "UPDATE lists_calendar SET price = '"+price+"' WHERE date = '"+date+"' and listing_id == '"+listing_ID+"';";
		CommandLine.sqlMngr.updateOp(update_price);
		float new_price = this.getPrice(listing_ID, date);
		System.out.println("Update price scuessfully, your current price of " + listing_ID +" on " + date + " is " + new_price);
	}

	public float getPrice(int listing_ID, String date){
		String get_price ="SELECT price FROM lists_calendar WHERE listing_id = '"+listing_ID+"' and date = '"+date+"'";
		float price = -1;
		try {
			ResultSet rs = CommandLine.sqlMngr.selectOp(get_price);
			price = rs.getFloat("price");
		} catch (SQLException e) {
			System.out.println("Exception occurs in class Calendar getPrice function");
			e.printStackTrace();
		}
		return price;
	}

	private String getLastDate(int listing_ID){
		String get_last = "SELECT last_date FROM lists_calendar WHERE listing_id = '"+listing_ID+"';";
		ResultSet rs = CommandLine.sqlMngr.selectOp(get_last);
		String last_date = null;
		try {
			last_date = rs.getString("last_date");
		} catch (SQLException e) {
			System.out.println("Exception occurs in Calendar.getLastDate");
			e.printStackTrace();
		}
		return last_date;
	}

	private String getStartDate(int listing_ID){
		String get_start = "SELECT start_date FROM lists_calendar WHERE listing_id = '"+listing_ID+"';";
		ResultSet rs = CommandLine.sqlMngr.selectOp(get_start);
		String start_date = null;
		try {
			start_date = rs.getString("start_date");
		} catch (SQLException e) {
			System.out.println("Exception occurs in Calendar.getStartDate");
			e.printStackTrace();
		}
		return start_date;
	}
	//this function need to be called once we connected to the database
	public void updateCalendar(){
		String get_listings = "SELECT listing_ID FROM listing;";
		ResultSet rs = CommandLine.sqlMngr.selectOp(get_listings);
		try {
			while(rs.next()){
				this.updateListCalendar(rs.getInt("listing_ID"));
			}
		} catch (SQLException e) {
			System.out.println("Exception occurs in Calendar.updatecalendar");
			e.printStackTrace();
		}
	}

	//it is a helper function of updateCalebdar
	//this function is to update the calendar of the listing, we want to maintain the listing always opens the 2 future month's calendar
	// this function also delete the calendar record that the date is before the current date
	private void updateListCalendar(int listing_ID){
		String last = this.getLastDate(listing_ID);
		String start = this.getStartDate(listing_ID);
		int diff_days = -3;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date last_date = sdf.parse(last);
			Date start_date = sdf.parse(start);
			Date curr_date = new Date();
			sdf.format(curr_date);
			diff_days = (int) ((curr_date.getTime() - start_date.getTime())/(1000*60*60*24));
		} catch (ParseException e) {
			System.out.println("Exception occurs in Calendar.updateListCalendar");
			e.printStackTrace();
		}
		System.out.println("test: the difference days from start day to current day is "+diff_days);
		String insert_date = "INSERT INTO lists_calendar (date)"
				+ "SELECT DATE('"+last+"') + INTERVAL a.i*10000 + b.i*1000 + c.i*100 + d.i*10 + e.i DAY"
				+ "FROM ints a JOIN ints b JOIN ints c JOIN ints d JOIN ints e"
				+ "WHERE(a.i*10000 + b.i*1000 + c.i*100 + d.i*10 + e.i) <= '"+diff_days+"'"
				+ "ORDER BY 1;";
		CommandLine.sqlMngr.excuteSql(insert_date);
		//delete the calendar record from the table if the date is before the current date
		String update_other_column = "UPDATE lists_calendar"
				+ "SET listing_id = '"+listing_ID+"',"
				+ "price = null,"
				+ "available = 'a',"
				+ "start_date = CURDATE(),"
				+ "last_date =  DATE_ADD(CURDATE(), INTERVAL 61 DAY) ;";
		CommandLine.sqlMngr.excuteSql(update_other_column);	
		String delete_befor = "DELET FORM lists_calendar WHERE listing_id = '"+listing_ID+"' and date < CURDATE();";
		CommandLine.sqlMngr.selectOp(delete_befor);
	}


}
