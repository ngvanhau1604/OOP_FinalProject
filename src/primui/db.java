import java.sql.*;
import javax.swing.JOptionPane;

public class db {
    private static final String DB_URL = "jdbc:sqlite:finalG.db";
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public void khoiTaoDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS DoThi(
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Ten TEXT NOT NULL,
                SoDinh INTEGER NOT NULL,
                SoCanh INTEGER NOT NULL,
                DinhBatDau INTEGER NOT NULL,
                DanhSachCanh TEXT NOT NULL
            );
            """;
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khởi tạo DB: " + e.getMessage());
        }
    }

    public void saveDoThiVaoDB(String ten, int n, int m, int s, String dsCanh) throws SQLException {
        String sql = "INSERT INTO DoThi(Ten, SoDinh, SoCanh, DinhBatDau, DanhSachCanh) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ten);
            ps.setInt(2, n);
            ps.setInt(3, m);
            ps.setInt(4, s);
            ps.setString(5, dsCanh);
            ps.executeUpdate();
        }
    }

    public ResultSet loadDoThiTuDB(int id) throws SQLException {
        String sql = "SELECT * FROM DoThi WHERE ID = ?";
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps.executeQuery();
    }
}