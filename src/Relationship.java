public class Relationship {
	private int Id;
	private int CodClient;
	private int CodFurnizor;
	private int NrFactura;
	private boolean PlataAutomata;
	
	Relationship () {
		Id=0;
		CodClient=0;
		CodFurnizor=0;
		NrFactura=0;
	}
	
	Relationship (int Id, int CodClient, int CodFurnizor) {
		this.Id=Id;
		this.CodClient=CodClient;
		this.CodFurnizor=CodFurnizor;
		PlataAutomata = false;
	}
	
	Relationship (int Id, int CodClient, int CodFurnizor, int NrFactura) {
		this.Id=Id;
		this.CodClient=CodClient;
		this.CodFurnizor=CodFurnizor;
		this.NrFactura=NrFactura;
	}
	
	Relationship(int Id, int CodClient, int CodFurnizor, int NrFactura, boolean PlataAutomata){
		this.Id=Id;
		this.CodClient=CodClient;
		this.CodFurnizor=CodFurnizor;
		this.NrFactura=NrFactura;
		this.PlataAutomata = PlataAutomata;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getCodClient() {
		return CodClient;
	}

	public void setCodClient(int codClient) {
		CodClient = codClient;
	}

	public int getCodFurnizor() {
		return CodFurnizor;
	}

	public void setCodFurnizor(int codFurnizor) {
		CodFurnizor = codFurnizor;
	}

	public int getNrFactura() {
		return NrFactura;
	}

	public void setNrFactura(int nrFactura) {
		NrFactura = nrFactura;
	}
	
	public boolean isPlataAutomata() {
		return PlataAutomata;
	}
	
	public void setPlataAutomata(boolean plataAutomata) {
		PlataAutomata = plataAutomata;
	}

	@Override
	public String toString() {
		return "Relationship [Id=" + Id + ", CodClient=" + CodClient + ", CodFurnizor=" + CodFurnizor + ", NrFactura="
				+ NrFactura + "]";
	}
}
