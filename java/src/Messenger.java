/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;



/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Messenger {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Messenger
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Messenger (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Messenger

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery


public List<List<String>> executeQueryResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      

      List<List<String>> result = new ArrayList<List<String>>();
      // iterates through the result set and output them to standard out.
      while (rs.next()){
		List<String> record = new ArrayList<String>();
	         for (int i=1; i<=numCol; ++i)
				record.add(rs.getString(i));	
         result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current 
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();
	
	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Messenger.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if
      
      Greeting();
      Messenger esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Messenger object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Messenger (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println();
            String mainTitle = "MAIN MENU";
            System.out.println(mainTitle);
            printDashes(mainTitle.length());
            System.out.println();
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            System.out.println();
            String authorisedUser = null;
			aUser au = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql);
						if(authorisedUser != null) 
						{
							au = new aUser(esql, authorisedUser);
						}
						break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end main switch

            if (authorisedUser != null) {
            //user menu
                boolean usermenu = true;

                String userTitle = au.login + "'s Menu"; 
                while(usermenu) {
                    System.out.println(userTitle);
                    printDashes(userTitle.length());
                    System.out.println();
                    
                    System.out.println("1. Manage Contacts");
                    System.out.println("2. View Notifications");
                    System.out.println("3. View Chats");
                    System.out.println("4. New Message");
                    System.out.println("9. Log out");
                    //Add sub-menu for all options
                  
                    switch(readChoice())
                    {
                        case 1: //managing contacts
                            //print menu to console
                            boolean manageContacts = true;
                            String subTitle1 = "\tManaging Contacts";
                            while(manageContacts)
                            {
                                System.out.println(subTitle1);
                                System.out.print("\t");
                                printDashes(subTitle1.length());
                                System.out.println();
                                System.out.println("\t1. Add to contact list");
                                System.out.println("\t2. Browse contact list");
                                System.out.println("\t3. Delete from contact list");
                                System.out.println("\t4. Add to block list");
                                System.out.println("\t5. Delete from block list");
                                System.out.println("\t6. Browse block list");
                                System.out.println("\t9. Return to Main Menu");
                                //determine user's actions                
                                switch(readChoice())
                                {
                                    case 1: 
                                        AddToContact(esql, au);
                                        break;
                                    case 2:
                                        ListContacts(esql, au);
                                        break;
                                    case 3:
                                        DeleteFromContact(esql, au);
                                        break;
                                    case 4:
                                        AddToBlock(esql, au);
                                        break;
                                    case 5:
                                        DeleteFromBlock(esql, au);
                                        break;
                                    case 6:
                                        ListBlocks(esql, au);
                                        break;
                                    case 9:
                                        manageContacts = false;
                                        break;
                                    default:
                                        System.out.println("Unrecognized choice!");
                                        break;
                                } //end manageContacts switch
                            } //end manageContacts while
                            break;
                        case 2: //viewing notifications
            
                            /*
                             *   TODO: List all notifications of authorized user
                             *   public static void ReadNotifications(Messenger esql, aUser au); (already declared below)
                             */
                            System.out.println("TODO: read all notifications of authorized user");
                            String subTitle2 = "\tNotifications List";
                            System.out.println(subTitle2);
                            System.out.println("\t");
                            System.out.println(subTitle2.length());
                            System.out.println("\tCALL ReadNotifications(esql, au) ");
                           
                            break;
                        case 3: //viewing chats
                            /*  
                             *  TODO: List all chats of authorized user

                             * Method to list current 10 chats or view a chat's details. 
                             *
                             * @param Messenger esql: to execute and update queries.
                             * @param aUser au: authorized user's information
                             * @param int choice: value to determine prev/next 10 chats or to select a chat
                             * @param int position: value of the starting position of a 10 chat group
                             * @return current value of a sequence
                             * @throws java.sql.SQLException when failed to execute the query
        
                             *  public static int BrowseChats(Messenger esql, aUser au, int choice, int position); not yet defined
                             *  Each chat should print like this:
                             *  ------------------------------
                             *  chat_id: 0                     
                             *  recipients: twang033, Jimmy
                             *  ------------------------------
                             *  .
                             *  .
                             *  .
                             *  ------------------------------
                             *  chat_id: 9
                             *  recipients: Jimmy
                             *  ------------------------------
                             *  If choice == 1
                             *      show list of chats as options in a sub-menu
                             * 
                             *  If choice == 2 or 3
                             *      show list of prev/next 10 chats
                             *  ------------------------------
                             *  1. chat_id: 0                     
                             *     recipients: twang033, Jimmy
                             *  ------------------------------
                             *  . 
                             *  .
                             *  .
                             *  ------------------------------
                             *  9. chat_id: 9
                             *     recipients: Jimmy
                             *  ------------------------------
                             * I propose a wrapper function where we have "ListChats" function that only take
                             * in the current 10 chats and we use that list as the 10 that we use for selecting
                             * a chat.
                             * 
                             */
                            //chat list
                            int chat_pos = 0;
                            boolean viewing_chats = true;
                            String subTitle3 = "\tChat List";
                            while(viewing_chats)
                            {
                                System.out.println("TODO: List all notifications of authorized user");
                                System.out.println(subTitle3);
                                System.out.println("\t");
                                printDashes(subTitle3.length());
                                System.out.println("\t1. Select a Chat");
                                System.out.println("\t2. Browse previous 10 chats");
                                System.out.println("\t3. Browse next 10 chats");
                                System.out.println("\t4. Go back to main menu");
                                //int choice is the integer to represent the "prev/next"
                                System.out.println("\tCALL ListChats(Messenger esql, aUser au, int choice");
                                switch(readChoice())
                                {
                                    case 1: //display chats as sub-menu
                                        System.out.print("CALL function current_pos = ListChats(esql, au, 1, chat_pos");
                                        break;
                                    case 2: 
                                        System.out.println("CALL function current_pos = ListChats(esql, au, 2, chat_pos");
                                        break;
                                    case 3:
                                         System.out.println("CALL function current_pos = ListChats(esql, au, 3, chat_pos");
                                         break;
                                    case 4:
                                         viewing_chats = false;
                                         break;
                                    default:
                                        System.out.println("Unrecognized choice!");
                                        break;
                                } //end viewing_chats switch
                            }// end viewing_chats while
                            break;
                        case 4: //new message
                            /*
                                TODO: Determine whether the "new message" function should return a boolean function or not.
                                
                             */
                            String subTitle4 = "\tNew Message";
                            System.out.println(subTitle4);
                            System.out.print("\t");
                            printDashes(subTitle4.length());
                            
                            usermenu = false;
                            break;
                        case 9:
                            usermenu = false;
                            break;
                        default: //invalid options
                            System.out.println("Unrecognized choice!");
                            break;
                    }//end usermenu switch
                }//end usermenu while
            }//end if authorised user != NULL
         }//end main while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main


/*============================================================*/
/*              START OF FUNCTION DEFINITIONS                 *
 *============================================================*/ 
   public static void printDashes(int size)
   {
        for(int i = 0; i < size; ++i)
        {
            System.out.print("-");
        }
   }//end printDashes

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /* functionCall: readChoice()
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /* functionCall: CreateUser(Messenger esql)
    * Creates a new user with privided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();

         //Creating empty contact\block lists for a user
         esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('block')");
         int block_id = esql.getCurrSeqVal("user_list_list_id_seq");
             esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('contact')");
         int contact_id = esql.getCurrSeqVal("user_list_list_id_seq");
             
         String query = String.format("INSERT INTO USR (phoneNum, login, password, block_list, contact_list) VALUES ('%s','%s','%s',%s,%s)", phone, login, password, block_id, contact_id);
             esql.executeUpdate(query);
             System.out.println ("User successfully created!");

      } catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser
   
   /* functionCall: String LogIn(Messenger esql)
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         
     //check if login information is in the database
         String query = String.format("SELECT * FROM Usr WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
         if (userNum > 0)
	        return login;
         else
            System.out.println("Error: " + login + " does not exist or incorrect login/password!");
            return null;
         } catch(Exception e){
             System.err.println (e.getMessage ());
             return null;
         }
   }//end LogIn

   /* functionCall: AddToContact(Messenger esql, aUser aUser au)
    * Description: Authorised user can add an existing user to their contact list.
    * @return: void
    **/
   public static void AddToContact(Messenger esql, aUser au){

        try{
          // get new contact login
             System.out.print("\tEnter user login to add: ");
             String logintoadd = in.readLine();

          // check if new contact is not yourself
            if(logintoadd.equals(au.login))
            {
                System.out.println("\tYou cannot add yourself as a contact\n");
                return;
            }
        //check if new contact exists in contact list
            String query1 = String.format("Select USR.login  From USR Where login = '%s'" , logintoadd);
            int numR = esql.executeQuery(query1);
            if( numR == 0)
            {
                System.out.println("\tUser does not exist\n");
                return;
            }
        /*check if there is a relation (if new contact is already in contact list) */
            String query = String.format("Select * from USER_LIST_CONTAINS where  list_member = '%s' AND list_id = '%s'" ,logintoadd,  au.contact_list);
            int contact_int = esql.executeQuery(query);
            //  check for empty list
            if( contact_int != 0)
            {
                System.out.println("\tyou are already pals\n");
                return;
            }
            else  /* Check if the new contact is in the block list. If so, confirm deletion. */
            {
                String query3 = String.format("select * from USER_LIST_CONTAINS where list_member ='%s' and list_id = '%s' ",logintoadd, au.block_list );
                int numR2 = esql.executeQuery(query3);
                if( numR2 == 1)
                {
                    System.out.println("\t" + logintoadd + " is on your block list");
                    System.out.println("\tThey will be deleted from block list if you do add");
                    System.out.println("\tare you sure? (y/n)");
                    String ans = in.readLine();
                    while(!ans.equals("n") && !ans.equals("y"))
                    {
                        System.out.println("\tError: invalid answer. (y/n) ? ");
                        ans = in.readLine();
                    }
                    //if no, don't delete from block list and return.	
                    if( ans.equals("n"))
                    {
                        return;
                    }
                    else{   //delete from block list
                        String update = String.format("Delete from USER_LIST_CONTAINS where list_member = '%s' and list_id = '%s'",logintoadd, au.block_list);
                        esql.executeUpdate(update);
                        System.out.println("\t" + logintoadd + " is now deleted from block list");
                    }
                }

                //Add new contact to contact list
                String query2 = String.format("INSERT INTO USER_LIST_CONTAINS(list_id, list_member) VALUES( '%s', '%s')", au.contact_list, logintoadd);
                esql.executeUpdate(query2);
                
                System.out.println("\tYou are now friends with " + logintoadd + "\n");
                
            }
        } catch( Exception e){
            System.err.println (e.getMessage() );
            return;
        }
   }//end AddToContact

   /* functionCall: ListContacts(Messenger esql, aUser au)
    * Allow the User to see their list of contacts
    * @return void
    **/

   public static void ListContacts(Messenger esql, aUser au ){

        try{
        //select all contacts that is in both USER and USER_LIST_CONTAINS
            String query = String.format("select u.list_member , USR.status  from USER_LIST_CONTAINS u, USR where u.list_id = '%s' and USR.login = u.list_member  " , au.contact_list);
            List< List<String>> contact_members = esql.executeQueryResult(query);
            if( contact_members.size()  <= 0)
            {
                System.out.println("\ncontact list is empty\n");
                return;
            }
        //print all user logins followed by their status message.
        //numDashes based on length of the contact's title size.
            String contact_title = au.login + "'s Contact List";
            System.out.println(contact_title);
            printDashes(contact_title.length());
            System.out.println();
            //these are for making sure that I do not keep recalling function calls.
            int contact_size = contact_members.size();
            int dashCalls = contact_size - 1;
            int contact_title_size = contact_title.length();

            for( int i = 0 ; i < contact_size; i++)
            {
                System.out.println(contact_members.get(i).get(0));
                if(contact_members.get(i).get(1) != null)
                {
                    String status = "Status: " + contact_members.get(i).get(1);

                    System.out.println(status);
                }
                if(i != dashCalls) //makes sure not to print the last dashed line
                {
                    printDashes(contact_title_size);
                    System.out.println();
                }
            }
            printDashes(contact_title_size);
            System.out.println();

        } catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
   }//end ListContacts

   public static void NewMessage(Messenger esql){
        try{
	    }catch (Exception e)
    	{
	    	System.err.println(e.getMessage());
    	}
   }//end NewMessage

   public static void ReadNotifications(Messenger esql){
     	try{

    	}catch (Exception e)
	    {
		    System.err.println(e.getMessage());
    	}
   }//end ReadNotifications

   public static void DeleteFromContact(Messenger esql, aUser au){
    	try{
	        System.out.print("\tEnter user login to delete: ");
        	String logintodelete = in.readLine();
            //check if the contact to delete is on your contact list
            String query = String.format("select * from USER_LIST_CONTAINS where list_member ='%s' and list_id = '%s' ",logintodelete, au.contact_list );
            int numR = esql.executeQuery(query);
            if( numR == 0)
            {
                System.out.println("\t" + logintodelete + " is not on your contact list" + "\n");
                return;
            }
            else{
                String update = String.format("Delete from USER_LIST_CONTAINS where list_member = '%s' and list_id = '%s'",logintodelete, au.contact_list);
                esql.executeUpdate(update);
                System.out.println("\t" + logintodelete + " is now deleted from contacts" + "\n");
                return;
            }
		
        }catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
   }//end DeleteFromContact

   public static void AddToBlock(Messenger esql, aUser au){
     	try{
        // get contact
            System.out.print("\tEnter user login to block: ");
            String logintoblock = in.readLine();
        
            if(logintoblock.equals(au.login))
            {
                System.out.println("\tYou cannot add yourself as a blocked contact\n");
                return;
            }

        //check if contact exists in block list
            String query1 = String.format("Select USR.login  From USR Where login = '%s'" , logintoblock);
            int numR = esql.executeQuery(query1);
            if( numR == 0)
            {
                System.out.println("\t" + logintoblock + " does not exist\n");
                return;
            }
        //check if there is a relation
            String query = String.format("Select * from USER_LIST_CONTAINS where  list_member = '%s' AND list_id = '%s'" ,logintoblock,  au.block_list);
            int block_int = esql.executeQuery(query);
            //  check if contact exists in the block list
            if( block_int != 0)
            {
                System.out.println("\t" + logintoblock + " is already blocked\n");
                return;
            }
            else //check if contact is in the contact list. 
            {
                String query3 = String.format("select * from USER_LIST_CONTAINS where list_member ='%s' and list_id = '%s' ",logintoblock, au.contact_list );
                int numR2 = esql.executeQuery(query3);
                if( numR2 == 1) //If in contact, confirm deletion from contact
                {
                    System.out.println("\n\t" + logintoblock + " is on your contact list\n");
                    System.out.println("\tThey will be deleted from contact list if you do block");
                    System.out.println("\tare you sure? (y/n): ");
                    String ans = in.readLine();
                    while(!ans.equals("n") && !ans.equals("y") )
                    {
                        System.out.println("\tError: invalid answer (y/n): ");
                        ans = in.readLine();
                    }
                    //if no, then don't delete from contact list and return.
                    if( ans.equals("n"))
                    {
                        return;
                    }
                    else{ //delete contact from contact list
                    String update = String.format("Delete from USER_LIST_CONTAINS where list_member = '%s' and list_id = '%s'",logintoblock, au.contact_list);
                    esql.executeUpdate(update);
                    System.out.println("\t" + logintoblock + " is now deleted from contacts\n");
                    }
                }

                //insert contact into the blocked list
                String query2 = String.format("INSERT INTO USER_LIST_CONTAINS(list_id, list_member) VALUES( '%s', '%s')", au.block_list, logintoblock);
                esql.executeUpdate(query2);
                System.out.println("\t" + logintoblock + " is now blocked\n");
            }
        }catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
   }//end AddToBlock

    public static void DeleteFromBlock(Messenger esql, aUser au){
        try{
	        System.out.print("\tEnter user login to unblock: ");
        	String logintounblock = in.readLine();

            //check if contact to unblock exists in the block list. If not, return, else delete from the block list
            String query = String.format("select * from USER_LIST_CONTAINS where list_member ='%s' and list_id = '%s' ",logintounblock, au.block_list );
            int numR = esql.executeQuery(query);
            if( numR == 0)
            {
                System.out.println("\t" + logintounblock + " is not on your block list\n");
                return;
                
            }
            else{
                String update = String.format("Delete from USER_LIST_CONTAINS where list_member = '%s' and list_id = '%s'",logintounblock, au.block_list);
                esql.executeUpdate(update);
                System.out.println("\t" + logintounblock + " is now deleted from blocked\n");
                return;
            }
        }catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
 
   }//end DeleteFromBlock

    public static void ListBlocks(Messenger esql, aUser au ){
        try{
            String query = String.format("select u.list_member   from USER_LIST_CONTAINS u, USR where u.list_id = '%s' and USR.login = u.list_member  " , au.block_list);
            List< List<String>> block_members = esql.executeQueryResult(query);
            if( block_members.size()  <= 0)
            {
                System.out.println("\n\tblock list is empty\n ");
                return;
            }
            String block_title = "\t" + au.login + "'s block list";
            int block_size = block_members.size();
            int dashCalls = block_size - 1;
            int numDashes = block_title.length();
            printDashes(numDashes);
            System.out.println();
            for( int i = 0 ; i < block_size ; i++)
            {
                System.out.println(block_members.get(i).get(0));
                if(i != dashCalls)
                {
                    printDashes(numDashes);
                    System.out.println();
                }
                
            }
            printDashes(numDashes);
            System.out.println();
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

       }//end ListBlocks

}//end Messenger
