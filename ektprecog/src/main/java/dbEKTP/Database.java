package dbEKTP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyStore.ProtectionParameter;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class Database {
	
	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		String driver = "org.gjt.mm.mysql.Driver";
		String url = "jdbc:mysql://localhost/skripsiktprecog";
		String username = "root";
		String password = "";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, username, password);
		return conn;
	}
	
	
	public static ArrayList<Integer> getId(Connection conn, String namatb) throws SQLException{
		ArrayList<Integer> id = new ArrayList<>();
		String sql = "select id from " +namatb;
		PreparedStatement psmt = conn.prepareStatement(sql);
		ResultSet rs = psmt.executeQuery();
		while(rs.next()){
			id.add(rs.getInt("id"));
		}
		return id;
	}
	
	//insert data
	public static void insertObject(String path, Connection conn, String karakter, String namatb) throws FileNotFoundException, SQLException{
		InputStream inputStream = new FileInputStream(new File(path));
		String sql = "INSERT INTO " +namatb+ " (karakter,fitur) values(?,?)";
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setString(1, karakter);
		statement.setBlob(2, inputStream);
		statement.executeUpdate();
		
	} 
	
	public void insertData(ArrayList<Double> data, ArrayList<Double> data2, String Karakter, String namatb){
		try {
			ObjectFeatureCrossings ofc = new ObjectFeatureCrossings();
			ofc.data=data;
			ofc.data2=data2;
			ofc.karakter=Karakter;
			FileOutputStream fileout = new FileOutputStream("data.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileout);
			out.writeObject(ofc);
			out.close();
			fileout.close();
			Connection conn = this.getConnection();
			insertObject("data.ser", conn, Karakter, namatb);
			conn.close();
		} catch (Exception e) {
			System.out.print("Error: " + e.getMessage());
		}
	}
	
	public void selectData(int id, String namatb) throws ClassNotFoundException, SQLException, IOException{
		Connection conn = this.getConnection();
		String sql = "select fitur from " +namatb+ " where id = ?";
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setInt(1, id);
		ResultSet rs = statement.executeQuery();
		File file = new File("Out.ser"); 
		FileOutputStream output = new FileOutputStream(file);
		while (rs.next()){
			
			InputStream input = rs.getBinaryStream("fitur"); 
			
			byte[] buffer = new byte[1024]; 
			
			while (input.read(buffer)>0) {
				output.write(buffer);     
			}
			
		}
		conn.close();
			
	}

}
