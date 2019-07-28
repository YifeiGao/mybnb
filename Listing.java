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
	private static final String[] listing_column_type = { "INT NOT NULL AUTO_INCREMENT", "DECIMAL(10, 8) NOT NULL", "DECIMAL(11, 8) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL","VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL","VARCHAR(30) NOT NULL"};
	private static final String listing_primary_key = "list ID";
	private static final String[] listing_column_name_for_insert = { "list_latitude", "list_longitude", "Country", "Province", "City", "house_number_and_Street","host_user_name", "list_type"};

	private static final String[] amenites_column_name = {"list_ID", "kitchen","heating","washer", "wifi", "indoor_fireplace", "iron", "Laptop-friendly_workspace", "crib", "self_check_in", "carbon_monoxide_detector", "shampoo", "air_conditioning", "dryer", "breakfast", "hangers", "hair_dryer", "TV", "hight_chair", "smoke_detector", "private_bathroom"};
	private static final String[] amenites_column_type = {"INT NOT NULL", "TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)"};
	private static final String amenites_primary_key = "list_ID";

	public static SQLController sqlMngr = new SQLController();
	public Listing(){
		try {
			sqlMngr.connect();
		} catch (ClassNotFoundException e) {
			System.err.println("Esception occurs in Listin.constructor");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int addListing(){
		int choice;
		int listing_ID = -100000;
		ArrayList<String> a = new ArrayList<String>(listing_column_name.length);
		String[] column_values = new String[listing_column_name.length];
		String[] amenites = new String [amenites_column_name.length];
		boolean check;
		do{
			this.listTypeMenu();
			choice = Integer.parseInt(CommandLine.sc.nextLine());
			check = CommandLine.checkInRange(choice, 0, column_values.length - 1);
			if(!check){
				System.out.println("Please choose a type that provided");
			}
		}while(!check);
		String type = listing_type[choice];
		a = CommandLine.getInfo(listing_column_name, 1, 8);
		a.add(type);
		column_values = a.toArray(new String[0]);;
		sqlMngr.insertOp("listing", listing_column_name, column_values);
		String get_listing_ID = "SELECT LAST_INSERT_ID() ;";
		ResultSet rs = sqlMngr.excuteSql(get_listing_ID);
		try{
			rs.absolute(1);
			listing_ID = rs.getInt("list_ID");
		}catch(SQLException e){
			System.err.println("Exception occurs in Listing.addLising");
			e.printStackTrace();
		}
		sqlMngr.insertOp("amenities",amenites_column_name, this.getAmenities(listing_ID));
		System.out.println("Listing added successfully, the listing ID of the listing is "+ listing_ID);
		return listing_ID;

	}

	private String[] getAmenities(int listing_ID){
		String[] amenities = new String[amenites_column_name.length];
		amenities[0] = Integer.toString(listing_ID);
		String t_or_f;
		System.out.println("Flowings are amenites, please type 'T' if the listing you add have the amenity, otherwise type'F'");
		for(int i = 1; i < amenites_column_name.length; i++ ){
			do{
				System.out.println("Do you have " + amenites_column_name[i] + " in your listing? (T/F)");
				t_or_f =  CommandLine.sc.nextLine();
			}while(! (t_or_f.equalsIgnoreCase("t") ||t_or_f.equalsIgnoreCase("f")));
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
		float price;
		String get_ave = "SELECT AVE(price) FROM lists_calendar WHERE (SELECT list_ID FROM listing l WHERE l. "
	}

	public boolean ownList(int list_ID, String host) throws SQLException{
		String sql = "SELECT * FROM listins WHERE listing_id = '"+list_ID+"' and host_user_name = '"+host+"';";
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
		String query = "SELECT list ID, list latitude, list longitude, Country, Province, City, house number and Street, list type, kitchen, heating, washer, wifi, indoor fireplace, iron, Laptop-friendly workspace, crib, self check_in, carbon monoxide detector, shampoo, air conditioning, dryer, breakfast, hangers, hair dryer, TV, hight chair, smoke detector, private bathroom FROM listing NATURAL JOIN amenities WHERE host user name = '" +user_id+ "';";
		ResultSet resultSet = sqlMngr.selectOp(query);
		System.out.println("Here is all the listings under your account.");
		sqlMngr.printRecord(resultSet);
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
			boolean re_select;
			do {	//DO WHILE user input date //
				valid_date = true;
				System.out.println("Please Enter your start date: ");
				start_date = CommandLine.sc.nextLine();
				System.out.println("Please Enter your end date: ");
				end_date = CommandLine.sc.nextLine();
				/***** Verify date *****/
				ArrayList<ArrayList<String>> result;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'");
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
				Date end = null;
				Date start = null;
				try {
					start = sdf.parse(start_date);
					end = sdf.parse(end_date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long difference_milies = start.getTime() - end.getTime();
				// start date cannot be later than end date
				if (difference_milies < 0) {
					valid_date = false;
					continue;
				}
				// length of the day including the first day
				day_lengths = TimeUnit.DAYS.convert(difference_milies, TimeUnit.DAYS) + 1;
				query = "SELECT listing_id, SUM(price) as total_price FROM list_calendar WHERE available = a AND date >= '" + start_date + "' AND date <= '" + end_date + "' GROUP BY listing_id HAVING COUNT(*) = '" + day_lengths + "';";
				ResultSet rs = CommandLine.sqlMngr.selectOp(query);
				result = CommandLine.sqlMngr.rsToList(rs);
				/***** End of verify date *****/
				valid_date = !result.isEmpty();
				if (!valid_date) {
					System.out.println("Please provide a valid start date and end date.");
				} else {	//date valid, ask for address input
					System.out.println("1. Enter address to locate the listing");
					System.out.println("2. Enter geographic coordinate and distance to look for nearby listings");
					System.out.println("3. Enter postal code to find nearby listings");
					do {	// DO WHILE verify user's choice //
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
							valid_date = false;
							break;
						} else {
							valid_choice = false;
						}
					} while(!valid_choice || re_select);// DO WHILE verify user's choice//
				}
			} while(!valid_date);//DO WHILE END user input date //
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
				query = "SELECT list_ID, host_user_name, list_latitude, list_longitude, Country, Province, City, house_number_and_Street, list_type, post_code, kitchen,heating,washer, wifi, indoor_fireplace, iron, Laptop-friendly_workspace, crib, self_check_in, carbon_monoxide_detector, shampoo, air_conditioning, dryer, breakfast, hangers, hair_dryer, TV, hight_chair, smoke_detector, private_bathroom, SUM(price) AS total_price FROM listing NATURAL JOIN amenites NATURAL JOIN list_calendar WHERE Country = '" + country + "' AND Province = '" + province + "' AND City = '" + city + "' AND house_number_and_street = '" + street + "' AND available = a AND date >= '" + start_date + "' AND date <= '" + end_date + "' GROUP BY listing_id HAVING COUNT(*) = '" + day_lengths + "';";
				rs = CommandLine.sqlMngr.selectOp(query);
				result = CommandLine.sqlMngr.rsToList(rs);
				//case where the listing is not found with the given address
				//either ask user to re-enter the address or back to search menu
				if(result.isEmpty()) {
					valid_choice = true;
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
					CommandLine.sqlMngr.printRecord(rs);

					/****** Ask user input list_ID ******/
					boolean valid_list_id;
					boolean allow_choice_again;
					do {// DO WHILE verify user's choice //
						valid_choice = true;
						allow_choice_again = false;
						System.out.println("You can sort the list by price by entering a: ascending, or d: descending.");
						System.out.println("Please enter c if you want to checkout, enter e if you want to re-enter the address, or enter r to select another search method:");
						choice = CommandLine.sc.nextLine();

						String asc_query = "SELECT list_ID, host_user_name, list_latitude, list_longitude, Country, Province, City, house_number_and_Street, list_type, post_code, kitchen,heating,washer, wifi, indoor_fireplace, iron, Laptop-friendly_workspace, crib, self_check_in, carbon_monoxide_detector, shampoo, air_conditioning, dryer, breakfast, hangers, hair_dryer, TV, hight_chair, smoke_detector, private_bathroom, SUM(price) AS total_price FROM listing NATURAL JOIN amenites NATURAL JOIN list_calendar WHERE Country = '" + country + "' AND Province = '" + province + "' AND City = '" + city + "' AND house_number_and_street = '" + street + "' AND available = a AND date >= '" + start_date + "' AND date <= '" + end_date + "' GROUP BY listing_id HAVING COUNT(*) = '" + day_lengths + "' ORDER BY total_price;";				
						String des_query = "SELECT list_ID, host_user_name, list_latitude, list_longitude, Country, Province, City, house_number_and_Street, list_type, post_code, kitchen,heating,washer, wifi, indoor_fireplace, iron, Laptop-friendly_workspace, crib, self_check_in, carbon_monoxide_detector, shampoo, air_conditioning, dryer, breakfast, hangers, hair_dryer, TV, hight_chair, smoke_detector, private_bathroom, SUM(price) AS total_price FROM listing NATURAL JOIN amenites NATURAL JOIN list_calendar WHERE Country = '" + country + "' AND Province = '" + province + "' AND City = '" + city + "' AND house_number_and_street = '" + street + "' AND available = a AND date >= '" + start_date + "' AND date <= '" + end_date + "' GROUP BY listing_id HAVING COUNT(*) = '" + day_lengths + "' ORDER BY total_price DESC;";

						if (choice.equalsIgnoreCase("a")) {
							allow_choice_again = true;
							query = asc_query;
							rs = CommandLine.sqlMngr.selectOp(query);
							result = CommandLine.sqlMngr.rsToList(rs);
						} else if (choice.equalsIgnoreCase("d")) {
							allow_choice_again = true;
							query = des_query;
							rs = CommandLine.sqlMngr.selectOp(query);
							result = CommandLine.sqlMngr.rsToList(rs);
						} else if (choice.equalsIgnoreCase("c")) {
							break;
						} if (choice.equalsIgnoreCase("e")) {
							re_enter = true;
						} if (choice.equalsIgnoreCase("r")) {
							terminate = true;
							return terminate;
						} else {
							valid_choice = false;
						}
					} while(!valid_choice || allow_choice_again);// DO WHILE END verify user's choice //
					if (choice.equalsIgnoreCase("c")) {
						ArrayList<String> values=null;
						do {	// DO WHILE verify listing id //
							valid_list_id = false;
							System.out.println("Please enter a listing id listed above to check its availability");
							list_ID = CommandLine.sc.nextLine();
							for (int i = 0; i < result.size(); i++) {
								if (result.get(i).get(0).equals(list_ID)) {
									valid_list_id = true;
									values = result.get(i);
									break;
								}
							}
						} while(!valid_list_id);// DO WHILE END verify listing id //
						/*** INSERT BOOKING ***/
						// "list_ID", "host_user_name", "renter_user_name", "start_date", "end_date"
						// values[0], values[1], User.getUser(), start_date, end_date
						String column_names[] = {"list_ID", "host_user_name", "renter_user_name", "start_date", "end_date"};
						String column_values[] = {values.get(0), values.get(1), username, start_date, end_date};
						CommandLine.sqlMngr.insertOp("booking", column_names, column_values);

						/*** UPDATE LIST CALENDAR ***/
						// "date", "listing_id", "available"
						// start_date -> end_date, list_ID, b
						query = "SELECT date FROM list_calendar WHERE listing_ID = '" + list_ID + "' AND date >= '" + start_date + "' AND date <= '" + end_date + "';";
						rs = CommandLine.sqlMngr.selectOp(query);
						result = CommandLine.sqlMngr.rsToList(rs);
						for (int i = 0; i < day_lengths; i++) {
							String date = result.get(i).get(0);
							query = "UPDATE list_calendar SET available = '" + "b" + "' WHERE listing_id = '" + list_ID + "' AND date = '" + date + "';";
						}
						System.out.println("Your booking has been successfully recorded!");
					}
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
				String sub_pc = post_code.substring(0, 3);
				// post code should has six digit in total
				query = "SELECT list_ID, host_user_name, list_latitude, list_longitude, Country, Province, City, house_number_and_Street, list_type, post_code, kitchen,heating,washer, wifi, indoor_fireplace, iron, Laptop-friendly_workspace, crib, self_check_in, carbon_monoxide_detector, shampoo, air_conditioning, dryer, breakfast, hangers, hair_dryer, TV, hight_chair, smoke_detector, private_bathroom, SUM(price) AS total_price FROM listing NATURAL JOIN amenites NATURAL JOIN list_calendar WHERE Country = '" + country + "' AND post_code LIKE '" + sub_pc + "___" + "' AND available = a AND date >= '" + start_date + "' AND date <= '" + end_date + "' GROUP BY listing_id HAVING COUNT(*) = '" + day_lengths + "';";
				rs = CommandLine.sqlMngr.selectOp(query);
				result = CommandLine.sqlMngr.rsToList(rs);
				//case where the listing is not found with the given address
				//either ask user to re-enter the address or back to search menu
				if(result.isEmpty()) {
					System.out.println("Sorry, the address you are looking for does not exist.");
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
					CommandLine.sqlMngr.printRecord(rs);

					/****** Ask user input list_ID ******/
					boolean valid_list_id;
					boolean allow_choice_again;
					do {// DO WHILE verify user's choice //
						valid_choice = true;
						allow_choice_again = false;
						System.out.println("You can sort the list by price by entering a: ascending, or d: descending.");
						System.out.println("Please enter c if you want to checkout, enter e if you want to re-enter the address, or enter r to select another search method:");
						choice = CommandLine.sc.nextLine();

						String asc_query = "SELECT list_ID, host_user_name, list_latitude, list_longitude, Country, Province, City, house_number_and_Street, list_type, post_code, kitchen,heating,washer, wifi, indoor_fireplace, iron, Laptop-friendly_workspace, crib, self_check_in, carbon_monoxide_detector, shampoo, air_conditioning, dryer, breakfast, hangers, hair_dryer, TV, hight_chair, smoke_detector, private_bathroom, SUM(price) AS total_price FROM listing NATURAL JOIN amenites NATURAL JOIN list_calendar WHERE Country = '" + country + "' AND post_code LIKE '" + sub_pc + "___" + "' AND available = a AND date >= '" + start_date + "' AND date <= '" + end_date + "' GROUP BY listing_id HAVING COUNT(*) = '" + day_lengths + "' ORDER BY total_price;";				
						String des_query = "SELECT list_ID, host_user_name, list_latitude, list_longitude, Country, Province, City, house_number_and_Street, list_type, post_code, kitchen,heating,washer, wifi, indoor_fireplace, iron, Laptop-friendly_workspace, crib, self_check_in, carbon_monoxide_detector, shampoo, air_conditioning, dryer, breakfast, hangers, hair_dryer, TV, hight_chair, smoke_detector, private_bathroom, SUM(price) AS total_price FROM listing NATURAL JOIN amenites NATURAL JOIN list_calendar WHERE Country = '" + country + "' AND post_code LIKE '" + sub_pc + "___" + "' AND available = a AND date >= '" + start_date + "' AND date <= '" + end_date + "' GROUP BY listing_id HAVING COUNT(*) = '" + day_lengths + "' ORDER BY total_price DESC;";

						if (choice.equalsIgnoreCase("a")) {
							allow_choice_again = true;
							query = asc_query;
							rs = CommandLine.sqlMngr.selectOp(query);
							result = CommandLine.sqlMngr.rsToList(rs);
						} else if (choice.equalsIgnoreCase("d")) {
							allow_choice_again = true;
							query = des_query;
							rs = CommandLine.sqlMngr.selectOp(query);
							result = CommandLine.sqlMngr.rsToList(rs);
						} else if (choice.equalsIgnoreCase("c")) {
							break;
						} if (choice.equalsIgnoreCase("e")) {
							re_enter = true;
						} if (choice.equalsIgnoreCase("r")) {
							terminate = true;
							return terminate;
						} else {
							valid_choice = false;
						}
					} while(!valid_choice || allow_choice_again);// DO WHILE END verify user's choice //
					if (choice.equalsIgnoreCase("c")) {
						ArrayList<String> values=null;
						do {	// DO WHILE verify listing id //
							valid_list_id = false;
							System.out.println("Please enter a listing id listed above to check its availability");
							list_ID = CommandLine.sc.nextLine();
							for (int i = 0; i < result.size(); i++) {
								if (result.get(i).get(0).equals(list_ID)) {
									valid_list_id = true;
									values = result.get(i);
									break;
								}
							}
						} while(!valid_list_id);// DO WHILE END verify listing id //
						/*** INSERT BOOKING ***/
						// "list_ID", "host_user_name", "renter_user_name", "start_date", "end_date"
						// values[0], values[1], User.getUser(), start_date, end_date
						String column_names[] = {"list_ID", "host_user_name", "renter_user_name", "start_date", "end_date"};
						String column_values[] = {values.get(0), values.get(1), username, start_date, end_date};
						CommandLine.sqlMngr.insertOp("booking", column_names, column_values);

						/*** UPDATE LIST CALENDAR ***/
						// "date", "listing_id", "available"
						// start_date -> end_date, list_ID, b
						query = "SELECT date FROM list_calendar WHERE listing_ID = '" + list_ID + "' AND date >= '" + start_date + "' AND date <= '" + end_date + "';";
						rs = CommandLine.sqlMngr.selectOp(query);
						result = CommandLine.sqlMngr.rsToList(rs);
						for (int i = 0; i < day_lengths; i++) {
							String date = result.get(i).get(0);
							query = "UPDATE list_calendar SET available = '" + "b" + "' WHERE listing_id = '" + list_ID + "' AND date = '" + date + "';";
						}
						System.out.println("Your booking has been successfully recorded!");
					}
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
				System.out.println("Please provide the longtitude:");
				lon = CommandLine.sc.nextLine();
				do {// DO WHILE enter distance //
					increase_distance = false;
					System.out.println("Please provide the maximum distance(KM):");
					distance = CommandLine.sc.nextLine();
					query = "SELECT list_ID, host_user_name, list_latitude, list_longitude, Country, Province, City, house_number_and_Street, list_type, post_code, kitchen,heating,washer, wifi, indoor_fireplace, iron, Laptop-friendly_workspace, crib, self_check_in, carbon_monoxide_detector, shampoo, air_conditioning, dryer, breakfast, hangers, hair_dryer, TV, hight_chair, smoke_detector, private_bathroom, SUM(price) AS total_price, 111.111 * DEGREES(ACOS(LEAST(COS(RADIANS(list_latitude)) * COS(RADIANS("+ lat +")) * COS(RADIANS(list_longtitude - " + lon + ")) + SIN(RADIANS(list_latitude))	* SIN(RADIANS(" + lat + ")), 1.0))) AS distance FROM listing NATURAL JOIN amenites NATURAL JOIN list_calendar WHERE distance < '" + distance + "' AND available = a AND date >= '" + start_date + "' AND date <= '" + end_date + "' GROUP BY listing_id HAVING COUNT(*) = '" + day_lengths + "';";
					rs = CommandLine.sqlMngr.selectOp(query);
					result = CommandLine.sqlMngr.rsToList(rs);
					//case where the listing is not found with the given address
					//either ask user to re-enter coordinate or back to search menu
					if(result.isEmpty()) {
						System.out.println("Sorry, the address you are looking for does not exist.");
						do {	// DO WHILE verify user's choice //
							valid_choice = true;
							System.out.println("Please enter i if you want to increase the distance, enter e if you want to re-enter address to continue, or enter r to select another search method:");
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
						CommandLine.sqlMngr.printRecord(rs);

						/****** Ask user input list_ID ******/
						boolean valid_list_id;
						boolean allow_choice_again;
						do {// DO WHILE verify user's choice //
							valid_choice = true;
							allow_choice_again = false;
							System.out.println("You can sort the list by price by entering a: ascending, or d: descending.");
							System.out.println("Please enter c if you want to checkout, enter e if you want to re-enter the address, or enter r to select another search method:");
							choice = CommandLine.sc.nextLine();

							String asc_query = "SELECT list_ID, host_user_name, list_latitude, list_longitude, Country, Province, City, house_number_and_Street, list_type, post_code, kitchen,heating,washer, wifi, indoor_fireplace, iron, Laptop-friendly_workspace, crib, self_check_in, carbon_monoxide_detector, shampoo, air_conditioning, dryer, breakfast, hangers, hair_dryer, TV, hight_chair, smoke_detector, private_bathroom, SUM(price) AS total_price, 111.111 * DEGREES(ACOS(LEAST(COS(RADIANS(list_latitude)) * COS(RADIANS("+ lat +")) * COS(RADIANS(list_longtitude - " + lon + ")) + SIN(RADIANS(list_latitude))	* SIN(RADIANS(" + lat + ")), 1.0))) AS distance FROM listing NATURAL JOIN amenites NATURAL JOIN list_calendar WHERE distance < '" + distance + "' AND available = a AND date >= '" + start_date + "' AND date <= '" + end_date + "' GROUP BY listing_id HAVING COUNT(*) = '" + day_lengths + "' ORDER BY total_price;";				
							String des_query = "SELECT list_ID, host_user_name, list_latitude, list_longitude, Country, Province, City, house_number_and_Street, list_type, post_code, kitchen,heating,washer, wifi, indoor_fireplace, iron, Laptop-friendly_workspace, crib, self_check_in, carbon_monoxide_detector, shampoo, air_conditioning, dryer, breakfast, hangers, hair_dryer, TV, hight_chair, smoke_detector, private_bathroom, SUM(price) AS total_price, 111.111 * DEGREES(ACOS(LEAST(COS(RADIANS(list_latitude)) * COS(RADIANS("+ lat +")) * COS(RADIANS(list_longtitude - " + lon + ")) + SIN(RADIANS(list_latitude))	* SIN(RADIANS(" + lat + ")), 1.0))) AS distance FROM listing NATURAL JOIN amenites NATURAL JOIN list_calendar WHERE distance < '" + distance + "' AND available = a AND date >= '" + start_date + "' AND date <= '" + end_date + "' GROUP BY listing_id HAVING COUNT(*) = '" + day_lengths + "' ORDER BY total_price DESC;";

							if (choice.equalsIgnoreCase("a")) {
								allow_choice_again = true;
								query = asc_query;
								rs = CommandLine.sqlMngr.selectOp(query);
								result = CommandLine.sqlMngr.rsToList(rs);
							} else if (choice.equalsIgnoreCase("d")) {
								allow_choice_again = true;
								query = des_query;
								rs = CommandLine.sqlMngr.selectOp(query);
								result = CommandLine.sqlMngr.rsToList(rs);
							} else if (choice.equalsIgnoreCase("c")) {
								break;
							} if (choice.equalsIgnoreCase("e")) {
								re_enter = true;
							} if (choice.equalsIgnoreCase("r")) {
								terminate = true;
								return terminate;
							} else {
								valid_choice = false;
							}
						} while(!valid_choice || allow_choice_again);// DO WHILE END verify user's choice //
						if (choice.equalsIgnoreCase("c")) {
							ArrayList<String> values=null;
							do {	// DO WHILE verify listing id //
								valid_list_id = false;
								System.out.println("Please enter a listing id listed above to check its availability");
								list_ID = CommandLine.sc.nextLine();
								for (int i = 0; i < result.size(); i++) {
									if (result.get(i).get(0).equals(list_ID)) {
										valid_list_id = true;
										values = result.get(i);
										break;
									}
								}
							} while(!valid_list_id);// DO WHILE END verify listing id //
							/*** INSERT BOOKING ***/
							// "list_ID", "host_user_name", "renter_user_name", "start_date", "end_date"
							// values[0], values[1], User.getUser(), start_date, end_date
							String column_names[] = {"list_ID", "host_user_name", "renter_user_name", "start_date", "end_date"};
							String column_values[] = {values.get(0), values.get(1), username, start_date, end_date};
							CommandLine.sqlMngr.insertOp("booking", column_names, column_values);

							/*** UPDATE LIST CALENDAR ***/
							// "date", "listing_id", "available"
							// start_date -> end_date, list_ID, b
							query = "SELECT date FROM list_calendar WHERE listing_ID = '" + list_ID + "' AND date >= '" + start_date + "' AND date <= '" + end_date + "';";
							rs = CommandLine.sqlMngr.selectOp(query);
							result = CommandLine.sqlMngr.rsToList(rs);
							for (int i = 0; i < day_lengths; i++) {
								String date = result.get(i).get(0);
								query = "UPDATE list_calendar SET available = '" + "b" + "' WHERE listing_id = '" + list_ID + "' AND date = '" + date + "';";
							}
							System.out.println("Your booking has been successfully recorded!");
						}
					}
				} while(increase_distance);
			} while(re_enter);// DO WHILE END enter address //
			return terminate;
		}

}


