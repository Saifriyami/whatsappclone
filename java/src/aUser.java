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
 * This Class the info needed for the current autho-user
 */
public class aUser{
	public String login;
	public String password;
	public String phoneNum;
	public String status;
	public int block_list;
	public int contact_list;
	
	public  aUser(Messenger esql, String login){
		this.login = login;
		System.out.println(login);
		String query = String.format("SELECT * FROM Usr WHERE  login = '%s'" , login);
		List<List<String>> al;
		try{
			al = esql.executeQueryResult(query);
		}catch (Exception e)
		{
			System.err.println(e.getMessage());
			return;
		}
		// TODO double check 
		//ListIterator li = new al.ListIterator();
		this.password = (String) al.get(0).get(1);
		System.out.println(password);
		this.phoneNum = (String) al.get(0).get(2);
		System.out.println(phoneNum);

		if(al.get(0).get(3) != null)
		{	this.status = (String) al.get(0).get(3);
			System.out.println(status);
		}
		else
			System.out.println("status == null");

		//System.out.println((String) al.get(0).get(3));
		this.block_list = Integer.parseInt( al.get(0).get(4) );
		System.out.println(block_list);
		this.contact_list = Integer.parseInt( al.get(0).get(5));
		System.out.println(contact_list);
	}
}
