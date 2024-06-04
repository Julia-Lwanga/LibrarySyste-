package vu.vulibrary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LibraryGUI extends JFrame {
    private JTextField txtBookID, txtTitle, txtAuthor, txtYear;
    private JTable table;
    private DefaultTableModel tableModel;
    private static final String DATABASE_URL = "jdbc:ucanaccess://C:/Users/julia lwanga/Documents/List of books.accdb";

    public LibraryGUI() {
        setTitle("VICTORIA LIBRARY");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("BookID:"));
        txtBookID = new JTextField();
        formPanel.add(txtBookID);

        formPanel.add(new JLabel("Title:"));
        txtTitle = new JTextField();
        formPanel.add(txtTitle);

        formPanel.add(new JLabel("Author:"));
        txtAuthor = new JTextField();
        formPanel.add(txtAuthor);

        formPanel.add(new JLabel("Year:"));
        txtYear = new JTextField();
        formPanel.add(txtYear);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 5, 5));

        JButton btnAdd = new JButton("Add Book");
        btnAdd.setBackground(Color.GREEN);
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });
        buttonsPanel.add(btnAdd);

        JButton btnDelete = new JButton("Delete Book");
        btnDelete.setBackground(Color.GREEN);
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBook();
            }
        });
        buttonsPanel.add(btnDelete);

        JButton btnRefresh = new JButton("Refresh List");
        btnRefresh.setBackground(Color.GREEN);
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadBooks();
            }
        });
        buttonsPanel.add(btnRefresh);

        // Center form panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(buttonsPanel, BorderLayout.SOUTH);
        getContentPane().add(centerPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"BookID", "Title", "Author", "Year"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Set table headers to bold
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(tableHeader.getFont().deriveFont(Font.BOLD));

        loadBooks();
    }

    private Connection getConnection() {
        try {
            return DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void addBook() {
        int bookId = Integer.parseInt(txtBookID.getText());
        String title = txtTitle.getText();
        String author = txtAuthor.getText();
        int year = Integer.parseInt(txtYear.getText());

        String query = "INSERT INTO Books (BookID, Title, Author, Year) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            if (conn != null) {
                pstmt.setInt(1, bookId);
                pstmt.setString(2, title);
                pstmt.setString(3, author);
                pstmt.setInt(4, year);
                pstmt.executeUpdate();
                loadBooks();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int bookId = (int) tableModel.getValueAt(selectedRow, 0);

            String query = "DELETE FROM Books WHERE BookID = ?";
            try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
                if (conn != null) {
                    pstmt.setInt(1, bookId);
                    pstmt.executeUpdate();
                    loadBooks();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadBooks() {
        String query = "SELECT * FROM Books";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            if (conn != null) {
                tableModel.setRowCount(0);
                while (rs.next()) {
                    int bookId = rs.getInt("BookID");
                    String title = rs.getString("Title");
                    String author = rs.getString("Author");
                    int year = rs.getInt("Year");
                    tableModel.addRow(new Object[]{bookId, title, author, year});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LibraryGUI().setVisible(true);
            }
        });
    }
}