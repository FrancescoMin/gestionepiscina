package view;

import controller.PiscinaController;
import exceptions.PiscinaException;
import java.util.Scanner;
import java.util.List;

public class MainCLI {
    private PiscinaController controller;
    private Scanner scanner;

    // ==========================================
    // COSTANTI (Risoluzione SonarCloud: "Define a constant instead of duplicating...")
    // ==========================================
    private static final String PROMPT_SCELTA = "Scelta: ";
    private static final String ERR_SCELTA_INVALIDA = "❌ Scelta non valida.";
    private static final String PROMPT_CF_CLIENTE = "Codice Fiscale Cliente (16 car): ";

    private static final String REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String REGEX_TELEFONO = "^\\+?[0-9\\s]+$";

    private static final String TIPO_EMAIL = "email";
    private static final String TIPO_TELEFONO = "telefono";
    private static final String TIPO_CELLULARE = "cellulare";

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
            System.out.print(PROMPT_SCELTA);

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
                    System.out.println(ERR_SCELTA_INVALIDA);
            }
        }
    }

    private void menuConfigurazione() {
        while (true) {
            System.out.println("\n--- AREA CONFIGURAZIONE ---");
            System.out.println("1. Gestione Anagrafica Clienti");
            System.out.println("2. Gestione Corsi");
            System.out.println("0. Torna al menu principale");
            System.out.print(PROMPT_SCELTA);

            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1": menuAnagrafica(); break;
                case "2": menuCorsi(); break;
                case "0": return;
                default: System.out.println(ERR_SCELTA_INVALIDA);
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
            System.out.print(PROMPT_SCELTA);

            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1": registraNuovoCliente(); break;
                case "2": aggiungiRecapitoAggiuntivo(); break;
                case "3": proceduraModificaRecapito(); break;
                case "0": return;
                default: System.out.println(ERR_SCELTA_INVALIDA);
            }
        }
    }

    private void menuCorsi() {
        while (true) {
            System.out.println("\n--- GESTIONE CORSI ---");
            System.out.println("1. Aggiungi Nuovo Corso");
            System.out.println("0. Torna indietro");
            System.out.print(PROMPT_SCELTA);

            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1": inserimentoNuovoCorso(); break;
                case "0": return;
                default: System.out.println(ERR_SCELTA_INVALIDA);
            }
        }
    }

    // ==========================================
    // METODI DI UTILITA' (Risoluzione SonarCloud: Cognitive Complexity)
    // ==========================================

    private String leggiCodiceFiscale(String prompt) {
        String cf;
        while (true) {
            System.out.print(prompt);
            cf = scanner.nextLine().trim().toUpperCase();
            if (cf.length() == 16) return cf;
            System.out.println("❌ ERRORE: Il Codice Fiscale deve essere di 16 caratteri.");
        }
    }

    private java.sql.Date leggiData(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return java.sql.Date.valueOf(scanner.nextLine().trim());
            } catch (IllegalArgumentException e) {
                System.out.println("❌ ERRORE: Formato data non valido. Usare YYYY-MM-DD.");
            }
        }
    }

    private String leggiStringaObbligatoria(String prompt, String messaggioErrore) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println(messaggioErrore);
        }
    }

    private String leggiCAP() {
        String cap;
        while (true) {
            System.out.print("CAP (5 cifre): ");
            cap = scanner.nextLine().trim();
            if (cap.matches("\\d{5}")) return cap;
            System.out.println("❌ ERRORE: Il CAP deve contenere esattamente 5 numeri.");
        }
    }

    /**
     * Gestisce l'acquisizione validata di un recapito (Email, Fisso, Cellulare).
     * @return Array di String [tipo, valore]. Array vuoto se saltato.
     */
    private String[] leggiRecapito(String promptTest, boolean allowEmpty) {
        String valore, tipo;
        while (true) {
            System.out.print(promptTest);
            valore = scanner.nextLine().trim();

            if (valore.isEmpty()) {
                if (allowEmpty) return new String[]{"", ""};
                System.out.println("❌ Il campo non può essere vuoto.");
                continue;
            }

            if (valore.matches(REGEX_EMAIL)) {
                return new String[]{TIPO_EMAIL, valore};
            } else if (valore.matches(REGEX_TELEFONO)) {
                String pulita = valore.replace("+39", "").trim();
                tipo = pulita.startsWith("3") ? TIPO_CELLULARE : TIPO_TELEFONO;
                return new String[]{tipo, valore};
            } else {
                System.out.println("❌ ERRORE: Formato non valido. Inserire Numero o Email corretta.");
            }
        }
    }

    // ==========================================
    // METODI OPERATIVI - MENU PRINCIPALE
    // ==========================================

    private void gestisciAccesso() {
        System.out.println("\n--- REGISTRAZIONE ACCESSO ---");
        String cf = leggiCodiceFiscale("Codice Fiscale (16 car): ");

        try {
            controller.registraAccesso(cf);
            System.out.println("✅ SUCCESSO: Accesso consentito! Il tornello è sbloccato.");
        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void gestisciIscrizione() {
        System.out.println("\n--- ISCRIZIONE CORSO ---");
        String cf = leggiCodiceFiscale(PROMPT_CF_CLIENTE);
        String corso = leggiStringaObbligatoria("Nome del Corso: ", "❌ ERRORE: Il nome corso è obbligatorio.");
        java.sql.Date dataInizio = leggiData("Data Inizio (YYYY-MM-DD): ");

        try {
            controller.iscriviCliente(cf, corso, dataInizio);
            System.out.println("✅ Iscrizione effettuata con successo.");
        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void generaReportPresenze() {
        System.out.println("\n--- REPORTISTICA PRESENZE ---");
        java.sql.Date dataInizio = leggiData("Data Inizio Periodo (YYYY-MM-DD): ");
        java.sql.Date dataFine = null;

        while (dataFine == null) {
            java.sql.Date tempDate = leggiData("Data Fine Periodo (YYYY-MM-DD): ");
            if (tempDate.before(dataInizio)) {
                System.out.println("❌ ERRORE: La data di fine non può essere precedente a quella di inizio.");
            } else {
                dataFine = tempDate;
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

        String cf = leggiCodiceFiscale("Codice Fiscale (16 car): ");
        String nome = leggiStringaObbligatoria("Nome: ", "❌ ERRORE: Il nome è obbligatorio.");
        String cognome = leggiStringaObbligatoria("Cognome: ", "❌ ERRORE: Il cognome è obbligatorio.");
        java.sql.Date dataNascita = leggiData("Data Nascita (YYYY-MM-DD): ");

        System.out.print("Via: ");
        String via = scanner.nextLine().trim();
        System.out.print("Città: ");
        String citta = scanner.nextLine().trim();

        String cap = leggiCAP();

        // Utilizzo del nuovo metodo estratto per ridurre drasticamente la complessità cognitiva
        String[] recapitoData = leggiRecapito("Inserisci Recapito (Cellulare, Fisso, Email) [Vuoto per saltare]: ", true);
        String tipoRecapito = recapitoData[0];
        String valoreRecapito = recapitoData[1];

        try {
            String badgeAssegnato = controller.registraCliente(cf, nome, cognome, dataNascita, via, citta, cap);

            System.out.println("✅ SUCCESSO: Cliente registrato correttamente.");
            System.out.println("💳 BADGE ASSEGNATO: " + badgeAssegnato);

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
        String cf = leggiCodiceFiscale(PROMPT_CF_CLIENTE);

        String[] recapitoData = leggiRecapito("Inserisci Recapito: ", false);

        try {
            controller.aggiungiRecapito(cf, recapitoData[0], recapitoData[1]);
            System.out.println("✅ Recapito aggiunto correttamente.");
        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void proceduraModificaRecapito() {
        System.out.println("\n--- MODIFICA RECAPITO ---");
        String cf = leggiCodiceFiscale(PROMPT_CF_CLIENTE);

        try {
            List<String[]> recapitiAttuali = controller.ottieniRecapitiCliente(cf);

            if (recapitiAttuali.isEmpty()) {
                System.out.println("⚠️ Nessun recapito registrato per questo cliente. Impossibile procedere con modifiche.");
                return;
            }

            System.out.println("\n[Recapiti attualmente associati al cliente]");
            for (String[] item : recapitiAttuali) {
                System.out.println("  -> Tipo: " + item[0] + " | Valore: " + item[1]);
            }
            System.out.println();

        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
            return;
        }

        String vecchio = leggiStringaObbligatoria("Inserisci il VECCHIO recapito da cambiare (scrivi il valore esatto): ", "❌ Il campo non può essere vuoto.");
        String[] recapitoData = leggiRecapito("Inserisci il NUOVO recapito: ", false);

        try {
            controller.aggiornaRecapito(cf, vecchio, recapitoData[1], recapitoData[0]);
            System.out.println("✅ Modifica completata con successo.");
        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void inserimentoNuovoCorso() {
        System.out.println("\n--- INSERIMENTO NUOVO CORSO ---");
        String nomeC = leggiStringaObbligatoria("Nome Corso: ", "❌ ERRORE: Il nome corso è obbligatorio.");

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