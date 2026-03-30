package DAO; // Usa il tuo package

import exceptions.PiscinaException; // Importa l'eccezione
import java.sql.*;

public class SegreteriaDAO {

    // 0. Login
    public boolean login(String username, String password) throws PiscinaException {
        String sql = "{call login(?, ?, ?)}";

        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.registerOutParameter(3, Types.VARCHAR); // Il parametro OUT p_role

            stmt.execute();

            String role = stmt.getString(3);
            // Restituisce true se il ruolo è segretario, false altrimenti
            return role != null && role.equals("segretario");

        } catch (SQLException e) {
            // Se le credenziali sono errate, il trigger MySQL lancia un errore che catturiamo qui
            throw new PiscinaException(e.getMessage());
        }
    }

    // 1. Registra Cliente
    public void registraCliente(String cf, String nome, String cognome, Date dataNascita,
                                String via, String citta, String cap, String codBadge) throws PiscinaException {
        String sql = "{call registrazione_cliente(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, cf);
            stmt.setString(2, nome);
            stmt.setString(3, cognome);
            stmt.setDate(4, dataNascita);
            stmt.setString(5, via);
            stmt.setString(6, citta);
            stmt.setString(7, cap);
            stmt.setString(8, codBadge);
            stmt.execute();
            // Nessun System.out.println qui!
        } catch (SQLException e) {
            throw new PiscinaException(e.getMessage());
        }
    }

    // 2. Iscrivi Cliente
    public void iscriviCliente(String cf, String nomeCorso, Date dataInizio) throws PiscinaException {
        String sql = "{call iscrivi_cliente(?, ?, ?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, cf);
            stmt.setString(2, nomeCorso);
            stmt.setDate(3, dataInizio);
            stmt.execute();
        } catch (SQLException e) {
            throw new PiscinaException(e.getMessage());
        }
    }

    // 3. Registra Accesso (Tornello)
    public void registraAccesso(String cf) throws PiscinaException {
        String sql = "{call registra_accesso(?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, cf);
            stmt.execute();
        } catch (SQLException e) {
            throw new PiscinaException(e.getMessage());
        }
    }

    // 4. Genera Report
    public void generaReportPresenze(Date dataInizio, Date dataFine) throws PiscinaException {
        String sql = "{call genera_report_presenze(?, ?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setDate(1, dataInizio);
            stmt.setDate(2, dataFine);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n=========================================================");
            System.out.println("                 REPORT PRESENZE PISCINA                 ");
            System.out.println("=========================================================");
            System.out.printf("%-15s | %-17s | %-17s%n", "Data", "Accessi Effettivi", "Presenze Previste");
            System.out.println("---------------------------------------------------------");

            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                System.out.printf("%-15s | %-17d | %-17d%n",
                        rs.getDate("Giorno").toString(), rs.getInt("AccessiEffettivi"), rs.getInt("PresenzePreviste"));
            }
            if (!hasResults) System.out.println("Nessun dato trovato.");
            System.out.println("=========================================================\n");
        } catch (SQLException e) {
            throw new PiscinaException("Errore SQL durante la generazione del report: " + e.getMessage());
        }
    }

    // 5. Visualizza Corsi (Tramite Stored Procedure)
    public void visualizzaCorsiDisponibili() throws PiscinaException {
        // Ora chiamiamo semplicemente la procedura, senza scrivere query complesse in Java!
        String sql = "{call visualizza_corsi_disponibili()}";

        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n--- ELENCO CORSI E SESSIONI DISPONIBILI ---");
            System.out.println("-----------------------------------------------------------------------");
            System.out.printf("%-20s | %-7s | %-15s | %-10s | %-5s%n", "Corso", "Costo", "Giorno", "Ora Inizio", "Vasca");
            System.out.println("-----------------------------------------------------------------------");

            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                String giorno = rs.getString("GiornoSettimana");
                if (giorno == null) {
                    System.out.printf("%-20s | €%-6.2f | %-15s | %-10s | %-5s%n",
                            rs.getString("NomeCorso"), rs.getDouble("Costo"), "Nessuna sessione", "-", "-");
                } else {
                    System.out.printf("%-20s | €%-6.2f | %-15s | %-10s | %-5d%n",
                            rs.getString("NomeCorso"), rs.getDouble("Costo"), giorno, rs.getTime("OraInizio").toString(), rs.getInt("NumVasca"));
                }
            }
            if (!hasResults) System.out.println("Nessun corso presente.");
            System.out.println("-----------------------------------------------------------------------\n");
        } catch (SQLException e) {
            throw new PiscinaException("Impossibile caricare i corsi: " + e.getMessage());
        }
    }

    // 6. Visualizza Iscrizioni (Tramite Stored Procedure)
    public void visualizzaIscrizioniCliente(String cf) throws PiscinaException {
        // Chiamiamo la procedura passando il parametro
        String sql = "{call visualizza_iscrizioni_cliente(?)}";

        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, cf);

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n--- ISCRIZIONI ATTIVE PER IL CLIENTE: " + cf + " ---");
                System.out.println("----------------------------------------------------");
                System.out.printf("%-20s | %-12s | %-12s%n", "Corso", "Data Inizio", "Data Fine");
                System.out.println("----------------------------------------------------");

                boolean hasResults = false;
                while (rs.next()) {
                    hasResults = true;
                    System.out.printf("%-20s | %-12s | %-12s%n",
                            rs.getString("NomeCorso"), rs.getDate("DataInizio").toString(), rs.getDate("DataFine").toString());
                }
                if (!hasResults) System.out.println("Nessuna iscrizione attiva.");
                System.out.println("----------------------------------------------------\n");
            }
        } catch (SQLException e) {
            throw new PiscinaException("Errore nel recupero iscrizioni: " + e.getMessage());
        }
    }

        // 7. Aggiungi Recapito (chiamato subito dopo la registrazione cliente)
        public void aggiungiRecapito(String cf, String tipo, String valore) throws PiscinaException {
            String sql = "{call aggiungi_recapito(?, ?, ?)}";

            try (Connection conn = DBManager.getConnection();
                 CallableStatement stmt = conn.prepareCall(sql)) {

                stmt.setString(1, cf);
                stmt.setString(2, tipo); // La CLI passerà "cellulare" o "email" grazie alla nostra regex
                stmt.setString(3, valore);

                stmt.execute();

            } catch (SQLException e) {
                throw new PiscinaException("Errore durante il salvataggio del recapito: " + e.getMessage());
            }
        }
    }