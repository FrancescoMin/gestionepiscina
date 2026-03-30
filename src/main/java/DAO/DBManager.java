import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    // Sostituisci con i tuoi parametri reali
    private static final String URL = "jdbc:mysql://localhost:3306/piscina";
    private static final String USER = "app_segretario";
    private static final String PASS = "password_segretario";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}