import java.util.Scanner;

public class MainCLI {
    private static final Scanner scanner = new Scanner(System.in);
    private static final SegreteriaDAO dao = new SegreteriaDAO();

    public static void main(String[] args) {
        System.out.println("=== SISTEMA GESTIONE PISCINA ===");

        // Fase di Login
        boolean autenticato = false;
        while (!autenticato) {
            System.out.print("Username: ");
            String user = scanner.nextLine();
            System.out.print("Password: ");
            String pass = scanner.nextLine();

            autenticato = dao.login(user, pass);
            if (!autenticato) {
                System.out.println("Credenziali errate. Riprova.\n");
            }
        }

        System.out.println("\nLogin effettuato con successo. Benvenuto Segretario!");
        mostraMenu();
    }

    private static void mostraMenu() {
        boolean esci = false;
        while (!esci) {
            System.out.println("\n--- MENU PRINCIPALE ---");
            System.out.println("1. Registra nuovo cliente");
            System.out.println("2. Iscrivi cliente a un corso");
            System.out.println("3. Registra accesso (Badge)");
            System.out.println("4. Genera Report Presenze");
            System.out.println("0. Esci");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine();

            switch (scelta) {
                case "1":
                    // Esempio di raccolta dati per la registrazione
                    System.out.print("Codice Fiscale: ");
                    String cf = scanner.nextLine();
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    System.out.print("Cognome: ");
                    String cognome = scanner.nextLine();
                    // ... raccogli gli altri campi ...
                    // dao.registraCliente(cf, nome, cognome, java.sql.Date.valueOf("1990-01-01"), "Via Roma", "Milano", "20100", "B001");
                    System.out.println("Funzione in fase di cablaggio...");
                    break;
                case "2":
                    System.out.println("Funzione Iscrizione (da implementare)");
                    break;
                case "3":
                    System.out.println("Funzione Accesso (da implementare)");
                    break;
                case "4":
                    System.out.println("Funzione Report (da implementare)");
                    break;
                case "0":
                    esci = true;
                    System.out.println("Uscita dal sistema. Arrivederci!");
                    break;
                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }
}