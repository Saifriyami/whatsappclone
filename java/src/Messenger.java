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
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
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
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Add to contact list");
                System.out.println("2. Browse contact list");
                System.out.println("3. Write a new message");
                System.out.println("4. Read notification list");
		System.out.println("5. Delete from contact list");
		System.out.println("6. Add to block list");
		System.out.println("7. Delete from block list");
		System.out.println("8. Browse block list");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: AddToContact(esql, au); break;
                   case 2: ListContacts(esql, au); break;
                   case 3: NewMessage(esql); break;
                   case 4: ReadNotifications(esql); break;
		   case 5: DeleteFromContact(esql, au); break;
		   case 6: AddToBlock(esql, au); break;
		   case 7: DeleteFromBlock(esql, au); break;
		   case 8: ListBlocks(esql, au); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
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
  
   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
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

   /*
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
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end
   
   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM Usr WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

   public static void AddToContact(Messenger esql, aUser au){

	try{
      // get contact
         System.out.print("\tEnter user login to add: ");
         String logintoadd = in.readLine();
	
	if(logintoadd.equals(au.login))
	{
		System.out.println("You cannot add yourself as a contact");
		return;
	}

	//check if new contact exists
		String query1 = String.format("Select USR.login  From USR Where login = '%s'" , logintoadd);
		int numR = esql.executeQuery(query1);
		if( numR == 0)
		{
			System.out.println("User does not exist");
			return;
		}
	//check if there is a relation
		String query = String.format("Select * from USER_LIST_CONTAINS where  list_member = '%s' AND list_id = '%s'" ,logintoadd,  au.contact_list);
		int contact_int = esql.executeQuery(query);
		//  check for empty list
		if( contact_int != 0)
		{
			System.out.println("you are already pals");
			return;
		}
		else
		{
			//create the list
			String query2 = String.format("INSERT INTO USER_LIST_CONTAINS(list_id, list_member) VALUES( '%s', '%s')", au.contact_list, logintoadd);
			esql.executeUpdate(query2);
			
			System.out.println("You are now friends with " + logintoadd);
			
		}
	} catch( Exception e){
		System.err.println (e.getMessage() );
		return;
	}
	// 
	
   }//end

   public static void ListContacts(Messenger esql, aUser au ){
	try{
	String query = String.format("select u.list_member , USR.status  from USER_LIST_CONTAINS u, USR where u.list_id = '%s' and USR.login = u.list_member  " , au.contact_list);
	List< List<String>> contact_members = esql.executeQueryResult(query);
	if( contact_members.size()  <= 0)
	{
		System.out.println("contact list is empty ");
		return;
	}
	System.out.println("------------------");

	for( int i = 0 ; i < contact_members.size() ; i++)
	{
		System.out.println(contact_members.get(i).get(0));
		if(contact_members.get(i).get(1) != null)
		{
			System.out.println( "Status: " + contact_members.get(i).get(1));
		}
		System.out.println("------------------");
		
	}
	} catch (Exception e)
	{
		System.err.println(e.getMessage());
	}

   }//end

   public static void NewMessage(Messenger esql){
     	try{
	}catch (Exception e)
	{
		System.err.println(e.getMessage());
	}

   }//end 

   public static void ReadNotifications(Messenger esql){
     	try{
	}catch (Exception e)
	{
		System.err.println(e.getMessage());
	}
 
   }//end

   public static void DeleteFromContact(Messenger esql, aUser au){
    	try{
	        System.out.print("\tEnter user login to delete: ");
        	String logintodelete = in.readLine();

		String query = String.format("select * from USER_LIST_CONTAINS where list_member ='%s' and list_id = '%s' ",logintodelete, au.contact_list );
		int numR = esql.executeQuery(query);
		if( numR == 0)
		{
			System.out.println(logintodelete + " is not on your contact list");
			return;
			
		}
		else{
			String update = String.format("Delete from USER_LIST_CONTAINS where list_member = '%s' and list_id = '%s'",logintodelete, au.contact_list);
			esql.executeUpdate(update);
			System.out.println(logintodelete + " is now deleted from contacts");
			return;
		}
		
	}catch (Exception e)
	{
		System.err.println(e.getMessage());
	}
 
   }//end Query6

   public static void AddToBlock(Messenger esql, aUser au){
     	try{
	// get contact
         System.out.print("\tEnter user login to block: ");
         String logintoblock = in.readLine();
	
	if(logintoblock.equals(au.login))
	{
		System.out.println("You cannot block yourself as a contact");
		return;
	}

	//check if new contact exists
		String query1 = String.format("Select USR.login  From USR Where login = '%s'" , logintoblock);
		int numR = esql.executeQuery(query1);
		if( numR == 0)
		{
			System.out.println("User does not exist");
			return;
		}
	//check if there is a relation
		String query = String.format("Select * from USER_LIST_CONTAINS where  list_member = '%s' AND list_id = '%s'" ,logintoblock,  au.block_list);
		int block_int = esql.executeQuery(query);
		//  check for empty list
		if( block_int != 0)
		{
			System.out.println(logintoblock + " is already blocked");
			return;
		}
		else
		{
			String query3 = String.format("select * from USER_LIST_CONTAINS where list_member ='%s' and list_id = '%s' ",logintoblock, au.contact_list );
			int numR2 = esql.executeQuery(query3);
			if( numR2 == 1)
			{
				System.out.println(logintoblock + " is on your contact list");
				System.out.println("They will be deleted from contact list if you do block");
				System.out.println("are you sure? (y/n)");
				String ans = in.readLine();
				if( ans.equals("n"))
				{
					return;
				}
			
				else{
				String update = String.format("Delete from USER_LIST_CONTAINS where list_member = '%s' and list_id = '%s'",logintoblock, au.contact_list);
				esql.executeUpdate(update);
				System.out.println(logintoblock + " is now deleted from contacts");
				return;
				}
			}

///////
			//create the list
			String query2 = String.format("INSERT INTO USER_LIST_CONTAINS(list_id, list_member) VALUES( '%s', '%s')", au.block_list, logintoblock);
			esql.executeUpdate(query2);
			
			System.out.println(  logintoblock + " is now blocked");
		}

		
	}catch (Exception e)
	{
		System.err.println(e.getMessage());
	}
 
   }//end

 public static void DeleteFromBlock(Messenger esql, aUser au){
    	try{
	        System.out.print("\tEnter user login to unblock: ");
        	String logintounblock = in.readLine();

		String query = String.format("select * from USER_LIST_CONTAINS where list_member ='%s' and list_id = '%s' ",logintounblock, au.block_list );
		int numR = esql.executeQuery(query);
		if( numR == 0)
		{
			System.out.println(logintounblock + " is not on your block list");
			return;
			
		}
		else{
			String update = String.format("Delete from USER_LIST_CONTAINS where list_member = '%s' and list_id = '%s'",logintounblock, au.block_list);
			esql.executeUpdate(update);
			System.out.println(logintounblock + " is now deleted from blocked");
			return;
		}
		
	}catch (Exception e)
	{
		System.err.println(e.getMessage());
	}
 
   }//end Query6

public static void ListBlocks(Messenger esql, aUser au ){
	try{
	String query = String.format("select u.list_member   from USER_LIST_CONTAINS u, USR where u.list_id = '%s' and USR.login = u.list_member  " , au.block_list);
	List< List<String>> contact_members = esql.executeQueryResult(query);
	if( contact_members.size()  <= 0)
	{
		System.out.println("block list is empty ");
		return;
	}
	System.out.println("------------------");

	for( int i = 0 ; i < contact_members.size() ; i++)
	{
		System.out.println(contact_members.get(i).get(0));
		System.out.println("------------------");
		
	}
	} catch (Exception e)
	{
		System.err.println(e.getMessage());
	}

   }//end



}//end Messenger
