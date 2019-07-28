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
	private static final String CONNECTION = "jdbc:mysql://127.0.0.1/mybnb";
	//Object that establishes and keeps the state of our application's
	//connection with the MySQL backend.
	//Object which communicates with the SQL backend delivering to it the
	//desired query from our application and returning the results of this
	//execution the same way that are received from the SQL backend.
	private Statement st = null;
	private Connection conn = null;

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

	// Controls the execution of functionality: "3. Print schema."
	/*public ArrayList<String> getSchema() {
		ArrayList<String> output = new ArrayList<String>();
		try {
			DatabaseMetaData meta = conn.getMetaData();
			ResultSet schemas = meta.getTables(null,null,"%",null);
			//ResultSet catalogs = meta.getCatalogs();
			while (schemas.next()) {
				output.add(schemas.getString("TABLE_NAME"));
			}
			schemas.close();
		} catch (SQLException e) {
			System.err.println("Retrieval of Schema Info failed!");
			e.printStackTrace();
			output.clear();
		}
		return output;
	}*/

	// Controls the execution of functionality: "4. Print table schema."
	/*public ArrayList<String> colSchema(String tableName) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			DatabaseMetaData meta = conn.getMetaData();
			ResultSet rs = meta.getColumns(null, null, tableName, null);
			while(rs.next()) {
				result.add(rs.getString(4));
				result.add(rs.getString(6));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Retrieval of Table Info failed!");
			e.printStackTrace();
			result.clear();
		}
		return result;
	}*/
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
				int counter = 0;
				String sql = "CREATE TABLE IF NOT EXISTS " + table + "(";
				for (counter = 0; counter < column_name.length; counter++) {
					sql = sql.concat("'" + column_name[counter] + "'");
					sql = sql.concat("'" +column_type[counter]+ "',");
				}
				sql = key != null ? sql.concat("PRIMARY KEY ( '" + key + "'));") : sql;
				ResultSet rs = st.executeQuery(sql);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createTempTable(String query){
		try{
			st.executeQuery(query);
		} catch(SQLException e){
			System.err.println("Exception triggered during create table operation execution!");
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
			ResultSetMetaData rsmd = rs.getMetaData();
			int colNum = rsmd.getColumnCount();
			System.out.println("");
			for (int i = 0; i < colNum; i++) {
				System.out.print(rsmd.getColumnLabel(i+1) + "\t");
			}
			System.out.println("");
			while(rs.next()) {
				for (int i = 0; i < colNum; i++) {
					System.out.print(rs.getString(i+1) + "\t");
				}
				System.out.println("");
			}
			rs.close();
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
	public int insertOp(String table, String[] column, String[] values) {
		int rowsAff = 0;
		int counter = 0;
		String query = "";
		System.out.print("Table: "+ table);
		//transform the user input into a valid SQL insert statement
		query = "INSERT INTO " + table + " (" + column + ") VALUES("; 
		for (counter = 0; counter < values.length - 1; counter++) {
			query = query.concat("'" + values[counter] + "',");
		}
		query = query.concat("'" + values[counter] + "');");
		System.out.println(query);
		System.out.println("");
		System.out.println("Rows affected: " + rowsAff);
		System.out.println("");
		int rows = 0; 
		try {
			rows = st.executeUpdate(query);
		} catch (SQLException e) {
			System.err.println("Exception triggered during Insert execution!");
			e.printStackTrace();
		}
		return rows;
	}

	//Controls the execution of an insert query, and return the auto incremented id.
	public int insertGetID(String query) {
		try{
			st.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
    	int id = -1;
			ResultSet rs = st.getGeneratedKeys();
			if (rs.next()) {
					id = rs.getInt(1);
			} 
			rs.close();
			return id;
		} catch(SQLException e){
			System.err.println("Exception triggered during insertGetID execution!");
			e.printStackTrace();
		}
		return -1;
	}

	public void updateOp(String query){
		try{
			st.executeQuery(query);
		} catch(SQLException e){
			System.err.println("Exception triggered during update operation execution!");
			e.printStackTrace();
		}
	}

	public boolean checkExist(String select_col, String[] check_col, String[] value, String table){
		boolean exist = false;
		int i;
		String sql = "SELECT '"+select_col+"' FROM '"+table+"' WHERE ";
		for(i = 0; i < check_col.length - 1; i++){
			sql = sql.concat(check_col[i]);
			sql = sql.concat(" = ");
			sql = sql.concat(value[i]);
			sql = sql.concat("and");
		}
		sql = sql.concat(check_col[i]);
		sql= sql.concat(" = ");
		sql = sql.concat(value[i]);
		sql = sql.concat(";");
		System.out.println("checkUnique sql :" + sql);
		try{
			if (this.selectOp(sql).next()){
				exist = true;
			}
		}catch(Exception e){
			System.err.println("Exception occur in User.checkUnqiue");
			e.printStackTrace();
		}
		return exist;
	}

	public void deleteOperation(String table, String where_condition){
		System.out.println(table);
		String query = "";
		query = "DELETE FROM" + table + "WHERE ";
		query = query.concat(where_condition);
		query = query.concat(";");
		System.out.println(query);
		this.deleteOp(query);

	}

	public void deleteOp(String query){
		try{
			st.executeQuery(query);
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
			while (rs.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1) System.out.print("");
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



}
