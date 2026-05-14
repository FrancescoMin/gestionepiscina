import DAO.SegreteriaDAO;
import exceptions.PiscinaException;
import java.util.Scanner;


public class MainCLI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SegreteriaDAO dao = new SegreteriaDAO();

        System.out.println("=== SISTEMA GESTIONE PISCINA ===");

        // --- FASE DI LOGIN ---
        boolean autenticato = false;
        while (!autenticato) {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            try {
                // Ora il login è protetto dal try-catch!
                if (dao.login(username, password)) {
                    autenticato = true;
                    System.out.println("\n✅ Login effettuato con successo. Benvenuto Segretario!");
                } else {
                    System.out.println("❌ Credenziali errate. Riprova.\n");
                }
            } catch (PiscinaException e) {
                System.out.println("❌ ERRORE: " + e.getMessage() + " Riprova.\n");
            }
        }

        // --- MENU PRINCIPALE ---
        boolean esci = false;
        while (!esci) {
            System.out.println("\n--- MENU PRINCIPALE ---");
            System.out.println("1. Registra nuovo cliente");
            System.out.println("2. Iscrivi cliente a un corso");
            System.out.println("3. Registra accesso (Badge)");
            System.out.println("4. Genera Report Presenze");
            System.out.println("5. Visualizza Corsi e Sessioni");
            System.out.println("6. Visualizza Iscrizioni Cliente");
            System.out.println("7. Aggiungi nuovo recapito a cliente esistente");
            System.out.println("8. Aggiungi nuovo corso");
            System.out.println("0. Esci");
            System.out.print("Scelta: ");
            String scelta = scanner.nextLine();

            switch (scelta) {
                case "1":
                    System.out.println("\n--- INSERIMENTO NUOVO CLIENTE ---");

                    // 1. Validazione CF
                    String cf;
                    while (true) {
                        System.out.print("Codice Fiscale (16 car): ");
                        cf = scanner.nextLine().trim().toUpperCase();
                        if (cf.length() == 16) break; // Se è giusto, esce dal ciclo e prosegue
                        System.out.println("❌ ERRORE: Il Codice Fiscale deve essere di 16 caratteri. Riprova.");
                    }

                    // 2. Validazione Nome
                    String nome;
                    while (true) {
                        System.out.print("Nome: ");
                        nome = scanner.nextLine().trim();
                        if (!nome.isEmpty()) break;
                        System.out.println("❌ ERRORE: Il nome è obbligatorio.");
                    }

                    // 3. Validazione Cognome
                    String cognome;
                    while (true) {
                        System.out.print("Cognome: ");
                        cognome = scanner.nextLine().trim();
                        if (!cognome.isEmpty()) break;
                        System.out.println("❌ ERRORE: Il cognome è obbligatorio.");
                    }

                    // 4. Validazione Data di Nascita (con try-catch incorporato)
                    java.sql.Date dataNascita = null;
                    while (dataNascita == null) {
                        System.out.print("Data Nascita (YYYY-MM-DD): ");
                        String dataStr = scanner.nextLine().trim();
                        try {
                            dataNascita = java.sql.Date.valueOf(dataStr);
                        } catch (IllegalArgumentException e) {
                            System.out.println("❌ ERRORE: Formato data non valido. Riprova usando YYYY-MM-DD.");
                        }
                    }

                    // 5. Input semplici (Via e Città)
                    System.out.print("Via: ");
                    String via = scanner.nextLine().trim();
                    System.out.print("Città: ");
                    String citta = scanner.nextLine().trim();

                    // 6. Validazione CAP
                    String cap;
                    while (true) {
                        System.out.print("CAP (5 cifre): ");
                        cap = scanner.nextLine().trim();
                        if (cap.matches("\\d{5}")) break;
                        System.out.println("❌ ERRORE: Il CAP deve essere composto esattamente da 5 numeri.");
                    }

                    System.out.print("Codice Badge: ");
                    String badge = scanner.nextLine().trim();

                    // 7. Validazione Recapito con distinzione Fisso/Cellulare/Email
                    String valoreRecapito;
                    String tipoRecapito = "";
                    while (true) {
                        System.out.print("Inserisci Recapito (Cellulare, Fisso o Email) [Lascia vuoto per saltare]: ");
                        valoreRecapito = scanner.nextLine().trim();

                        if (valoreRecapito.isEmpty()) break;

                        // 1. Controllo EMAIL
                        if (valoreRecapito.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                            tipoRecapito = "email";
                            break;
                        }
                        // 2. Controllo NUMERI (Fisso o Cellulare)
                        else if (valoreRecapito.matches("^\\+?[0-9\\s]+$")) {
                            // Rimuoviamo il prefisso internazionale per il controllo iniziale
                            String pulita = valoreRecapito.replace("+39", "").trim();

                            if (pulita.startsWith("3")) {
                                tipoRecapito = "cellulare";
                            } else if (pulita.startsWith("0")) {
                                tipoRecapito = "telefono"; // Fisso
                            } else {
                                tipoRecapito = "telefono"; // Default se non inizia con 0 o 3 (es. numeri verdi)
                            }
                            break;
                        }
                        else {
                            System.out.println("❌ ERRORE: Formato non riconosciuto. Inserisci un numero valido o un'email.");
                        }
                    }

                    // --- CHIAMATA AL DATABASE ---
                    try {
                        dao.registraCliente(cf, nome, cognome, dataNascita, via, citta, cap, badge);
                        if (!valoreRecapito.isEmpty()) {
                            dao.aggiungiRecapito(cf, tipoRecapito, valoreRecapito);
                        }
                        System.out.println("✅ SUCCESSO: Cliente registrato correttamente!");

                    } catch (exceptions.PiscinaException e) {
                        // Questo scatta solo se c'è un errore logico nel DB (es. CF già esistente)
                        System.out.println("❌ IMPOSSIBILE REGISTRARE: " + e.getMessage());
                    }
                    break;

                case "2":
                    System.out.println("\n--- ISCRIZIONE CLIENTE A UN CORSO ---");
                    System.out.print("Codice Fiscale Cliente: ");
                    String cfIscrizione = scanner.nextLine().trim().toUpperCase();
                    if (cfIscrizione.length() != 16) {
                        System.out.println("❌ ERRORE INPUT: Il Codice Fiscale deve essere di 16 caratteri.");
                        continue;
                    }
                    System.out.print("Nome Corso (es. Acquagym): ");
                    String corso = scanner.nextLine();
                    System.out.print("Data Inizio (YYYY-MM-DD): ");
                    String dataInizioStr = scanner.nextLine();

                    try {
                        java.sql.Date dataInizio = java.sql.Date.valueOf(dataInizioStr);
                        dao.iscriviCliente(cfIscrizione, corso, dataInizio);
                        System.out.println("✅ SUCCESSO: Iscrizione completata correttamente!");
                    } catch (IllegalArgumentException e) {
                        System.out.println("❌ ERRORE INPUT: Formato data non valido.");
                    } catch (PiscinaException e) {
                        System.out.println("❌ IMPOSSIBILE ISCRIVERE: " + e.getMessage());
                    }
                    break;

                case "3":
                    System.out.println("\n--- REGISTRAZIONE ACCESSO ---");
                    System.out.print("Passa il badge (Inserisci CF): ");
                    String cfAccesso = scanner.nextLine().trim().toUpperCase();
                    if (cfAccesso.length() != 16) {
                        System.out.println("❌ ERRORE INPUT: Badge non valido (CF errato).");
                        continue;
                    }
                    try {
                        dao.registraAccesso(cfAccesso);
                        System.out.println("✅ SUCCESSO: Accesso consentito! Il tornello è sbloccato.");
                    } catch (PiscinaException e) {
                        System.out.println("🚫 ACCESSO NEGATO: " + e.getMessage());
                    }
                    break;

                case "4":
                    System.out.println("\n--- GENERAZIONE REPORT PRESENZE ---");
                    java.sql.Date dataInizioRep = null;
                    java.sql.Date dataFineRep = null;

                    // 1. Ciclo per la validazione della Data Inizio
                    while (true) {
                        System.out.print("Data Inizio (YYYY-MM-DD): ");
                        String repInizioStr = scanner.nextLine().trim();
                        try {
                            dataInizioRep = java.sql.Date.valueOf(repInizioStr);
                            break; // Formato corretto, esce dal ciclo
                        } catch (IllegalArgumentException e) {
                            System.out.println("❌ ERRORE: Formato data non valido. Riprova usando YYYY-MM-DD.");
                        }
                    }

                    // 2. Ciclo per la validazione della Data Fine (con controllo logico)
                    while (true) {
                        System.out.print("Data Fine (YYYY-MM-DD): ");
                        String repFineStr = scanner.nextLine().trim();
                        try {
                            dataFineRep = java.sql.Date.valueOf(repFineStr);

                            // Controllo: la fine non può venire prima dell'inizio
                            if (dataFineRep.before(dataInizioRep)) {
                                System.out.println("❌ ERRORE: La data di fine non può essere precedente a quella di inizio (" + dataInizioRep + "). Riprova.");
                            } else {
                                break; // Formato e logica corretti, esce dal ciclo!
                            }
                        } catch (IllegalArgumentException e) {
                            System.out.println("❌ ERRORE: Formato data non valido. Riprova usando YYYY-MM-DD.");
                        }
                    }

                    // --- CHIAMATA AL DATABASE ---
                    try {
                        dao.generaReportPresenze(dataInizioRep, dataFineRep);
                    } catch (exceptions.PiscinaException e) {
                        System.out.println("❌ ERRORE REPORT: " + e.getMessage());
                    }
                    break;

                case "5":
                    try {
                        dao.visualizzaCorsiDisponibili();
                    } catch (PiscinaException e) {
                        System.out.println("❌ ERRORE DATABASE: " + e.getMessage());
                    }
                    break;

                case "6":
                    System.out.println("\n--- RICERCA ISCRIZIONI ATTIVE ---");
                    System.out.print("Inserisci il Codice Fiscale del cliente: ");
                    String cfRicerca = scanner.nextLine().trim().toUpperCase();
                    if (cfRicerca.length() != 16) {
                        System.out.println("❌ ERRORE INPUT: Il Codice Fiscale deve essere di 16 caratteri.");
                        continue;
                    }
                    try {
                        dao.visualizzaIscrizioniCliente(cfRicerca);
                    } catch (PiscinaException e) {
                        System.out.println("❌ ERRORE DATABASE: " + e.getMessage());
                    }
                    break;

                case "7":
                    System.out.println("\n--- AGGIUNGI NUOVO RECAPITO ---");

                    // 1. Validazione Codice Fiscale
                    String cfRecapito;
                    while (true) {
                        System.out.print("Codice Fiscale del Cliente (16 car): ");
                        cfRecapito = scanner.nextLine().trim().toUpperCase();
                        if (cfRecapito.length() == 16) break;
                        System.out.println("❌ ERRORE: Il Codice Fiscale deve essere di 16 caratteri. Riprova.");
                    }

                    // 2. Acquisizione e riconoscimento tipo recapito
                    String valoreNuovoRecapito;
                    String tipoNuovoRecapito = "";
                    while (true) {
                        System.out.print("Inserisci Recapito (Cellulare, Fisso o Email): ");
                        valoreNuovoRecapito = scanner.nextLine().trim();

                        if (valoreNuovoRecapito.isEmpty()) {
                            System.out.println("❌ ERRORE: Il campo non può essere vuoto in questa operazione.");
                            continue;
                        }

                        // Controllo EMAIL
                        if (valoreNuovoRecapito.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                            tipoNuovoRecapito = "email";
                            break;
                        }
                        // Controllo NUMERI (Fisso o Cellulare)
                        else if (valoreNuovoRecapito.matches("^\\+?[0-9\\s]+$")) {
                            // Rimuove l'eventuale prefisso per il controllo iniziale
                            String pulita = valoreNuovoRecapito.replace("+39", "").trim();

                            if (pulita.startsWith("3")) {
                                tipoNuovoRecapito = "cellulare";
                            } else {
                                tipoNuovoRecapito = "telefono";
                            }
                            break;
                        }
                        else {
                            System.out.println("❌ ERRORE: Formato non riconosciuto. Inserisci un numero valido o un'email.");
                        }
                    }

                    // 3. Esecuzione tramite DAO
                    try {
                        dao.aggiungiRecapito(cfRecapito, tipoNuovoRecapito, valoreNuovoRecapito);
                        System.out.println("✅ SUCCESSO: Recapito registrato correttamente per il cliente.");
                    } catch (exceptions.PiscinaException e) {
                        System.out.println("❌ ERRORE DURANTE L'INSERIMENTO: " + e.getMessage());
                    }
                    break;

                case "8":
                    System.out.println("\n--- INSERIMENTO NUOVO CORSO ---");
                    System.out.print("Nome Corso: ");
                    String nomeC = scanner.nextLine().trim();
                    System.out.print("Descrizione: ");
                    String descC = scanner.nextLine().trim();

                    double costo = -1;
                    while (costo < 0) {
                        System.out.print("Costo Mensile (€): ");
                        try {
                            costo = Double.parseDouble(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("❌ ERRORE: Inserisci un valore numerico valido.");
                        }
                    }

                    int min = 0, max = 0;
                    while (min <= 0 || max <= min) {
                        try {
                            System.out.print("Min Partecipanti: ");
                            min = Integer.parseInt(scanner.nextLine());
                            System.out.print("Max Partecipanti: ");
                            max = Integer.parseInt(scanner.nextLine());
                            if (max <= min) System.out.println("❌ ERRORE: Il massimo deve essere superiore al minimo.");
                        } catch (NumberFormatException e) {
                            System.out.println("❌ ERRORE: Inserisci numeri interi.");
                        }
                    }

                    try {
                        dao.aggiungiCorso(nomeC, descC, costo, min, max);
                        System.out.println("✅ Corso aggiunto con successo!");
                    } catch (PiscinaException e) {
                        System.out.println("❌ " + e.getMessage());
                    }
                    break;

                case "0":
                    System.out.println("Uscita dal sistema. Arrivederci!");
                    esci = true;
                    break;

                default:
                    System.out.println("❌ Scelta non valida. Riprova.");
            }
        }
        scanner.close();
    }
}
