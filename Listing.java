package project;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Listing {
	private static String[] listing_type = {"House", "Bed_and_breakfast", "Bungalow", "Chalet", "Guest_suite", "Hostel", "Loft", "Townhouse", "Apartment", "Boutique_hotle", "Cabin", "Cottage", "Guesthouse", "Hotel", "Resort", "Villa"};
	private static final String[] listing_column_name = {"list_ID", "list_latitude", "list_longitude", "Country", "Province", "City", "house_number_and_Street","host_user_name", "list_type"};
	private static final String[] listing_column_type = { "INT NOT NULL AUTO_INCREMENT", "DECIMAL(10, 8) NOT NULL", "DECIMAL(11, 8) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL","VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL","VARCHAR(30) NOT NULL"};
	private static final String listing_primary_key = "list ID";

	private static final String[] amenites_column_name = {"list_ID", "kitchen","heating","washer", "wifi", "indoor_fireplace", "iron", "Laptop-friendly_workspace", "crib", "self_check_in", "carbon_monoxide_detector", "shampoo", "air_conditioning", "dryer", "breakfast", "hangers", "hair_dryer", "TV", "hight_chair", "smoke_detector", "private_bathroom", "Country", "Province","City", "house_number_and_Street"};
	private static final String[] amenites_column_type = {"INT NOT NULL", "TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL"};
	private static final String amenites_primary_key = "Country, Province, City, house number and Street";


	//this function is not finished
	public int addListing(){
		int choice;
		int listing_ID;
		String[] column_values = new String[listing_column_name.length];
		String[] amenites = new String [amenites_column_name.length];
		boolean check = false;
		column_values = CommandLine.getInfo(listing_type, listing_type.length - 1);
		do{
			this.listTypeMenu();
			choice = Integer.parseInt(CommandLine.sc.nextLine());
			check = CommandLine.checkInRange(choice, 0, column_values.length - 1);
			if(!check){
				System.out.println("Please choose a type that provided");
			}
		}while(!check);
		column_values[listing_type.length - 1] = listing_type[choice];
		User.sqlMngr.insertOp("listing", listing_column_name, column_values);
		return listing_ID;

	}

	public boolean ownList(int list_ID, String host) throws SQLException{
		String sql = "SELECT * FROM listins WHERE listing_id = '"+list_ID+"' and host_user_name = '"+host+"';";
		ResultSet rs = new CommandLine().sqlMngr.selectOp(sql);
		return rs.next();
	}


	private void listTypeMenu(){
		System.out.println("=========LISTING TYPES=========");
		for(int i = 0; i < 16; i++){
			System.out.println(i + listing_type[i]);
		}
		System.out.print("Please select the type of your listing [0-15]: ");
	}

	public void getHostListings(){
		String query = "SELECT list ID, list latitude, list longitude, Country, Province, City, house number and Street, list type, kitchen, heating, washer, wifi, indoor fireplace, iron, Laptop-friendly workspace, crib, self check_in, carbon monoxide detector, shampoo, air conditioning, dryer, breakfast, hangers, hair dryer, TV, hight chair, smoke detector, private bathroom FROM listing NATURAL JOIN amenities WHERE host user name = '" + User.getUser() + "';";
		ResultSet resultSet = CommandLine.sqlMngr.selectOp(query);
		System.out.println("Here is all the listings under your account.");
		CommandLine.sqlMngr.printRecord(resultSet);
	}
}
