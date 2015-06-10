package paketti;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {

	String driver = "org.sqlite.JDBC";
	String table = "Osoitekirja";
	String database = "jdbc:sqlite:" + table + ".db";
	
	Connection connection = null;
	Statement statement = null;
	ResultSet resultSet = null;
	PreparedStatement preparedStatement = null;
	
	//ctor
	DataBase() throws ClassNotFoundException, SQLException
	{
		connect();
	}
	
	
	//establishing the connection to the database
	private void connect() throws ClassNotFoundException, SQLException{
		
		Class.forName(driver);
		connection = DriverManager.getConnection(database);
		statement = connection.createStatement();
		createTableIfNotExists();

	}
	
	//create table if it doesnt exist (e.g. on the first time running the program)
	private void createTableIfNotExists() throws SQLException{
		statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + " (`ID` INTEGER PRIMARY KEY AUTOINCREMENT, "
																			+ "Nimi TEXT, "
																			+ "Osoite TEXT, "
																			+ "Puhelinnumero TEXT, "
																			+ "Email TEXT);");
		resultSet = execute("SELECT * FROM Osoitekirja");	
		if (resultSet.next() == false){
		insert("Esimerkki", "Esimerkki", "Esimerkki", "Esimerkki");		
		}
	}
	
	//disconnect from the database
	public void disconnect() throws SQLException{

		resultSet.close();
		statement.close();
		connection.close();
		System.out.println("Closed database successfully!");

	}
	
	//execute query with given string (hardcoded)
	public ResultSet execute(String query) throws SQLException{
		
		resultSet = statement.executeQuery(query);

		return resultSet;
	}
	
	//deletes data depending on the id. ID is determined properly in MainWindow
	public void delete(int id) throws SQLException{
		
		preparedStatement = connection.prepareStatement("DELETE FROM Osoitekirja WHERE ID = ?");
		preparedStatement.setInt(1, id);
		preparedStatement.executeUpdate();
	}
	
	//adds a new row to the database
	public void insert(String name, String address, String phone, String email)
			throws SQLException{

		preparedStatement = connection.prepareStatement("INSERT INTO Osoitekirja (Nimi, Osoite, Puhelinnumero, Email) VALUES (?, ?, ?, ?)");
		preparedStatement.setString(1, name);
		preparedStatement.setString(2, address);
		preparedStatement.setString(3, phone);
		preparedStatement.setString(4, email);
		preparedStatement.executeUpdate();

	}
	
	//updates a row 
	public void update(String name, String address, String phone, String email, int id)
			throws SQLException{

		preparedStatement = connection.prepareStatement("UPDATE Osoitekirja SET Nimi = ?, Osoite = ?, Puhelinnumero = ?, Email = ? WHERE ID = ?");
		preparedStatement.setString(1, name);
		preparedStatement.setString(2, address);
		preparedStatement.setString(3, phone);
		preparedStatement.setString(4, email);
		preparedStatement.setInt(5, id);
		preparedStatement.executeUpdate();

	}
	
	//search with given keyword, returns resultSet with all the information that was found
	public ResultSet search(String searchQuery) throws SQLException{

			preparedStatement = connection.prepareStatement("SELECT * FROM Osoitekirja WHERE Nimi LIKE ? OR Osoite LIKE ? OR Puhelinnumero LIKE ? OR Email LIKE ?");
			preparedStatement.setString(1, "%" + searchQuery + "%");
			preparedStatement.setString(2, "%" + searchQuery + "%");
			preparedStatement.setString(3, "%" + searchQuery + "%");
			preparedStatement.setString(4, "%" + searchQuery + "%");
			resultSet = preparedStatement.executeQuery();
			
			return (resultSet);
	}
	
}
