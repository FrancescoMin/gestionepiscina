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
            System.out.println("2. 📝 GESTIONE ISCRIZIONI");
            System.out.println("3. 📊 REPORTISTICA PRESENZE");
            System.out.println("4. ⚙️  CONFIGURAZIONE (Anagrafica e Corsi)");
            System.out.println("0. ESCI");
            System.out.print(PROMPT_SCELTA);

            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1": gestisciAccesso(); break;
                case "2": menuIscrizioni(); break;
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
            System.out.println("3. Gestione Orari Piscina");
            System.out.println("0. Torna al menu principale");
            System.out.print(PROMPT_SCELTA);

            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1": menuAnagrafica(); break;
                case "2": menuCorsi(); break;
                case "3": menuOrari(); break;
                case "0": return;
                default: System.out.println(ERR_SCELTA_INVALIDA);
            }
        }
    }

    private void menuAnagrafica() {
        while (true) {
            System.out.println("\n--- GESTIONE ANAGRAFICA CLIENTI ---");
            System.out.println("1. Registra Nuovo Cliente");
            System.out.println("2. Modifica Dati Residenza Cliente"); // NUOVO
            System.out.println("3. Aggiungi Recapito a Cliente Esistente");
            System.out.println("4. Modifica Recapito Esistente");
            System.out.println("5. Disattiva Cliente (Eliminazione)"); // NUOVO
            System.out.println("0. Torna indietro");
            System.out.print(PROMPT_SCELTA);

            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1": registraNuovoCliente(); break;
                case "2": modificaDatiCliente(); break; // NUOVO
                case "3": aggiungiRecapitoAggiuntivo(); break;
                case "4": proceduraModificaRecapito(); break;
                case "5": disattivaCliente(); break; // NUOVO
                case "0": return;
                default: System.out.println(ERR_SCELTA_INVALIDA);
            }
        }
    }

    private void menuCorsi() {
        while (true) {
            System.out.println("\n--- GESTIONE CORSI ---");
            System.out.println("1. Visualizza Corsi Attivi");
            System.out.println("2. Aggiungi Nuovo Corso");
            System.out.println("3. Modifica Dati Corso Esistente");
            System.out.println("4. Sospendi/Elimina Corso");
            System.out.println("5. Gestisci Calendario di un Corso");
            System.out.println("0. Torna indietro");
            System.out.print(PROMPT_SCELTA);

            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1": visualizzaCorsi(); break;
                case "2": inserimentoNuovoCorso(); break;
                case "3": proceduraModificaCorso(); break;
                case "4": disattivaCorso(); break;
                case "5": gestisciCalendarioCorso(); break;
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
        String valore;
        String tipo;
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

    private void menuIscrizioni() {
        while (true) {
            System.out.println("\n--- GESTIONE ISCRIZIONI ---");
            System.out.println("1. Iscrivi Cliente a un Corso");
            System.out.println("2. Annulla Iscrizione Esistente (Disdetta)");
            System.out.println("0. Torna al menu principale");
            System.out.print(PROMPT_SCELTA);

            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1": gestisciIscrizione(); break; // Il tuo metodo esistente
                case "2": annullaIscrizione(); break;  // Il nuovo metodo
                case "0": return;
                default: System.out.println(ERR_SCELTA_INVALIDA);
            }
        }
    }

    private void annullaIscrizione() {
        System.out.println("\n--- ANNULLA ISCRIZIONE CORSO ---");
        String cf = leggiCodiceFiscale(PROMPT_CF_CLIENTE);
        String corso = leggiStringaObbligatoria("Nome del Corso da cui disiscriversi: ", "❌ ERRORE: Il nome corso è obbligatorio.");

        System.out.print("Sei sicuro di voler annullare l'iscrizione? (S/N): ");
        String conferma = scanner.nextLine().trim().toUpperCase();

        if (conferma.equals("S")) {
            try {
                controller.annullaIscrizione(cf, corso);
                System.out.println("✅ Iscrizione annullata con successo.");
            } catch (PiscinaException e) {
                System.out.println("❌ " + e.getMessage());
            }
        } else {
            System.out.println("Operazione annullata.");
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

    private void modificaDatiCliente() {
        System.out.println("\n--- MODIFICA DATI RESIDENZA CLIENTE ---");
        String cf = leggiCodiceFiscale(PROMPT_CF_CLIENTE);

        System.out.print("Nuova Via: ");
        String via = scanner.nextLine().trim();

        System.out.print("Nuova Città: ");
        String citta = scanner.nextLine().trim();

        String cap = leggiCAP();

        try {
            controller.aggiornaDatiCliente(cf, via, citta, cap);
            System.out.println("✅ Modifica dati anagrafici completata con successo.");
        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void disattivaCliente() {
        System.out.println("\n--- DISATTIVA CLIENTE ---");
        System.out.println("⚠️ Attenzione: Questa operazione disattiverà il profilo del cliente.");
        String cf = leggiCodiceFiscale(PROMPT_CF_CLIENTE);

        System.out.print("Sei sicuro di voler procedere? (S/N): ");
        String conferma = scanner.nextLine().trim().toUpperCase();

        if (conferma.equals("S")) {
            try {
                controller.disattivaCliente(cf);
                System.out.println("✅ Cliente disattivato con successo.");
            } catch (PiscinaException e) {
                System.out.println("❌ " + e.getMessage());
            }
        } else {
            System.out.println("Operazione annullata.");
        }
    }

    private java.sql.Time leggiOra(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                // JDBC richiede il formato HH:mm:ss. Se l'utente digita HH:mm, aggiungiamo i secondi.
                if (input.length() == 5) {
                    input += ":00";
                }
                return java.sql.Time.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ ERRORE: Formato ora non valido. Usare il formato HH:mm.");
            }
        }
    }

    private void menuOrari() {
        while (true) {
            System.out.println("\n--- GESTIONE ORARI PISCINA ---");
            System.out.println("1. Visualizza Orari Attuali");
            System.out.println("2. Modifica Orario di un Giorno");
            System.out.println("0. Torna indietro");
            System.out.print(PROMPT_SCELTA);

            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1": visualizzaOrari(); break;
                case "2": proceduraModificaOrario(); break;
                case "0": return;
                default: System.out.println(ERR_SCELTA_INVALIDA);
            }
        }
    }

    private void visualizzaOrari() {
        System.out.println("\n--- ORARI SETTIMANALI DI APERTURA ---");
        try {
            List<String[]> orari = controller.ottieniOrariPiscina();
            for (String[] o : orari) {
                // Rimuove i millisecondi o i secondi in eccesso se presenti nel dump di MySQL
                String apertura = o[1].substring(0, 5);
                String chiusura = o[2].substring(0, 5);
                System.out.printf("🔹 %-10s : %s - %s%n", o[0], apertura, chiusura);
            }
        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void proceduraModificaOrario() {
        visualizzaOrari(); // Mostra il palinsesto prima di chiedere modifiche

        System.out.println("\n--- MODIFICA ORARIO GIORNALIERO ---");
        String giorno = leggiStringaObbligatoria("Inserisci il giorno da modificare (es. 'Lunedì'): ", "❌ Il giorno è obbligatorio.");

        java.sql.Time apertura = leggiOra("Nuovo orario di APERTURA (HH:mm): ");
        java.sql.Time chiusura = leggiOra("Nuovo orario di CHIUSURA (HH:mm): ");

        try {
            controller.aggiornaOrarioPiscina(giorno, apertura, chiusura);
            System.out.println("✅ Orario aggiornato con successo sul database.");
        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void visualizzaCorsi() {
        System.out.println("\n--- ELENCO CORSI ATTIVI ---");
        try {
            java.util.List<String[]> corsi = controller.getCorsiAttivi();
            if (corsi.isEmpty()) {
                System.out.println("Nessun corso attualmente attivo.");
                return;
            }

            for (String[] c : corsi) {
                System.out.printf("🔹 Corso: %s | Costo: €%s | Min: %s | Max: %s | Vasca: %s%n   Descrizione: %s%n",
                        c[0], c[2], c[3], c[4], c[5], c[1]);

                // Integrazione: Recupero e stampa del calendario per questo specifico corso
                java.util.List<String> orari = controller.getOrariDiUnCorso(c[0]);
                if (orari.isEmpty()) {
                    System.out.println("   [Nessun orario pianificato]");
                } else {
                    System.out.println("   Calendario:");
                    for (String o : orari) {
                        System.out.println("     - " + o);
                    }
                }
                System.out.println(); // Riga vuota di spaziatura tra un corso e l'altro
            }
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

        int vasca = 0;
        while (vasca <= 0) {
            System.out.print("Numero Vasca (1, 2, o 3): ");
            try {
                vasca = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ ERRORE: Inserisci un numero intero valido.");
            }
        }

        try {
            controller.aggiungiCorso(nomeC, descC, costo, min, max, vasca);
            System.out.println("✅ Corso aggiunto con successo!");
        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void proceduraModificaCorso() {
        System.out.println("\n--- MODIFICA CORSO ---");
        String nomeC = leggiStringaObbligatoria("Nome del corso da modificare: ", "❌ ERRORE: Il nome è obbligatorio.");

        System.out.print("Nuova Descrizione: ");
        String descC = scanner.nextLine().trim();

        double costo = -1;
        while (costo < 0) {
            System.out.print("Nuovo Costo Mensile (€): ");
            try {
                costo = Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ ERRORE: Inserisci un valore numerico valido.");
            }
        }

        int min = 0, max = 0;
        while (min <= 0 || max <= min) {
            try {
                System.out.print("Nuovo Min Partecipanti: ");
                min = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Nuovo Max Partecipanti: ");
                max = Integer.parseInt(scanner.nextLine().trim());
                if (max <= min) {
                    System.out.println("❌ ERRORE: Il massimo deve essere superiore al minimo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ ERRORE: Inserisci numeri interi.");
            }
        }

        int vasca = 0;
        while (vasca <= 0) {
            System.out.print("Nuovo Numero Vasca (1, 2, o 3): ");
            try {
                vasca = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ ERRORE: Inserisci un numero intero valido.");
            }
        }

        try {
            controller.aggiornaCorso(nomeC, descC, costo, min, max, vasca);
            System.out.println("✅ Corso modificato con successo.");
        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void disattivaCorso() {
        System.out.println("\n--- SOSPENDI CORSO ---");
        String nomeC = leggiStringaObbligatoria("Nome del corso da disattivare: ", "❌ ERRORE: Il nome è obbligatorio.");

        System.out.print("Sei sicuro di voler procedere? (S/N): ");
        String conferma = scanner.nextLine().trim().toUpperCase();

        if (conferma.equals("S")) {
            try {
                controller.disattivaCorso(nomeC);
                System.out.println("✅ Corso disattivato con successo. Non sarà più visibile tra quelli attivi.");
            } catch (PiscinaException e) {
                System.out.println("❌ " + e.getMessage());
            }
        } else {
            System.out.println("Operazione annullata.");
        }
    }

    private void gestisciCalendarioCorso() {
        System.out.println("\n--- CALENDARIO CORSO ---");
        String nomeCorso = leggiStringaObbligatoria("Inserisci il nome del corso: ", "Il nome è obbligatorio.");

        try {
            List<String> orariAttuali = controller.getOrariDiUnCorso(nomeCorso);
            if (orariAttuali.isEmpty()) {
                System.out.println("Nessun orario attualmente pianificato per questo corso.");
            } else {
                System.out.println("Orari attuali:");
                for (String o : orariAttuali) {
                    System.out.println("- " + o);
                }
            }
        } catch (PiscinaException e) {
            System.out.println("❌ " + e.getMessage());
            return;
        }

        System.out.print("\nVuoi aggiungere una nuova fascia oraria? (S/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("S")) {
            String giorno = "";
            java.util.List<String> giorniValidi = java.util.Arrays.asList(
                    "Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica",
                    "Lunedi", "Martedi", "Mercoledi", "Giovedi", "Venerdi"
            );

            while (true) {
                System.out.print("Giorno della settimana (es. Lunedì): ");
                giorno = scanner.nextLine().trim();

                if (giorno.length() > 0) {
                    // Rende sempre maiuscola la prima lettera e minuscole le altre
                    giorno = giorno.substring(0, 1).toUpperCase() + giorno.substring(1).toLowerCase();
                }

                if (giorniValidi.contains(giorno)) {
                    // Corregge automaticamente eventuali assenze di accento
                    if (giorno.equals("Lunedi")) giorno = "Lunedì";
                    if (giorno.equals("Martedi")) giorno = "Martedì";
                    if (giorno.equals("Mercoledi")) giorno = "Mercoledì";
                    if (giorno.equals("Giovedi")) giorno = "Giovedì";
                    if (giorno.equals("Venerdi")) giorno = "Venerdì";
                    break; // Input valido, esce dal ciclo
                } else {
                    System.out.println("❌ ERRORE: Inserisci un giorno della settimana valido.");
                }
            }
            java.sql.Time inizio = leggiOra("Ora Inizio (HH:mm): ");
            java.sql.Time fine = leggiOra("Ora Fine (HH:mm): ");

            try {
                controller.aggiungiOrarioCorso(nomeCorso, giorno, inizio, fine);
                System.out.println("✅ Orario inserito correttamente nel calendario.");
            } catch (PiscinaException e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }

}