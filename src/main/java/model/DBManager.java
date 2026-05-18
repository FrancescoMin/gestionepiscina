package model;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBManager {
    private static String url;
    private static String user;
    private static String pass;

    // Aggiunta del costruttore privato per nascondere quello pubblico implicito
    private DBManager() {
        throw new IllegalStateException("Classe di utilità: non può essere istanziata.");
    }

    // Il blocco statico inizializza i parametri una sola volta all'avvio dell'applicazione
    static {
        Properties props = new Properties();
        try (InputStream input = DBManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("Errore: Impossibile trovare il file config.properties in src/main/resources/");
            }

            // Caricamento dei parametri in memoria
            props.load(input);

            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            pass = props.getProperty("db.pass");

            if (url == null || user == null || pass == null) {
                throw new IllegalStateException("Errore: Una o più proprietà obbligatorie mancano nel config.properties");
            }

        } catch (IOException e) {
            throw new ExceptionInInitializerError("Impossibile leggere il file di configurazione del database: " + e.getMessage());
        }
    }

    /**
     * Fornisce una connessione attiva verso il database MySQL utilizzando
     * i parametri letti dinamicamente dal file di configurazione.
     * * @return Connection oggetto di connessione JDBC
     * @throws SQLException in caso di fallimento della connessione o credenziali errate
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
}