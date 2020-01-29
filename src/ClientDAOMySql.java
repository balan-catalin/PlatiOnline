import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClientDAOMySql implements PlatiOnlineDAO{
	
	public static final String CONNECTION_URL = "jdbc:mysql://localhost/plati_db?useSSL=false";
	
	//v
	@Override
	public <E> int add(E element) {
		try {
			Client cl = (Client)element;
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"Insert into client (Nume, SumaCont, Username, Password) values (?,?,?,?)", 
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, cl.getNume());
			ps.setDouble(2, cl.getSumaCont());
			ps.setString(3, cl.getUsername());
			ps.setString(4, cl.getPassword());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			int id = -2;
			if(rs.next()) {
				id = rs.getInt(1);
			}
			closeConnection(conn);
			return id;
		}
		catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	//v
	@Override
	public <E> boolean update(E element) {
		try {
			Client cl = (Client)element;
			Connection conn =getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"Update client set SumaCont = ? where CodClient = ?");
			ps.setDouble(1, cl.getSumaCont());
			ps.setInt(2, cl.getCodClient());
			int affectedRows = ps.executeUpdate();
			closeConnection(conn);
			return affectedRows == 1;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	//v
	@Override
	public boolean deleteById(int Id) {
		try {
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"delete from client where CodClient = ?");
			ps.setInt(1, Id);
			int affectedRows = ps.executeUpdate();
			return affectedRows == 1;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	//v
	@Override
	public Client findById(int Id) {
		Client cl = null;
		try {
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"select * from client where CodClient = ?");
			ps.setInt(1, Id);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				int CodClient = rs.getInt(1);
				String Nume = rs.getString(2);
				double SumaCont = rs.getDouble(3);
				String Username = rs.getString(4);
				String Password = rs.getString(5);
				cl = new Client(CodClient,Nume,SumaCont,Username,Password);
			}
			return cl;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	//v
	@Override
	public Client[] getAll() {
		try {
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"select * from client");
			ResultSet rs = ps.executeQuery();
			List<Client> listaClienti = new ArrayList<Client>();
			while(rs.next()) {
				int CodClient = rs.getInt(1);
				String Nume = rs.getString(2);
				double SumaCont = rs.getDouble(3);
				String Username = rs.getString(4);
				String Password = rs.getString(5);
				listaClienti.add(new Client(CodClient,Nume,SumaCont,Username,Password));
			}
			closeConnection(conn);
			return listaClienti.toArray(new Client[listaClienti.size()]);		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Utilizator checkUser(String Username, String Password) {
		try {
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"Select * from client where Username = ? and Password = ?");
			ps.setString(1, Username);
			
			ps.setString(2, Password);
			ResultSet rs = ps.executeQuery();
			Client cl = null;
			if(rs.next()) {
				int CodClient = rs.getInt(1);
				String Nume = rs.getString(2);
				double SumaCont = rs.getDouble(3);
				String username = rs.getString(4);
				String password = rs.getString(5);
				cl = new Client(CodClient,Nume,SumaCont,username,password);
			}
			closeConnection(conn);
			return cl;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(CONNECTION_URL, "root", "1234");
	}

	public static void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}
	
}
