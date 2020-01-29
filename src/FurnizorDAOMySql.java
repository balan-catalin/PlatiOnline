import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.mysql.jdbc.Statement;

public class FurnizorDAOMySql implements PlatiOnlineDAO {

	@Override
	public <E> int add(E element) {
		try {
			Furnizor fr = (Furnizor) element;
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"insert into furnizor(NumeFurnizor,TipFurnizor,Username,Password) values (?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, fr.getNume());
			ps.setString(2, fr.getTip());
			ps.setString(3, fr.getUsername());
			ps.setString(4, fr.getPassword());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			int id = -2;
			if(rs.next()) {
				id = rs.getInt(1);
			}
			closeConnection(conn);
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

	}

	@Override
	public <E> boolean update(E element) {
		try {
			Furnizor fr = (Furnizor) element;
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"update furnizor set NumeFurnizor=?, TipFurnizor=?, Username=?, Password=? where CodFurnizor=?");
			ps.setString(1, fr.getNume());
			ps.setString(2, fr.getTip());
			ps.setString(3, fr.getUsername());
			ps.setString(4, fr.getPassword());
			ps.setInt(5, fr.getCodFurnizor());
			int affectedRows = ps.executeUpdate();
			closeConnection(conn);
			return affectedRows == 1;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteById(int Id) {
		try {
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement("delete from furnizor where CodFurnizor = ?");
			ps.setInt(1, Id);
			int affectedRows = ps.executeUpdate();
			closeConnection(conn);
			return affectedRows == 1;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Furnizor findById(int Id) {
		try {
			Furnizor fr = null;
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement("Select * from furnizor where CodFurnizor = ?");
			ps.setInt(1, Id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int CodFurnizor = rs.getInt(1);
				String NumeFurnizor = rs.getString(2);
				String TipFurnizor = rs.getString(3);
				String Username = rs.getString(4);
				String Password = rs.getString(5);
				fr = new Furnizor(CodFurnizor, NumeFurnizor, TipFurnizor, Username, Password);
			}
			closeConnection(conn);
			return fr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Furnizor[] getAll() {
		try {
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement("Select * from Furnizor");
			List<Furnizor> listaFurnizori = new ArrayList<>();
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int CodFurnizor = rs.getInt(1);
				String NumeFurnizor = rs.getString(2);
				String TipFurnizor = rs.getString(3);
				String Username = rs.getString(4);
				String Password = rs.getString(5);
				listaFurnizori.add(new Furnizor(CodFurnizor, NumeFurnizor, TipFurnizor, Username, Password));
			}
			closeConnection(conn);
			return listaFurnizori.toArray(new Furnizor[listaFurnizori.size()]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Utilizator checkUser(String Username, String Password) {
		try {
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"Select * from furnizor where Username = ? and Password = ?");
			ps.setString(1, Username);
			ps.setString(2, Password);
			ResultSet rs = ps.executeQuery();
			Furnizor fr = null;
			if(rs.next()) {
				int CodFurnizor = rs.getInt(1);
				String NumeFurnizor = rs.getString(2);
				String TipFurnizor = rs.getString(3);
				String username = rs.getString(4);
				String password = rs.getString(5);
				fr = new Furnizor(CodFurnizor, NumeFurnizor, TipFurnizor, username, password);
			}
			closeConnection(conn);
			return fr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(ClientDAOMySql.CONNECTION_URL, "root", "1234");
	}

	public static void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}
}
