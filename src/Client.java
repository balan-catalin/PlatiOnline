import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Client extends Utilizator {
	private int CodClient;
	private String Nume;
	private double SumaCont;

	Client() {
		super();
		CodClient = 0;
		Nume = null;
		SumaCont = 0;
	}

	Client(String Nume, String Username, String Password) {
		super(Username, Password);
		CodClient = 0;
		this.Nume = Nume;
		SumaCont = 0.0;
	}

	Client(int CodClient, String Nume, double SumaCont, String Username, String Password) {
		super(Username, Password);
		this.CodClient = CodClient;
		this.Nume = Nume;
		this.SumaCont = SumaCont;
	}

	public int getCodClient() {
		return CodClient;
	}

	public void setCodClient(int codClient) {
		CodClient = codClient;
	}

	public String getNume() {
		return Nume;
	}

	public void setNume(String nume) {
		Nume = nume;
	}

	public double getSumaCont() {
		return SumaCont;
	}

	public void setSumaCont(double sumaCont) {
		SumaCont = sumaCont;
	}

	public List<Furnizor> getFurnizoriAbonati() {
		try {
			Connection conn = ClientDAOMySql.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"SELECT furnizor.* FROM furnizor, relationship where furnizor.CodFurnizor = relationship.CodFurnizor and relationship.CodClient = ? and relationship.NrFactura is null");
			ps.setInt(1, CodClient);
			ResultSet rs = ps.executeQuery();
			List<Furnizor> listaFurnizori = new ArrayList<>();
			while (rs.next()) {
				int CodFurnizor = rs.getInt(1);
				String NumeFurnizor = rs.getString(2);
				String TipFurnizor = rs.getString(3);
				String Username = rs.getString(4);
				String Password = rs.getString(5);
				listaFurnizori.add(new Furnizor(CodFurnizor, NumeFurnizor, TipFurnizor, Username, Password));
			}
			return listaFurnizori;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Furnizor> getFurnizoriNeabonati() {
		try {
			Connection conn = ClientDAOMySql.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"SELECT furnizor.* FROM furnizor where CodFurnizor not in (select CodFurnizor from relationship where CodClient = ? and NrFactura is null)");
			ps.setInt(1, CodClient);
			ResultSet rs = ps.executeQuery();
			List<Furnizor> listaFurnizori = new ArrayList<>();
			while (rs.next()) {
				int CodFurnizor = rs.getInt(1);
				String NumeFurnizor = rs.getString(2);
				String TipFurnizor = rs.getString(3);
				String Username = rs.getString(4);
				String Password = rs.getString(5);
				listaFurnizori.add(new Furnizor(CodFurnizor, NumeFurnizor, TipFurnizor, Username, Password));
			}
			return listaFurnizori;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Furnizor> getFurnizorCuFaraPlataAutomata(boolean plataAutomata) {
		try {
			Connection conn = ClientDAOMySql.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"SELECT furnizor.* FROM furnizor, relationship where relationship.NrFactura is null and relationship.PlataAutomata = ? and CodClient = ? and furnizor.CodFurnizor = relationship.CodFurnizor");
			ps.setInt(1, (plataAutomata) ? 1 : 0);
			ps.setInt(2, CodClient);
			ResultSet rs = ps.executeQuery();
			List<Furnizor> listaFurnizori = new ArrayList<>();
			while (rs.next()) {
				int CodFurnizor = rs.getInt(1);
				String NumeFurnizor = rs.getString(2);
				String TipFurnizor = rs.getString(3);
				String Username = rs.getString(4);
				String Password = rs.getString(5);
				listaFurnizori.add(new Furnizor(CodFurnizor, NumeFurnizor, TipFurnizor, Username, Password));
			}
			return listaFurnizori;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Factura> getFacturiByClient() {
		try {
			Connection conn = ClientDAOMySql.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"select factura.* from factura, relationship where relationship.NrFactura = factura.NrFactura and codClient = ?");
			ps.setInt(1, CodClient);
			ResultSet rs = ps.executeQuery();
			List<Factura> listaFacturi = new ArrayList<>();
			while (rs.next()) {
				int NrFactura = rs.getInt(1);
				Double Suma = rs.getDouble(2);
				String DataEmisa = rs.getString(3);
				String DataScadenta = rs.getString(4);
				double penalitati = rs.getDouble(5);
				boolean st = (rs.getInt(6) == 0) ? false : true;
				Factura fa = new Factura(NrFactura, Suma, DataEmisa, DataScadenta, st);
				fa.setPenalitati(penalitati);
				listaFacturi.add(fa);
			}
			return listaFacturi;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Factura> getFacturiByClientFurnizor(int CodFurnizor) {
		try {
			Connection conn = ClientDAOMySql.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"select factura.* from factura, relationship where relationship.NrFactura = factura.NrFactura and codClient = ? and codFurnizor = ?");
			ps.setInt(1, CodClient);
			ps.setInt(2, CodFurnizor);
			ResultSet rs = ps.executeQuery();
			List<Factura> listaFacturi = new ArrayList<>();
			while (rs.next()) {
				int NrFactura = rs.getInt(1);
				Double Suma = rs.getDouble(2);
				String DataEmisa = rs.getString(3);
				String DataScadenta = rs.getString(4);
				double penalitati = rs.getDouble(5);
				boolean st = (rs.getInt(6) == 0) ? false : true;
				Factura fa = new Factura(NrFactura, Suma, DataEmisa, DataScadenta, st);
				fa.setPenalitati(penalitati);
				listaFacturi.add(fa);
			}
			return listaFacturi;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Factura> getFacturiNeplatiteByFurnizor(int CodFurnizor) {
		try {
			Connection conn = ClientDAOMySql.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"select factura.* from factura, relationship where relationship.NrFactura = factura.NrFactura and codClient = ? and relationship.CodFurnizor = ? and factura.Status = 0");
			ps.setInt(1, CodClient);
			ps.setInt(2, CodFurnizor);
			ResultSet rs = ps.executeQuery();
			List<Factura> listaFacturi = new ArrayList<>();
			while (rs.next()) {
				int NrFactura = rs.getInt(1);
				Double Suma = rs.getDouble(2);
				String DataEmisa = rs.getString(3);
				String DataScadenta = rs.getString(4);
				double penalitati = rs.getDouble(5);
				boolean st = (rs.getInt(6) == 0) ? false : true;
				Factura fa = new Factura(NrFactura, Suma, DataEmisa, DataScadenta, st);
				fa.setPenalitati(penalitati);
				listaFacturi.add(fa);
			}
			return listaFacturi;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Factura> getFacturiNeplatiteById() {
		try {
			Connection conn = ClientDAOMySql.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"select factura.* from factura, relationship where relationship.NrFactura = factura.NrFactura and codClient = ? and Status = 0");
			ps.setInt(1, CodClient);
			ResultSet rs = ps.executeQuery();
			List<Factura> listaFacturi = new ArrayList<>();
			while (rs.next()) {
				int NrFactura = rs.getInt(1);
				Double Suma = rs.getDouble(2);
				String DataEmisa = rs.getString(3);
				String DataScadenta = rs.getString(4);
				double penalitati = rs.getDouble(5);
				boolean st = (rs.getInt(6) == 0) ? false : true;
				Factura fa = new Factura(NrFactura, Suma, DataEmisa, DataScadenta, st);
				fa.setPenalitati(penalitati);
				listaFacturi.add(fa);
			}
			return listaFacturi;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return "Cod-> " + CodClient + "     Nume-> " + Nume;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (!(o.getClass() == getClass()))
			return false;
		Client cl = (Client) o;
		return cl.getCodClient() == CodClient && Objects.equals(cl.getNume(), Nume);
	}
}
