package project;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Calendar {

	private static String[] lists_calendar_name = {"date", "listing_id", "price", "available"};
	//available column has 3 types of value. 'a' rep available, 'b' rep booked, 'u' rep unavailable.
	private static String[] lists_calendar_type = {"DATE NOT NULL", "INT NULL", "FLOAT NULL,", "VARCHAR(1) NULL"};
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
				+ "price = '"+price+"'"
				+ "available = 'a';";
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

}
