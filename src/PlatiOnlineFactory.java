public class PlatiOnlineFactory {
	
	PlatiOnlineDAO createClientSQL(){
		return new ClientDAOMySql();
	}
	
	PlatiOnlineDAO createFurnizorSQL() {
		return new FurnizorDAOMySql();
	}
	
	PlatiOnlineDAO createFacturaSQL() {
		return new FacturaDAOMySql();
	}
	
	PlatiOnlineDAO createRelationshipSQL() {
		return new RelationshipDAOMySql();
	}
}
