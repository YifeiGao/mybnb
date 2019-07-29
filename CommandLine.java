package project;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

public class CommandLine {

	// 'sqlMngr' is the object which interacts directly with MySQL
	public static SQLController sqlMngr = null;
	// 'sc' is needed in order to scan the inputs provided by the user
	public static Scanner sc = null;



	//Public functions - CommandLine State Functions

	/* Function used for initializing an istance of current
	 * class
	 */
	public boolean startSession() {
		boolean success = true;
		if (sc == null) {
			sc = new Scanner(System.in);
		}
		if (sqlMngr == null) {
			sqlMngr = new SQLController();
		}
		try {
			success = sqlMngr.connect();
		} catch (ClassNotFoundException e) {
			success = false;
			System.err.println("Establishing connection triggered an exception!");
			e.printStackTrace();
			sc = null;
			sqlMngr = null;
		}
		return success;
	}

	public boolean execute(){
		sqlMngr.initialize();
		if(sc != null && sqlMngr != null){
			User u = new User();
			Listing l = new Listing();
			int choice = -1;
			String input = "";
			do{
				boolean log_in = false;
				log_in = u.userStart();
				boolean con = true;
				if(log_in){

					do{
						boolean is_host = l.checkHost(u.getCurrU());
						u.userMenu();
						input = sc.nextLine();
						try{
							choice = Integer.parseInt(input);
							switch(choice){
							case 0:
								log_in = false;
								u.logOut();
								break;
							case 1:
								u.makeBooking();
								break;
							case 2:
								u.printHistory(choice);
								break;
							case 3:
								u.deleteFutureBooking(choice);
								break;
							case 4:
								u.writeComments(choice);
								break;
							case 5:
								con = u.hostNotice();
								if(con){
									u.addListing();

								}
								break;
							case 6:
								con = u.hostNotice();
								if(con){
									if(is_host){
										u.getHostListings();
									}
									else{
										System.out.println("Sorry you are not a user, please select the orther operations");
									}
								}
								break;
							case 7:
								con = u.hostNotice();
								if(con){
									if(is_host){
										u.printHistory(choice);
									}
									else{
										System.out.println("Sorry you are not a user, please select the orther operations");
									}
								}
								break;
							case 8:
								con = u.hostNotice();
								if(con){
									if(is_host){
										u.deleteFutureBooking(choice);
									}
									else{
										System.out.println("Sorry you are not a user, please select the orther operations");
									}
								}
								break;
							case 9:
								con = u.hostNotice();
								if(con){
									if(is_host){
										u.updateAvai();
									}
									else{
										System.out.println("Sorry you are not a user, please select the orther operations");
									}
								}
								break;
							case 10:
								con = u.hostNotice();
								if(con){
									if(is_host){
										u.writeComments(choice);
									}
									else{
										System.out.println("Sorry you are not a user, please select the orther operations");
									}
								}
								break;
							case 11:
								con = u.hostNotice();
								if(con){
									if(is_host){
										u.updatePrice();
									}
									else{
										System.out.println("Sorry you are not a user, please select the orther operations");
									}
								}
								break;
							default:
								break;
							}
						}catch (NumberFormatException e){
							input = "-1";
							System.err.println("NumberFormatException occurs in CommandLine.excute");
							e.printStackTrace();
						}
					}while(log_in);
				}
				System.out.println("Please type anything to go to the user start page");
			}while(!sc.next().equalsIgnoreCase("EXIT"));
			return true;
		}
		else {
			System.out.println("");
			System.out.println("Connection could not been established! Bye!");
			System.out.println("");
			return false;
		}
	}



	/**
	 * get the value that typed form command line
	 * @param column: a string array which stores the column names
	 * @param c: the number of column that need the user's input value
	 * @return a string array that stores the value of columns
	 */
	public static ArrayList<String> getInfo(String[] column, int start_c, int end_c){
		ArrayList<String> values = new ArrayList<String>();
		for (int i = start_c; i <= end_c; i ++){
			System.out.println("Please type the " +  column[i] + " below");
			values.add(i, CommandLine.sc.nextLine());
		}
		return values;
	}




	public static boolean checkInRange(int input, int min, int max){
		boolean check;
		check = input >= min & input <= max;
		return check;
	}


	/* Function that acts as destructor of an instance of this class.
	 * Performs some housekeeping setting instance's private field
	 * to null
	 */
	public void endSession() {
		if (sqlMngr != null)
			sqlMngr.disconnect();
		if (sc != null) {
			sc.close();
		}
		sqlMngr = null;
		sc = null;
	}






}
