package project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Listing {
	private static String[] listing_type = {"House", "Bed_and_breakfast", "Bungalow", "Chalet", "Guest_suite", "Hostel", "Loft", "Townhouse", "Apartment", "Boutique_hotle", "Cabin", "Cottage", "Guesthouse", "Hotel", "Resort", "Villa"};
	private static final String[] listing_column_name = {"list_ID", "list_latitude", "list_longitude", "Country", "Province", "City", "house_number_and_Street","host_user_name", "post_code", "list_type"};
	private static final String[] listing_column_type = { "INT NOT NULL AUTO_INCREMENT", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL","VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL","VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL"};
	private static final String listing_primary_key = "list_ID";
	private static final String[] listing_column_name_for_insert = { "list_latitude", "list_longitude", "Country", "Province", "City", "house_number_and_Street","post_code", "list_type","host_user_name"};

	private static final String[] amenities_column_name = {"list_ID", "kitchen","heating","washer", "wifi", "indoor_fireplace", "iron", "Laptop_friendly_workspace", "crib", "self_check_in", "carbon_monoxide_detector", "shampoo", "air_conditioning", "dryer", "breakfast", "hangers", "hair_dryer", "TV", "hight_chair", "smoke_detector", "private_bathroom"};
	private static final String[] amenities_column_type = {"INT NOT NULL", "TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL","TINYINT(1) NOT NULL"};
	private static final String amenities_primary_key = "list_ID";

	public static SQLController sqlMngr = new SQLController();
	public Listing(){
		sqlMngr = CommandLine.sqlMngr;
	}
	public static String[] getListingColumn(){
		return listing_column_name;
	}

	public static String[] getListingColumnType(){
		return listing_column_type;
	}

	public static String getListingKey(){
		return listing_primary_key;
	}
	public static String[] getAmColumn(){
		return amenities_column_name;
	}

	public static String[] getAmColumnType(){
		return amenities_column_type;
	}

	public static String getAmKey(){
		return amenities_primary_key;
	}


	public int addListing(String host_user){
		int choice;
		int list_ID = -100000;
		ArrayList<String> a = new ArrayList<String>(listing_column_name.length);
		String[] column_values = new String[listing_column_name.length];
		String[] amenities = new String [amenities_column_name.length];
		boolean check;
		do{
			this.listTypeMenu();
			choice = Integer.parseInt(CommandLine.sc.nextLine());
			check = CommandLine.checkInRange(choice, 0, listing_type.length - 1);
			if(!check){
				System.out.println("Please choose a type that provided");
			}
		}while(!check);
		String type = listing_type[choice];
		a = CommandLine.getInfo(listing_column_name_for_insert, 0, 6);
		System.out.println("listing type: "+ type);
		a.add(type);
		a.add(host_user);
		column_values = a.toArray(new String[0]);;
		sqlMngr.insertOp("listing", listing_column_name_for_insert, column_values, true);

		try{
			ResultSet rs = sqlMngr.st.getGeneratedKeys();
			rs.next();
			list_ID = rs.getInt(1);
			rs.close();
		}catch(SQLException e){
			System.err.println("Exception occurs in Listing.addLising");
			e.printStackTrace();
		}
		sqlMngr.insertOp("amenities",amenities_column_name, this.getAmenities(list_ID), false);
		System.out.println("Listing added successfully, the listing ID of the listing is "+ list_ID);
		return list_ID;

	}

	private String[] getAmenities(int list_ID){
		String[] amenities = new String[amenities_column_name.length];
		amenities[0] = Integer.toString(list_ID);
		String t_or_f;
		System.out.println("Flowings are amenities, please type 'T' if the listing you add have the amenity, otherwise type'F'");
		for(int i = 1; i < amenities_column_name.length; i++ ){
			do{
				System.out.println("Do you have " + amenities_column_name[i] + " in your listing? (T/F)");
				t_or_f =  CommandLine.sc.nextLine();
			}while(! (t_or_f.equalsIgnoreCase("t") || t_or_f.equalsIgnoreCase("f")));
			if(t_or_f.equalsIgnoreCase("t")){
				amenities[i] = Integer.toString(1);
			}
			else{
				amenities[i] = Integer.toString(0);
			}
		}
		return amenities;
	}

	public float getAvePrice(int l_ID){
		float price = -100;
		String get_l_info = "SELECT Country, Province, City FROM listing WHERE list_ID = '"+l_ID+"';";
		String country = "";
		String province = "";
		String city = "";
		ResultSet info = sqlMngr.selectOp(get_l_info);
		try {
			if(info.next()){
				country = info.getString("Country");
				province = info.getString("Province");
				city = info.getString("City");
				info.close();
			}
		} catch (SQLException e1) {
			System.out.println("Exception occurs in Listing.getAvP 1");
			e1.printStackTrace();
		}
		String get_ave = "SELECT AVG(price) ave_p FROM (SELECT list_ID FROM listing WHERE Country = '"+country+"' AND Province = '"+province+"' AND City = '"+city+"') t1 INNER JOIN lists_calendar t2 WHERE t1.list_ID = t2.list_ID";
		ResultSet rs = sqlMngr.selectOp(get_ave);
		try {
			if(rs.next()){
				price = rs.getFloat("ave_p");
				rs.close();
			}
		} catch (SQLException e) {
			System.out.println("Exception occurs in Listing.getAvP 2");
			e.printStackTrace();
		}
		return price;
	}

	public boolean ownList(int list_ID, String host) throws SQLException{
		String sql = "SELECT * FROM listing WHERE list_ID = '"+list_ID+"' and host_user_name = '"+host+"';";
		ResultSet rs =sqlMngr.selectOp(sql);
		return rs.next();
	}


	private void listTypeMenu(){
		System.out.println("=========LISTING TYPES=========");
		for(int i = 0; i < 16; i++){
			System.out.println(i + ". " + listing_type[i]);
		}
		System.out.print("Please select the type of your listing [0-15]: ");
	}

	public void getHostListings(String user_id){
		String query = "SELECT list_ID, list_latitude, list_longitude, Country, Province, City, house_number_and_Street, list_type, kitchen, heating, washer, wifi, indoor_fireplace, iron, Laptop_friendly_workspace, crib, self_check_in, carbon_monoxide_detector, shampoo, air_conditioning, dryer, breakfast, hangers, hair_dryer, TV, hight_chair, smoke_detector, private_bathroom FROM listing NATURAL JOIN amenities WHERE host_user_name = '" +user_id+ "';";
		ResultSet rs = sqlMngr.selectOp(query);
				System.out.println("Here is all the listings under your account.");
				sqlMngr.printRecord(rs);
	}
	
	public boolean checkHost(String user_name){
		boolean is_host;
		is_host = sqlMngr.checkExist("list_ID", new String[] {"host_user_name"}, new String[] {user_name}, "listing");
		return is_host;
	}

	// Ask user to input start date and end date first
	public void getListing(String username) {
		String start_date;
		String end_date;
		String choice;
		String query;
		boolean valid_date;
		boolean valid_choice;
		long day_lengths;
		boolean re_enter; boolean re_select;
		do {	//DO WHILE user input date: valid_date //
			valid_date = true;
			re_enter = false;
			System.out.println("Please Enter your start date: (yyyy-MM-dd)");
			start_date = CommandLine.sc.nextLine();
			System.out.println("Please Enter your end date: (yyyy-MM-dd)");
			end_date = CommandLine.sc.nextLine();
			/***** Verify date *****/
			ArrayList<ArrayList<String>> result;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date end = null;
			Date start = null;
			try {
				start = sdf.parse(start_date);
				end = sdf.parse(end_date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("Please provide a valid date");
				valid_date = false;
				continue;
			}
			long difference_milies = end.getTime() - start.getTime();
			// start date cannot be later than end date
			if (difference_milies < 0) {
				System.out.println("End date cannot be earlier than start date.");
				valid_date = false;
				continue;
			}
			// length of the day including the first day
			day_lengths = difference_milies / (1000 * 60 * 60 * 24) + 1;
			System.out.println("===" + day_lengths + "===");
			query = "SELECT list_ID, SUM(price) as total_price FROM lists_calendar WHERE available = 'a' AND date >= '" + start_date + "' AND date <= '" + end_date + "' GROUP BY list_ID HAVING COUNT(*) = '" + day_lengths + "';";
			ResultSet rs = sqlMngr.selectOp(query);
			result = sqlMngr.rsToList(rs);
			/***** End of verify date *****/
			valid_date = !result.isEmpty();
			if (!valid_date) {
				System.out.println("Sorry, no results found. Please provide another start date and end date.");
				continue;
			} else {	//date valid, ask for address input
				do {	// DO WHILE verify user's choice //
					System.out.println("1. Enter address to locate the listing");
					System.out.println("2. Enter geographic coordinate and distance to look for nearby listings");
					System.out.println("3. Enter postal code to find nearby listings");
					valid_choice = true;
					re_select = false;
					System.out.println("Please choose one of the previous options [1-3] to start, or you can enter r to re-enter start date and end date");
					choice = CommandLine.sc.nextLine();
					if (choice.equals("1")) {
						re_select = getListingByAddress(start_date, end_date, day_lengths, username);
					} else if (choice.equals("2")) {
						re_select = getListingByGeoCoord(start_date, end_date, day_lengths, username);
					} else if (choice.equals("3")) {
						re_select = getListingByPostCode(start_date, end_date, day_lengths, username);
					} else if (choice.equalsIgnoreCase("r")) {
						re_enter = true;
						break;
					} else {
						valid_choice = false;
					}
				} while(!valid_choice || re_select);// DO WHILE verify user's choice//
			}
		} while(!valid_date || re_enter);//DO WHILE END user input date //
	}
	
	private boolean getListingByAddress(String start_date, String end_date, long day_lengths, String username) {
		boolean terminate = false;
		boolean re_enter;
		ResultSet rs;
		ArrayList<ArrayList<String>> result;
		String choice;
		boolean valid_choice;
		String list_ID;
		String query;
		String country; String city; String province; String street;
		/****** Ask User Input Address ******/
		do {	// DO WHILE enter address //
			re_enter = false;
			System.out.println("Please provide the Country:");
			country = CommandLine.sc.nextLine();
			System.out.println("Please provide the Province:");
			province = CommandLine.sc.nextLine();		
			System.out.println("Please provide the City:");
			city = CommandLine.sc.nextLine();		
			System.out.println("Please provide House Number and Street:");
			street = CommandLine.sc.nextLine();
			String query_select = "SELECT list_ID, host_user_name, list_latitude, list_longitude, Country, Province, City, house_number_and_Street, list_type, post_code, kitchen,heating,washer, wifi, indoor_fireplace, iron, Laptop_friendly_workspace, crib, self_check_in, carbon_monoxide_detector, shampoo, air_conditioning, dryer, breakfast, hangers, hair_dryer, TV, hight_chair, smoke_detector, private_bathroom, SUM(price) AS total_price FROM listing NATURAL JOIN amenities NATURAL JOIN lists_calendar WHERE Country = '" + country + "' AND Province = '" + province + "' AND City = '" + city + "' AND house_number_and_street = '" + street + "' AND available = 'a' AND date >= '" + start_date + "' AND date <= '" + end_date + "' GROUP BY list_ID HAVING COUNT(*) = '" + day_lengths + "'";
			query = query_select.concat(";");
			rs = sqlMngr.selectOp(query);
			result = sqlMngr.rsToList(rs);
			//case where the listing is not found with the given address
			//either ask user to re-enter the address or back to search menu
			if(result.isEmpty()) {
				System.out.println("Sorry, the address you are looking for does not exist.");
				do {	// DO WHILE verify user's choice //
					valid_choice = true;
					System.out.println("Please enter e if you want to re-enter address to continue, or enter r to select another search method:");
					choice = CommandLine.sc.nextLine();
					// case where user choose to re-enter the address
					if (choice.equalsIgnoreCase("e")) {
						re_enter = true;
					} else if (choice.equalsIgnoreCase("r")) {// case where user choose to select a new search method
						terminate = true;
						return terminate;
					} else {
						valid_choice = false;
					}
				} while(!valid_choice);// DO WHILE END verify user's choice //
			} else {	// Case where the listing is not empty
				query = "CREATE TABLE address_listing AS (" + query_select + ");";
				sqlMngr.createTempTable(query);
				rs = sqlMngr.selectOp("SELECT * FROM address_listing;");
				sqlMngr.printRecord(rs);

				/****** Ask user input list_ID ******/
				boolean allow_choose_again;
				boolean table_exist = false;	//table: curr_listing
				boolean check_p = false; boolean check_f = false; 
				do {// DO WHILE verify user's choice //
					valid_choice = true;
					allow_choose_again = false;
					System.out.println("You can sort the list by price by entering a: ascending, or d: descending.");
					System.out.println("Please enter p if you want to filter by price range, enter f if you want to filter by amenities, enter s if you want to re-set filtering");
					System.out.println("Please enter c if you want to checkout a listing, enter e if you want to re-enter the address, or enter r to select another search method:");
					choice = CommandLine.sc.nextLine();
					// Operate query on the newly created table address_listing
					if (choice.equalsIgnoreCase("a")) {
						allow_choose_again = true;
						if (table_exist) {
							query = "CREATE TABLE temp AS (SELECT * FROM curr_listing ORDER BY total_price);";
							updateTempTable(query, table_exist);
						} else {
							query = "CREATE TABLE curr_listing AS (SELECT * FROM address_listing ORDER BY total_price);";
							updateTempTable(query, table_exist);
							table_exist = true;
						}
					} else if (choice.equalsIgnoreCase("d")) {
						allow_choose_again = true;
						if (table_exist) {
							query = "CREATE TABLE temp AS (SELECT * FROM curr_listing ORDER BY total_price DESC);";
							updateTempTable(query, table_exist);
						} else {
							query = "CREATE TABLE curr_listing AS (SELECT * FROM address_listing ORDER BY total_price DESC);";
							updateTempTable(query, table_exist);
							table_exist = true;
						}
					} else if (choice.equalsIgnoreCase("p") && !check_p) {
						check_p = true;
						allow_choose_again = true;
						boolean valid_price;
						do {
							valid_price = true;
							System.out.println("Please set the lowest price:");
							String price_low = CommandLine.sc.nextLine();
							System.out.println("Please set the highest price:");
							String price_high = CommandLine.sc.nextLine();
							if (price_low.compareTo(price_high) > 0) {
								valid_price = false;
								System.out.println("Please enter a valid price range");
								continue;
							}
							if (table_exist) {
								query = "CREATE TABLE temp AS (SELECT * FROM curr_listing WHERE total_price >= '" + price_low + "' AND total_price <= '" + price_high + "');";
								updateTempTable(query, table_exist);
							} else {
								query = "CREATE TABLE curr_listing AS (SELECT * FROM address_listing WHERE total_price >= '" + price_low + "' AND total_price <= '" + price_high + "');";
								updateTempTable(query, table_exist);
								table_exist = true;
							}
						} while (!valid_price);
					} else if (choice.equalsIgnoreCase("f") && !check_f) {
						boolean valid_amenities;
						allow_choose_again = true;
						check_f = true;
						do {
							valid_amenities = true;
							System.out.println();
							System.out.println("Please provide amenities included above you are looking for(separate by space):");
							String input = CommandLine.sc.nextLine();
							String[] amenities = input.split(" ");
							ArrayList<String> list = new ArrayList<String>(Arrays.asList(this.amenities_column_name));
							String query_table_exist = "CREATE TABLE temp AS (SELECT * FROM curr_listing WHERE";
							String query_table_nexist = "CREATE TABLE curr_listing AS(SELECT * FROM address_listing WHERE";
							boolean check_first = true;
							for (String a : amenities) {
								// user input should match column names in amenities table, except for list_ID
								if (!list.contains(a) || a.equals("list_ID")) {
									valid_amenities = false;
									break;
								} else {
									// forming the query
									if (check_first) {
										check_first = false;
										query_table_exist += " " + a + " = 1";
										query_table_nexist += " " + a + " = 1";
									} else {
										query_table_exist += " AND " + a + " = 1";
										query_table_nexist += " AND " + a + " = 1";
									}
								}
							}
							if (valid_amenities) {
								query_table_exist += ");";
								query_table_nexist += ");";
								System.out.println("==="+query_table_exist+"===");
								if (table_exist) {
									query = query_table_exist;
									this.updateTempTable(query, table_exist);
								} else {
									query = query_table_nexist;
									this.updateTempTable(query, table_exist);
									table_exist = true;
								}
							}
						} while(!valid_amenities);
					} else if (choice.equalsIgnoreCase("s")) {
						allow_choose_again = true;
						check_f = false; check_p = false;
						if (table_exist) {
							sqlMngr.dropTempTable("DROP TABLE curr_listing");
							table_exist = false;
						}
						query = "SELECT * FROM address_listing;";
						sqlMngr.printRecord(sqlMngr.selectOp(query));
					} else if (choice.equalsIgnoreCase("c")) {	
					} else if (choice.equalsIgnoreCase("e")) {
						re_enter = true;
						if (table_exist) {
							sqlMngr.dropTempTable("DROP TABLE curr_listing");
							table_exist = false;
						}
						sqlMngr.dropTempTable("DROP TABLE address_listing");
						break;
					} else if (choice.equalsIgnoreCase("r")) {
						if (table_exist) {
						sqlMngr.dropTempTable("DROP TABLE curr_listing");
						table_exist = false;							
						}
						sqlMngr.dropTempTable("DROP TABLE address_listing");
						terminate = true;
						return terminate;
					} else {
						valid_choice = false;
					}
					// Checkout procedure: insert into Booking and Update List Calendar
					boolean valid_list_id;
					if (choice.equalsIgnoreCase("c")) {
						ArrayList<String> values=null;
						do {	// DO WHILE verify listing id //
							valid_list_id = true;
							System.out.println("Please enter a listing id listed above to checkout");
							list_ID = CommandLine.sc.nextLine();
							if (table_exist) {
								query = "SELECT * FROM curr_listing WHERE list_ID = '" + list_ID + "';";
							} else {
								query = "SELECT * FROM address_listing WHERE list_ID = '" + list_ID + "';";
							}
							rs = sqlMngr.selectOp(query);
							result = sqlMngr.rsToList(rs);
							if (!result.isEmpty()) {
								values = result.get(0);
								if(table_exist) {
									sqlMngr.dropTempTable("DROP TABLE curr_listing;");
									table_exist = false;
								}					
								sqlMngr.dropTempTable("DROP TABLE address_listing;");
							}
						} while(!valid_list_id);// DO WHILE END verify listing id //
						/*** INSERT BOOKING ***/
						// "list_ID", "host_user_name", "renter_user_name", "start_date", "end_date"
						// values[0], values[1], username, start_date, end_date
					
						String column_names[] = {"list_ID", "host_user_name", "renter_user_name", "start_date", "end_date"};
						String column_values[] = {values.get(0), values.get(1), username, start_date, end_date};
						//sqlMngr.insertOp("booking", column_names, column_values);
						//query = "INSERT INTO booking(list_ID, host_user_name, renter_user_name, start_date, end_date) VALUES ('"+values.get(0)+"', '"+values.get(1)+"', '"+username+"', '"+start_date+"', '"+end_date+"');";
						//int booking_ID = sqlMngr.insertGetID(query);
						int booking_ID = sqlMngr.insertOp("booking", column_names, column_values, true);
						/*** UPDATE LIST CALENDAR ***/
						// "date", "list_ID", "available"
						// start_date -> end_date, list_ID, b
						query = "SELECT date FROM lists_calendar WHERE list_ID = '" + list_ID + "' AND date >= '" + start_date + "' AND date <= '" + end_date + "';";
						rs = sqlMngr.selectOp(query);
						result = sqlMngr.rsToList(rs);
						for (int i = 0; i < day_lengths; i++) {
							String date = result.get(i).get(0);
							query = "UPDATE lists_calendar SET available = 'b' WHERE list_ID = '" + list_ID + "' AND date = '" + date + "';";
							sqlMngr.updateOp(query);
							System.out.println("==="+query+"===");
						}
						System.out.println("You've successfully checked out. Your booking id is: " + booking_ID);
						System.out.println("===aca:"+allow_choose_again+"===");
						System.out.println("===!vc:"+!valid_choice+"===");
					}
				} while(!valid_choice || allow_choose_again);// DO WHILE END verify user's choice //
			}
		} while(re_enter);// DO WHILE END enter address //
		return terminate;
	}

	private boolean getListingByPostCode(String start_date, String end_date, long day_lengths, String username) {
		boolean terminate = false;
		boolean re_enter;
		ResultSet rs;
		ArrayList<ArrayList<String>> result;
		String choice;
		boolean valid_choice;
		String list_ID;
		String query;
		String country; String post_code;
		/****** Ask User Input Address ******/
		do {	// DO WHILE enter post code //
			re_enter = false;
			System.out.println("Please provide the Country:");
			country = CommandLine.sc.nextLine();
			System.out.println("Please provide the Post Code(no whitespace):");
			post_code = CommandLine.sc.nextLine();
			if (post_code.length() != 6) {
				System.out.println("Please provide a valid postal code.");
				re_enter = true;
				continue;
			}
			String sub_pc = post_code.substring(0, 3);
			// post code should has six digit in total
			String query_select = "SELECT list_ID, host_user_name, list_latitude, list_longitude, Country, Province, City, house_number_and_Street, list_type, post_code, kitchen,heating,washer, wifi, indoor_fireplace, iron, Laptop_friendly_workspace, crib, self_check_in, carbon_monoxide_detector, shampoo, air_conditioning, dryer, breakfast, hangers, hair_dryer, TV, hight_chair, smoke_detector, private_bathroom, SUM(price) AS total_price FROM listing NATURAL JOIN amenities NATURAL JOIN lists_calendar WHERE Country = '" + country + "' AND post_code LIKE '" + sub_pc + "___" + "' AND available = 'a' AND date >= '" + start_date + "' AND date <= '" + end_date + "' GROUP BY list_ID HAVING COUNT(*) = '" + day_lengths + "'";
			query = query_select.concat(";");
			rs = sqlMngr.selectOp(query);
			result = sqlMngr.rsToList(rs);
			//case where the listing is not found with the given address
			//either ask user to re-enter the address or back to search menu
			if(result.isEmpty()) {
				System.out.println("Sorry, the postal code you are looking for does not exist.");
				do {	// DO WHILE verify user's choice //
					valid_choice = true;
					System.out.println("Please enter e if you want to re-enter post code to continue, or enter r to select another search method:");
					choice = CommandLine.sc.nextLine();
					// case where user choose to re-enter the address
					if (choice.equalsIgnoreCase("e")) {
						re_enter = true;
					} else if (choice.equalsIgnoreCase("r")) {// case where user choose to select a new search method
						terminate = true;
						return terminate;
					} else {
						valid_choice = false;
					}
				} while(!valid_choice);// DO WHILE END verify user's choice //
			} else {	// Case where the listing is not empty
				query = "CREATE TABLE address_listing AS (" + query_select + ");";
				sqlMngr.createTempTable(query);
				rs = sqlMngr.selectOp("SELECT * FROM address_listing;");
				sqlMngr.printRecord(rs);

				/****** Ask user input list_ID ******/
				boolean allow_choose_again;
				boolean table_exist = false;	//table: curr_listing
				boolean check_p = false; boolean check_f = false;
				do {// DO WHILE verify user's choice //
					valid_choice = true;
					allow_choose_again = false;
					System.out.println("You can sort the list by price by entering a: ascending, or d: descending.");
					System.out.println("Please enter p if you want to filter by price range, enter f if you want to filter by amenities, enter s if you want to re-set filtering");
					System.out.println("Please enter c if you want to checkout a listing, enter e if you want to re-enter the postal code, or enter r to select another search method:");
					choice = CommandLine.sc.nextLine();

					// Operate query on the newly created table address_listing
					if (choice.equalsIgnoreCase("a")) {
						allow_choose_again = true;
						if (table_exist) {
							query = "CREATE TABLE temp AS (SELECT * FROM curr_listing ORDER BY total_price);";
							updateTempTable(query, table_exist);
						} else {
							query = "CREATE TABLE curr_listing AS (SELECT * FROM address_listing ORDER BY total_price);";
							updateTempTable(query, table_exist);
							table_exist = true;
						}
					} else if (choice.equalsIgnoreCase("d")) {
						allow_choose_again = true;
						if (table_exist) {
							query = "CREATE TABLE temp AS (SELECT * FROM curr_listing ORDER BY total_price DESC);";
							updateTempTable(query, table_exist);
						} else {
							query = "CREATE TABLE curr_listing AS (SELECT * FROM address_listing ORDER BY total_price DESC);";
							updateTempTable(query, table_exist);
							table_exist = true;
						}
					} else if (choice.equalsIgnoreCase("p") && !check_p) {
						check_p = true;
						allow_choose_again = true;
						boolean valid_price;
						do {
							valid_price = true;
							System.out.println("Please set the lowest price:");
							String price_low = CommandLine.sc.nextLine();
							System.out.println("Please set the highest price:");
							String price_high = CommandLine.sc.nextLine();
							if (price_low.compareTo(price_high) > 0) {
								valid_price = false;
								System.out.println("Please enter a valid price range");
								continue;
							}
							if (table_exist) {
								query = "CREATE TABLE temp AS (SELECT * FROM curr_listing WHERE total_price >= '" + price_low + "' AND total_price <= '" + price_high + "');";
								updateTempTable(query, table_exist);
							} else {
								query = "CREATE TABLE curr_listing AS (SELECT * FROM address_listing WHERE total_price >= '" + price_low + "' AND total_price <= '" + price_high + "');";
								updateTempTable(query, table_exist);
								table_exist = true;
							}
						} while (!valid_price);
					} else if (choice.equalsIgnoreCase("f") && !check_f) {
						boolean valid_amenities;
						allow_choose_again = true;
						check_f = true;
						do {
							valid_amenities = true;
							System.out.println();
							System.out.println("Please provide amenities included above you are looking for(separate by space):");
							String input = CommandLine.sc.nextLine();
							String[] amenities = input.split(" ");
							ArrayList<String> list = new ArrayList<String>(Arrays.asList(this.amenities_column_name));
							String query_table_exist = "CREATE TABLE temp AS (SELECT * FROM curr_listing WHERE";
							String query_table_nexist = "CREATE TABLE curr_listing AS(SELECT * FROM address_listing WHERE";
							boolean check_first = true;
							for (String a : amenities) {
								// user input should match column names in amenities table, except for list_ID
								if (!list.contains(a) || a.equals("list_ID")) {
									valid_amenities = false;
									break;
								} else {
									// forming the query
									if (check_first) {
										check_first = false;
										query_table_exist += " " + a + " = 1";
										query_table_nexist += " " + a + " = 1";
									} else {
										query_table_exist += " AND " + a + " = 1";
										query_table_nexist += " AND " + a + " = 1";
									}
								}
							}
							if (valid_amenities) {
								query_table_exist += ");";
								query_table_nexist += ");";
								System.out.println("==="+query_table_exist+"===");
								if (table_exist) {
									query = query_table_exist;
									this.updateTempTable(query, table_exist);
								} else {
									query = query_table_nexist;
									this.updateTempTable(query, table_exist);
									table_exist = true;
								}
							}
						} while(!valid_amenities);
					} else if (choice.equalsIgnoreCase("s")) {
						allow_choose_again = true;
						check_f = false; check_p = false;
						if (table_exist) {
							sqlMngr.dropTempTable("DROP TABLE curr_listing");
							table_exist = false;
						}
						query = "SELECT * FROM address_listing;";
						sqlMngr.printRecord(sqlMngr.selectOp(query));
					} else if (choice.equalsIgnoreCase("c")) {	
					} else if (choice.equalsIgnoreCase("e")) {
						re_enter = true;
						if (table_exist) {
							sqlMngr.dropTempTable("DROP TABLE curr_listing");
							table_exist = false;
						}
						break;
					} if (choice.equalsIgnoreCase("r")) {
						if (table_exist) {
							sqlMngr.dropTempTable("DROP TABLE curr_listing");
							table_exist = false;							
							}
							sqlMngr.dropTempTable("DROP TABLE address_listing");
							terminate = true;
							return terminate;
						} else {
							valid_choice = false;
						}
					// Checkout procedure: insert into Booking and Update List Calendar
					boolean valid_list_id;
					if (choice.equalsIgnoreCase("c")) {
						ArrayList<String> values=null;
						do {	// DO WHILE verify listing id //
							valid_list_id = true;
							System.out.println("Please enter a listing id listed above to checkout");
							list_ID = CommandLine.sc.nextLine();
							if (table_exist) {
								query = "SELECT * FROM curr_listing WHERE list_ID = '" + list_ID + "';";
							} else {
								query = "SELECT * FROM address_listing WHERE list_ID = '" + list_ID + "';";
							}
							rs = sqlMngr.selectOp(query);
							result = sqlMngr.rsToList(rs);
							if (!result.isEmpty()) {
								values = result.get(0);
								if(table_exist) {
									sqlMngr.dropTempTable("DROP TABLE curr_listing;");
									table_exist = false;
								}					
								sqlMngr.dropTempTable("DROP TABLE address_listing;");
							}
						} while(!valid_list_id);// DO WHILE END verify listing id //
						/*** INSERT BOOKING ***/
						// "list_ID", "host_user_name", "renter_user_name", "start_date", "end_date"
						// values[0], values[1], username, start_date, end_date
					
						String column_names[] = {"list_ID", "host_user_name", "renter_user_name", "start_date", "end_date"};
						String column_values[] = {values.get(0), values.get(1), username, start_date, end_date};
						//sqlMngr.insertOp("booking", column_names, column_values);
						//query = "INSERT INTO booking(list_ID, host_user_name, renter_user_name, start_date, end_date) VALUES ('"+values.get(0)+"', '"+values.get(1)+"', '"+username+"', '"+start_date+"', '"+end_date+"');";
						//int booking_ID = sqlMngr.insertGetID(query);
						int booking_ID = sqlMngr.insertOp("booking", column_names, column_values, true);
						/*** UPDATE LIST CALENDAR ***/
						// "date", "list_ID", "available"
						// start_date -> end_date, list_ID, b
						query = "SELECT date FROM lists_calendar WHERE list_ID = '" + list_ID + "' AND date >= '" + start_date + "' AND date <= '" + end_date + "';";
						rs = sqlMngr.selectOp(query);
						result = sqlMngr.rsToList(rs);
						for (int i = 0; i < day_lengths; i++) {
							String date = result.get(i).get(0);
							query = "UPDATE lists_calendar SET available = 'b' WHERE list_ID = '" + list_ID + "' AND date = '" + date + "';";
							sqlMngr.updateOp(query);
							System.out.println("==="+query+"===");
						}
						System.out.println("You've successfully checked out. Your booking id is: " + booking_ID);
						System.out.println("===aca:"+allow_choose_again+"===");
						System.out.println("===!vc:"+!valid_choice+"===");
					}
				} while(!valid_choice || allow_choose_again);// DO WHILE END verify user's choice //
			}
		} while(re_enter);// DO WHILE END enter address //
		return terminate;
	}

	private boolean getListingByGeoCoord(String start_date, String end_date, long day_lengths, String username) {
		boolean terminate = false;
		boolean re_enter;
		boolean increase_distance;
		ResultSet rs;
		ArrayList<ArrayList<String>> result;
		String choice;
		boolean valid_choice;
		String list_ID;
		String query;
		String lat; String lon; String distance;
		/****** Ask User Input Address ******/
		do {	// DO WHILE enter address //
			re_enter = false;
			System.out.println("Please provide the latitude:");
			lat = CommandLine.sc.nextLine();
			System.out.println("Please provide the longitude:");
			lon = CommandLine.sc.nextLine();
			boolean listing_exist = false;
			do {// DO WHILE enter distance //
				increase_distance = false;
				System.out.println("Please provide the maximum distance(KM):");
				distance = CommandLine.sc.nextLine();
				String query_select = "SELECT list_ID, host_user_name, list_latitude, list_longitude, Country, Province, City, house_number_and_Street, list_type, post_code, kitchen,heating,washer, wifi, indoor_fireplace, iron, Laptop_friendly_workspace, crib, self_check_in, carbon_monoxide_detector, shampoo, air_conditioning, dryer, breakfast, hangers, hair_dryer, TV, hight_chair, smoke_detector, private_bathroom, SUM(price) AS total_price, 111.111 * DEGREES(ACOS(LEAST(COS(RADIANS(list_latitude)) * COS(RADIANS('"+lat+"')) * COS(RADIANS(list_longitude - '"+lon+"')) + SIN(RADIANS(list_latitude))	* SIN(RADIANS('"+lat+"')), 1.0))) AS distance FROM listing NATURAL JOIN amenities NATURAL JOIN lists_calendar WHERE available = 'a' AND date >= '"+start_date+"' AND date <= '"+end_date+"' GROUP BY list_ID HAVING COUNT(*) = '" + day_lengths + "' AND distance <= '" + distance + "'";
				query = query_select.concat(";");
				rs = sqlMngr.selectOp(query);
				result = sqlMngr.rsToList(rs);
				//case where the listing is not found with the given address
				//either ask user to re-enter coordinate or back to search menu
				if(result.isEmpty()) {
					System.out.println("Sorry, the address you are looking for does not exist.");
					do {	// DO WHILE verify user's choice //
						valid_choice = true;
						System.out.println("Please enter i if you want to increase the distance, enter e if you want to re-enter coordinate to continue, or enter r to select another search method:");
						choice = CommandLine.sc.nextLine();
						// case where user choose to re-enter the address
						if (choice.equalsIgnoreCase("i")) {
							increase_distance = true;
							continue;
						} else if (choice.equalsIgnoreCase("e")) {
							re_enter = true;
							continue;
						} else if (choice.equalsIgnoreCase("r")) {// case where user choose to select a new search method
							terminate = true;
							return terminate;
						} else {
							valid_choice = false;
						}
					} while(!valid_choice);// DO WHILE END verify user's choice //
				} else {	// Case where the listing is not empty
					if (listing_exist) {
						sqlMngr.dropTempTable("DROP TABLE address_listing;");
					} else {listing_exist = true;}
					query = "CREATE TABLE address_listing AS (" + query_select + ");";
					sqlMngr.createTempTable(query);
					rs = sqlMngr.selectOp("SELECT * FROM address_listing;");
					sqlMngr.printRecord(rs);

					/****** Ask user input list_ID ******/
					boolean allow_choose_again;
					boolean table_exist = false;	//table: curr_listing
					boolean check_p = false; boolean check_f = false;
					do {// DO WHILE verify user's choice //
						valid_choice = true;
						allow_choose_again = false;
						System.out.println("You can sort the list by price by entering a: ascending, or d: descending.");
						System.out.println("You can also sort the list by distance by entering ad: ascending, or dd: descending.");
						System.out.println("Please enter i if you want to increase the distance, or enter p if you want to filter by price range, enter f if you want to filter by amenities, enter s if you want to re-set filtering");
						System.out.println("Please enter c if you want to checkout a listing, enter e if you want to re-enter the coordinate and distance, or enter r to return to select search method:");
						choice = CommandLine.sc.nextLine();
						// Operate query on the newly created table address_listing
						if (choice.equalsIgnoreCase("a")) {
							allow_choose_again = true;
							if (table_exist) {
								query = "CREATE TABLE temp AS (SELECT * FROM curr_listing ORDER BY total_price);";
								updateTempTable(query, table_exist);
							} else {
								query = "CREATE TABLE curr_listing AS (SELECT * FROM address_listing ORDER BY total_price);";
								updateTempTable(query, table_exist);
								table_exist = true;
							}
						} else if (choice.equalsIgnoreCase("d")) {
							allow_choose_again = true;
							if (table_exist) {
								query = "CREATE TABLE temp AS (SELECT * FROM curr_listing ORDER BY total_price DESC);";
								updateTempTable(query, table_exist);
							} else {
								query = "CREATE TABLE curr_listing AS (SELECT * FROM address_listing ORDER BY total_price DESC);";
								updateTempTable(query, table_exist);
								table_exist = true;
							}
						} else if (choice.equalsIgnoreCase("ad")) {
							allow_choose_again = true;
							if (table_exist) {
								query = "CREATE TABLE temp AS (SELECT * FROM curr_listing ORDER BY distance);";
								updateTempTable(query, table_exist);
							} else {
								query = "CREATE TABLE curr_listing AS (SELECT * FROM address_listing ORDER BY distance);";
								updateTempTable(query, table_exist);
								table_exist = true;
							}
						} else if (choice.equalsIgnoreCase("dd")) {
							allow_choose_again = true;
							if (table_exist) {
								query = "CREATE TABLE temp AS (SELECT * FROM curr_listing ORDER BY distance DESC);";
								updateTempTable(query, table_exist);
							} else {
								query = "CREATE TABLE curr_listing AS (SELECT * FROM address_listing ORDER BY distance DESC);";
								updateTempTable(query, table_exist);
								table_exist = true;
							}
						} else if (choice.equalsIgnoreCase("p") && !check_p) {
							check_p = true;
							allow_choose_again = true;
							boolean valid_price;
							do {
								valid_price = true;
								System.out.println("Please set the lowest price:");
								String price_low = CommandLine.sc.nextLine();
								System.out.println("Please set the highest price:");
								String price_high = CommandLine.sc.nextLine();
								if (price_low.compareTo(price_high) > 0) {
									valid_price = false;
									System.out.println("Please enter a valid price range");
									continue;
								}
								if (table_exist) {
									query = "CREATE TABLE temp AS (SELECT * FROM curr_listing WHERE total_price >= '" + price_low + "' AND total_price <= '" + price_high + "');";
									updateTempTable(query, table_exist);
								} else {
									query = "CREATE TABLE curr_listing AS (SELECT * FROM address_listing WHERE total_price >= '" + price_low + "' AND total_price <= '" + price_high + "');";
									updateTempTable(query, table_exist);
									table_exist = true;
								}
							} while (!valid_price);
						} else if (choice.equalsIgnoreCase("f") && !check_f) {
							boolean valid_amenities;
							allow_choose_again = true;
							check_f = true;
							do {
								valid_amenities = true;
								System.out.println();
								System.out.println("Please provide amenities included above you are looking for(separate by space):");
								String input = CommandLine.sc.nextLine();
								String[] amenities = input.split(" ");
								ArrayList<String> list = new ArrayList<String>(Arrays.asList(this.amenities_column_name));
								String query_table_exist = "CREATE TABLE temp AS (SELECT * FROM curr_listing WHERE";
								String query_table_nexist = "CREATE TABLE curr_listing AS(SELECT * FROM address_listing WHERE";
								boolean check_first = true;
								for (String a : amenities) {
									// user input should match column names in amenities table, except for list_ID
									if (!list.contains(a) || a.equals("list_ID")) {
										valid_amenities = false;
										break;
									} else {
										// forming the query
										if (check_first) {
											check_first = false;
											query_table_exist += " " + a + " = 1";
											query_table_nexist += " " + a + " = 1";
										} else {
											query_table_exist += " AND " + a + " = 1";
											query_table_nexist += " AND " + a + " = 1";
										}
									}
								}
								if (valid_amenities) {
									query_table_exist += ");";
									query_table_nexist += ");";
									System.out.println("==="+query_table_exist+"===");
									if (table_exist) {
										query = query_table_exist;
										this.updateTempTable(query, table_exist);
									} else {
										query = query_table_nexist;
										this.updateTempTable(query, table_exist);
										table_exist = true;
									}
								}
							} while(!valid_amenities);
						} else if (choice.equalsIgnoreCase("s") || choice.equalsIgnoreCase("i")) {
							check_f = false; check_p = false;
							if (table_exist) {
								sqlMngr.dropTempTable("DROP TABLE curr_listing");
								table_exist = false;
							}
							if (choice.equalsIgnoreCase("i")) {
								increase_distance = true;
								continue;
							} else {
								allow_choose_again = true;
							}
							query = "SELECT * FROM address_listing;";
							sqlMngr.printRecord(sqlMngr.selectOp(query));
						} else if (choice.equalsIgnoreCase("c")) {	
						} else if (choice.equalsIgnoreCase("e")) {
							re_enter = true;
							if (table_exist) {
								sqlMngr.dropTempTable("DROP TABLE curr_listing");
								table_exist = false;
							}
							sqlMngr.dropTempTable("DROP TABLE address_listing");
							break;
						} else if (choice.equalsIgnoreCase("r")) {
							if (table_exist) {
							sqlMngr.dropTempTable("DROP TABLE curr_listing");
							table_exist = false;				
							}
							sqlMngr.dropTempTable("DROP TABLE address_listing");
							terminate = true;
							return terminate;
						} else {
							valid_choice = false;
						}
						// Checkout procedure: insert into Booking and Update List Calendar
						boolean valid_list_id;
						if (choice.equalsIgnoreCase("c")) {
							ArrayList<String> values=null;
							do {	// DO WHILE verify listing id //
								valid_list_id = true;
								System.out.println("Please enter a listing id listed above to checkout");
								list_ID = CommandLine.sc.nextLine();
								if (table_exist) {
									query = "SELECT * FROM curr_listing WHERE list_ID = '" + list_ID + "';";
								} else {
									query = "SELECT * FROM address_listing WHERE list_ID = '" + list_ID + "';";
								}
								rs = sqlMngr.selectOp(query);
								result = sqlMngr.rsToList(rs);
								if (!result.isEmpty()) {
									values = result.get(0);
									if(table_exist) {
										sqlMngr.dropTempTable("DROP TABLE curr_listing;");
										table_exist = false;
									}					
									sqlMngr.dropTempTable("DROP TABLE address_listing;");
								}
							} while(!valid_list_id);// DO WHILE END verify listing id //
							/*** INSERT BOOKING ***/
							// "list_ID", "host_user_name", "renter_user_name", "start_date", "end_date"
							// values[0], values[1], username, start_date, end_date
						
							
							String column_names[] = {"list_ID", "host_user_name", "renter_user_name", "start_date", "end_date"};
							String column_values[] = {values.get(0), values.get(1), username, start_date, end_date};
							//sqlMngr.insertOp("booking", column_names, column_values);
							//query = "INSERT INTO booking(list_ID, host_user_name, renter_user_name, start_date, end_date) VALUES ('"+values.get(0)+"', '"+values.get(1)+"', '"+username+"', '"+start_date+"', '"+end_date+"');";
							//int booking_ID = sqlMngr.insertGetID(query);
							int booking_ID = sqlMngr.insertOp("booking", column_names, column_values, true);
							/*** UPDATE LIST CALENDAR ***/
							// "date", "list_ID", "available"
							// start_date -> end_date, list_ID, b
							query = "SELECT date FROM lists_calendar WHERE list_ID = '" + list_ID + "' AND date >= '" + start_date + "' AND date <= '" + end_date + "';";
							rs = sqlMngr.selectOp(query);
							result = sqlMngr.rsToList(rs);
							for (int i = 0; i < day_lengths; i++) {
								String date = result.get(i).get(0);
								query = "UPDATE lists_calendar SET available = 'b' WHERE list_ID = '" + list_ID + "' AND date = '" + date + "';";
								sqlMngr.updateOp(query);
								System.out.println("==="+query+"===");
							}
							System.out.println("You've successfully checked out. Your booking id is: " + booking_ID);
							System.out.println("===aca:"+allow_choose_again+"===");
							System.out.println("===!vc:"+!valid_choice+"===");
						}
					} while(!valid_choice || allow_choose_again);// DO WHILE END verify user's choice //
				}
			} while(increase_distance);
		} while(re_enter);// DO WHILE END enter address //
		return terminate;
	}


	private void updateTempTable(String query, boolean exist) {
		if (!exist) {
			sqlMngr.createTempTable(query);
		} else {
			sqlMngr.createTempTable(query);
			sqlMngr.dropTempTable("DROP TABLE curr_listing");
			query = "CREATE TABLE curr_listing AS (SELECT * FROM temp);";
			sqlMngr.createTempTable(query);
			sqlMngr.dropTempTable("DROP TABLE temp");
		}
		query = "SELECT * FROM curr_listing;";
		ResultSet rs = sqlMngr.selectOp(query);
		sqlMngr.printRecord(rs);
	}


}


