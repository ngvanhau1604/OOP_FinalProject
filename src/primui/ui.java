import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class ui extends JFrame {
    private JTextField txtTenDoThi, txtSoDinh, txtSoCanh, txtDinhBatDau;
    private JTextArea txtDanhSachCanh, txtKetQua;
    private JButton btnChayPrim, btnLuu, btnTai;
    private GraphPanel graphPanel;
    private int dinhBatDauMST;
    private db dbManager;

    public ui() {
        super("Thuật toán Prim - MST");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        dbManager = new db();
        dbManager.khoiTaoDatabase();

        JPanel panelNhapLieu = new JPanel(new GridLayout(8, 1, 5, 5));
        panelNhapLieu.setBorder(BorderFactory.createTitledBorder("Nhập dữ liệu"));

        txtTenDoThi = new JTextField();
        txtSoDinh = new JTextField();
        txtSoCanh = new JTextField();
        txtDinhBatDau = new JTextField();
        txtDanhSachCanh = new JTextArea(8, 30);
        txtDanhSachCanh.setBorder(BorderFactory.createTitledBorder("Danh sách cạnh (u v w)"));

        panelNhapLieu.add(new JLabel("Tên đồ thị:"));
        panelNhapLieu.add(txtTenDoThi);
        panelNhapLieu.add(new JLabel("Số đỉnh (n):"));
        panelNhapLieu.add(txtSoDinh);
        panelNhapLieu.add(new JLabel("Số cạnh (m):"));
        panelNhapLieu.add(txtSoCanh);
        panelNhapLieu.add(new JLabel("Đỉnh bắt đầu (s):"));
        panelNhapLieu.add(txtDinhBatDau);

        JPanel left = new JPanel(new BorderLayout(5,5));
        left.add(panelNhapLieu, BorderLayout.NORTH);
        left.add(new JScrollPane(txtDanhSachCanh), BorderLayout.CENTER);

        // Nút chức năng
        btnChayPrim = new JButton("Chạy Prim");
        btnChayPrim.addActionListener(e -> chayPrim());

        btnLuu = new JButton("Lưu vào CSDL");
        btnLuu.addActionListener(e -> luuDoThi());

        btnTai = new JButton("Tải từ CSDL");
        btnTai.addActionListener(e -> taiDoThi());

        JPanel panelNut = new JPanel();
        panelNut.add(btnChayPrim);
        panelNut.add(btnLuu);
        panelNut.add(btnTai);
        left.add(panelNut, BorderLayout.SOUTH);

        txtKetQua = new JTextArea(10, 30);
        txtKetQua.setEditable(false);
        txtKetQua.setBorder(BorderFactory.createTitledBorder("Kết quả"));
        left.add(new JScrollPane(txtKetQua), BorderLayout.EAST);

        graphPanel = new GraphPanel();
        graphPanel.setPreferredSize(new Dimension(800, 600));
        graphPanel.setBorder(BorderFactory.createTitledBorder("Minh họa đồ thị"));

        add(left, BorderLayout.WEST);
        add(graphPanel, BorderLayout.CENTER);
    }

    private void chayPrim() {
        try {
            int n = Integer.parseInt(txtSoDinh.getText().trim());
            dinhBatDauMST = Integer.parseInt(txtDinhBatDau.getText().trim());

            List<prim.Canh> danhSachCanh = new ArrayList<>();
            Scanner sc = new Scanner(txtDanhSachCanh.getText());
            while (sc.hasNext()) {
                if (!sc.hasNextInt()) { sc.next(); continue; }
                int u = sc.nextInt();
                if (!sc.hasNextInt()) break;
                int v = sc.nextInt();
                if (!sc.hasNextInt()) break;
                int w = sc.nextInt();
                danhSachCanh.add(new prim.Canh(u, v, w));
            }

            List<prim.Canh> cayKhungNhoNhat = prim.prim(n, danhSachCanh, dinhBatDauMST);

            int tongChiPhi = prim.tinhTongChiPhi(cayKhungNhoNhat);

            StringBuilder sb = new StringBuilder("Các cạnh trong MST:\n");
            for (prim.Canh c : cayKhungNhoNhat)
                sb.append("(").append(c.u).append(", ").append(c.v).append(") = ").append(c.w).append("\n");
            sb.append("Tổng chi phí = ").append(tongChiPhi);
            txtKetQua.setText(sb.toString());

            graphPanel.setGraph(n, danhSachCanh, cayKhungNhoNhat, dinhBatDauMST);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu nhập không hợp lệ: " + ex.getMessage());
        }
    }

    private void luuDoThi() {
        try {
            String ten = txtTenDoThi.getText().trim();
            if (ten.isEmpty()) { 
                JOptionPane.showMessageDialog(this, "Nhập tên đồ thị trước khi lưu."); 
                return; 
            }
            int n = Integer.parseInt(txtSoDinh.getText().trim());
            int m = Integer.parseInt(txtSoCanh.getText().trim());
            int s = Integer.parseInt(txtDinhBatDau.getText().trim());
            String ds = txtDanhSachCanh.getText();
            
            dbManager.saveDoThiVaoDB(ten, n, m, s, ds);
            JOptionPane.showMessageDialog(this, "Đã lưu đồ thị vào CSDL (DoThi).");
            
            // Clear fields after saving
            txtTenDoThi.setText("");
            txtSoDinh.setText("");
            txtSoCanh.setText("");
            txtDinhBatDau.setText("");
            txtDanhSachCanh.setText("");
            txtKetQua.setText("");
            graphPanel.setGraph(0, null, null, 0);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số đỉnh/số cạnh/đỉnh bắt đầu phải là số nguyên.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu CSDL: " + ex.getMessage());
        }
    }

    private void taiDoThi() {
        String idStr = JOptionPane.showInputDialog(this, "Nhập ID đồ thị cần tải:");
        if (idStr == null || idStr.trim().isEmpty()) return;
        try {
            int id = Integer.parseInt(idStr.trim());
            ResultSet rs = dbManager.loadDoThiTuDB(id);
            if (rs.next()) {
                txtTenDoThi.setText(rs.getString("Ten"));
                txtSoDinh.setText(String.valueOf(rs.getInt("SoDinh")));
                txtSoCanh.setText(String.valueOf(rs.getInt("SoCanh")));
                txtDinhBatDau.setText(String.valueOf(rs.getInt("DinhBatDau")));
                txtDanhSachCanh.setText(rs.getString("DanhSachCanh"));
                JOptionPane.showMessageDialog(this, "Đã tải đồ thị ID = " + id);
                chayPrim();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy đồ thị ID = " + id);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID phải là số nguyên.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải từ CSDL: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ui u = new ui();
            u.setLocationRelativeTo(null);
            u.setVisible(true);
        });
    }
}

class GraphPanel extends JPanel {
    private int n;
    private List<prim.Canh> danhSachCanh;
    private List<prim.Canh> cayKhungNhoNhat;
    private Point[] viTriDinh;
    private int dinhBatDauMST;

    public void setGraph(int n, List<prim.Canh> danhSachCanh, List<prim.Canh> cayKhungNhoNhat, int dinhBatDauMST) {
        this.n = n;
        this.danhSachCanh = danhSachCanh;
        this.cayKhungNhoNhat = cayKhungNhoNhat;
        this.dinhBatDauMST = dinhBatDauMST;
        viTriDinh = new Point[n + 1];

        int width = getWidth() > 0 ? getWidth() : 800;
        int height = getHeight() > 0 ? getHeight() : 600;
        int cx = width / 2, cy = height / 2;
        int radius = Math.min(width, height) / 2 - 60;
        if (radius < 150) radius = 150;

        for (int i = 1; i <= n; i++) {
            double angle = 2 * Math.PI * i / n;
            int x = (int) (cx + radius * Math.cos(angle));
            int y = (int) (cy + radius * Math.sin(angle));
            viTriDinh[i] = new Point(x, y);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (danhSachCanh == null || viTriDinh == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.GRAY);
        g2.setStroke(new BasicStroke(1.5f));
        for (prim.Canh c : danhSachCanh) {
            if (c.u < 1 || c.v < 1 || c.u >= viTriDinh.length || c.v >= viTriDinh.length) continue;
            Point a = viTriDinh[c.u];
            Point b = viTriDinh[c.v];
            g2.drawLine(a.x, a.y, b.x, b.y);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(String.valueOf(c.w), (a.x + b.x)/2, (a.y + b.y)/2);
            g2.setColor(Color.GRAY);
        }
        if (cayKhungNhoNhat != null) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3f));
            for (prim.Canh c : cayKhungNhoNhat) {
                if (c.u < 1 || c.v < 1 || c.u >= viTriDinh.length || c.v >= viTriDinh.length) continue;
                Point a = viTriDinh[c.u];
                Point b = viTriDinh[c.v];
                g2.drawLine(a.x, a.y, b.x, b.y);
            }
        }
        for (int i = 1; i <= n; i++) {
            Point p = viTriDinh[i];
            if (i == dinhBatDauMST) g2.setColor(Color.RED);
            else g2.setColor(Color.BLUE);
            g2.fillOval(p.x - 15, p.y - 15, 30, 30);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(String.valueOf(i), p.x - 5, p.y + 5);
        }
    }
}