package com.college.sms.ui;

import com.college.sms.dao.LibraryDAO;
import com.college.sms.dao.StudentDAO;
import com.college.sms.model.Book;
import com.college.sms.model.BookIssue;
import com.college.sms.model.Student;
import com.college.sms.model.User;
import com.college.sms.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;

/**
 * Student-facing Library panel:
 * Shows books borrowed by this student and catalog search.
 */
public class StudentLibraryPanel extends JPanel {

    private final LibraryDAO libraryDAO = new LibraryDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final Student student;

    private final DefaultTableModel myBooksModel;
    private final JTable myBooksTable;
    private final DefaultTableModel catalogModel;
    private final JTable catalogTable;
    private final JTextField txtSearch = new JTextField(15);

    public StudentLibraryPanel(User user) {
        Student s = null;
        if (user.getStudentId() != null) {
            s = studentDAO.getStudentById(user.getStudentId());
        }
        if (s == null) {
            s = studentDAO.getStudentByRollNo(user.getUsername());
        }
        if (s == null) {
            List<Student> all = studentDAO.getAllStudents();
            s = all.isEmpty() ? new Student(1, "BBA001", "Student", "BBA CA", 5, "", "") : all.get(0);
        }
        this.student = s;

        setBackground(Theme.BG);
        setLayout(new GridLayout(2, 1, 8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. TOP: MY BORROWED BOOKS & DUE DATES
        JPanel pnlMyBooks = new JPanel(new BorderLayout(6, 6));
        pnlMyBooks.setBackground(Theme.CARD_BG);
        pnlMyBooks.setBorder(BorderFactory.createTitledBorder("📖 My Borrowed Books & Due Dates (Roll No: " + student.getRollNo() + ")"));

        myBooksModel = new DefaultTableModel(
                new String[]{"Issue ID", "Book Title", "Issue Date", "Due Date", "Status", "Late Fine (₹)", "Remarks"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        myBooksTable = new JTable(myBooksModel);
        Theme.styleTable(myBooksTable);
        pnlMyBooks.add(new JScrollPane(myBooksTable), BorderLayout.CENTER);

        JPanel pnlMyBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlMyBottom.setBackground(Theme.CARD_BG);
        JButton btnRefreshMy = new JButton("🔄 Refresh My Books");
        btnRefreshMy.addActionListener(e -> refreshMyBooks());
        pnlMyBottom.add(btnRefreshMy);
        pnlMyBooks.add(pnlMyBottom, BorderLayout.SOUTH);

        add(pnlMyBooks);

        // 2. BOTTOM: SEARCH LIBRARY CATALOG
        JPanel pnlCatalog = new JPanel(new BorderLayout(6, 6));
        pnlCatalog.setBackground(Theme.CARD_BG);
        pnlCatalog.setBorder(BorderFactory.createTitledBorder("🔍 Search Library Book Catalog"));

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlSearch.setBackground(Theme.CARD_BG);
        pnlSearch.add(new JLabel("Search Title / Author / Subject:"));
        pnlSearch.add(txtSearch);
        JButton btnSearch = new JButton("🔍 Search");
        JButton btnShowAll = new JButton("📚 Show All Books");
        pnlSearch.add(btnSearch);
        pnlSearch.add(btnShowAll);
        pnlCatalog.add(pnlSearch, BorderLayout.NORTH);

        catalogModel = new DefaultTableModel(
                new String[]{"Book ID", "ISBN", "Title", "Author", "Category", "Available Copies", "Shelf Location"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        catalogTable = new JTable(catalogModel);
        Theme.styleTable(catalogTable);
        pnlCatalog.add(new JScrollPane(catalogTable), BorderLayout.CENTER);

        btnSearch.addActionListener(e -> searchCatalog());
        btnShowAll.addActionListener(e -> refreshCatalog());

        add(pnlCatalog);

        // Initial Data Load
        refreshMyBooks();
        refreshCatalog();
    }

    private void refreshMyBooks() {
        myBooksModel.setRowCount(0);
        List<BookIssue> list = libraryDAO.getStudentIssues(student.getStudentId());
        String today = LocalDate.now().toString();
        for (BookIssue bi : list) {
            double fine = bi.getFineAmount();
            if (!"RETURNED".equalsIgnoreCase(bi.getStatus())) {
                fine = LibraryDAO.computeLateFine(bi.getDueDate(), today, 5.0);
            }
            myBooksModel.addRow(new Object[]{
                    bi.getIssueId(),
                    bi.getBookTitle(),
                    bi.getIssueDate(),
                    bi.getDueDate(),
                    bi.getStatus(),
                    String.format("%.2f", fine),
                    bi.getRemarks() != null ? bi.getRemarks() : ""
            });
        }
    }

    private void refreshCatalog() {
        catalogModel.setRowCount(0);
        List<Book> books = libraryDAO.getAllBooks();
        for (Book b : books) {
            catalogModel.addRow(new Object[]{
                    b.getBookId(),
                    b.getIsbn() != null ? b.getIsbn() : "-",
                    b.getTitle(),
                    b.getAuthor(),
                    b.getCategory(),
                    b.getAvailableCopies() > 0 ? (b.getAvailableCopies() + " Available") : "Out of Stock",
                    b.getShelfLocation()
            });
        }
    }

    private void searchCatalog() {
        String q = txtSearch.getText().trim();
        if (q.isEmpty()) {
            refreshCatalog();
            return;
        }
        catalogModel.setRowCount(0);
        List<Book> books = libraryDAO.searchBooks(q);
        for (Book b : books) {
            catalogModel.addRow(new Object[]{
                    b.getBookId(),
                    b.getIsbn() != null ? b.getIsbn() : "-",
                    b.getTitle(),
                    b.getAuthor(),
                    b.getCategory(),
                    b.getAvailableCopies() > 0 ? (b.getAvailableCopies() + " Available") : "Out of Stock",
                    b.getShelfLocation()
            });
        }
    }
}
