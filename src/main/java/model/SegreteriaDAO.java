package model;

import exceptions.PiscinaException;
import java.sql.*;

public class SegreteriaDAO {


    public String registraCliente(String cf, String nome, String cognome, java.sql.Date dataNascita, String via, String citta, String cap) throws PiscinaException {
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

            // Registrazione del parametro OUT per il badge
            stmt.registerOutParameter(8, java.sql.Types.VARCHAR);

            stmt.execute();

            // Recupero del valore calcolato dal database
            return stmt.getString(8);

        } catch (SQLException e) {
            throw new PiscinaException("Errore registrazione cliente: " + e.getMessage());
        }
    }

    public void aggiungiRecapito(String cf, String tipo, String valore) throws PiscinaException {
        String sql = "{call aggiungi_recapito(?, ?, ?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, cf);
            stmt.setString(2, tipo);
            stmt.setString(3, valore);
            stmt.execute();
        } catch (SQLException e) {
            throw new PiscinaException("Errore durante l'inserimento del recapito: " + e.getMessage());
        }
    }

    public void modificaRecapito(String cf, String vecchioValore, String nuovoValore, String tipo) throws PiscinaException {
        String sql = "UPDATE recapito SET Valore = ?, Tipo = ? WHERE CF_Cliente = ? AND Valore = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuovoValore);
            pstmt.setString(2, tipo);
            pstmt.setString(3, cf);
            pstmt.setString(4, vecchioValore);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new PiscinaException("Nessun recapito corrispondente trovato per la modifica.");
            }
        } catch (SQLException e) {
            throw new PiscinaException("Errore durante la modifica del recapito: " + e.getMessage());
        }
    }

    public void iscriviCliente(String cf, String nomeCorso, java.sql.Date dataInizio) throws PiscinaException {
        String sql = "{call iscrivi_cliente(?, ?, ?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, cf);
            stmt.setString(2, nomeCorso);
            stmt.setDate(3, dataInizio);
            stmt.execute();
        } catch (SQLException e) {
            throw new PiscinaException("Errore durante l'iscrizione al corso: " + e.getMessage());
        }
    }

    public void registraAccesso(String cf) throws PiscinaException {
        String sql = "{call registra_accesso(?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, cf);
            stmt.execute();
        } catch (SQLException e) {
            throw new PiscinaException("Accesso negato dal sistema: " + e.getMessage());
        }
    }

    public void generaReportPresenze(java.sql.Date dataInizio, java.sql.Date dataFine) throws PiscinaException {
        String sql = "{call genera_report_presenze(?, ?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setDate(1, dataInizio);
            stmt.setDate(2, dataFine);

            try (ResultSet rs = stmt.executeQuery()) {

                System.out.println("\n=========================================================");
                System.out.println("                 REPORT PRESENZE PISCINA                 ");
                System.out.println("=========================================================");
                System.out.printf("%-15s | %-17s | %-17s%n", "Data", "Accessi Effettivi", "Presenze Previste");
                System.out.println("---------------------------------------------------------");

                boolean hasData = false;
                while (rs.next()) {
                    hasData = true;
                    System.out.printf("%-15s | %-17d | %-17d%n",
                            rs.getDate("Giorno").toString(), rs.getInt("AccessiEffettivi"), rs.getInt("PresenzePreviste"));
                }
                if (!hasData) {
                    System.out.println("Nessun accesso registrato in questo periodo.");
                }
            }
        } catch (SQLException e) {
            throw new PiscinaException("Errore generazione report: " + e.getMessage());
        }
    }

    public void aggiungiCorso(String nome, String desc, double costo, int min, int max) throws PiscinaException {
        String sql = "{call inserisci_nuovo_corso(?, ?, ?, ?, ?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, desc);
            stmt.setDouble(3, costo);
            stmt.setInt(4, min);
            stmt.setInt(5, max);
            stmt.execute();
        } catch (SQLException e) {
            throw new PiscinaException("Errore durante l'inserimento del corso: " + e.getMessage());
        }
    }

    public java.util.List<String[]> getRecapitiCliente(String cf) throws PiscinaException {
        java.util.List<String[]> elencoRecapiti = new java.util.ArrayList<>();
        String sql = "SELECT Tipo, Valore FROM recapito WHERE CF_Cliente = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cf);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] recapito = new String[2];
                    recapito[0] = rs.getString("Tipo");
                    recapito[1] = rs.getString("Valore");
                    elencoRecapiti.add(recapito);
                }
            }
        } catch (SQLException e) {
            throw new PiscinaException("Errore durante il recupero dei recapiti del cliente: " + e.getMessage());
        }
        return elencoRecapiti;
    }
}