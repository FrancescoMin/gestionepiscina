package model;

import exceptions.PiscinaException;
import java.sql.*;

public class SegreteriaDAO {

    public boolean login(String username, String password) throws PiscinaException {
        String sql = "{call login(?, ?, ?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.registerOutParameter(3, java.sql.Types.VARCHAR);

            stmt.execute();
            String role = stmt.getString(3);
            return "segretario".equalsIgnoreCase(role);

        } catch (SQLException e) {
            throw new PiscinaException("Autenticazione fallita: " + e.getMessage());
        }
    }

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
        String sql = "{call modifica_recapito(?, ?, ?, ?)}";

        try (Connection conn = DBManager.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, cf);
            cstmt.setString(2, vecchioValore);
            cstmt.setString(3, nuovoValore);
            cstmt.setString(4, tipo.trim().toLowerCase());

            // Esegue la procedura. Se ROW_COUNT() è 0, il DB lancia l'errore
            // e Java finisce direttamente nel blocco catch qui sotto.
            cstmt.execute();

        } catch (SQLException e) {
            // Intercetta il SIGNAL lanciato da MySQL
            throw new PiscinaException(e.getMessage());
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

    public void annullaIscrizione(String cf, String nomeCorso) throws PiscinaException {
        String sql = "{call annulla_iscrizione(?, ?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, cf);
            stmt.setString(2, nomeCorso);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new PiscinaException("Nessuna iscrizione trovata per questo cliente al corso specificato o corso non esistente.");
            }
        } catch (SQLException e) {
            throw new PiscinaException("Errore durante l'annullamento dell'iscrizione: " + e.getMessage());
        }
    }

    public void registraAccesso(String codBadge) throws PiscinaException {
        String sql = "{call registra_accesso(?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, codBadge);
            stmt.execute();
        } catch (SQLException e) {
            throw new PiscinaException("Accesso negato dal tornello: " + e.getMessage());
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

    public void aggiungiCorso(String nome, String desc, double costo, int min, int max, int numVasca) throws PiscinaException {
        String sql = "{call inserisci_nuovo_corso(?, ?, ?, ?, ?, ?)}"; // Aggiunto un punto interrogativo
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, desc);
            stmt.setDouble(3, costo);
            stmt.setInt(4, min);
            stmt.setInt(5, max);
            stmt.setInt(6, numVasca); // Set del nuovo parametro
            stmt.execute();
        } catch (SQLException e) {
            throw new PiscinaException("Errore durante l'inserimento del corso: " + e.getMessage());
        }
    }

    public void aggiornaCorso(String nome, String desc, double costo, int min, int max, int numVasca) throws PiscinaException {
        String sql = "{call modifica_corso(?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, desc);
            stmt.setDouble(3, costo);
            stmt.setInt(4, min);
            stmt.setInt(5, max);
            stmt.setInt(6, numVasca);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new PiscinaException("Nessun corso trovato con questo nome.");
            }
        } catch (SQLException e) {
            throw new PiscinaException("Errore durante la modifica del corso: " + e.getMessage());
        }
    }

    public java.util.List<String[]> getCorsiAttivi() throws PiscinaException {
        java.util.List<String[]> corsi = new java.util.ArrayList<>();
        String sql = "SELECT NomeCorso, Descrizione, CostoMensile, NumMinPartecipanti, NumMaxPartecipanti, NumVasca FROM Corso WHERE Attivo = TRUE";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String[] corso = new String[6]; // Dimensione array aumentata a 6
                corso[0] = rs.getString("NomeCorso");
                corso[1] = rs.getString("Descrizione");
                corso[2] = String.valueOf(rs.getDouble("CostoMensile"));
                corso[3] = String.valueOf(rs.getInt("NumMinPartecipanti"));
                corso[4] = String.valueOf(rs.getInt("NumMaxPartecipanti"));
                corso[5] = String.valueOf(rs.getInt("NumVasca")); // Acquisizione vasca
                corsi.add(corso);
            }
        } catch (SQLException e) {
            throw new PiscinaException("Errore nel recupero dell'elenco corsi: " + e.getMessage());
        }
        return corsi;
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

    public void aggiornaDatiCliente(String cf, String via, String citta, String cap) throws PiscinaException {
        String sql = "{call modifica_cliente(?, ?, ?, ?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, cf);
            stmt.setString(2, via);
            stmt.setString(3, citta);
            stmt.setString(4, cap);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new PiscinaException("Nessun cliente trovato con questo Codice Fiscale.");
            }
        } catch (SQLException e) {
            throw new PiscinaException("Errore durante l'aggiornamento dei dati: " + e.getMessage());
        }
    }

    public void disattivaCliente(String cf) throws PiscinaException {
        String sql = "{call disattiva_cliente(?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, cf);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new PiscinaException("Nessun cliente trovato con questo Codice Fiscale.");
            }
        } catch (SQLException e) {
            throw new PiscinaException("Errore durante la disattivazione del cliente: " + e.getMessage());
        }
    }

    public java.util.List<String[]> getOrariPiscina() throws PiscinaException {
        java.util.List<String[]> orari = new java.util.ArrayList<>();
        String sql = "SELECT GiornoSettimana, OrarioApertura, OrarioChiusura FROM OrarioPiscina";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String[] riga = new String[3];
                riga[0] = rs.getString("GiornoSettimana");
                riga[1] = rs.getString("OrarioApertura");
                riga[2] = rs.getString("OrarioChiusura");
                orari.add(riga);
            }
        } catch (SQLException e) {
            throw new PiscinaException("Errore nel recupero degli orari: " + e.getMessage());
        }
        return orari;
    }

    public void aggiornaOrario(String giorno, java.sql.Time apertura, java.sql.Time chiusura) throws PiscinaException {
        String sql = "{call modifica_orario(?, ?, ?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, giorno);
            stmt.setTime(2, apertura);
            stmt.setTime(3, chiusura);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new PiscinaException("Giorno non trovato. Verificare l'esattezza del nome (es. 'Lunedì').");
            }
        } catch (SQLException e) {
            throw new PiscinaException("Errore nell'aggiornamento dell'orario: " + e.getMessage());
        }
    }

    public void disattivaCorso(String nome) throws PiscinaException {
        String sql = "{call disattiva_corso(?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, nome);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new PiscinaException("Nessun corso trovato con questo nome.");
            }
        } catch (SQLException e) {
            throw new PiscinaException("Errore durante la disattivazione del corso: " + e.getMessage());
        }
    }

    public void aggiungiOrarioCorso(String nomeCorso, String giorno, java.sql.Time inizio, java.sql.Time fine) throws PiscinaException {
        String sql = "INSERT INTO CalendarioCorso (NomeCorso, GiornoSettimana, OraInizio, OraFine) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nomeCorso);
            pstmt.setString(2, giorno);
            pstmt.setTime(3, inizio);
            pstmt.setTime(4, fine);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            // Intercetta il segnale 45000 del trigger
            if ("45000".equals(e.getSQLState())) {
                throw new PiscinaException(e.getMessage());
            }
            throw new PiscinaException("Errore durante l'inserimento dell'orario: " + e.getMessage());
        }
    }

    public java.util.List<String> getOrariDiUnCorso(String nomeCorso) throws PiscinaException {
        java.util.List<String> orari = new java.util.ArrayList<>();
        String sql = "SELECT GiornoSettimana, OraInizio, OraFine FROM CalendarioCorso WHERE NomeCorso = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nomeCorso);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orari.add(rs.getString("GiornoSettimana") + " | " +
                            rs.getTime("OraInizio").toString() + " - " +
                            rs.getTime("OraFine").toString());
                }
            }
        } catch (SQLException e) {
            throw new PiscinaException("Errore nel recupero del calendario: " + e.getMessage());
        }
        return orari;
    }
    public java.util.List<String[]> getPalinsestoCompleto() throws PiscinaException {
        java.util.List<String[]> righe = new java.util.ArrayList<>();
        String sql = "SELECT GiornoSettimana, OraInizio, OraFine, NomeCorso, NumVasca, CapienzaVasca, CostoMensile FROM view_palinsesto_corsi";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String[] r = new String[7];
                r[0] = rs.getString("GiornoSettimana");
                r[1] = rs.getTime("OraInizio").toString().substring(0, 5);
                r[2] = rs.getTime("OraFine").toString().substring(0, 5);
                r[3] = rs.getString("NomeCorso");
                r[4] = String.valueOf(rs.getInt("NumVasca"));
                r[5] = String.valueOf(rs.getInt("CapienzaVasca"));
                r[6] = String.valueOf(rs.getDouble("CostoMensile"));
                righe.add(r);
            }
        } catch (SQLException e) {
            throw new PiscinaException("Errore consultazione palinsesto: " + e.getMessage());
        }
        return righe;
    }


    public java.util.List<String[]> getIscrizioniCliente(String cf) throws PiscinaException {
        java.util.List<String[]> iscrizioni = new java.util.ArrayList<>();
        String sql = "{call visualizza_iscrizioni_cliente(?)}";

        try (Connection conn = DBManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, cf);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String[] riga = new String[3];
                    riga[0] = rs.getString("NomeCorso");
                    riga[1] = rs.getDate("DataInizio").toString();
                    riga[2] = rs.getDate("DataFine").toString();
                    iscrizioni.add(riga);
                }
            }
        } catch (SQLException e) {
            throw new PiscinaException("Errore durante il recupero delle iscrizioni: " + e.getMessage());
        }
        return iscrizioni;
    }

    public java.util.List<String[]> getAvvisiScadenze() throws PiscinaException {
        java.util.List<String[]> avvisi = new java.util.ArrayList<>();
        String sql = "SELECT DataCreazione, CF_Cliente, NomeCorso, TipoAvviso, Messaggio FROM AvvisoScadenza ORDER BY IDAvviso DESC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String[] r = new String[5];
                r[0] = rs.getDate("DataCreazione").toString();
                r[1] = rs.getString("CF_Cliente");
                r[2] = rs.getString("NomeCorso");
                r[3] = rs.getString("TipoAvviso");
                r[4] = rs.getString("Messaggio");
                avvisi.add(r);
            }
        } catch (SQLException e) {
            throw new PiscinaException("Errore durante il recupero degli avvisi: " + e.getMessage());
        }
        return avvisi;
    }


}