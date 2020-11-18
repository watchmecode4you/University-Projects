import java.sql.*;

public class Connect{
	private static final String DB_URL = "jdbc:mysql://localhost:3306/address_book";
    private static final String USER = "root";
    private static final String PASS = "";
    private static final String driver = "com.mysql.jdbc.Driver";
    public Connection con ;
       
    public void getConn() throws SQLException {
    	try{
    		Class.forName(driver);
    		con = DriverManager.getConnection(DB_URL,USER,PASS);
    		System.out.println("Successfuly connected to database");
    	}catch (ClassNotFoundException ex){
    		System.out.println("Driver not found");
    	}	
    }
}
