import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Statement;

public class FacturaDAOMySql implements PlatiOnlineDAO {

	@Override
	public <E> int add(E element) {
		try {
			Factura fa = (Factura) element;
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"insert into factura(Suma, DataEmisa, DataScadenta, Status) values (?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setDouble(1, fa.getSuma());
			ps.setString(2, fa.getDataEmisa().format(MainClass.formatter));
			ps.setString(3, fa.getDataScadenta().format(MainClass.formatter));
			ps.setInt(4, (fa.isStatus()) ? 1 : 0);
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
			Factura fa = (Factura) element;
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"update factura set Suma=?, DataEmisa=?, DataScadenta=?, Penalitati = ?, Status=? where NrFactura=?");
			ps.setDouble(1, fa.getSuma());
			ps.setString(2, fa.getDataEmisa().format(MainClass.formatter));
			ps.setString(3, fa.getDataScadenta().format(MainClass.formatter));
			ps.setDouble(4, fa.getPenalitati());
			ps.setInt(5, (fa.isStatus()) ? 1 : 0);
			ps.setInt(6, fa.getNrFactura());
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
			PreparedStatement ps = conn.prepareStatement("delete from factura where NrFactura = ?");
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
	public Factura findById(int Id) {
		try {
			Factura fa = null;
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement("Select * from factura where NumarFactura = ?");
			ps.setInt(1, Id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int NrFactura = rs.getInt(1);
				Double Suma = rs.getDouble(2);
				String DataEmisa = rs.getString(3);
				String DataScadenta = rs.getString(4);
				double Penalitati = rs.getDouble(5);
				boolean st = (rs.getInt(6) == 0) ? false : true;
				fa = new Factura(NrFactura, Suma, DataEmisa, DataScadenta, st);
				fa.setPenalitati(Penalitati);
			}
			closeConnection(conn);
			return fa;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Factura[] getAll() {
		try {
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement("Select * from factura");
			List<Factura> listaFacturi = new ArrayList<>();
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int NrFactura = rs.getInt(1);
				Double Suma = rs.getDouble(2);
				String DataEmisa = rs.getString(3);
				String DataScadenta = rs.getString(4);
				double Penalitati = rs.getDouble(5);
				boolean st = (rs.getInt(6) == 0) ? false : true;
				Factura fa = new Factura(NrFactura, Suma, DataEmisa, DataScadenta, st);
				fa.setPenalitati(Penalitati);
				listaFacturi.add(fa);
			}
			closeConnection(conn);
			return listaFacturi.toArray(new Factura[listaFacturi.size()]);
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
