package project;
import java.security.interfaces.RSAKey;
import java.sql.*;
import java.util.ArrayList;

/*
 * This class acts as the medium between our CommandLine interface
 * and the SQL Backend. It is a controller class.
 */
public class SQLController {

	private static final String dbClassName = "com.mysql.jdbc.Driver";
	private static final String CONNECTION = "jdbc:mysql://127.0.0.1/test1";
	//Object that establishes and keeps the state of our application's
	//connection with the MySQL backend.
	//Object which communicates with the SQL backend delivering to it the
	//desired query from our application and returning the results of this
	//execution the same way that are received from the SQL backend.
	public Statement st = null;
	public Connection conn = null;

	// Initialize current instance of this class.
	public boolean connect() throws ClassNotFoundException {
		boolean success;
		Class.forName(dbClassName);;
		String user = "root";
		String pass = "Sq@19870202";
		try {
			conn = DriverManager.getConnection(CONNECTION, user, pass);
			st = conn.createStatement();

			success = true;
			System.out.println("connected!!!");
		} catch (SQLException e) {
			System.err.println("Connection could not be established!");
			e.printStackTrace();
			success = false;
		}
		return success;
	}


	// Destroy the private objects/fields of current instance of this class.
	// Acts like a destructor.
	public void disconnect() {
		try {
			st.close();
			conn.close();
		} catch (SQLException e) {
			System.err.println("Exception occured while disconnecting!");
			e.printStackTrace();
		} finally {
			st = null;
			conn = null;
		}
	}

	public void initialize(){
		this.createTable("users", User.getUserColumn(), User.getUserColumnType(), User.getUserKey());
		this.createTable("listing", Listing.getListingColumn(), Listing.getListingColumnType(), Listing.getListingKey());
		this.createTable("amenities", Listing.getAmColumn(), Listing.getAmColumnType(), Listing.getAmKey());
		this.createTable("booking", Booking.getBookingColumn(), Booking.getBookingColumnType(), Booking.getBookingKey());
		this.createTable("lists_calendar", ListCalendar.getCColumn(), ListCalendar.getCColumnType(), ListCalendar.getCKey());
		this.createTable("ints", ListCalendar.getIColumn(), ListCalendar.getIColumnType(), null);
		//ListCalendar.initialInts();
	}
	/**
	 * the function is use to create table
	 * @param table: the name of the table
	 * @param column_name: a string array that stores the name of columns
	 * @param column_type: a string array that stores the type of the columns
	 * @param key: primary key of the table, it is optional , if the key is null then do nothing otherwise concatenate it with the create table query
	 */
	public void createTable(String table, String[] column_name, String[] column_type, String key){
		try {
			boolean exist ;
			//check if the table exists or not
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, table, null);
			exist = tables.next();
			if(!exist){
				int counter;
				String sql = "CREATE TABLE IF NOT EXISTS " + table + "(";
				for (counter = 0; counter < column_name.length -1; counter++) {
					sql = sql.concat(column_name[counter] + " ");
					sql = sql.concat(column_type[counter]+ ", ");
				}
				if(key != null){
					sql = sql.concat(column_name[column_name.length - 1] + " ");
					sql = sql.concat(column_type[column_type.length - 1]+ ", ");
					sql = sql.concat("PRIMARY KEY (" + key + "));");
				}
				else{
					sql = sql.concat(column_name[column_name.length - 1] + " ");
					sql = sql.concat(column_type[column_type.length - 1]+ "); ");
				}
				st.executeUpdate(sql);
				if(table.equals("ints")){
					this.insertOp("ints", new String[] {"i"}, new String[]{Integer.toString(0)}, false);
					this.insertOp("ints", new String[] {"i"}, new String[]{Integer.toString(1)}, false);
					this.insertOp("ints", new String[] {"i"}, new String[]{Integer.toString(2)}, false);
					this.insertOp("ints", new String[] {"i"}, new String[]{Integer.toString(3)}, false);
					this.insertOp("ints", new String[] {"i"}, new String[]{Integer.toString(4)}, false);
					this.insertOp("ints", new String[] {"i"}, new String[]{Integer.toString(5)}, false);
					this.insertOp("ints", new String[] {"i"}, new String[]{Integer.toString(6)}, false);
					this.insertOp("ints", new String[] {"i"}, new String[]{Integer.toString(7)}, false);
					this.insertOp("ints", new String[] {"i"}, new String[]{Integer.toString(8)}, false);
					this.insertOp("ints", new String[] {"i"}, new String[]{Integer.toString(9)}, false);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Controls the execution of a select query.
	//Functionality: "2. Select a record."
	public ResultSet selectOp(String query) {
		ArrayList<String> result = new ArrayList<String>();
		ResultSet rs = null;
		try {
			rs = st.executeQuery(query);
		} catch (SQLException e) {
			System.err.println("Exception triggered during Select execution!");
			e.printStackTrace();
		}
		System.out.println();

		return rs;
	}

	public ArrayList<ArrayList<String>> rsToList(ResultSet rs){
		ArrayList<ArrayList<String>> result_list = new 	ArrayList<ArrayList<String>>();
		try{
			ResultSetMetaData md = rs.getMetaData();
			int column_number = md.getColumnCount();

			while(rs.next()){
				ArrayList<String> row = new ArrayList<String>();
				for(int i = 1; i <= column_number; i++){
					row.add(rs.getString(i));
				}
				result_list.add(row);
			}
		}catch (SQLException e) {
			System.err.println("Exception triggered during rsToList");
			e.printStackTrace();
		}
		return result_list;
	}

	//Controls the execution of an insert query.
	//Functionality: "1. Insert a record."
	public int insertOp(String table, String[] column, String[] values, boolean return_generated_keys) {
		int rowsAff = 0;
		int counter = 0;
		String query = "";
		String column_name = "INSERT INTO " + table +" (";
		String column_values = "VALUES(";
		System.out.print("Table: "+ table);
		//transform the user input into a valid SQL insert statement
		for (counter = 0; counter < values.length - 1; counter++) {
			column_name = column_name.concat(column[counter] + ", ");
			column_values = column_values.concat("'" + values[counter] + "',");
		}
		column_name = column_name.concat(column[counter] + ") ");
		column_values = column_values.concat("'" + values[counter] + "');");
		query = column_name.concat(column_values);
		int rows = 0; 
		try {
			if(!return_generated_keys){
				rows = st.executeUpdate(query);
			}
			else{
				rows = st.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			}
		} catch (SQLException e) {
			System.err.println("Exception triggered during Insert execution!");
			e.printStackTrace();
		}
		return rows;
	}

	public void updateOp(String query){
		try{
			st.executeUpdate(query);
		} catch(SQLException e){
			System.err.println("Exception triggered during update operation execution!");
			e.printStackTrace();
		}
	}

	public boolean checkExist(String select_col, String[] check_col, String[] value, String table){
		int i;
		boolean exist;
		String sql = "SELECT "+select_col+" FROM "+table+" WHERE ";
		for(i = 0; i < check_col.length - 1; i++){
			sql = sql.concat(check_col[i]);
			sql = sql.concat(" = ");
			sql = sql.concat("'" + value[i] + "'");
			sql = sql.concat(" AND ");
		}
		sql = sql.concat(check_col[i]);
		sql= sql.concat(" = ");
		sql = sql.concat("'" + value[i] + "'");
		sql = sql.concat(";");
		try{
			ResultSet rs = this.selectOp(sql);
			boolean test = rs.next();
			if (test){
				rs.close();
				exist = true;
			}
			else{
				rs.close();
				exist = false;
			}

		}catch(Exception e){
			System.err.println("Exception occur in SQLController.checkExist");
			e.printStackTrace();
			return false;
		}
		return exist;
	}

	public void deleteOperation(String table, String where_condition){
		System.out.println(table);
		String query = "";
		query = "DELETE FROM " + table + " WHERE ";
		query = query.concat(where_condition);
		query = query.concat(";");
		System.out.println(query);
		this.deleteOp(query);

	}

	public void deleteOp(String query){
		try{
			st.executeUpdate(query);
		} catch(SQLException e){
			System.err.println("Exception triggered during Delete execution!");
			e.printStackTrace();
		}
	}

	//print the records that stores in the resultset rs,
	//TO DO:
	// need to print in table format
	public void printRecord(ResultSet rs){
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			for(int j = 1; j <= columnsNumber; j++){
				if (j > 1) System.out.print("     ");
				System.out.print(rsmd.getColumnName(j));
			}
			System.out.println();
			while (rs.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1) System.out.print("             ");
					String columnValue = rs.getString(i);
					System.out.print(columnValue);
				}
				System.out.println("");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ResultSet excuteSql(String sql){
		ResultSet rs = null;
		try{
			rs = st.executeQuery(sql);
		} catch(SQLException e){
			System.err.println("Exception triggered during excuteSql execution!");
			e.printStackTrace();
		}
		return rs;

	}
	//Controls the execution of an insert query, and return the auto incremented id.
	public int insertGetID(String query) {
		try{
			st.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			int id = -1;
			ResultSet rs = st.getGeneratedKeys();
			id = rs.getInt(1);
			rs.close();
			return id;
		} catch(SQLException e){
			System.err.println("Exception triggered during insertGetID execution!");
			e.printStackTrace();
		}
		return -1;
	}
	public void createTempTable(String query){
		try{
			st.executeUpdate(query);
		} catch(SQLException e){
			System.err.println("Exception triggered during create table operation execution!");
			e.printStackTrace();
		}
	}
	public void dropTempTable(String query){
		try{
			st.executeUpdate(query);
		} catch(SQLException e){
			System.err.println("Exception triggered during drop table operation execution!");
			e.printStackTrace();
		}
	}




}
