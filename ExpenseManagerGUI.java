import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class ExpenseManagerGUI {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/expense"; // Update with your DB name
    private static final String DB_USER = "root"; // Update with your username
    private static final String DB_PASSWORD = "Aditya@2004"; // Update with your password

    private Connection conn;

    private JFrame frame;
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JTextField amountField, categoryField, descriptionField;
    private JButton addButton, editButton, deleteButton, reportButton, weeklyReportButton, monthlyReportButton;

    public ExpenseManagerGUI() {
        try {
            conn = DriverManager.getConnection(DATABASE_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to MySQL database.");
            createTableIfNotExist();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
            return;
        }

        initializeGUI();
    }

    private void createTableIfNotExist() {
        String sql = "CREATE TABLE IF NOT EXISTS expenses ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "amount DECIMAL(10,2) NOT NULL,"
                + "category VARCHAR(100) NOT NULL,"
                + "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "description TEXT)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    private void initializeGUI() {
        frame = new JFrame("Expense Manager");
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add/Edit Expense"));

        inputPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        inputPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        inputPanel.add(categoryField);

        inputPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        addButton = new JButton("Add Expense");
        editButton = new JButton("Edit Expense");
        deleteButton = new JButton("Delete Expense");
        reportButton = new JButton("Generate Summary");
        weeklyReportButton = new JButton("Weekly Report");
        monthlyReportButton = new JButton("Monthly Report");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(reportButton);
        buttonPanel.add(weeklyReportButton);
        buttonPanel.add(monthlyReportButton);

        inputPanel.add(buttonPanel);

        frame.add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Amount", "Category", "Date", "Description"}, 0);
        expenseTable = new JTable(tableModel);
        frame.add(new JScrollPane(expenseTable), BorderLayout.CENTER);

        addButton.addActionListener(e -> addExpense());
        editButton.addActionListener(e -> editExpense());
        deleteButton.addActionListener(e -> deleteExpense());
        reportButton.addActionListener(e -> generateReport());
        weeklyReportButton.addActionListener(e -> generateWeeklyReport());
        monthlyReportButton.addActionListener(e -> generateMonthlyReport());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        loadExpenses();
    }

    private void loadExpenses() {
        tableModel.setRowCount(0);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM expenses")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getTimestamp("date"),
                        rs.getString("description")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to load expenses: " + e.getMessage());
        }
    }

    private void addExpense() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryField.getText();
            String description = descriptionField.getText();

            String sql = "INSERT INTO expenses (amount, category, description) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, amount);
                pstmt.setString(2, category);
                pstmt.setString(3, description);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Expense added.");
                loadExpenses();
                amountField.setText("");
                categoryField.setText("");
                descriptionField.setText("");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Failed to add expense: " + e.getMessage());
        }
    }

    private void editExpense() {
        int row = expenseTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Select an expense to edit.");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryField.getText();
            String description = descriptionField.getText();

            String sql = "UPDATE expenses SET amount = ?, category = ?, description = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, amount);
                pstmt.setString(2, category);
                pstmt.setString(3, description);
                pstmt.setInt(4, id);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Expense updated.");
                loadExpenses();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Failed to update: " + e.getMessage());
        }
    }

    private void deleteExpense() {
        int row = expenseTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Select an expense to delete.");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        try {
            String sql = "DELETE FROM expenses WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Expense deleted.");
                loadExpenses();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Failed to delete: " + e.getMessage());
        }
    }

    private void generateReport() {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "SELECT category, SUM(amount) AS total FROM expenses GROUP BY category")) {
            StringBuilder report = new StringBuilder("Expense Summary by Category:\n");
            while (rs.next()) {
                report.append(rs.getString("category"))
                        .append(": ")
                        .append(rs.getDouble("total"))
                        .append("\n");
            }
            JOptionPane.showMessageDialog(frame, report.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to generate report: " + e.getMessage());
        }
    }

    private void generateWeeklyReport() {
        String sql = "SELECT category, SUM(amount) AS total FROM expenses WHERE date >= NOW() - INTERVAL 7 DAY GROUP BY category";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            StringBuilder report = new StringBuilder("Weekly Expense Summary by Category:\n");
            while (rs.next()) {
                report.append(rs.getString("category"))
                        .append(": ")
                        .append(rs.getDouble("total"))
                        .append("\n");
            }
            JOptionPane.showMessageDialog(frame, report.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to generate weekly report: " + e.getMessage());
        }
    }

    private void generateMonthlyReport() {
        String sql = "SELECT category, SUM(amount) AS total FROM expenses WHERE date >= NOW() - INTERVAL 1 MONTH GROUP BY category";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            StringBuilder report = new StringBuilder("Monthly Expense Summary by Category:\n");
            while (rs.next()) {
                report.append(rs.getString("category"))
                        .append(": ")
                        .append(rs.getDouble("total"))
                        .append("\n");
            }
            JOptionPane.showMessageDialog(frame, report.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to generate monthly report: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseManagerGUI::new);
    }
}
