/*
 * LibraryModel.java
 * Author:
 * Created on:
 */



import javax.swing.*;
import java.sql.*;

public class LibraryModel {

    // For use in creating dialogs and making them modal
    private JFrame dialogParent;
    private Connection con = null;
    String userid="merrithenr";
    String password = "Ories-10";


    public LibraryModel(JFrame parent, String userid, String password) {
    	dialogParent = parent;
		try {
			Class.forName("org.postgresql.Driver");
		}catch(ClassNotFoundException cnfe) {
			System.out.println("Can not find"
				+"the driver class: "
				+"\nEither I have not installed it"
				+"properly or \n postgresql.jar "
				+" file is not in my CLASSPATH");
		}
		String url = "jdbc:postgresql://db.ecs.vuw.ac.nz/"+userid+"_jdbc";
		try {
			con = DriverManager.getConnection(url,userid,password);
		}catch(SQLException sqlex) {
			System.out.println("Can not connect");
			System.out.println(sqlex.getMessage());
		}
    }

    public String bookLookup(int isbn) {
    	//setting up the sets and making them equal to null, will be more useful later
    	ResultSet rs = null;
    	Statement s = null;
    	//setting up the result array
    	String[] result = new String[2];
    	//start of the try catch
    	try {
    		//links to the connection and create statement
    		s = con.createStatement();
    		// asks to start the statement and execute the query and then let the result be placed into the result set
    		rs = s.executeQuery("SELECT count(*) FROM book WHERE isbn = "+isbn);
    		//goes through all of the returned values into the result set.
    		while(rs.next()){
    			//gets the amount of books with this isbn
    			int size = rs.getInt(1);
    			if(size < 1) {
    				// tells the user if there is no books under this isbn
    				return "There is no book under this isbn";
    			}
    		}
    		// if it is all okay, it runs this sql query
    		s = con.createStatement();
    		rs = s.executeQuery("SELECT title, name, surname FROM book " +
    				"NATURAL JOIN book_author "
    				+"NATURAL JOIN author"
    				+" WHERE isbn = "+isbn+
    				"ORDER BY authorseqno DESC");
    		int i =0;
    		// puts all returned values into the result array
    		while(rs.next()) {
    			//first value will be the one that needs to be returned
    			result[i] = rs.getString(1)+", "
    		+rs.getString(2)+", "
    					+rs.getString(3);
    		i++;
    		}
    	}catch(SQLException sqlex) {
    		System.out.println(sqlex.getMessage());
    	}
    // returns the first value in the array due to author sequence all that is returned
    	return result[0];
    }

    public String showCatalogue() {
    	//setting up the variables
    	ResultSet rs = null;
    	Statement s = null;
    	String[] result = null;
    	PreparedStatement prstmt=null;
    	int size= 0;
    	// starts a try catch
    	try {
    		//sets up the query
    		String query = "SELECT count(*) FROM book";
    		// preparing a statement
    		s = con.createStatement();
    		//prepares the statement by loading the query
    		prstmt = con.prepareStatement(query);
    		//executing and putting the result into the result set
    		rs = prstmt.executeQuery();
    		while(rs.next()) {
    			//checking the size and making the size equal to this
    			size=rs.getInt(1);
    		}
    		//running another query
    		s = con.createStatement();
    		rs = s.executeQuery("SELECT title FROM book ");
    		// making the result array equal to the size of the book
    		result = new String[size];
    		int i =0;
    		// goes through each of the resultset and places them into the result array
    		while(rs.next()) {
    			result[i] = rs.getString(1);
    			i++;
    		}
    	//catches if sql errors occur
    	}catch(SQLException sqlex) {
    		System.out.println(sqlex.getMessage());
    	}
    	// goes through and places results into one string to be returned and printed
    	String toReturn = "";
    	for(int i =1; i<result.length;i++) {
    		toReturn = toReturn+result[i]+"\r\n";
    	}
    // returns string to be printed
    	return toReturn;
    }

    public String showLoanedBooks() {
    	// setting up the variables needed
    	ResultSet rs = null;
    	Statement s = null;
    	String[] result = null;
    	PreparedStatement prstmt=null;
    	int size= 0;
    	// starts the try catch
    	try {
    		// sets the query up and places it into a prepare statement
    		String query = "SELECT count(*) FROM cust_book";
    		s = con.createStatement();
    		prstmt = con.prepareStatement(query);
    		//executes the query
    		rs = prstmt.executeQuery();
    		//places number of books currently in cust_book
    		while(rs.next()) {
    			size=rs.getInt(1);
    		}
    		//if there are no books in this file, then it returns that no books are currently borrowed
    		if(size ==0) {
    			return "There are currently no books being borrowed";
    		}
    		// makes the result array the size of the amount of books in the cust_book
    		result = new String[size];
    		//executes the query to recieve all loaned book
    		rs = s.executeQuery("SELECT title FROM book "+
    		"INNER JOIN cust_book ON book.isbn=cust_book.isbn");
    		//runs through all of the files and places them into the result array
    		int i =0;
    		while(rs.next()) {
    			result[i] = rs.getString(1);
    			i++;
    		}
    	//catches any sql errors that come up
    	}catch(SQLException sqlex) {
    		System.out.println(sqlex.getMessage());
    	}
    	// places results into a string to be returned and printed
    	String toReturn = "";
    	for(int i =1; i<result.length;i++) {
    		toReturn = toReturn+result[i]+"\r\n";
    	}
    	return toReturn;
	}

    public String showAuthor(int authorID) {
    	//preparing the variables
    	ResultSet rs = null;
    	Statement s = null;
    	String result = null;
    	//starts the try catch for the connection
    	try {
    		// checks if there is any authors in the author table with this author id
    		s = con.createStatement();
    		rs = s.executeQuery("SELECT count(*) FROM author WHERE authorid = "+authorID);
    		while(rs.next()){
    			int size = rs.getInt(1);
    			if(size < 1) {
    				return "There is no author with this authorID";
    			}
    		}
    		//gets the name of the author with this author ID
    		s = con.createStatement();
    		rs = s.executeQuery("SELECT name, surname FROM author " +
    				" WHERE authorid = "+authorID);
    		// places the result into a result string
    		while(rs.next()) {
    			result = rs.getString(1)+" "
    		+rs.getString(2);
    		}
    	//catches if there are any sql errors
    	}catch(SQLException sqlex) {
    		System.out.println(sqlex.getMessage());
    	}
    	//returns the result string
    	return result;
	}

    public String showAllAuthors() {
    	//setting up the variables
    	ResultSet rs = null;
    	Statement s = null;
    	String[] result = null;
    	PreparedStatement prstmt=null;
    	int size= 0;
    	//start of the try catch
    	try {
    		//starts by getting the size of the table itself, placing into a size variable
    		String query = "SELECT count(*) FROM author";
    		s = con.createStatement();
    		prstmt = con.prepareStatement(query);
    		rs = prstmt.executeQuery();
    		while(rs.next()) {
    			size=rs.getInt(1);
    		}
    		// makes it so the result array is the correct size.
    		result = new String[size];
    		// executes next query and places these into the result array
    		s = con.createStatement();
    		rs = s.executeQuery("SELECT name, surname FROM author");
    		int i =0;
    		while(rs.next()) {
    			result[i] = rs.getString(1) + " " + rs.getString(2);
    			i++;
    		}
    	// catches and prints the sql errors
    	}catch(SQLException sqlex) {
    		System.out.println(sqlex.getMessage());
    	}
    	// places result array into a readable string and returns the return string
    	String toReturn = "";
    	for(int i =1; i<result.length;i++) {
    		toReturn = toReturn+result[i]+"\r\n";
    	}
	return toReturn;
    }

    public String showCustomer(int customerID) {
    	// sets up the variables needed
    	ResultSet rs = null;
    	Statement s = null;
    	String result = null;
    	//starts the try catch
    	try {
    		// check that there is a customer with this customer id
    		s = con.createStatement();
    		rs = s.executeQuery("SELECT count(*) FROM customer WHERE customerid = "+customerID);
    		while(rs.next()){
    			int size = rs.getInt(1);
    			if(size < 1) {
    				return "There is no customer under this customerID";
    			}
    		}
    		//sends the query and places the result into a result string
    		s = con.createStatement();
    		rs = s.executeQuery("SELECT f_name, l_name, city FROM customer " +
    				" WHERE customerid = "+customerID);
    		while(rs.next()) {
    			result = rs.getString(1)+" "
    		+rs.getString(2)
    		+"	"+rs.getString(3);
    		}
    	// catches sql errors sent to users and then presents them to the user
    	}catch(SQLException sqlex) {
    		System.out.println(sqlex.getMessage());
    	}
    // returns the result string
	return result;
    }


    public String showAllCustomers() {
    	// sets up the variables
    	ResultSet rs = null;
    	Statement s = null;
    	String[] result = null;
    	PreparedStatement prstmt=null;
    	int size= 0;
    	// starts the try catch
    	try {
    		// checks if there are any customers within the customer table
    		String query = "SELECT count(*) FROM customer";
    		s = con.createStatement();
    		prstmt = con.prepareStatement(query);
    		rs = prstmt.executeQuery();
    		while(rs.next()) {
    			size=rs.getInt(1);
    		}
    		// tells the user if there are currently no customers in the table
    		if(size ==0) {
    			return "There are currently no customers";
    		}
    		// sets the size of the result array based on the size
    		result = new String[size];
    		// executes the query and places the result in the result array
    		s = con.createStatement();
    		rs = s.executeQuery("SELECT l_name, f_name, city FROM customer");
    		int i =0;
    		while(rs.next()) {
    			result[i] = rs.getString(1) + " " + rs.getString(2)+"	"+rs.getString(3);
    			i++;
    		}
    	// catches any sql error and sends to the programmer
    	}catch(SQLException sqlex) {
    		System.out.println(sqlex.getMessage());
    	}
    	//places all result array into a return string, and then proceeds to return this string
    	String toReturn = "";
    	for(int i =1; i<result.length;i++) {
    		toReturn = toReturn+result[i]+"\r\n";
    	}
	return toReturn;

    }

    public String borrowBook(int isbn, int customerID,
			     int day, int month, int year) {
    	// sets up the variables
    	ResultSet rs = null;
    	Statement s = null;
    	int size = 0;
    	//starts the try catch
    	try {
    		//checks whether the customer id exists in the customer table
    		s = con.createStatement();
    		rs = s.executeQuery("SELECT customerid FROM customer " +
    				" WHERE customerid = "+customerID);
    		while(rs.next()) {
    			size +=1;
    		}
    		if(size == 0) {
    			return "Sorry this customerID does not exist";
    		}
    		//checks whether this book exists within the book table
    		s = con.createStatement();
    		rs = s.executeQuery("SELECT count(*) FROM book WHERE isbn = "+isbn);
    		while(rs.next()){
    			size = rs.getInt(1);
    			if(size < 1) {
    				return "There is no book under this isbn";
    			}
    		}
    		// uses a lock in order to make sure that it inserts book into cust_book and selects the exact book
    		rs = s.executeQuery("BEGIN WORK;"+
    				"LOCK TABLE book IN SHARE MODE; " +
    				"SELECT * FROM book WHERE isbn = "+isbn + ";"
    				+"INSERT INTO cust_book VALUES "
    				+"("+isbn+",CAST('"+year+"-"+month+"-"+day+"' AS Date),"+customerID+");"
    				+" COMMIT WORK;");

    	//catches if any sql errors occur and sends to the programmer
    	}catch(SQLException sqlex) {
    		System.out.println(sqlex.getMessage());
    	}
    // gives a nice little return message
	return "Thank you for borrowing the book I hope that you enjoy.";
	};


    public String returnBook(int isbn, int customerid) {
    	// sets up the variables
    	ResultSet rs = null;
    	Statement s = null;
    	int size = 0;
    	//start of the try catch statement
    	try {
    		//checks whether the isbn exists within book table and whether the book needs to be returned at all
    		s = con.createStatement();
    		rs = s.executeQuery("SELECT customerid FROM cust_book " +
    				" WHERE customerid = "+customerid);
    		while(rs.next()) {
    			size +=1;
    		}
    		if(size == 0) {
    			return "Sorry this customer does not have anything to return right now.";
    		}
    		s = con.createStatement();
    		rs = s.executeQuery("SELECT count(*) FROM book WHERE isbn = "+isbn);
    		while(rs.next()){
    			size = rs.getInt(1);
    			if(size < 1) {
    				return "There is no book under this isbn";
    			}
    		}
    		// if there is then the book can be returned using a lock and instead of inserting cust_book, it deletes based on customerid and isbn
    		rs = s.executeQuery("BEGIN WORK;"+
    				"LOCK TABLE cust_book IN SHARE MODE; " +
    				"SELECT * FROM cust_book WHERE isbn = "+isbn
    				+ "AND customerid = " + customerid + ";"
    				+"DELETE FROM cust_book WHERE isbn = " +isbn
    				+"AND customerid = "+customerid+";"
    				+" COMMIT WORK;");
    	//catches any sql syntax errors and sends the to programmer
    	}catch(SQLException sqlex) {
    		System.out.println(sqlex.getMessage());
    	}
    //returns a nice little message
	return "Thank you for returning the book I hope that you enjoyed";
    }

    public void closeDBConnection() {
    	//closes the connection within a try catch, if any errors occur with sql syntax, programmer will be notified
    	 try {
    		 con.close();
    	 }catch(SQLException sqlex) {
    		 System.out.println(sqlex.getMessage());
    	 }
    }

    public String deleteCus(int customerID) {
    	//sets up all the variables that the program will need
    	Statement s = null;
    	int size= 0;
    	ResultSet rs = null;
    	//starts the try catch
    	try {
    		// creates the statement
    		s = con.createStatement();
    		// executes the query asking to be executed and then checks if there is any customer under this id
    		rs = s.executeQuery("SELECT count(*) FROM customer WHERE customerid = "+customerID);
    		while(rs.next()){
    			 size = rs.getInt(1);
    			if(size < 1) {
    				return "There is no customer under this customerID";
    			}
    		}
    		s = con.createStatement();
    		// executes the query asking to be executed and then checks if there is any customer under this id renting books
    		// if there is then it deletes this customer from this table as well
    		rs = s.executeQuery("SELECT count(*) FROM cust_book WHERE customerid = "+customerID);
    		while(rs.next()){
    			 size = rs.getInt(1);
    			if(size < 1) {
    				s=con.createStatement();
    	    		size = s.executeUpdate("DELETE FROM cust_book WHERE customerid = " + customerID);
    			}
    		}
    		//creates statements and deletes the customer from the customer table.
    		s=con.createStatement();
        	size = s.executeUpdate("DELETE FROM customer WHERE customerid = "+customerID);
    		}
    	// catches if there is any sql errors and tells the prgrammer
    	catch(SQLException sqlex) {
    		System.out.println(sqlex.getMessage());
    	}
    	// returns that this customer was deleted.
    	return "CustomerID " + customerID + " was deleted from the database.";
    }

    public String deleteAuthor(int authorID) {
    	// sets the variables up
    	ResultSet rs = null;
    	Statement s = null;
    	int size= 0;
    	// starts the try catch connection
    	try {
    		//creates the statement and checks whether there is an author under this id
    		s=con.createStatement();
			rs = s.executeQuery("SELECT count(*) FROM author WHERE authorid = "+authorID);
			while(rs.next()) {
				if(rs.getInt(1)==0) {
					return "There is no Author under that AuthorID";
				}
			}
			//checks that this author has a book and deletes the author from this table
    		s=con.createStatement();
    		rs = s.executeQuery("SELECT count(*) FROM book_author WHERE authorid = "+authorID);
    		while(rs.next()) {
    			if(rs.getInt(1)>0) {
    				s=con.createStatement();
    				size = s.executeUpdate("DELETE FROM book_author WHERE authorid = "+authorID);
    			}
    		}
    	//creates statement and deletes the author from the table based on author id
    		s=con.createStatement();
			size = s.executeUpdate("DELETE FROM author WHERE authorid = " + authorID);
    	}
    	catch(SQLException sqlex) {
    		System.out.println(sqlex.getMessage());
    	}
    	return "Author ID " + authorID + " was deleted from the database.";
    }

    public String deleteBook(int isbn) {
    	// sets up variables needed for this function
    	ResultSet rs = null;
    	Statement s = null;
    	int size= 0;
    	// starts the try catch statement
    	try {
    		//creates the statement and checks if there is a book with this isbn in the table
    		s=con.createStatement();
			rs = s.executeQuery("SELECT count(*) FROM book WHERE isbn = "+isbn);
			while(rs.next()) {
				if(rs.getInt(1)==0) {
					return "There is no book under that isbn";
				}
			}
			//checks if this book is in the book_author table and if so it is deleted
    		s=con.createStatement();
    		rs = s.executeQuery("SELECT count(*) FROM book_author WHERE isbn = "+isbn);
    		while(rs.next()) {
    			if(rs.getInt(1)>0) {
    				s=con.createStatement();
    				size = s.executeUpdate("DELETE FROM book_author WHERE isbn = "+isbn);
    			}
    		}
    		//deletes this book with the isbn from the table
        	s=con.createStatement();
    		size = s.executeUpdate("DELETE FROM book WHERE isbn = " + isbn);
    	}
    	// catches an incorrect sql syntax and alerts the programmer
    	catch(SQLException sqlex) {
    		System.out.println(sqlex.getMessage());
    	}
    	// returns the string saying it has been deleted
    	return "Book isbn " + isbn + " was deleted from the database.";
    }
}