package com.college.sms.ui;

import com.college.sms.dao.LibraryDAO;
import com.college.sms.dao.StudentDAO;
import com.college.sms.model.Book;
import com.college.sms.model.BookIssue;
import com.college.sms.model.Student;
import com.college.sms.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
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
 * Full-featured Library Management System panel.
 * Used by Librarian and HOD/Admin (full power), and Teacher (catalog view).
 */
public class LibraryPanel extends JPanel {

    private final LibraryDAO libraryDAO = new LibraryDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final boolean readOnly;

    // --- Tab 1: Book Catalog ---
    private final DefaultTableModel catalogModel;
    private final JTable catalogTable;
    private final JTextField txtBookId = new JTextField(5);
    private final JTextField txtIsbn = new JTextField(12);
    private final JTextField txtTitle = new JTextField(18);
    private final JTextField txtAuthor = new JTextField(14);
    private final JTextField txtCategory = new JTextField(12);
    private final JTextField txtTotalCopies = new JTextField(4);
    private final JTextField txtShelf = new JTextField(8);
    private final JTextField txtSearch = new JTextField(15);

    // --- Tab 2: Issue Book ---
    private final JComboBox<String> cmbIssueBook = new JComboBox<>();
    private final JComboBox<String> cmbIssueStudent = new JComboBox<>();
    private final JTextField txtIssueDate = new JTextField(10);
    private final JTextField txtDueDate = new JTextField(10);
    private final JTextField txtIssueRemarks = new JTextField(15);
    private final DefaultTableModel activeIssuesModel;
    private final JTable activeIssuesTable;

    // --- Tab 3: Return & Fine ---
    private final DefaultTableModel returnModel;
    private final JTable returnTable;
    private final JTextField txtReturnIssueId = new JTextField(5);
    private final JTextField txtReturnDate = new JTextField(10);
    private final JTextField txtFine = new JTextField(8);
    private final JTextField txtReturnRemarks = new JTextField(15);

    public LibraryPanel(boolean readOnly) {
        this.readOnly = readOnly;
        setLayout(new BorderLayout());

        JTabbedPane libraryTabs = new JTabbedPane();

        // 1. CATALOG TAB
        catalogModel = new DefaultTableModel(
                new String[]{"ID", "ISBN", "Book Title", "Author", "Category", "Total Copies", "Available", "Shelf"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        catalogTable = new JTable(catalogModel);
        Theme.styleTable(catalogTable);
        catalogTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && catalogTable.getSelectedRow() >= 0) {
                populateBookForm(catalogTable.getSelectedRow());
            }
        });

        JPanel pnlCatalog = new JPanel(new BorderLayout(8, 8));
        pnlCatalog.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Top search bar
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlSearch.add(new JLabel("Search Books:"));
        pnlSearch.add(txtSearch);
        JButton btnSearch = new JButton("🔍 Search");
        JButton btnRefreshCatalog = new JButton("🔄 Refresh");
        pnlSearch.add(btnSearch);
        pnlSearch.add(btnRefreshCatalog);
        pnlCatalog.add(pnlSearch, BorderLayout.NORTH);
        pnlCatalog.add(new JScrollPane(catalogTable), BorderLayout.CENTER);

        if (!readOnly) {
            // Book Form for Add/Edit
            JPanel pnlBookForm = new JPanel(new BorderLayout(6, 6));
            JPanel formGrid = new JPanel(new GridLayout(4, 4, 6, 6));
            formGrid.setBorder(BorderFactory.createTitledBorder("Book Details (Select row to edit)"));

            txtBookId.setEditable(false);
            formGrid.add(new JLabel("Book ID:")); formGrid.add(txtBookId);
            formGrid.add(new JLabel("ISBN:")); formGrid.add(txtIsbn);
            formGrid.add(new JLabel("Title:")); formGrid.add(txtTitle);
            formGrid.add(new JLabel("Author:")); formGrid.add(txtAuthor);
            formGrid.add(new JLabel("Category:")); formGrid.add(txtCategory);
            formGrid.add(new JLabel("Total Copies:")); formGrid.add(txtTotalCopies);
            formGrid.add(new JLabel("Shelf Location:")); formGrid.add(txtShelf);
            formGrid.add(new JLabel("")); formGrid.add(new JLabel(""));

            JPanel btnGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            JButton btnAddBook = new JButton("➕ Add Book");
            JButton btnUpdateBook = new JButton("✏ Update Book");
            JButton btnDeleteBook = new JButton("🗑 Delete Book");
            JButton btnClearBook = new JButton("🧹 Clear");

            btnGrid.add(btnAddBook);
            btnGrid.add(btnUpdateBook);
            btnGrid.add(btnDeleteBook);
            btnGrid.add(btnClearBook);

            pnlBookForm.add(formGrid, BorderLayout.CENTER);
            pnlBookForm.add(btnGrid, BorderLayout.SOUTH);
            pnlCatalog.add(pnlBookForm, BorderLayout.SOUTH);

            btnAddBook.addActionListener(e -> onAddBook());
            btnUpdateBook.addActionListener(e -> onUpdateBook());
            btnDeleteBook.addActionListener(e -> onDeleteBook());
            btnClearBook.addActionListener(e -> clearBookForm());
        }

        btnSearch.addActionListener(e -> searchBooks());
        btnRefreshCatalog.addActionListener(e -> refreshCatalog());

        libraryTabs.addTab("📖 Book Catalog", pnlCatalog);

        // Tabs 2 & 3 only for full access (Librarian, HOD)
        if (!readOnly) {
            // 2. ISSUE TAB
            JPanel pnlIssue = new JPanel(new BorderLayout(8, 8));
            pnlIssue.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            JPanel formIssue = new JPanel(new GridLayout(3, 4, 8, 8));
            formIssue.setBorder(BorderFactory.createTitledBorder("Issue Book to Student"));

            formIssue.add(new JLabel("Select Book (Available):")); formIssue.add(cmbIssueBook);
            formIssue.add(new JLabel("Select Student:")); formIssue.add(cmbIssueStudent);
            formIssue.add(new JLabel("Issue Date (YYYY-MM-DD):")); formIssue.add(txtIssueDate);
            formIssue.add(new JLabel("Due Date (14 Days):")); formIssue.add(txtDueDate);
            formIssue.add(new JLabel("Remarks / Note:")); formIssue.add(txtIssueRemarks);

            JButton btnDoIssue = new JButton("📤 Issue Book");
            btnDoIssue.setBackground(new Color(16, 124, 65));
            btnDoIssue.setForeground(Color.WHITE);
            btnDoIssue.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnDoIssue.setOpaque(true);
            btnDoIssue.addActionListener(e -> onIssueBook());
            formIssue.add(btnDoIssue);

            activeIssuesModel = new DefaultTableModel(
                    new String[]{"Issue ID", "Book ID", "Book Title", "Roll No", "Student Name", "Issue Date", "Due Date", "Status"}, 0) {
                @Override
                public boolean isCellEditable(int row, int col) { return false; }
            };
            activeIssuesTable = new JTable(activeIssuesModel);
            Theme.styleTable(activeIssuesTable);

            pnlIssue.add(formIssue, BorderLayout.NORTH);
            pnlIssue.add(new JScrollPane(activeIssuesTable), BorderLayout.CENTER);
            libraryTabs.addTab("📤 Issue Book", pnlIssue);

            // 3. RETURN & FINE TAB
            JPanel pnlReturn = new JPanel(new BorderLayout(8, 8));
            pnlReturn.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            returnModel = new DefaultTableModel(
                    new String[]{"Issue ID", "Book ID", "Book Title", "Roll No", "Student Name", "Issue Date", "Due Date", "Status", "Fine (₹)"}, 0) {
                @Override
                public boolean isCellEditable(int row, int col) { return false; }
            };
            returnTable = new JTable(returnModel);
            Theme.styleTable(returnTable);
            returnTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && returnTable.getSelectedRow() >= 0) {
                    populateReturnForm(returnTable.getSelectedRow());
                }
            });

            JPanel formReturn = new JPanel(new GridLayout(2, 5, 8, 8));
            formReturn.setBorder(BorderFactory.createTitledBorder("Return Book & Calculate Overdue Fine (₹5/day)"));

            txtReturnIssueId.setEditable(false);
            formReturn.add(new JLabel("Issue ID:")); formReturn.add(txtReturnIssueId);
            formReturn.add(new JLabel("Return Date:")); formReturn.add(txtReturnDate);
            formReturn.add(new JLabel("Fine Amount (₹):")); formReturn.add(txtFine);
            formReturn.add(new JLabel("Remarks:")); formReturn.add(txtReturnRemarks);

            JButton btnDoReturn = new JButton("📥 Process Return");
            btnDoReturn.setBackground(new Color(25, 90, 160));
            btnDoReturn.setForeground(Color.WHITE);
            btnDoReturn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnDoReturn.setOpaque(true);
            btnDoReturn.addActionListener(e -> onReturnBook());
            formReturn.add(btnDoReturn);

            pnlReturn.add(new JScrollPane(returnTable), BorderLayout.CENTER);
            pnlReturn.add(formReturn, BorderLayout.SOUTH);
            libraryTabs.addTab("📥 Return & Fines", pnlReturn);
        } else {
            activeIssuesModel = null;
            activeIssuesTable = null;
            returnModel = null;
            returnTable = null;
        }

        add(libraryTabs, BorderLayout.CENTER);

        // Initial Data Load
        refreshCatalog();
        if (!readOnly) {
            refreshIssueDropdowns();
            refreshActiveIssues();
            refreshReturnTable();
            txtIssueDate.setText(LocalDate.now().toString());
            txtDueDate.setText(LocalDate.now().plusDays(14).toString());
            txtReturnDate.setText(LocalDate.now().toString());
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
                    b.getTotalCopies(),
                    b.getAvailableCopies(),
                    b.getShelfLocation()
            });
        }
    }

    private void searchBooks() {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            refreshCatalog();
            return;
        }
        catalogModel.setRowCount(0);
        List<Book> books = libraryDAO.searchBooks(query);
        for (Book b : books) {
            catalogModel.addRow(new Object[]{
                    b.getBookId(),
                    b.getIsbn() != null ? b.getIsbn() : "-",
                    b.getTitle(),
                    b.getAuthor(),
                    b.getCategory(),
                    b.getTotalCopies(),
                    b.getAvailableCopies(),
                    b.getShelfLocation()
            });
        }
    }

    private void populateBookForm(int row) {
        txtBookId.setText(String.valueOf(catalogModel.getValueAt(row, 0)));
        txtIsbn.setText(String.valueOf(catalogModel.getValueAt(row, 1)));
        txtTitle.setText(String.valueOf(catalogModel.getValueAt(row, 2)));
        txtAuthor.setText(String.valueOf(catalogModel.getValueAt(row, 3)));
        txtCategory.setText(String.valueOf(catalogModel.getValueAt(row, 4)));
        txtTotalCopies.setText(String.valueOf(catalogModel.getValueAt(row, 5)));
        txtShelf.setText(String.valueOf(catalogModel.getValueAt(row, 7)));
    }

    private void clearBookForm() {
        txtBookId.setText("");
        txtIsbn.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        txtCategory.setText("");
        txtTotalCopies.setText("5");
        txtShelf.setText("Section A1");
        catalogTable.clearSelection();
    }

    private void onAddBook() {
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String cat = txtCategory.getText().trim();
        String isbn = txtIsbn.getText().trim();
        String copiesStr = txtTotalCopies.getText().trim();
        String shelf = txtShelf.getText().trim();

        if (title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Book Title and Author are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int copies = 1;
        try {
            copies = Integer.parseInt(copiesStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Total Copies must be a valid number.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Book b = new Book(0, isbn, title, author, cat.isEmpty() ? "General" : cat, copies, copies, shelf);
        if (libraryDAO.addBook(b)) {
            JOptionPane.showMessageDialog(this, "Book added successfully!");
            clearBookForm();
            refreshCatalog();
            refreshIssueDropdowns();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add book. ISBN may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdateBook() {
        if (txtBookId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a book from table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(txtBookId.getText().trim());
        Book existing = libraryDAO.getBookById(id);
        if (existing == null) return;

        existing.setIsbn(txtIsbn.getText().trim());
        existing.setTitle(txtTitle.getText().trim());
        existing.setAuthor(txtAuthor.getText().trim());
        existing.setCategory(txtCategory.getText().trim());
        try {
            int tot = Integer.parseInt(txtTotalCopies.getText().trim());
            int diff = tot - existing.getTotalCopies();
            existing.setTotalCopies(tot);
            existing.setAvailableCopies(Math.max(0, existing.getAvailableCopies() + diff));
        } catch (Exception ignored) {}
        existing.setShelfLocation(txtShelf.getText().trim());

        if (libraryDAO.updateBook(existing)) {
            JOptionPane.showMessageDialog(this, "Book updated successfully!");
            clearBookForm();
            refreshCatalog();
            refreshIssueDropdowns();
        } else {
            JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDeleteBook() {
        if (txtBookId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a book from table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(txtBookId.getText().trim());
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (libraryDAO.deleteBook(id)) {
                JOptionPane.showMessageDialog(this, "Book deleted.");
                clearBookForm();
                refreshCatalog();
                refreshIssueDropdowns();
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete book.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshIssueDropdowns() {
        cmbIssueBook.removeAllItems();
        List<Book> books = libraryDAO.getAllBooks();
        for (Book b : books) {
            if (b.getAvailableCopies() > 0) {
                cmbIssueBook.addItem(b.getBookId() + ": " + b.getTitle() + " (Avail: " + b.getAvailableCopies() + ")");
            }
        }

        cmbIssueStudent.removeAllItems();
        List<Student> students = studentDAO.getAllStudents();
        for (Student s : students) {
            cmbIssueStudent.addItem(s.getStudentId() + ": " + s.getRollNo() + " - " + s.getFullName());
        }
    }

    private void refreshActiveIssues() {
        if (activeIssuesModel == null) return;
        activeIssuesModel.setRowCount(0);
        List<BookIssue> issues = libraryDAO.getActiveIssues();
        for (BookIssue bi : issues) {
            activeIssuesModel.addRow(new Object[]{
                    bi.getIssueId(),
                    bi.getBookId(),
                    bi.getBookTitle(),
                    bi.getRollNo(),
                    bi.getStudentName(),
                    bi.getIssueDate(),
                    bi.getDueDate(),
                    bi.getStatus()
            });
        }
    }

    private void refreshReturnTable() {
        if (returnModel == null) return;
        returnModel.setRowCount(0);
        List<BookIssue> issues = libraryDAO.getActiveIssues();
        String today = LocalDate.now().toString();
        for (BookIssue bi : issues) {
            double fine = LibraryDAO.computeLateFine(bi.getDueDate(), today, 5.0);
            returnModel.addRow(new Object[]{
                    bi.getIssueId(),
                    bi.getBookId(),
                    bi.getBookTitle(),
                    bi.getRollNo(),
                    bi.getStudentName(),
                    bi.getIssueDate(),
                    bi.getDueDate(),
                    bi.getStatus(),
                    String.format("%.2f", fine)
            });
        }
    }

    private void onIssueBook() {
        if (cmbIssueBook.getSelectedItem() == null || cmbIssueStudent.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select both a Book and a Student.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookItem = (String) cmbIssueBook.getSelectedItem();
        int bookId = Integer.parseInt(bookItem.split(":")[0].trim());

        String studentItem = (String) cmbIssueStudent.getSelectedItem();
        int studentId = Integer.parseInt(studentItem.split(":")[0].trim());
        String rollNo = studentItem.split(":")[1].split("-")[0].trim();

        String issueDate = txtIssueDate.getText().trim();
        String dueDate = txtDueDate.getText().trim();
        String remarks = txtIssueRemarks.getText().trim();

        boolean ok = libraryDAO.issueBook(bookId, studentId, rollNo, issueDate, dueDate, remarks);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Book successfully issued to " + rollNo + "!\nDue Date: " + dueDate);
            refreshCatalog();
            refreshIssueDropdowns();
            refreshActiveIssues();
            refreshReturnTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to issue book. No copies available.", "Issue Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateReturnForm(int row) {
        txtReturnIssueId.setText(String.valueOf(returnModel.getValueAt(row, 0)));
        txtReturnDate.setText(LocalDate.now().toString());
        txtFine.setText(String.valueOf(returnModel.getValueAt(row, 8)));
        txtReturnRemarks.setText("Returned on time");
        double fine = 0.0;
        try {
            fine = Double.parseDouble(String.valueOf(returnModel.getValueAt(row, 8)));
        } catch (Exception ignored) {}
        if (fine > 0) {
            txtReturnRemarks.setText("Overdue fine ₹" + fine + " collected");
        }
    }

    private void onReturnBook() {
        if (txtReturnIssueId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an issued book record from the table above.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int issueId = Integer.parseInt(txtReturnIssueId.getText().trim());
        String returnDate = txtReturnDate.getText().trim();
        double fine = 0.0;
        try {
            fine = Double.parseDouble(txtFine.getText().trim());
        } catch (Exception ignored) {}
        String remarks = txtReturnRemarks.getText().trim();

        boolean ok = libraryDAO.returnBook(issueId, returnDate, fine, remarks);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Book successfully returned!\nCopy count updated in catalog.");
            txtReturnIssueId.setText("");
            txtFine.setText("0.00");
            refreshCatalog();
            refreshIssueDropdowns();
            refreshActiveIssues();
            refreshReturnTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to process return.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
