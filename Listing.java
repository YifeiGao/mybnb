package project;

public class Listing {
	private static String[] listing_type = {"House", "Bed and breakfast", "Bungalow", "Chalet", "Guest suite", "Hostel", "Loft", "Townhouse", "Apartment", "Boutique hotle", "Cabin", "Cottage", "Guesthouse", "Hotel", "Resort", "Villa"};
	private static final String[] listing_column_name = {"list ID", "list latitude", "list longitude", "Country", "Province", "City", "house number and Street","host user name", "list type"};
	private static final String[] listing_column_type = { "INT NOT NULL AUTO_INCREMENT", "DECIMAL(10, 8) NOT NULL", "DECIMAL(11, 8) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL","VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL","VARCHAR(30) NOT NULL"};
	private static final String listing_primary_key = "list ID";

	private static final String[] amenites_column_name = {"list ID", "kitchen","heating","washer", "wifi", "indoor fireplace", "iron", "Laptop-friendly workspace", "crib", "self check_in", "carbon monoxide detector", "shampoo", "air conditioning", "dryer", "breakfast", "hangers", "hair dryer", "TV", "hight chair", "smoke detector", "private bathroom", "Country", "Province","City", "house number and Street"};
	private static final String[] amenites_column_type = {"INT NOT NULL", "TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)","TINYINT(1)", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL"};
	private static final String amenites_primary_key = "Country, Province, City, house number and Street";

	public void addListing(){
		int choice;
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

	}


	private void listTypeMenu(){
		System.out.println("=========LISTING TYPES=========");
		for(int i = 0; i < 16; i++){
			System.out.println(i + listing_type[i]);
		}
		System.out.print("Please select the type of your listing [0-15]: ");
	}

	public void getHostListings(){

	}
}
