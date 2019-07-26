package project;

import java.util.ArrayList;
import java.util.Arrays;

public class User {

	private static final String[] user_column_name = {"name", "password", "list address", "birth", "occup", "SIN", "canclellation", "user name"};
	private static final String[] user_column_type = {"VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "INT NOT NULL", "VARCHAR(30) NOT NULL", "INT NOT NULL", "INT DEFAULT 0", "VARCHAR(30) NOT NULL"};
	private static final String user_primary_key = "user name";

	private static String user_id;

	public static SQLController sqlMngr = null;
	/**
	 * function will be operate after connected successfully, it handle user's login and register
	 * String choice: the use's choice which have 2 possible values: 1 for login and 2 for register
	 * boolean check: a boolean type that check if the user typed the number that are provided
	 */
	public void userStart(SQLController sqlC){
		sqlMngr = sqlC;
		String choice;
		boolean check = false;
		choice = CommandLine.sc.nextLine();
		System.out.println("Welcome to Mybnb, if you want to login press 1, if you are not a user yet please press 2 to register");
		while (!check){
			check = choice.equals("1") | choice.equals("2");
		}
		if (choice.equals("1")){
			//login operations
			this.login();
		}
		else if (choice.equals("2")){
			//create a user
			this.createUser();
		}
	}
	/**
	 * the function that handle login
	 * String pass: the password that the user typed in the command line
	 * String sql: the Mysql query that need to be executed
	 */
	private void login(){
		String pass;
		String sql;
		ArrayList <ArrayList<String>> result = new ArrayList <ArrayList<String>>();
		boolean check_pass;
		int choice = 0;
		String user_name;
		//check if the userID that the user typed exist, if not, type again or go regist
		do{
			System.out.println("Please type your username below");
			user_name = CommandLine.sc.nextLine();
			if(!CommandLine.sqlMngr.checkExist("*", new String[] {"user name"}, new String[] {user_name}, "users")){
				System.out.println("Sorry the user id is not found, please type 1 to try again or type 2 to regist");
				do{
					System.out.print("Pleas type your choice [1-2]");
					choice = Integer.parseInt(CommandLine.sc.nextLine());
				}while(choice != 2 || choice != 1);
				switch(choice){
				case 1:
					//the user want to type the user name again
					break;
				case 2:
					//go to create a user and exit the login function
					this.createUser();
					System.out.println("Going to regist");
					return;
				}
			}
		}while(choice == 1);
		//check if the password that the user typed is matched according to the database
		do{
			System.out.println("Please type your password below");
			pass = CommandLine.sc.nextLine();
			sql = "SELECT password FROM users WHERE user name = '"+user_name+"';";
			result = CommandLine.sqlMngr.rsToList(CommandLine.sqlMngr.selectOp(sql));	
			check_pass = (result.get(0).equals(pass));
			if(!check_pass){
				System.out.println("incorrect password");
			}
		}while (!check_pass);
		//set the user name as current user if log in successfully
		this.setUser(user_name);
		System.out.println("Logged in , please choose a options provided below");
		this.operations();
		/*if (result.get(0).equals("host")){
			this.hostOp();
		}
		else if (result.get(0).equals("renter")){
			this.renterOp();
		}*/

	}
	/**
	 * Regist operation
	 */
	private void createUser(){
		boolean user_exist;
		String[] column_values = new String[user_column_name.length];
		column_values = CommandLine.getInfo(user_column_name, user_column_name.length);
		// check if the user name is already existed, if so then choose another user name
		do{
			user_exist = CommandLine.sqlMngr.checkExist("*", new String[] {"user name"}, new String[] {column_values[Arrays.asList(user_column_name).indexOf("user name")]}, "users");
			if(user_exist){
				System.out.println("Sorry the user name already exist, please choose another user name");
				String [] get_user = {"user name"};
				column_values[Arrays.asList(user_column_name).indexOf("user name")] = CommandLine.getInfo(get_user, 1)[0];
			}
		}while(user_exist);
		int row_affect = CommandLine.sqlMngr.insertOp("users", user_column_name, column_values);
		//set the user name that the user registed as the current user name once the user created a user
		// the user will be auto logged in after register
		this.setUser(column_values[Arrays.asList(user_column_name).indexOf("user name")]);
		System.out.println("Regist sucessfully! Your user name is: " + this.getUser());
		System.out.println("Please choose a operation that you want to do");
		this.operations();
	}

	public static String getUser(){
		return user_id;
	}

	public static void setUser(String user){
		user_id = user;
	}

	/**
	 * 
	 */
	private void operations(){
		this.userMenu();

	}

	/**
	 * it is the operations that the renter can do 
	 */
	private void userMenu() {
		System.out.println("=========OPERATIONS=========");
		System.out.println("0. Log out.");
		System.out.println("1. Make a booking.");
		System.out.println("2. Check your booking history.");
		System.out.println("3. Delete a future booking that you booked.");
		System.out.println("4. Write comments and rate a host.");
		System.out.println("5. As a host, add a listing");
		System.out.println("6. Get the all your own lists' information.");
		System.out.println("7. Check booking history of the listings that you own.");
		System.out.println("8. Delete a future booking of a list that you own.");
		System.out.println("9. Update the availability of the list");
		System.out.println("10. Write comments and rate a renter that has rented your listing.");
		System.out.println("11. Update the price of a listing");
		System.out.print("Choose one of the previous options [0-11]: ");
	}

	private boolean hostNotice(){
		boolean con = true;
		String c_or_q;
		System.out.println("Notice: this is an operation of the host type user. If you are not a host yet, do you want to become a host and continue?");
		System.out.println("Please type 'C' to continue, otherwise type 'Q' to quit and rechoose an operaiton");
		c_or_q = CommandLine.sc.nextLine();
		if(c_or_q.equalsIgnoreCase("Q")){
			// re-chose the operation
			con = false;
		}
		else if(c_or_q.equalsIgnoreCase("C")){
			//continue the operation
			con = true;
		}
		return con;
	}
	//operation 1
	private void makeBooking(){

	}
	//operation 2/7
	private void printHistory(int choice){
		Booking b = new Booking();
		// if the user chose 2 then print his/her rental history of the listings that he/she rented, else if the user chose 7 then print the rental history of the listings that he/she owns
		if(choice == 2){
			b.getRentalHistory();
		}
		else if (choice == 7){
			b.getHostHistory();
		}
	}

	//operation 3/8
	private void deleteFutureBooking(int choice){
		Booking b = new Booking();
		if(choice == 3){
			//delete a future booking as renter
			b.deletBooking("renter");
		}
		else if(choice == 8){
			//delete a future booking as a host
			b.deletBooking("host");
		}
	}

	//operation 4/10
	private void writeComments(int choice){

	}

	//operation 5
	private void addListing(){

		Listing l = new Listing();
		l.addListing();
	}

	//operation 6
	private void getHostListings(){
		Listing l = new Listing();
		l.getHostListings();
	}

	//operation 9
	private void updateAvai(){
		//check if the current user owns listings
		boolean check;
		check = sqlMngr.checkExist("*", new String[]{"host name"}, new String[] {User.user_id}, "listing");
		if(!check){
			System.out.println("According our records, you are not a host yet. Please add listing first to become a host");
			//TO Do:
			//print the operation menue again and let the user re-choose an operation
		}
		System.out.println("Please enter the listing ID");
		String listId = CommandLine.sc.nextLine();

	}

	//operation 11
	private void updatePrice(){
		String status;
		System.out.println("Following are the listings that you own");
		//TO DO:
		//print the listing that the user owns
		System.out.println("Please enter the listing ID that you want to update");
		int listing_ID = Integer.parseInt(CommandLine.sc.nextLine());
		//TO Do:
		//check if the listing ID that the host typed is correct
		System.out.println("Please enter the date that you want to change, please type in yyyy-MM-dd format");
		String date = CommandLine.sc.nextLine();
		//TO DO:
		//get the status of the listing on that day, if it is booked then need to delete the booking first, if it is unavailable update to available first 
		float price = new Calendar().getPrice(listing_ID, date);
		System.out.println("According to our record, the price of the listing "+ listing_ID + " on " + date + " was " + price);
		System.out.println("Please type the new price on that day");
		float new_price = Float.parseFloat(CommandLine.sc.nextLine());
		new Calendar().updatePrice(listing_ID, date, new_price);

	}

}
