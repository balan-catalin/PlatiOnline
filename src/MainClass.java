import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MainClass {
	private Scanner sc = new Scanner(System.in);
	static Utilizator ActiveUser = null;
	Furnizor ActiveFurnizor;
	Client ActiveClient;
	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private DecimalFormat decimalFormat = new DecimalFormat("#.##");

	public static void main(String[] args) {
		MainClass mc = new MainClass();
		mc.generareFacturi();
		mc.runMenu();
	}

	public void runMenu() {
		boolean exit = false;
		do {
			headerMenu();
			switch (choice()) {
			case 0:
				exit = exitApplication();
				break;
			case 1:
				if (logIn())
					exit = clientLoggedIn();
				break;
			case 2:
				signUp();
				break;
			default:
				System.out.println("\nIntroduceti o optiune valida!");
			}
		} while (!exit);
	}

	private boolean clientLoggedIn() {
		boolean exit = false;
		if (ActiveClient != null) {
			plataAutomata();
			notificareDezabonare();
			do {
				clientMenu();
				switch (choice()) {
				case 0:
					exit = exitApplication();
					break;
				case 1:
					while (!abonare())
						abonare();
					break;
				case 2:
					while (!addCash()) {
					}
					break;
				case 3:
					viewBills();
					break;
				case 4:
					while (platesteFacturi())
						;
					break;
				case 5:
					while (abonarePlataAutomata())
						;
					break;
				case 6:
					while (dezabonarePlataAutomata())
						;
					break;
				case 7:
					while (dezabonare())
						;
					break;
				case 8:
					ActiveClient = null;
					return false;
				default:
					System.out.println("\nIntroduceti o optiune valida!");
				}
			} while (!exit);
		} else if (ActiveFurnizor != null)
			do {
				furnizorMenu();
				switch (choice()) {
				case 0:
					exit = exitApplication();
					break;
				case 1:
					vizualizareClienti();
					break;
				case 2:
					vizulalizareFacturiEmise();
					break;
				case 3:
					vizualizareClientiRauPlatnici();
				case 4:
					ActiveFurnizor = null;
					return false;
				default:
					System.out.println("\nIntroduceti o optiune valida!");
				}
			} while (!exit);
		return true;
	}

	private void headerMenu() {
		System.out.println("\n------------- Plati Online -------------");
		System.out.println("1. LogIn");
		System.out.println("2. SignUp");
		System.out.println("0. Exit");
	}

	private void clientMenu() {
		System.out.println("\n------------- " + ActiveClient.getNume() + " -------------");
		System.out.println("1. Abonare");
		System.out.println("2. Adaugare bani");
		System.out.println("3. Vizualizare facturi");
		System.out.println("4. Plateste facturi");
		System.out.println("5. Abonare plata automata");
		System.out.println("6. Dezabonare plata automata");
		System.out.println("7. Dezabonare");
		System.out.println("8. LogOut");
		System.out.println("0. Exit");
	}

	private void furnizorMenu() {
		System.out.println("\n------------- " + ActiveFurnizor.getNume() + " -------------");
		System.out.println("1. Clienti");
		System.out.println("2. Facturi emise");
		System.out.println("3. Clienti rau-platnici");
		System.out.println("4. LogOut");
		System.out.println("0. Exit");
	}

	private boolean logIn() {
		System.out.println("\n------------- LogIn -------------");
		System.out.println("\n0. Inapoi");
		System.out.print("\nIntroduceti numele de utilizator:");
		String username = sc.nextLine();
		if (username.equals("0"))
			return false;
		System.out.print("Introduceti parola: ");
		String password = sc.nextLine();
		//char[] Password = System.console().readPassword("Introduceti parola: ");
		//String password = new String(Password);
		if (password.equals("0"))
			return false;
		if (FurnizorDAOMySql.checkUser(username, password) != null) {
			ActiveFurnizor = (Furnizor) FurnizorDAOMySql.checkUser(username, password);
			return true;
		} else if (ClientDAOMySql.checkUser(username, password) != null) {
			ActiveClient = (Client) ClientDAOMySql.checkUser(username, password);
			return true;
		} else {
			System.out.println("\nNume de utilizator sau parola gresite!");
			return logIn();
		}
	}

	private boolean signUp() {
		System.out.println("\n------------- SignUp -------------");
		System.out.println("\n0. Inapoi");
		System.out.print("\nIntroduceti numele: ");
		String Nume = sc.nextLine();
		if (Nume.equals("0"))
			return false;
		System.out.print("Introduceti numele de utilizator: ");
		String Username = sc.nextLine();
		if (Username.equals("0"))
			return false;
		System.out.print("Introduceti Parola: ");
		String Password = sc.nextLine();
		if (Password.equals("0"))
			return false;
		System.out.println("\nAti introdus:\nNumele: " + Nume + "\nNume de utilizator: " + Username + "\nParola: "
				+ Password + "\n\nSalvati?  D/N");
		if (sc.nextLine().toLowerCase().charAt(0) == 'd') {
			Client cl = new Client(Nume, Username, Password);
			ClientDAOMySql clientDB = new ClientDAOMySql();
			clientDB.add(cl);
		} else
			signUp();
		return true;
	}

	private boolean abonare() {
		System.out.println("\n------------- Abonare -------------");
		List<Furnizor> listaFurnizoriNeabonati = ActiveClient.getFurnizoriNeabonati();
		if (listaFurnizoriNeabonati.size() == 0) {
			System.out.println("Te-ai abonat la toti furnizorii!");
			return inapoi();
		}
		System.out.println("\nAlege din urmatorii furnizori:");
		for (Furnizor fr : listaFurnizoriNeabonati) {
			System.out.println(fr);
		}
		System.out.println("0. Inapoi");
		int ch = choice();
		if (ch == 0)
			return true;
		for (Furnizor fr : listaFurnizoriNeabonati) {
			if (ch == fr.getCodFurnizor()) {
				if (areFacturiNeplatite(fr)) {
					System.out.println(
							"Platiti toate facturile de la acest furnizor inainte de a va putea abona din nou la el!");
					return inapoi();
				} else {
					RelationshipDAOMySql relDB = new RelationshipDAOMySql();
					relDB.addClientFurnizor(new Relationship(0, ActiveClient.getCodClient(), ch));
					System.out.println("\nTe-ai abonat la furnizorul: " + fr);
					generareFactura(fr, ActiveClient, LocalDate.now());
					return !inapoi();
				}
			}
		}
		return false;
	}

	private boolean addCash() {
		System.out.println("\n------------- Adaugare bani -------------");
		System.out.println("\nSuma curenta din cont: " + decimalFormat.format(ActiveClient.getSumaCont()) + " Lei");
		System.out.println("0. Inapoi");
		System.out.print("Introduceti suma dorita: ");
		double sumaDeAdaugat = 0;
		try {
			sumaDeAdaugat = Double.parseDouble(sc.nextLine());
			if (sumaDeAdaugat < 0)
				throw new NumberFormatException();
			if (sumaDeAdaugat == 0)
				return true;
			ActiveClient.setSumaCont(ActiveClient.getSumaCont() + sumaDeAdaugat);
			ClientDAOMySql cdb = new ClientDAOMySql();
			if (cdb.update(ActiveClient)) {
				System.out.println("Suma actuala este: " + decimalFormat.format(ActiveClient.getSumaCont()) + " Lei");
				return true;
			} else {
				return false;
			}
		} catch (NumberFormatException e) {
			System.out.println("\nIntroduceti o optiune valida!");
			return false;
		}
	}

	private boolean viewBills() {
		System.out.println("\n------------- Vizualizare Facturi -------------");
		List<Factura> listaFacturi = ActiveClient.getFacturiByClient();
		if (listaFacturi.size() == 0) {
			System.out.println("\nNu aveti nici o factura!");
			return false;
		}
		boolean firstIteration = true;
		int counter = 0;
		Collections.sort(listaFacturi);
		for (Factura fact : listaFacturi) {
			if (firstIteration) {
				System.out.println("\n" + fact);
				firstIteration = false;
				counter++;
			} else if (counter < 5) {
				System.out.println("---------------\n" + fact);
				counter++;
			} else {
				System.out.println("\n1. Vezi urmatoarele 5 facturi");
				System.out.println("0. Inapoi");
				int ch;
				do {
					ch = choice();
					switch (ch) {
					case 1:
						counter = 0;
						break;
					case 0:
						return true;
					default:
						System.out.println("Introduceti una din optiuni!");
					}
				} while (ch != 1);
			}
		}
		return inapoi();
	}

	private boolean platesteFacturi() {
		System.out.println("\n------------- Plateste Facturi -------------");
		List<Factura> listaFacturi = ActiveClient.getFacturiNeplatiteById();
		if (listaFacturi.size() == 0) {
			System.out.println("\nAveti toate facturile platite!");
			return !inapoi();
		}
		boolean firstIteration = true;
		for (Factura fact : listaFacturi) {
			if (firstIteration) {
				System.out.println("\n" + fact);
				firstIteration = false;
			} else
				System.out.println("---------------\n" + fact);
		}
		System.out.println("\n0. Inapoi");
		System.out.println("\nIntroduceti numarul facturii pe care doriti sa o platiti!");
		int ch = choice();
		if (ch == 0) {
			return false;
		} else {
			for (Factura fact : listaFacturi) {
				if (fact.getNrFactura() == ch) {
					if (fact.getSuma() + fact.getPenalitati() <= ActiveClient.getSumaCont()) {
						FacturaDAOMySql factDB = new FacturaDAOMySql();
						ClientDAOMySql clientDB = new ClientDAOMySql();
						double copySumaCont = ActiveClient.getSumaCont();
						fact.setStatus(true);
						ActiveClient.setSumaCont(ActiveClient.getSumaCont() - fact.getSuma() - fact.getPenalitati());
						if (factDB.update(fact) && clientDB.update(ActiveClient)) {
							System.out.println("\nAti platit facura Nr. " + fact.getNrFactura() + " in valoare de: "
									+ fact.getSuma() + " Lei si penalitati in valoare de: " + fact.getPenalitati()
									+ " Lei");
							return inapoi();
						} else {
							fact.setStatus(false); // anuleaza modificarile
							ActiveClient.setSumaCont(copySumaCont); // anuleaza modificarile
							factDB.update(fact); // anuleaza update-ul in baza de date in caz ca acesta a avut loc
							clientDB.update(ActiveClient);
							System.out.println("\nNu s-a putut face plata. Incercati din nou!");
							return inapoi();
						}
					} else {
						System.out.println("Nu aveti suficienti bani in cont!\nAlementati contul cu cel putin: "
								+ decimalFormat.format(fact.getSuma() + fact.getPenalitati() - ActiveClient.getSumaCont()) + " Lei");
						return !inapoi();
					}
				}
			}
			System.out.println("\n>>>> Nu exista aceasta factura! <<<<");
			return inapoi();
		}
	}

	private boolean abonarePlataAutomata() {
		System.out.println("\n------------- Abonare Plata Automata -------------\n");
		List<Furnizor> listaFurnizori = ActiveClient.getFurnizorCuFaraPlataAutomata(false);
		if (listaFurnizori.size() == 0) {
			System.out.println("Aveti plata automata la toti furnizorii la care v-ati abonat!");
			return !inapoi();
		}
		for (Furnizor furn : listaFurnizori) {
			System.out.println(furn);
		}
		System.out.println("\n0. Inapoi");
		System.out.println("\nIntroduceti numarul furnizorului!");
		int ch = choice();
		if (ch == 0)
			return false;
		for (Furnizor fr : listaFurnizori) {
			if (ch == fr.getCodFurnizor()) {
				RelationshipDAOMySql relDB = new RelationshipDAOMySql();
				Relationship rel = relDB.findAbonatByClientFurnizor(ActiveClient.getCodClient(), fr.getCodFurnizor());
				rel.setPlataAutomata(true);
				relDB.update(rel);
				System.out.println("\nTe-ai abonat la furnizorul: " + fr);
				plataAutomata();
				return inapoi();
			}
		}
		System.out.println("\nIntroduceti un furnizor disponbil!");
		return false;
	}

	private boolean dezabonarePlataAutomata() {
		System.out.println("\n------------- Dezabonare Plata Automata -------------\n");
		List<Furnizor> listaFurnizori = ActiveClient.getFurnizorCuFaraPlataAutomata(true);
		if (listaFurnizori.size() == 0) {
			System.out.println("Nu aveti nici un furnizor la plata automata!");
			return !inapoi();
		}
		for (Furnizor furn : listaFurnizori) {
			System.out.println(furn);
		}
		System.out.println("\n0. Inapoi");
		System.out.println("\nIntroduceti numarul furnizorului!");
		int ch = choice();
		if (ch == 0)
			return false;
		for (Furnizor fr : listaFurnizori) {
			if (ch == fr.getCodFurnizor()) {
				RelationshipDAOMySql relDB = new RelationshipDAOMySql();
				Relationship rel = relDB.findAbonatByClientFurnizor(ActiveClient.getCodClient(), fr.getCodFurnizor());
				rel.setPlataAutomata(false);
				relDB.update(rel);
				System.out.println("\nTe-ai dezabonat de la furnizorul: " + fr);
				return inapoi();
			}
		}
		System.out.println("\nIntroduceti un furnizor disponbil!");
		return false;
	}

	private boolean dezabonare() {
		System.out.println("\n------------- Dezabonare -------------");
		List<Furnizor> listaFurnizori = ActiveClient.getFurnizoriAbonati();
		if (listaFurnizori.size() == 0) {
			System.out.println("\nV-ati dezabonat de la toti furnizorii!");
			return !inapoi();
		}
		System.out.println();
		for (Furnizor furn : listaFurnizori) {
			System.out.println(furn);
		}
		System.out.println("\n0. Inapoi");
		System.out.println("\nIntroduceti numarul furnizorului!");
		int ch = choice();
		if (ch == 0)
			return false;
		for (Furnizor furn : listaFurnizori) {
			if (ch == furn.getCodFurnizor()) {
				if (ActiveClient.getFacturiNeplatiteByFurnizor(furn.getCodFurnizor()).size() > 0) {
					System.out.println("\nPlateste toate facturile de la acest furnizor inainte de a te dezabona!");
					return !inapoi();
				} else {
					RelationshipDAOMySql relDB = new RelationshipDAOMySql();
					relDB.deleteForDezabonare(furn.getCodFurnizor(), ActiveClient.getCodClient());
					System.out.println("\nTe-ai dezabonat de la furnizorul " + furn);
					return inapoi();
				}
			}
		}
		return true;
	}

	private void vizualizareClienti() {
		System.out.println("\n------------- Clienti -------------\n");
		List<Client> listaClienti = ActiveFurnizor.getClientiAbonati();
		if (listaClienti.size() == 0)
			System.out.println("Nu aveti clienti abonati!");
		else
			for (Client cl : listaClienti) {
				//System.out.println("--> " + cl.getNume() + " || " + cl.getUsername());
				int sumaDatorata = 0;
				for (Factura fa : cl.getFacturiByClientFurnizor(ActiveFurnizor.getCodFurnizor()))
					if (fa.isStatus() == false) {
						sumaDatorata += fa.getSuma() + fa.getPenalitati();
					}
					System.out.println("--> " + cl.getNume() + " || Suma datorata: " + sumaDatorata);
			}
		inapoi();
	}

	private boolean vizulalizareFacturiEmise() {
		System.out.println("\n------------- Facturi Emise -------------\n");
		List<Factura> listaFacturi = ActiveFurnizor.getFacturiEmise();
		if (listaFacturi.size() == 0) {
			System.out.println("Nu aveti nici o factura emisa!");
			return inapoi();
		} else {
			boolean firstIteration = true;
			int counter = 0;
			Collections.sort(listaFacturi);
			for (Factura fa : listaFacturi)
				if (firstIteration) {
					System.out.println(fa + "\n\tClient: " + fa.getClientByBill().getNume());
					firstIteration = false;
					counter++;
				} else if (counter < 5) {
					System.out.println("\n--------------------");
					System.out.println(fa + "\n\tClient: " + fa.getClientByBill().getNume());
					counter++;
				} else {
					System.out.println("\n1. Vezi urmatoarele 5 facturi");
					System.out.println("0. Inapoi");
					int ch;
					do {
						ch = choice();
						switch (ch) {
						case 1:
							counter = 0;
							break;
						case 0:
							return true;
						default:
							System.out.println("Introduceti una din optiuni!");
						}
					} while (ch != 1);
				}
			return inapoi();
		}
	}

	private boolean vizualizareClientiRauPlatnici() {
		System.out.println("\n------------- Facturi Emise -------------\n");
		ClientDAOMySql clientDB = new ClientDAOMySql();
		Client[] listaClienti = clientDB.getAll();
		List<Client> listaClientiAbonati = ActiveFurnizor.getClientiAbonati();
		boolean exista = false;
		for (Client cl : listaClienti) {
			if (!listaClientiAbonati.contains(cl)) {
				int sumaDatorata = 0;
				for (Factura fa : cl.getFacturiByClientFurnizor(ActiveFurnizor.getCodFurnizor()))
					if (fa.isStatus() == false) {
						sumaDatorata += fa.getSuma() + fa.getPenalitati();
					}
				if (sumaDatorata != 0) {
					System.out.println("--> " + cl.getNume() + " || Suma datorata: " + sumaDatorata);
					exista = true;
				}
			}
		}
		if(!exista)
			System.out.println("Nu aveti clienti rau-platnici!");
		return inapoi();
	}

	private void generareFacturi() {
		ClientDAOMySql clientDB = new ClientDAOMySql();
		Client[] listaClienti = clientDB.getAll();
		for (Client cl : listaClienti) {
			List<Furnizor> listaFurnizoriAbonati = cl.getFurnizoriAbonati();
			List<Furnizor> listaFurnizoriFaraPlataAutomata = cl.getFurnizorCuFaraPlataAutomata(false);
			Double sumaCont = cl.getSumaCont();
			for (Furnizor fr : listaFurnizoriAbonati) {
				if (listaFurnizoriFaraPlataAutomata.contains(fr)) {
					// numarul de facturi neplatite in functie de client si furnizor
					int counter = cl.getFacturiNeplatiteByFurnizor(fr.getCodFurnizor()).size();
					// toate facturile in functie de client si furnizor
					List<Factura> listaFacturi = cl.getFacturiByClientFurnizor(fr.getCodFurnizor());
					// ultima data a facturii in functie de client si furnizor
					LocalDate lastDate = listaFacturi.get(listaFacturi.size() - 1).getDataEmisa();
					while (counter < 3 && lastDate.plusDays(2).isBefore(LocalDate.now())) {
						Factura fa = generareFactura(fr, cl, lastDate.plusDays(3));
						lastDate = fa.getDataEmisa();
						counter++;
					}
					if (counter >= 3) {
						RelationshipDAOMySql relDB = new RelationshipDAOMySql();
						relDB.deleteForDezabonare(fr.getCodFurnizor(), cl.getCodClient());
						for (Factura fa : listaFacturi) {
							if (!fa.isStatus()) {
								fa.updatePenalitati();
								FacturaDAOMySql factDB = new FacturaDAOMySql();
								factDB.update(fa);
							}
						}
					}
				} else {
					// numarul facturilor neplatibile din lipsa de bani
					int counter = numarDeFacturiNeplatibile(cl, fr, sumaCont);
					// toate facturile in functie de client si furnizor
					List<Factura> listaFacturi = cl.getFacturiByClientFurnizor(fr.getCodFurnizor());
					LocalDate lastDate = listaFacturi.get(listaFacturi.size() - 1).getDataEmisa();
					while (counter < 3 && lastDate.plusDays(2).isBefore(LocalDate.now())) {
						Factura fa = generareFactura(fr, cl, lastDate.plusDays(3));
						lastDate = fa.getDataEmisa();
						counter++;
					}
				}
			}
		}
	}

	private void plataAutomata() {
		List<Furnizor> listaFurnizoriLaPlataAutomata = ActiveClient.getFurnizorCuFaraPlataAutomata(true);
		boolean firstIteration = true;
		for (Furnizor fr : listaFurnizoriLaPlataAutomata) {
			List<Factura> listaFacturi = ActiveClient.getFacturiByClientFurnizor(fr.getCodFurnizor());
			int counter = 0;
			for (Factura fa : listaFacturi) {
				if (fa.isStatus() == false) {
					if (platesteFactura(fa)) {
						System.out.println("\nA fost platita factura cu numarul " + fa.getNrFactura()
								+ " in valoare de " + fa.getSuma() + " Lei si penalitati in valoare de "
								+ fa.getPenalitati() + " Lei");
					} else {
						if (firstIteration) {
							System.out.println("\nNu mai aveti suficienti bani in cont!");
							firstIteration = false;
						}
						counter++;
					}
				}
			}
			if (counter >= 3) {
				RelationshipDAOMySql relDb = new RelationshipDAOMySql();
				relDb.deleteForDezabonare(fr.getCodFurnizor(), ActiveClient.getCodClient());
				for (Factura fa : listaFacturi)
					if (!fa.isStatus())
						setPenalitati(fa);
				System.out.println(
						"\nAi fost dezabonat de la furnizorul >> " + fr.getNume() + " << pentru neplata facturilor!");
			}
		}
	}

	private void notificareDezabonare() {
		List<Furnizor> listaFurnizori = ActiveClient.getFurnizoriNeabonati();
		for (Furnizor fr : listaFurnizori) {
			for (Factura fa : ActiveClient.getFacturiByClientFurnizor(fr.getCodFurnizor()))
				if (fa.isStatus() == false) {
					System.out.println("\nAi fost dezabonat de la furnizorul >> " + fr.getNume()
							+ " << pentru neplata facturilor!");
					break;
				}
		}
	}

	private boolean areFacturiNeplatite(Furnizor fr) {
		if (ActiveClient.getFacturiNeplatiteByFurnizor(fr.getCodFurnizor()).size() > 0)
			return true;
		else
			return false;
	}

	private Factura generareFactura(Furnizor furnizor, Client client, LocalDate dataEmiterii) {
		FacturaDAOMySql factDB = new FacturaDAOMySql();
		RelationshipDAOMySql relDB = new RelationshipDAOMySql();
		Factura factura = new Factura(0, getRandomValue(), dataEmiterii.format(formatter),
				dataEmiterii.plusDays(3).format(formatter), false);
		int NrFactura = factDB.add(factura);
		relDB.add(new Relationship(0, client.getCodClient(), furnizor.getCodFurnizor(), NrFactura, false));
		return factura;
	}

	private int numarDeFacturiNeplatibile(Client cl, Furnizor fr, Double sumaCont) {
		List<Factura> listaFacturiNeplatite = cl.getFacturiNeplatiteByFurnizor(fr.getCodFurnizor());
		int counter = 0;
		for (Factura factura : listaFacturiNeplatite) {
			if (factura.getSuma() < sumaCont) {
				sumaCont -= factura.getSuma();
			} else
				counter++;
		}
		return counter;
	}

	private boolean platesteFactura(Factura fact) {
		if (fact.getSuma() + fact.getPenalitati() < ActiveClient.getSumaCont()) {
			FacturaDAOMySql factDB = new FacturaDAOMySql();
			ClientDAOMySql clientDB = new ClientDAOMySql();
			fact.setStatus(true);
			ActiveClient.setSumaCont(ActiveClient.getSumaCont() - fact.getSuma() - fact.getPenalitati());
			factDB.update(fact);
			clientDB.update(ActiveClient);
			return true;
		} else {
			return false;
		}
	}

	private boolean setPenalitati(Factura fact) {
		FacturaDAOMySql factDB = new FacturaDAOMySql();
		fact.updatePenalitati();
		return factDB.update(fact);
	}

	private double getRandomValue() {
		Random rn = new Random();
		int number = rn.nextInt(150);
		if (number < 10)
			return getRandomValue();
		else
			return number + Double.parseDouble(decimalFormat.format(rn.nextDouble()));
	}

	private boolean inapoi() {
		System.out.println("\n0. Continuati");
		while (choice() != 0)
			System.out.println("Introduceti \"0\" pentru a contiuna!");
		return true;
	}

	private boolean exitApplication() {
		System.out.println("------------- La revedere! -------------");
		return true;
	}

	private int choice() {
		System.out.print("\n-->");
		int ch = -1;
		try {
			ch = Integer.parseInt(sc.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("\nIntroduceti o optiune valida!");
		}
		return ch;
	}
}
