package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	//Date instance
	Date date = new Date();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
	String dateFormat = formatter.format(date);

	// constructor
	public Dao() {
	  
	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
							+ "&user=fp411&password=411");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		final String createTicketsTable = "CREATE TABLE kaust2_tickets(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200), date VARCHAR(10))";
		final String createUsersTable = "CREATE TABLE kaust2_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";
		final String createArchiveTable = "CREATE TABLE kaust2_archive(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200), date VARCHAR(10))";

		try {

			// execute queries to create tables

			statement = getConnection().createStatement();

			statement.executeUpdate(createTicketsTable);
			statement.executeUpdate(createUsersTable);
			statement.executeUpdate(createArchiveTable);
			System.out.println("Created tables in given database...");

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// add users to user table
		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {

				sql = "insert into kaust2_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
						+ rowData.get(1) + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql);
			}
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int insertRecords(String ticketName, String ticketDesc) {
		int id = 0;

		try {
			statement = getConnection().createStatement();
			statement.executeUpdate("Insert into kaust2_tickets" + "(ticket_issuer, ticket_description, date) values(" + " '"
					+ ticketName + "','" + ticketDesc + "','" + dateFormat + "')", Statement.RETURN_GENERATED_KEYS);

			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Records inserted");
		return id;

	}

	//Reads all current records within the table
	public ResultSet readRecords() {
		System.out.println("Creating statement....");
		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM kaust2_tickets");
			System.out.println("Records read");
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}

	//Reads only the current users opened tickets
	public ResultSet readYourTickets(String ticketIssuer) {
		System.out.println("Reading your tickets....");
		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM kaust2_tickets WHERE ticket_issuer = '" + ticketIssuer + "'");
			System.out.println("Your records read");

		} catch(SQLException e7) {
			e7.printStackTrace();
		}
		return results;
	}
	
	//Reads all current records in Archives
	public ResultSet readArchiveRecords() {
		System.out.println("Reading archives...");
		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM kaust2_archive");
			System.out.println("Archive read");
			//connect.close();
		} catch (SQLException e5) {
			e5.printStackTrace();
		}
		return results;
	}

	//Updates the records of a specific ticket with new information
	public int updateRecords(String ticketID, String ticketDesc) {
		System.out.println("Creating statement....");
		int rowsAffected = 0;
		try {
			statement = connect.createStatement();
			//Finds specific ticket and updates the description
			String sql = "UPDATE kaust2_tickets SET ticket_description = '" + ticketDesc + "' WHERE ticket_id = " + ticketID;
			rowsAffected = statement.executeUpdate(sql);
			System.out.println("Records updated: " + rowsAffected);
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
		return rowsAffected;
	}

	//Deletes selected records from the table
	public void deleteRecords(String ticketID) {
		System.out.println("Deleting Ticket....");
		try {
			statement = connect.createStatement();
			//Finds specific ticket
			String sql = "DELETE FROM kaust2_tickets WHERE ticket_id = ?";
			int response = JOptionPane.showConfirmDialog(null, "Delete ticket #"
				+ ticketID + "?", "Confirm", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
			//If-Else statements for different available options
			if (response == JOptionPane.NO_OPTION) {
				System.out.println("No ticket deleted");
			}
			else if (response == JOptionPane.YES_OPTION) {
				PreparedStatement preparedStatement = connect.prepareStatement(sql);
				preparedStatement.setString(1, ticketID);
				int rowsAffected = preparedStatement.executeUpdate();
				if (rowsAffected > 0) {
					System.out.println("Ticket deleted");
				}
				else {
					System.out.println("No Ticket found with ID: " + ticketID);
				}
			}
			else if (response == JOptionPane.CLOSED_OPTION) {
				System.out.println("Request Cancelled");
			}
		} catch (SQLException e3) {
			e3.printStackTrace();
		}
	}

	//closes tickets and moves them to archives and allows the description to be updated
	public void closeRecords(String ticketID, String ticketDesc) {
		System.out.println("Moving records...");
		try {
			
			statement = connect.createStatement();
			//Asks what record to close
			String moveSQL = "INSERT INTO kaust2_archive (ticket_id, ticket_issuer, ticket_description, date) " +
						  "SELECT ticket_id, ticket_issuer, ticket_description, date " +
						  "FROM kaust2_tickets " +
						  "WHERE ticket_id = ?";
			String deleteSQL = "DELETE FROM kaust2_tickets WHERE ticket_id = ?";
			String updateSQL = "UPDATE kaust2_tickets SET ticket_description = ? WHERE ticket_id = ?";
			int response = JOptionPane.showConfirmDialog(null, "Close ticket #" 
			+ ticketID + "?", "Confirm", JOptionPane.YES_NO_OPTION, 
			JOptionPane.QUESTION_MESSAGE);
			
			//If-Else statements for different avilable options
			if (response == JOptionPane.NO_OPTION) {
				System.out.println("No ticket closed");
			}
			else if (response == JOptionPane.YES_OPTION) {

				// Update the description
				PreparedStatement updateStatement = connect.prepareStatement(updateSQL);
				updateStatement.setString(1, ticketDesc);
				updateStatement.setString(2, ticketID);
				int rowsUpdated = updateStatement.executeUpdate();

				
				if (rowsUpdated > 0) {
					System.out.println("Ticket description updated");
				
					PreparedStatement moveStatement = connect.prepareStatement(moveSQL);
					moveStatement.setString(1, ticketID);
					int rowsAffected = moveStatement.executeUpdate();
					
					if (rowsAffected > 0) {
						System.out.println("Ticket moved to archives");

						// Now delete the record
						PreparedStatement deleteStatement = connect.prepareStatement(deleteSQL);
						deleteStatement.setString(1, ticketID);
						int rowsDeleted = deleteStatement.executeUpdate();

						if (rowsDeleted > 0) {
							System.out.println("Ticket deleted from original table");
						}
						else {
							System.out.println("Failed to delete record from original table");
						}
					}
					else {
						System.out.println("No ticket found with ID: " + ticketID);
					}
				}
				else {
					System.out.println("Description failed to update");
				}
			}
			else if (response == JOptionPane.CLOSED_OPTION) {
				System.out.println("Request Cancelled"); 
			}
		} catch (SQLException e4) {
			e4.printStackTrace();
		}
	}
}



