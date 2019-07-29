package project;

import java.sql.*;
import java.text.*;
import java.util.Date;

public class ListCalendar {

	private static String[] lists_calendar_name = {"date", "list_ID", "price", "available","start_date", "last_date"};
	//available column has 3 types of value. 'a' rep available, 'b' rep booked, 'u' rep unavailable.
	private static String[] lists_calendar_type = {"DATE NOT NULL", "INT NOT NULL", "FLOAT NULL", "VARCHAR(1) NULL", "DATE NULL", "DATE NULL" };
	private static String lists_calendar_primary_key = "list_ID, date";

	private static String[] ints = {"i"};
	private static String[] ints_type = {"TINYINT(1) NOT NULL"};
	private static String ints_primary_key = "null";

	public static SQLController sqlMngr = new SQLController();
	public ListCalendar(){
		/*try {
			sqlMngr.connect();
		} catch (ClassNotFoundException e) {
			System.err.println("Esception occurs in ListCalendar.constructor");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		sqlMngr = CommandLine.sqlMngr;
	}
	public static String[] getCColumn(){
		return lists_calendar_name;
	}

	public static String[] getCColumnType(){
		return lists_calendar_type;
	}
	public static String getCKey(){
		return lists_calendar_primary_key;
	}
	public static String[] getIColumn(){
		return ints;
	}

	public static String[] getIColumnType(){
		return ints_type;
	}
	public static String getIKey(){
		return ints_primary_key;
	}


	public static void initialInts(){
		/*for (int j = 0; j <= 9 ; j++){

			sqlMngr.insertOp("ints", new String[] {"i"} , new String[] {Integer.toString(j)});
		}*/
	}

	//call this function when the host wants to add a new listing, it will open future 2 month's calendar and initial the status as 'a'(available)
	public void insertListCalendar(int listing_ID, float price){
		//sqlMngr.createTable("calendar", new String[] {"date"}, new String[] {"DATE NOT NULL"}, "date");

		try {
			String insert_date;
			String sql = "SELECT DATE(CURDATE()) + INTERVAL a.i*10000 + b.i*1000 + c.i*100 + d.i*10 + e.i DAY d FROM ints a JOIN ints b JOIN ints c JOIN ints d JOIN ints e WHERE (a.i*10000 + b.i*1000 + c.i*100 + d.i*10 + e.i) <= 61 ORDER BY 1;"; 
			ResultSet t = sqlMngr.selectOp(sql);
			while(t.next()){
				String dt = t.getString(1);
				insert_date = "INSERT INTO lists_calendar (date, list_ID) VALUES ('"+dt+"', "+listing_ID+");"; 
				sqlMngr.conn.createStatement().executeUpdate(insert_date);
			}
			t.close();
		} catch (SQLException e) {
			System.err.println("Exception occurs in ListCalendar.insertlistCalendar");
			e.printStackTrace();
		}


		String update_other_column = "UPDATE lists_calendar "
				+ "SET price = "+price+", "
				+ "available = 'a', "
				+ "start_date = CURDATE(),"
				+ "last_date = DATE_ADD(CURDATE(), INTERVAL 61 DAY);";
		sqlMngr.updateOp(update_other_column);	
	}

	public String checkStatus(int listing_ID, String date){
		String sql = "SELECT available FROM lists_calendar WHERE list_ID = '"+listing_ID+"' and date = '"+date+"';";
		String status = "";
		try {
			ResultSet rs = sqlMngr.selectOp(sql);
			rs.next();
			status = rs.getString("available");
		} catch (SQLException e) {
			System.out.println("Exception occurs in checkStatus in class Calendar");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}

	public boolean checkDate(int listing_ID, String date){
		String sql  = "SELECT * FROM lists_calendar WHERE list_ID = "+listing_ID+" AND date = '"+date+"';";
		boolean exist;
		ResultSet rs;
		try{
			rs = sqlMngr.selectOp(sql);
			if(rs.next()){
				exist = true;
			}
			else{
				exist = false;
			}
			rs.close();
		}catch(SQLException e){
			System.err.println("Exception occurs in Calendar.checkDate");
			e.printStackTrace();
			exist = false;
		}
		return exist;
	}

	//call this function when renter deletes a future booking, set status as 'a'
	//call this function when host wants to change the availability if the listing
	public void updateAva(int listing_ID, String date, String status){
		String update_ava = "UPDATE lists_calendar SET available = '"+status+"' WHERE date = '"+date+"' and list_ID = '"+listing_ID+"';";
		sqlMngr.updateOp(update_ava);
	}

	//call this function when host wants to update the listing's price of a certain day
	public void updatePrice(int listing_ID, String date, float price){
		String update_price = "UPDATE lists_calendar SET price = "+price+" WHERE date = '"+date+"' and list_ID = '"+listing_ID+"';";
		sqlMngr.updateOp(update_price);
		float new_price = this.getPrice(listing_ID, date);
		System.out.println("Update price scuessfully, your current price of " + listing_ID +" on " + date + " is " + new_price);
	}

	public float getPrice(int listing_ID, String date){
		String get_price ="SELECT price FROM lists_calendar WHERE list_ID = '"+listing_ID+"' and date = '"+date+"'";
		float price = -1;
		try {
			ResultSet rs = sqlMngr.selectOp(get_price);
			rs.next();
			price = rs.getFloat("price");
			rs.close();
		} catch (SQLException e) {
			System.out.println("Exception occurs in class Calendar getPrice function");
			e.printStackTrace();
		}
		return price;
	}

	private String getLastDate(int listing_ID){
		String get_last = "SELECT last_date FROM lists_calendar WHERE list_ID = '"+listing_ID+"';";
		ResultSet rs = sqlMngr.selectOp(get_last);
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
		String get_start = "SELECT start_date FROM lists_calendar WHERE list_ID = '"+listing_ID+"';";
		ResultSet rs = sqlMngr.selectOp(get_start);
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
		String get_listings = "SELECT list_ID FROM listing;";
		ResultSet rs = sqlMngr.selectOp(get_listings);
		try {
			while(rs.next()){
				this.updateListCalendar(rs.getInt("list_ID"));
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
		sqlMngr.excuteSql(insert_date);
		//delete the calendar record from the table if the date is before the current date
		String update_other_column = "UPDATE lists_calendar"
				+ "SET list_ID = '"+listing_ID+"',"
				+ "price = null,"
				+ "available = 'a',"
				+ "start_date = CURDATE(),"
				+ "last_date =  DATE_ADD(CURDATE(), INTERVAL 61 DAY) ;";
		sqlMngr.excuteSql(update_other_column);	
		String delete_befor = "DELET FORM lists_calendar WHERE list_ID = '"+listing_ID+"' and date < CURDATE();";
		sqlMngr.selectOp(delete_befor);
	}


}
