import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Furnizor extends Utilizator {
	private int CodFurnizor;
	private String Nume;
	private String Tip;

	Furnizor() {
		super();
		CodFurnizor = 0;
		Nume = null;
		Tip = null;
	}

	Furnizor(int CodFurnizor, String Nume, String Tip, String Username, String Password) {
		super(Username, Password);
		this.CodFurnizor = CodFurnizor;
		this.Nume = Nume;
		this.Tip = Tip;
	}

	public int getCodFurnizor() {
		return CodFurnizor;
	}

	public void setCodFurnizor(int codFurnizor) {
		CodFurnizor = codFurnizor;
	}

	public String getNume() {
		return Nume;
	}

	public void setNume(String nume) {
		Nume = nume;
	}

	public String getTip() {
		return Tip;
	}

	public void setTip(String tip) {
		Tip = tip;
	}
	
	public List<Client> getClientiAbonati(){
		try {
			Connection conn = ClientDAOMySql.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"select client.* from client, relationship where client.CodClient = relationship.CodClient and relationship.CodFurnizor = ? and relationship.NrFactura is null;");
			ps.setInt(1, CodFurnizor);
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
			ClientDAOMySql.closeConnection(conn);
			return listaClienti;		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Factura> getFacturiEmise(){
		try {
			Connection conn = ClientDAOMySql.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"select factura.* from factura, relationship where relationship.CodFurnizor = ? and relationship.NrFactura= factura.NrFactura; ");
			ps.setInt(1, CodFurnizor);
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
			ClientDAOMySql.closeConnection(conn);
			return listaFacturi;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return CodFurnizor + "). " + Nume + " || " + Tip;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null)
			return false;
		if(!(o.getClass() == this.getClass()))
			return false;
		Furnizor furn = (Furnizor) o;
		return CodFurnizor == furn.getCodFurnizor() && 
				Objects.equals(Nume, furn.getNume()) && 
				Objects.equals(Tip, furn.getTip());

	}
}
