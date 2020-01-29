import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class Factura implements Comparable<Factura> {
	private int NrFactura;
	private double Suma;
	private LocalDate DataEmisa;
	private LocalDate DataScadenta;
	private double Penalitati;
	private boolean Status;

	Factura() {
		NrFactura = 0;
		Suma = 0;
		DataEmisa = null;
		DataScadenta = null;
		Penalitati = 0;
		Status = false;
	}

	Factura(int NrFactura, double Suma, String DataEmisa, String DataScadenta, boolean Status) {
		this.NrFactura = NrFactura;
		this.Suma = Suma;
		this.DataEmisa = LocalDate.parse(DataEmisa, MainClass.formatter);
		this.DataScadenta = LocalDate.parse(DataScadenta, MainClass.formatter);
		this.Penalitati = 0;
		this.Status = Status;
	}

	public int getNrFactura() {
		return NrFactura;
	}

	public void setNrFactura(int nrFactura) {
		NrFactura = nrFactura;
	}

	public double getSuma() {
		return Suma;
	}

	public void setSuma(double suma) {
		Suma = suma;
	}

	public LocalDate getDataEmisa() {
		// return DataEmisa.format(MainClass.formatter);
		return DataEmisa;
	}

	public void setDataEmisa(LocalDate dataEmisa) {
		// DataEmisa = LocalDate.parse(dataEmisa, MainClass.formatter);
		DataEmisa = dataEmisa;
	}

	public LocalDate getDataScadenta() {
		// return DataScadenta.format(MainClass.formatter);
		return DataScadenta;
	}

	public void setDataScadenta(LocalDate dataScadenta) {
		// DataScadenta = LocalDate.parse(dataScadenta, MainClass.formatter);
		DataScadenta = dataScadenta;
	}

	public double getPenalitati() {
		return Penalitati;
	}

	public void setPenalitati(double penalitati) {
		Penalitati = penalitati;
	}

	public void updatePenalitati() {
		Penalitati = Suma * 0.1;
	}

	public boolean isStatus() {
		return Status;
	}

	public void setStatus(boolean status) {
		Status = status;
	}

	private Furnizor getFurnizorByBill() {
		try {
			Connection conn = ClientDAOMySql.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"SELECT furnizor.* FROM furnizor, relationship where relationship.NrFactura = ? and relationship.CodFurnizor = furnizor.CodFurnizor;");
			ps.setInt(1, NrFactura);
			ResultSet rs = ps.executeQuery();
			Furnizor fr = null;
			if (rs.next()) {
				int CodFurnizor = rs.getInt(1);
				String NumeFurnizor = rs.getString(2);
				String TipFurnizor = rs.getString(3);
				String Username = rs.getString(4);
				String Password = rs.getString(5);
				fr = new Furnizor(CodFurnizor, NumeFurnizor, TipFurnizor, Username, Password);
			}
			return fr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Client getClientByBill() {
		try {
			Connection conn = ClientDAOMySql.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"SELECT client.* FROM client, relationship where relationship.NrFactura = ? and relationship.CodClient = client.CodClient;");
			ps.setInt(1, NrFactura);
			ResultSet rs = ps.executeQuery();
			Client cl = null;
			if (rs.next()) {
				int CodClient = rs.getInt(1);
				String Nume = rs.getString(2);
				double SumaCont = rs.getDouble(3);
				String Username = rs.getString(4);
				String Password = rs.getString(5);
				cl = new Client(CodClient, Nume, SumaCont, Username, Password);
			}
			ClientDAOMySql.closeConnection(conn);
			return cl;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		String status = (!Status) ? "Neachitat" : "Achitat";
		Furnizor furn = getFurnizorByBill();
		return "Factura Nr. " + NrFactura + "\n\tValoare: >>>>> " + Suma + " Lei <<<<<" + "\n\tFurnizor: "
				+ furn.getNume() + " - " + furn.getTip() + "\n\tData Emisa: " + DataEmisa.format(MainClass.formatter)
				+ "\n\tData Scadenta: " + DataScadenta.format(MainClass.formatter) + "\n\tPenalitati: " + Penalitati
				+ " Lei" + "\n\tStatus: " + status;
	}

	@Override
	public int compareTo(Factura fa) {
		if (this.DataEmisa.isBefore(fa.DataEmisa))
			return 1;
		else if (this.DataEmisa.isAfter(fa.DataEmisa))
			return -1;
		else
			return 0;
	}
}
