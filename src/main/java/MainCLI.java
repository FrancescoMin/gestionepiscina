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
            System.out.println("0. Esci");
            System.out.print("Scelta: ");
            String scelta = scanner.nextLine();

            switch (scelta) {
                case "1":
                    System.out.println("\n--- INSERIMENTO NUOVO CLIENTE ---");
                    System.out.print("Codice Fiscale (16 car): ");
                    String cf = scanner.nextLine();
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    System.out.print("Cognome: ");
                    String cognome = scanner.nextLine();
                    System.out.print("Data Nascita (YYYY-MM-DD): ");
                    String dataStr = scanner.nextLine();
                    System.out.print("Via: ");
                    String via = scanner.nextLine();
                    System.out.print("Città: ");
                    String citta = scanner.nextLine();
                    System.out.print("CAP: ");
                    String cap = scanner.nextLine();
                    System.out.print("Codice Badge: ");
                    String badge = scanner.nextLine();

                    try {
                        java.sql.Date dataNascita = java.sql.Date.valueOf(dataStr);
                        dao.registraCliente(cf, nome, cognome, dataNascita, via, citta, cap, badge);
                        System.out.println("✅ SUCCESSO: Cliente registrato correttamente!");
                    } catch (IllegalArgumentException e) {
                        System.out.println("❌ ERRORE INPUT: Formato data non valido (usa YYYY-MM-DD).");
                    } catch (PiscinaException e) {
                        System.out.println("❌ IMPOSSIBILE REGISTRARE: " + e.getMessage());
                    }
                    break;

                case "2":
                    System.out.println("\n--- ISCRIZIONE CLIENTE A UN CORSO ---");
                    System.out.print("Codice Fiscale Cliente: ");
                    String cfIscrizione = scanner.nextLine();
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
                    String cfAccesso = scanner.nextLine();

                    try {
                        dao.registraAccesso(cfAccesso);
                        System.out.println("✅ SUCCESSO: Accesso consentito! Il tornello è sbloccato.");
                    } catch (PiscinaException e) {
                        System.out.println("🚫 ACCESSO NEGATO: " + e.getMessage());
                    }
                    break;

                case "4":
                    System.out.println("\n--- GENERAZIONE REPORT PRESENZE ---");
                    System.out.print("Data Inizio (YYYY-MM-DD): ");
                    String repInizioStr = scanner.nextLine();
                    System.out.print("Data Fine (YYYY-MM-DD): ");
                    String repFineStr = scanner.nextLine();

                    try {
                        java.sql.Date dataInizioRep = java.sql.Date.valueOf(repInizioStr);
                        java.sql.Date dataFineRep = java.sql.Date.valueOf(repFineStr);

                        if (dataFineRep.before(dataInizioRep)) {
                            System.out.println("❌ ERRORE: La data di fine precede la data di inizio.");
                        } else {
                            dao.generaReportPresenze(dataInizioRep, dataFineRep);
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("❌ ERRORE INPUT: Formato data non valido.");
                    } catch (PiscinaException e) {
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
                    String cfRicerca = scanner.nextLine();

                    try {
                        dao.visualizzaIscrizioniCliente(cfRicerca);
                    } catch (PiscinaException e) {
                        System.out.println("❌ ERRORE DATABASE: " + e.getMessage());
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