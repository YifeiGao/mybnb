package project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
}
