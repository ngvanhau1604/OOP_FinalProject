import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Database {
    public static Connection getConnection() {
        try {
            String url = "jdbc:sqlite:finalG.db"; 
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Kết nối SQLite thất bại!");
            e.printStackTrace();
            return null;
        }
    }
}
