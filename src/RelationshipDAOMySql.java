import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Statement;

public class RelationshipDAOMySql implements PlatiOnlineDAO {

	@Override
	public <E> int add(E element) {
		try {
			Relationship rs = (Relationship) element;
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"insert into relationship(CodClient, CodFurnizor, NrFactura) values (?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, rs.getCodClient());
			ps.setInt(2, rs.getCodFurnizor());
			ps.setInt(3, rs.getNrFactura());
			ps.executeUpdate();
			ResultSet res = ps.getGeneratedKeys();
			int id = -2;
			if(res.next()) {
				id = res.getInt(1);
			}
			closeConnection(conn);
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public <E> void addClientFurnizor(E element) {
		try {
			Relationship rs = (Relationship) element;
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"insert into relationship(CodClient, CodFurnizor, PlataAutomata) values (?,?,0)", Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, rs.getCodClient());
			ps.setInt(2, rs.getCodFurnizor());
			//ps.setInt(3, 0);
			ps.executeUpdate();
			closeConnection(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public <E> boolean update(E element) {
		try {
			Relationship rs = (Relationship) element;
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"update relationship set CodClient = ?, CodFurnizor = ?, PlataAutomata = ? where Id = ?");
			ps.setInt(1, rs.getCodClient());
			ps.setInt(2, rs.getCodFurnizor());
			//ps.setInt(3, rs.getNrFactura());
			ps.setInt(3, (rs.isPlataAutomata()) ? 1 : 0);
			ps.setInt(4, rs.getId());
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
			PreparedStatement ps = conn.prepareStatement("delete from relationship where Id = ?");
			ps.setInt(1, Id);
			int affectedRows = ps.executeUpdate();
			closeConnection(conn);
			return affectedRows == 1;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean deleteForDezabonare(int CodFurnizor, int CodClient) {
		try {
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement("delete from relationship where CodFurnizor = ? and CodClient = ? and NrFactura is null");
			ps.setInt(1, CodFurnizor);
			ps.setInt(2, CodClient);
			int affectedRows = ps.executeUpdate();
			closeConnection(conn);
			return affectedRows == 1;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Relationship findById(int CodClient) {
		try {
			Relationship rel = null;
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement("Select * from relationship where CodClient = ? and NrFactura is null");
			ps.setInt(1, CodClient);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int IdRel = rs.getInt(1);
				int codClient = rs.getInt(2);
				int CodFurnizor = rs.getInt(3);
				int NrFactura = rs.getInt(4);
				boolean PlataAutomata = (rs.getInt(5) == 1) ? true : false;
				rel = new Relationship(IdRel, codClient, CodFurnizor, NrFactura, PlataAutomata);
			}
			closeConnection(conn);
			return rel;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Relationship findAbonatByClientFurnizor(int CodClient, int CodFurnizor) {
		try {
			Relationship rel = null;
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement("Select * from relationship where CodClient = ? and CodFurnizor = ? and NrFactura is null");
			ps.setInt(1, CodClient);
			ps.setInt(2, CodFurnizor);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int IdRel = rs.getInt(1);
				int codClient = rs.getInt(2);
				int codFurnizor = rs.getInt(3);
				int NrFactura = rs.getInt(4);
				boolean PlataAutomata = (rs.getInt(5) == 1) ? true : false;
				rel = new Relationship(IdRel, codClient, codFurnizor, NrFactura, PlataAutomata);
			}
			closeConnection(conn);
			return rel;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Relationship[] getAll() {
		try {
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement("Select * from relationship");
			List<Relationship> listaRelationships = new ArrayList<>();
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int IdRel = rs.getInt(1);
				int CodClient = rs.getInt(2);
				int CodFurnizor = rs.getInt(3);
				int NrFactura = rs.getInt(4);
				listaRelationships.add(new Relationship(IdRel, CodClient, CodFurnizor, NrFactura));
			}
			closeConnection(conn);
			return listaRelationships.toArray(new Relationship[listaRelationships.size()]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(ClientDAOMySql.CONNECTION_URL, "root", "1234");
	}

	public void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}
}
