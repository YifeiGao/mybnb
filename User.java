package project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class User {

	private static final String[] user_column_name = {"name", "password", "list_address", "birth", "occup", "SIN", "user_name","host_canclellation", "renter_canclellation"};
	private static final String[] user_column_type = {"VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "DATE NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "VARCHAR(30) NOT NULL", "INT DEFAULT 0", "INT DEFAULT 0"};
	private static final String user_primary_key = "user_name";

	private String curr_user;

	public static SQLController sqlMngr = new SQLController();
	public User(){
		//sqlMngr.connect();
		sqlMngr = CommandLine.sqlMngr;
	}
	public static String[] getUserColumn(){
		return user_column_name;
	}
	
	public static String[] getUserColumnType(){
		return user_column_type;
	}
	
	public static String getUserKey(){
		return user_primary_key;
	}
	/**
	 * function will be operate after connected successfully, it handle user's login and register
	 * String choice: the use's choice which have 2 possible values: 1 for login and 2 for register
	 * boolean check: a boolean type that check if the user typed the number that are provided
	 */
	public boolean userStart(){
		String choice;
		boolean check;
		boolean log_in;
		System.out.println("Welcome to Mybnb, if you want to login press 1, if you are not a user yet please press 2 to register");
		do{
			choice = CommandLine.sc.nextLine();
			check = choice.equals("1") || choice.equals("2");
			if(!check){
				System.out.println("Please chose an operation that provided");
			}
		}while(!check);
		if (choice.equals("1")){
			//login operations
			log_in = this.login();
		}
		else{
			//create a user
			log_in = this.createUser();
		}
		return log_in;
	}
	/**
	 * the function that handle login
	 * String pass: the password that the user typed in the command line
	 * String sql: the Mysql query that need to be executed
	 */
	private boolean login(){
		String pass;
		String sql;
		String result;
		boolean check_pass;
		int choice;
		String user_name;
		boolean log_in = false;
		//check if the userID that the user typed exist, if not, type again or go regist
		do{
			System.out.println("Please type your username below");
			user_name = CommandLine.sc.nextLine();
			choice = 0;
			boolean exist = sqlMngr.checkExist("*", new String[] {"user_name"}, new String[] {user_name}, "users");
			if(!exist){
				System.out.println("Sorry the user id is not found, please type 1 to try again or type 2 to regist");
				do{
					System.out.println("Pleas type your choice [1-2]:");
					choice = Integer.parseInt(CommandLine.sc.nextLine());
				}while(!(choice == 1 || choice == 2));
			}
		}while(choice == 1 & !sqlMngr.checkExist("*", new String[] {"user_name"}, new String[] {user_name}, "users"));
		if(choice == 2){
			log_in = this.createUser();
			return log_in;
		}
		else{
			try{
				//check if the password that the user typed is matched according to the database
				do{
					System.out.println("Please type your password below");
					pass = CommandLine.sc.nextLine();
					sql = "SELECT password FROM users WHERE user_name = '"+user_name+"';";
					ResultSet rs = sqlMngr.selectOp(sql);
					rs.next();
					result = rs.getString("password");
					check_pass = (result.equals(pass));
					if(!check_pass){
						System.out.println("incorrect password");
					}
					rs.close();
				}while (!check_pass);
			}catch(SQLException e){
				System.err.println("Exception in User.login");
				e.printStackTrace();
			}
			//set the user name as current user if log in successfully
			this.setUser(user_name);
			//System.out.println("Logged in , please choose a options provided below");
			System.out.println("test: the current user is "+ curr_user);
			log_in = true;
			return log_in;
		}
		//System.out.println("")
		//this.operations();
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
	private boolean createUser(){
		boolean user_exist;
		String[] column_values = new String[user_column_name.length];
		column_values = CommandLine.getInfo(user_column_name, 0, 6).toArray(new String[0]);
		// check if the user name is already existed, if so then choose another user name
		do{
			user_exist = sqlMngr.checkExist("*", new String[] {"user_name"}, new String[] {column_values[Arrays.asList(user_column_name).indexOf("user_name")]}, "users");
			if(user_exist){
				System.out.println("Sorry the user name already exist, please choose another user name");
				String [] get_user = {"user_name"};
				column_values[Arrays.asList(user_column_name).indexOf("user_name")] = CommandLine.getInfo(get_user,1,1).get(0);
			}
		}while(user_exist);
		int row_affect = sqlMngr.insertOp("users", user_column_name, column_values,false);
		//set the user name that the user registed as the current user name once the user created a user
		// the user will be auto logged in after register
		this.setUser(column_values[Arrays.asList(user_column_name).indexOf("user_name")]);
		System.out.println("Regist sucessfully! Your user name is: " + this.curr_user);
		return true;
	}

	public  void setUser(String user){
		curr_user = user;
	}


	/**
	 * it is the operations that the renter can do 
	 */
	public void userMenu() {
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

	public boolean hostNotice(){
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
	//operation 0
	// sql keeps connected after user logout
	public void logOut(){
		this.setUser(null);
		for (int i = 0; i < 5; i++) {
			System.out.println(".");
		}
		System.out.println("Logged out successfully");
		System.out.println("");
		// Provide re-login/sign up options
		/*String choice;
		boolean check = false;
		System.out.println("Welcome to Mybnb, if you want to login press 1, if you are not a user yet please press 2 to register");
		choice = CommandLine.sc.nextLine();
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
		}*/
	}


	//operation 1
	public void makeBooking(){
		Listing l = new Listing();
		System.out.println("Please make a booking for the coming 2months");
		l.getListing(this.curr_user);

	}
	//operation 2/7
	public void printHistory(int choice){
		Booking b = new Booking();
		// if the user chose 2 then print his/her rental history of the listings that he/she rented, else if the user chose 7 then print the rental history of the listings that he/she owns
		if(choice == 2){
			b.getRentalHistory(this.curr_user);
		}
		else if (choice == 7){
			b.getHostHistory(this.curr_user);
		}
	}

	//operation 3/8
	public void deleteFutureBooking(int choice){
		Booking b = new Booking();
		if(choice == 3){
			//delete a future booking as renter
			b.deletBooking("renter", this.curr_user);
		}
		else if(choice == 8){
			//delete a future booking as a host
			b.deletBooking("host", this.curr_user);
		}
	}

	// if opt.4 is chosen, print his/her recent and completed rental histories first,
	// else if it is opt.10, print all recent and completed rental histories of all listings that he/she owns
	// then ask them to input ratings and comments
	public void writeComments(int choice){
		Comments c = new Comments();
		Booking b = new Booking();
		if (choice == 4) {
			b.getRecentCompletedRentalHistory(this.curr_user);
			c.insertRateAndComments("renter", this.curr_user);

		} else if (choice == 10) {
			b.getRecentCompletedHostHistory(this.curr_user);
			c.insertRateAndComments("host", this.curr_user);
		}
	}

	//operation 5
	public void addListing(){
		int l_ID;
		float price;
		Listing l = new Listing();
		l_ID = l.addListing(this.curr_user);
		price = l.getAvePrice(l_ID);
		ListCalendar c = new ListCalendar();
		System.out.println("The system provid a suggestion price according to tsthe listing's address, the price is "+ price +". It will be the initial price in your listing calendar, you can update the price by doing the operation 11");
		c.insertListCalendar(l_ID, price);

	}

	//operation 6
	public void getHostListings(){
		Listing l = new Listing();
		l.getHostListings(curr_user);

	}

	//operation 9
	public void updateAvai(){
		//check if the current user owns listings
		boolean check;
		boolean own_list;
		boolean date_exist;
		String date;
		String status;
		String d_q;
		String delete_fu_b;
		int listId = -1000;
		check = sqlMngr.checkExist("*", new String[]{"host name"}, new String[] {curr_user}, "listing");
		if(!check){
			System.out.println("According our records, you are not a host yet. Please add listing first to become a host. You will be sent to the operation menu page");
		}
		else{
			try{
				do{
					System.out.println("Following are the listings that you owned");
					new Listing().getHostListings(curr_user);
					System.out.println("Please enter the listing ID");
					listId = Integer.parseInt(CommandLine.sc.nextLine());
					own_list = new Listing().ownList(listId, curr_user);
					if(!own_list){
						System.out.println("According to our records. you do not own this listing, check if the listId is correct ");
					}
				}while(!own_list);
			}catch(SQLException e){
				System.out.println("Exception occurs in User.updateAvai");
				e.printStackTrace();
			}
			do{
				System.out.println("Please enter the date you want to change (yyyy-MM-dd)");
				date = CommandLine.sc.nextLine();
				date_exist = new ListCalendar().checkDate(listId, date);
				if(!date_exist){
					System.out.println("According to our records, the date is not open yet. The system only allow the host to open the future 2 months' calendar");
				}
			}while (!date_exist);
			String original_status = new ListCalendar().checkStatus(listId, date);
			System.out.println("The current status of the listing on that day is " + original_status);
			do{
				System.out.println("Please chose the status that you want to change. Type 'a' to change it availiable, or type 'u' to make it unavailiable");
				status = CommandLine.sc.nextLine();
			}while (!(status.equals("a") || status.equals("u")));
			if(original_status.equals("b")){
				System.out.println("The listing is booked on that day, do you want cancle this booking?");
				do{
					System.out.println("Type 'd' to delete the booking , type 'q' to quit and go back to operation menue padge");
					d_q = CommandLine.sc.nextLine();
				}while (!(d_q.equalsIgnoreCase("d") || d_q.equalsIgnoreCase("q")));
				if (d_q.equalsIgnoreCase("d")){
					//delete future booking
					delete_fu_b = "DELETE FORM booking WHERE list_ID =  '"+listId+"' AND start_date <= '"+date+"' AND end_date >= '"+date+"';";
					sqlMngr.excuteSql(delete_fu_b);
					Booking b = new Booking();
					b.updateCancellation("host", this.curr_user);
				}
				else{
				}
			}
			else{
				new ListCalendar().updateAva(listId, date, status);
			}
		}
	}

	//operation 11
	public void updatePrice(){
		String status;
		System.out.println("Following are the listings that you own");
		//TO DO:
		//print the listing that the user owns
		System.out.println("Please enter the listing ID that you want to update");
		int list_ID = Integer.parseInt(CommandLine.sc.nextLine());
		//TO Do:
		//check if the listing ID that the host typed is correct
		System.out.println("Please enter the date that you want to change, please type in yyyy-MM-dd format");
		String date = CommandLine.sc.nextLine();
		//TO DO:
		//get the status of the listing on that day, if it is booked then need to delete the booking first, if it is unavailable update to available first 
		float price = new ListCalendar().getPrice(list_ID, date);
		System.out.println("According to our record, the price of the listing "+ list_ID + " on " + date + " was " + price);
		System.out.println("Please type the new price on that day");
		float new_price = Float.parseFloat(CommandLine.sc.nextLine());
		new ListCalendar().updatePrice(list_ID, date, new_price);

	}


}
