package view;

import controller.PiscinaController;
import exceptions.PiscinaException;
import java.util.Scanner;

public class MainCLI {
    private PiscinaController controller;
    private Scanner scanner;

    public MainCLI() {
        this.controller = new PiscinaController();
        this.scanner = new Scanner(System.in);
    }

    public void avviaApp() {
        while (true) {
            System.out.println("\n=== GESTIONALE PISCINA - MENU PRINCIPALE ===");
            System.out.println("1. 🟦 REGISTRAZIONE ACCESSO (Tornello)");
            System.out.println("2. 📝 ISCRIVI CLIENTE A UN CORSO");
            System.out.println("3. 📊 REPORTISTICA PRESENZE");
            System.out.println("4. ⚙️  CONFIGURAZIONE (Anagrafica e Corsi)");
            System.out.println("0. ESCI");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1": gestisciAccesso(); break;
                case "2": gestisciIscrizione(); break;
                case "3": generaReportPresenze(); break;
                case "4": menuConfigurazione(); break;
                case "0":
                    System.out.println("Chiusura del gestionale. Arrivederci.");
                    return;
                default:
                    System.out.println("❌ Scelta non valida.");
            }
        }
    }

    private void menuConfigurazione() {
        while (true) {
            System.out.println("\n--- AREA CONFIGURAZIONE ---");
            System.out.println("1. Gestione Anagrafica Clienti");
            System.out.println("2. Gestione Corsi");
            System.out.println("0. Torna al menu principale");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1": menuAnagrafica(); break;
                case "2": menuCorsi(); break;
                case "0": return;
                default: System.out.println("❌ Scelta non valida.");
            }
        }
    }

    private void menuAnagrafica() {
        while (true) {
            System.out.println("\n--- GESTIONE ANAGRAFICA CLIENTI ---");
            System.out.println("1. Registra Nuovo Cliente");
            System.out.println("2. Aggiungi Recapito a Cliente Esistente");
            System.out.println("3. Modifica Recapito Esistente");
            System.out.println("0. Torna indietro");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1": registraNuovoCliente(); break;
                case "2": aggiungiRecapitoAggiuntivo(); break;
                case "3": proceduraModificaRecapito(); break;
                case "0": return;
                default: System.out.println("❌ Scelta non valida.");
            }
        }
    }

    private void menuCorsi() {
        while (true) {
            System.out.println("\n--- GESTIONE CORSI ---");
            System.out.println("1. Aggiungi Nuovo Corso");
            System.out.println("0. Torna indietro");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1": inserimentoNuovoCorso(); break;
                case "0": return;
                default: System.out.println("❌ Scelta non valida.");
            }
        }
    }

    // ==========================================
    // METODI OPERATIVI - MENU PRINCIPALE
    // ==========================================

    private void gestisciAccesso() {
        System.out.println("\n--- REGISTRAZIONE ACCESSO ---");
        String cf;
        while (true) {
            System.out.print("Codice Fiscale (16 car): ");
            cf = scanner.nextLine().trim().toUpperCase();
            if (cf.length() == 16) break;
            System.out.println("❌ ERRORE: Il Codice Fiscale deve essere di 16 caratteri.");
        }

        try {
            controller.registraAccesso(cf);
            System.out.println("✅ SUCCESSO: Accesso consentito! Il tornello è sbloccato.");
        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void gestisciIscrizione() {
        System.out.println("\n--- ISCRIZIONE CORSO ---");
        String cf;
        while (true) {
            System.out.print("Codice Fiscale Cliente (16 car): ");
            cf = scanner.nextLine().trim().toUpperCase();
            if (cf.length() == 16) break;
            System.out.println("❌ ERRORE: Il Codice Fiscale deve essere di 16 caratteri.");
        }

        System.out.print("Nome del Corso: ");
        String corso = scanner.nextLine().trim();

        java.sql.Date dataInizio = null;
        while (dataInizio == null) {
            System.out.print("Data Inizio (YYYY-MM-DD): ");
            try {
                dataInizio = java.sql.Date.valueOf(scanner.nextLine().trim());
            } catch (IllegalArgumentException e) {
                System.out.println("❌ ERRORE: Formato data non valido.");
            }
        }

        try {
            controller.iscriviCliente(cf, corso, dataInizio);
            System.out.println("✅ Iscrizione effettuata con successo.");
        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void generaReportPresenze() {
        System.out.println("\n--- REPORTISTICA PRESENZE ---");
        java.sql.Date dataInizio = null;
        java.sql.Date dataFine = null;

        while (dataInizio == null) {
            System.out.print("Data Inizio Periodo (YYYY-MM-DD): ");
            try {
                dataInizio = java.sql.Date.valueOf(scanner.nextLine().trim());
            } catch (IllegalArgumentException e) {
                System.out.println("❌ ERRORE: Formato data non valido.");
            }
        }

        while (dataFine == null) {
            System.out.print("Data Fine Periodo (YYYY-MM-DD): ");
            try {
                java.sql.Date tempDate = java.sql.Date.valueOf(scanner.nextLine().trim());
                if (tempDate.before(dataInizio)) {
                    System.out.println("❌ ERRORE: La data di fine non può essere precedente a quella di inizio.");
                } else {
                    dataFine = tempDate;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("❌ ERRORE: Formato data non valido.");
            }
        }

        try {
            controller.generaReport(dataInizio, dataFine);
        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    // ==========================================
    // METODI OPERATIVI - SOTTOMENU
    // ==========================================

    private void registraNuovoCliente() {
        System.out.println("\n--- INSERIMENTO NUOVO CLIENTE ---");

        String cf;
        while (true) {
            System.out.print("Codice Fiscale (16 car): ");
            cf = scanner.nextLine().trim().toUpperCase();
            if (cf.length() == 16) break;
            System.out.println("❌ ERRORE: Il Codice Fiscale deve essere di 16 caratteri.");
        }

        String nome;
        while (true) {
            System.out.print("Nome: ");
            nome = scanner.nextLine().trim();
            if (!nome.isEmpty()) break;
            System.out.println("❌ ERRORE: Il nome è obbligatorio.");
        }

        String cognome;
        while (true) {
            System.out.print("Cognome: ");
            cognome = scanner.nextLine().trim();
            if (!cognome.isEmpty()) break;
            System.out.println("❌ ERRORE: Il cognome è obbligatorio.");
        }

        java.sql.Date dataNascita = null;
        while (dataNascita == null) {
            System.out.print("Data Nascita (YYYY-MM-DD): ");
            try {
                dataNascita = java.sql.Date.valueOf(scanner.nextLine().trim());
            } catch (IllegalArgumentException e) {
                System.out.println("❌ ERRORE: Formato data non valido.");
            }
        }

        System.out.print("Via: ");
        String via = scanner.nextLine().trim();

        System.out.print("Città: ");
        String citta = scanner.nextLine().trim();

        String cap;
        while (true) {
            System.out.print("CAP (5 cifre): ");
            cap = scanner.nextLine().trim();
            if (cap.matches("\\d{5}")) break;
            System.out.println("❌ ERRORE: Il CAP deve contenere esattamente 5 numeri.");
        }

        System.out.print("Codice Badge: ");
        String badge = scanner.nextLine().trim();

        String valoreRecapito = "";
        String tipoRecapito = "";
        while (true) {
            System.out.print("Inserisci Recapito (Cellulare, Fisso, Email) [Vuoto per saltare]: ");
            valoreRecapito = scanner.nextLine().trim();

            if (valoreRecapito.isEmpty()) break;

            if (valoreRecapito.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                tipoRecapito = "email";
                break;
            } else if (valoreRecapito.matches("^\\+?[0-9\\s]+$")) {
                String pulita = valoreRecapito.replace("+39", "").trim();
                tipoRecapito = pulita.startsWith("3") ? "cellulare" : "telefono";
                break;
            } else {
                System.out.println("❌ ERRORE: Formato non valido.");
            }
        }

        try {
            controller.registraCliente(cf, nome, cognome, dataNascita, via, citta, cap, badge);
            System.out.println("✅ SUCCESSO: Cliente registrato.");
            if (!valoreRecapito.isEmpty()) {
                controller.aggiungiRecapito(cf, tipoRecapito, valoreRecapito);
                System.out.println("✅ Recapito salvato.");
            }
        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void aggiungiRecapitoAggiuntivo() {
        System.out.println("\n--- AGGIUNGI NUOVO RECAPITO ---");
        String cf;
        while (true) {
            System.out.print("Codice Fiscale Cliente (16 car): ");
            cf = scanner.nextLine().trim().toUpperCase();
            if (cf.length() == 16) break;
            System.out.println("❌ ERRORE: Il Codice Fiscale deve essere di 16 caratteri.");
        }

        String valore, tipo = "";
        while (true) {
            System.out.print("Inserisci Recapito: ");
            valore = scanner.nextLine().trim();
            if (valore.isEmpty()) {
                System.out.println("❌ Il campo non può essere vuoto.");
                continue;
            }
            if (valore.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                tipo = "email"; break;
            } else if (valore.matches("^\\+?[0-9\\s]+$")) {
                String pulita = valore.replace("+39", "").trim();
                tipo = pulita.startsWith("3") ? "cellulare" : "telefono";
                break;
            }
            System.out.println("❌ Formato non valido.");
        }

        try {
            controller.aggiungiRecapito(cf, tipo, valore);
            System.out.println("✅ Recapito aggiunto correttamente.");
        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void proceduraModificaRecapito() {
        System.out.println("\n--- MODIFICA RECAPITO ---");
        String cf;
        while (true) {
            System.out.print("Codice Fiscale Cliente (16 car): ");
            cf = scanner.nextLine().trim().toUpperCase();
            if (cf.length() == 16) break;
            System.out.println("❌ ERRORE: Il Codice Fiscale deve essere di 16 caratteri.");
        }

        try {
            // Recupero ed esposizione dei recapiti correnti
            java.util.List<String[]> recapitiAttuali = controller.ottieniRecapitiCliente(cf);

            if (recapitiAttuali.isEmpty()) {
                System.out.println("⚠️ Nessun recapito registrato per questo cliente. Impossibile procedere con modifiche.");
                return;
            }

            System.out.println("\n[Recapiti attualmente associati al cliente]");
            for (int i = 0; i < recapitiAttuali.size(); i++) {
                String[] item = recapitiAttuali.get(i);
                System.out.println("  -> Tipo: " + item[0] + " | Valore: " + item[1]);
            }
            System.out.println();

        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
            return;
        }

        System.out.print("Inserisci il VECCHIO recapito da cambiare (scrivi il valore esatto): ");
        String vecchio = scanner.nextLine().trim();

        String nuovo, tipo = "";
        while (true) {
            System.out.print("Inserisci il NUOVO recapito: ");
            nuovo = scanner.nextLine().trim();
            if (nuovo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                tipo = "email";
                break;
            } else if (nuovo.matches("^\\+?[0-9\\s]+$")) {
                String pulita = nuovo.replace("+39", "").trim();
                tipo = pulita.startsWith("3") ? "cellulare" : "telefono";
                break;
            }
            System.out.println("❌ Formato non valido.");
        }

        try {
            controller.aggiornaRecapito(cf, vecchio, nuovo, tipo);
            System.out.println("✅ Modifica completata con successo.");
        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void inserimentoNuovoCorso() {
        System.out.println("\n--- INSERIMENTO NUOVO CORSO ---");
        System.out.print("Nome Corso: ");
        String nomeC = scanner.nextLine().trim();
        System.out.print("Descrizione: ");
        String descC = scanner.nextLine().trim();

        double costo = -1;
        while (costo < 0) {
            System.out.print("Costo Mensile (€): ");
            try {
                costo = Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ ERRORE: Inserisci un valore numerico valido.");
            }
        }

        int min = 0, max = 0;
        while (min <= 0 || max <= min) {
            try {
                System.out.print("Min Partecipanti: ");
                min = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Max Partecipanti: ");
                max = Integer.parseInt(scanner.nextLine().trim());
                if (max <= min) {
                    System.out.println("❌ ERRORE: Il massimo deve essere superiore al minimo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ ERRORE: Inserisci numeri interi.");
            }
        }

        try {
            controller.aggiungiCorso(nomeC, descC, costo, min, max);
            System.out.println("✅ Corso aggiunto con successo!");
        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }
}