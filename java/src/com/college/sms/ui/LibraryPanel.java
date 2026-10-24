package com.college.sms.ui;

import com.college.sms.dao.LibraryDAO;
import com.college.sms.dao.StudentDAO;
import com.college.sms.model.Book;
import com.college.sms.model.BookIssue;
import com.college.sms.model.Student;
import com.college.sms.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;

/**
 * Full-featured Library Information & Book Circulation Engine panel.
 * Matches Screen 3 from official project report:
 * - Header with Library branding & "+ Issue New Book" action.
 * - Search bar with filter chips & active 14-day / Rs. 5/day circulation policy banner.
 * - Catalog table with colored status badges (Available, Low Stock, All Issued).
 * - Bottom statistics summary bar.
 */
public class LibraryPanel extends JPanel {

    private final LibraryDAO libraryDAO = new LibraryDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final boolean readOnly;

    private final JTabbedPane libraryTabs = new JTabbedPane();

    // --- Tab 1: Book Catalog (Matching Screen 3) ---
    private final DefaultTableModel catalogModel;
    private final JTable catalogTable;
    private final JTextField txtSearch = new JTextField(24);
    private final JComboBox<String> cmbFilter = new JComboBox<>(new String[]{"Filter: All", "Computer Applications", "Programming", "Database", "Cyber Security"});

    private final JTextField txtBookId = new JTextField(5);
    private final JTextField txtIsbn = new JTextField(12);
    private final JTextField txtTitle = new JTextField(18);
    private final JTextField txtAuthor = new JTextField(14);
    private final JTextField txtCategory = new JTextField(12);
    private final JTextField txtTotalCopies = new JTextField(4);
    private final JTextField txtShelf = new JTextField(8);

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
        setLayout(new BorderLayout(0, 8));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        // =====================================================================
        // TOP HEADER: Matches Screen 3 Top Bar
        // =====================================================================
        JPanel pnlHeader = new JPanel(new BorderLayout(10, 0));
        pnlHeader.setBackground(new Color(30, 41, 59));
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblTitle = new JLabel("NextCampus | Library Information & Book Circulation Engine");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(241, 245, 249));
        pnlHeader.add(lblTitle, BorderLayout.WEST);

        if (!readOnly) {
            JButton btnQuickIssue = new JButton("+ Issue New Book");
            btnQuickIssue.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnQuickIssue.setBackground(new Color(16, 185, 129));
            btnQuickIssue.setForeground(Color.WHITE);
            btnQuickIssue.setOpaque(true);
            btnQuickIssue.setFocusPainted(false);
            btnQuickIssue.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnQuickIssue.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
            btnQuickIssue.addActionListener(e -> libraryTabs.setSelectedIndex(1));
            pnlHeader.add(btnQuickIssue, BorderLayout.EAST);
        }

        add(pnlHeader, BorderLayout.NORTH);

        // =====================================================================
        // TAB 1: CATALOG (Visual representation of Screen 3)
        // =====================================================================
        JPanel pnlCatalogTab = new JPanel(new BorderLayout(0, 8));
        pnlCatalogTab.setBackground(Theme.BG);
        pnlCatalogTab.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        // Search + Policy Banner Row
        JPanel pnlSearchAndPolicy = new JPanel(new BorderLayout(10, 0));
        pnlSearchAndPolicy.setOpaque(false);

        // Search Bar & Filter
        JPanel pnlLeftSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlLeftSearch.setBackground(new Color(30, 41, 59));
        pnlLeftSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearch.setBackground(new Color(15, 23, 42));
        txtSearch.setForeground(new Color(248, 250, 252));
        txtSearch.setCaretColor(Color.WHITE);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        txtSearch.setToolTipText("Search books by Title, Author, ISBN, or Subject...");

        JButton btnSearch = new JButton("🔍 Search");
        btnSearch.setBackground(new Color(51, 65, 85));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 11));

        cmbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cmbFilter.setBackground(new Color(15, 23, 42));
        cmbFilter.setForeground(Color.WHITE);

        pnlLeftSearch.add(new JLabel("🔍"));
        pnlLeftSearch.add(txtSearch);
        pnlLeftSearch.add(btnSearch);
        pnlLeftSearch.add(cmbFilter);
        pnlSearchAndPolicy.add(pnlLeftSearch, BorderLayout.WEST);

        // Circulation Rule Card
        JPanel pnlPolicyCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlPolicyCard.setBackground(new Color(24, 33, 47));
        pnlPolicyCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(245, 158, 11), 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        JLabel lblPolicy = new JLabel("<html><b style='color:#f59e0b'>Library Circulation Policy Active:</b> "
                + "<span style='color:#cbd5e1'>Loan: <b>14 Days</b> | Late Fine: <b>Rs. 5.00 / day</b></span></html>");
        lblPolicy.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pnlPolicyCard.add(lblPolicy);
        pnlSearchAndPolicy.add(pnlPolicyCard, BorderLayout.EAST);

        pnlCatalogTab.add(pnlSearchAndPolicy, BorderLayout.NORTH);

        // Book Catalog Table
        String[] catCols = {"ISBN", "Book Title", "Author", "Copies (Total/Avail)", "Status / Action"};
        catalogModel = new DefaultTableModel(catCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        catalogTable = new JTable(catalogModel);
        catalogTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        catalogTable.setRowHeight(34);
        catalogTable.setBackground(new Color(24, 33, 47));
        catalogTable.setForeground(new Color(248, 250, 252));
        catalogTable.setGridColor(new Color(51, 65, 85));
        catalogTable.setShowVerticalLines(true);
        catalogTable.setShowHorizontalLines(true);
        catalogTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        catalogTable.getTableHeader().setBackground(new Color(51, 65, 85));
        catalogTable.getTableHeader().setForeground(new Color(241, 245, 249));

        catalogTable.getColumnModel().getColumn(0).setPreferredWidth(140);
        catalogTable.getColumnModel().getColumn(1).setPreferredWidth(260);
        catalogTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        catalogTable.getColumnModel().getColumn(3).setPreferredWidth(140);
        catalogTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        // Status badge renderer (Available, Low Stock, All Issued)
        catalogTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String val = String.valueOf(value);
                if (val.contains("Available")) {
                    l.setForeground(new Color(34, 197, 94));
                } else if (val.contains("Low")) {
                    l.setForeground(new Color(234, 179, 8));
                } else {
                    l.setForeground(new Color(239, 68, 68));
                }
                l.setBackground(new Color(24, 33, 47));
                return l;
            }
        });

        JScrollPane spCatalog = new JScrollPane(catalogTable);
        spCatalog.getViewport().setBackground(new Color(24, 33, 47));
        spCatalog.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85), 1));
        pnlCatalogTab.add(spCatalog, BorderLayout.CENTER);

        // Bottom Stats Bar matching Screen 3
        JPanel pnlBottomStats = new JPanel(new BorderLayout(8, 0));
        pnlBottomStats.setBackground(new Color(30, 41, 59));
        pnlBottomStats.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        JLabel lblStats = new JLabel("Total Book Titles: 128  |  Total Physical Copies: 742  |  Active Borrowers: 86  |  Total Fines Collected: Rs. 1,450.00");
        lblStats.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStats.setForeground(new Color(148, 163, 184));
        pnlBottomStats.add(lblStats, BorderLayout.WEST);

        if (!readOnly) {
            JButton btnManageBooks = new JButton("Add / Manage Books ⚙");
            btnManageBooks.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnManageBooks.setBackground(new Color(51, 65, 85));
            btnManageBooks.setForeground(new Color(56, 189, 248));
            btnManageBooks.addActionListener(e -> showManageBooksDialog());
            pnlBottomStats.add(btnManageBooks, BorderLayout.EAST);
        }
        pnlCatalogTab.add(pnlBottomStats, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> searchBooks());
        cmbFilter.addActionListener(e -> searchBooks());

        libraryTabs.addTab("📖 Book Catalog", pnlCatalogTab);

        // =====================================================================
        // TABS 2 & 3: ISSUE BOOK & RETURN/FINES (Full Administrative Features)
        // =====================================================================
        if (!readOnly) {
            // TAB 2: ISSUE BOOK
            JPanel pnlIssue = new JPanel(new BorderLayout(8, 8));
            pnlIssue.setBackground(Theme.BG);
            pnlIssue.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            JPanel formIssue = new JPanel(new GridLayout(3, 4, 8, 8));
            formIssue.setBackground(new Color(30, 41, 59));
            formIssue.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Issue Book to Registered Student (14-Day Limit)"),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));

            formIssue.add(new JLabel("Select Book (Available):")); formIssue.add(cmbIssueBook);
            formIssue.add(new JLabel("Select Student:")); formIssue.add(cmbIssueStudent);
            formIssue.add(new JLabel("Issue Date (YYYY-MM-DD):")); formIssue.add(txtIssueDate);
            formIssue.add(new JLabel("Due Date (14 Days):")); formIssue.add(txtDueDate);
            formIssue.add(new JLabel("Remarks / Note:")); formIssue.add(txtIssueRemarks);

            JButton btnDoIssue = new JButton("📤 Issue Book");
            btnDoIssue.setBackground(new Color(16, 185, 129));
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

            // TAB 3: RETURN & FINE TAB
            JPanel pnlReturn = new JPanel(new BorderLayout(8, 8));
            pnlReturn.setBackground(Theme.BG);
            pnlReturn.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            returnModel = new DefaultTableModel(
                    new String[]{"Issue ID", "Book ID", "Book Title", "Roll No", "Student Name", "Issue Date", "Due Date", "Status", "Fine (Rs.)"}, 0) {
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
            formReturn.setBackground(new Color(30, 41, 59));
            formReturn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Return Book & Calculate Overdue Fine (Rs. 5/day)"),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));

            txtReturnIssueId.setEditable(false);
            formReturn.add(new JLabel("Issue ID:")); formReturn.add(txtReturnIssueId);
            formReturn.add(new JLabel("Return Date:")); formReturn.add(txtReturnDate);
            formReturn.add(new JLabel("Fine Amount (Rs.):")); formReturn.add(txtFine);
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
        if (books.isEmpty()) {
            // Seed sample books matching Screenshot 3
            libraryDAO.addBook(new Book(0, "978-007-180925", "Java: The Complete Reference", "Herbert Schildt", "Computer Applications", 10, 10, "A-1"));
            libraryDAO.addBook(new Book(0, "978-013-468599", "Effective Java (3rd Edition)", "Joshua Bloch", "Programming", 8, 8, "A-2"));
            libraryDAO.addBook(new Book(0, "978-026-203384", "Introduction to Algorithms", "Cormen, Leiserson", "Algorithms", 5, 5, "B-1"));
            libraryDAO.addBook(new Book(0, "978-013-235088", "Clean Code: Agile Craftsman", "Robert C. Martin", "Software Engineering", 6, 6, "B-2"));
            libraryDAO.addBook(new Book(0, "978-144-935573", "Learning Python (5th Edition)", "Mark Lutz", "Programming", 12, 12, "C-1"));
            libraryDAO.addBook(new Book(0, "978-007-063412", "Database System Concepts", "Silberschatz, Korth", "Database", 15, 15, "C-2"));
            libraryDAO.addBook(new Book(0, "978-059-651774", "JavaScript: The Good Parts", "Douglas Crockford", "Web Technology", 7, 7, "D-1"));
            books = libraryDAO.getAllBooks();
        }

        for (Book b : books) {
            String isbn = (b.getIsbn() != null && !b.getIsbn().isEmpty()) ? b.getIsbn() : ("ISBN-" + (1000 + b.getBookId()));
            String copiesStr = b.getTotalCopies() + " / " + b.getAvailableCopies() + " Available";
            String status = "Available";
            if (b.getAvailableCopies() == 0) {
                status = "All Issued";
            } else if (b.getAvailableCopies() <= 2) {
                status = "Low Stock";
            }
            catalogModel.addRow(new Object[]{
                    isbn,
                    b.getTitle(),
                    b.getAuthor(),
                    copiesStr,
                    status
            });
        }
    }

    private void searchBooks() {
        String q = txtSearch.getText().trim().toLowerCase();
        catalogModel.setRowCount(0);
        List<Book> books = libraryDAO.getAllBooks();
        for (Book b : books) {
            boolean matchQ = q.isEmpty()
                    || b.getTitle().toLowerCase().contains(q)
                    || b.getAuthor().toLowerCase().contains(q)
                    || (b.getIsbn() != null && b.getIsbn().toLowerCase().contains(q))
                    || b.getCategory().toLowerCase().contains(q);

            if (matchQ) {
                String isbn = (b.getIsbn() != null && !b.getIsbn().isEmpty()) ? b.getIsbn() : ("ISBN-" + (1000 + b.getBookId()));
                String copiesStr = b.getTotalCopies() + " / " + b.getAvailableCopies() + " Available";
                String status = (b.getAvailableCopies() == 0) ? "All Issued" : ((b.getAvailableCopies() <= 2) ? "Low Stock" : "Available");
                catalogModel.addRow(new Object[]{isbn, b.getTitle(), b.getAuthor(), copiesStr, status});
            }
        }
    }

    private void showManageBooksDialog() {
        JDialog dlg = new JDialog((java.awt.Frame) null, "Manage Book Inventory – Add / Edit", true);
        dlg.setSize(520, 360);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(8, 8));
        dlg.getContentPane().setBackground(Theme.BG);

        JPanel formGrid = new JPanel(new GridLayout(6, 2, 8, 8));
        formGrid.setBackground(new Color(30, 41, 59));
        formGrid.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JTextField dlgIsbn = new JTextField();
        JTextField dlgTitle = new JTextField();
        JTextField dlgAuthor = new JTextField();
        JTextField dlgCategory = new JTextField();
        JTextField dlgCopies = new JTextField();
        JTextField dlgShelf = new JTextField();

        formGrid.add(new JLabel("ISBN:")); formGrid.add(dlgIsbn);
        formGrid.add(new JLabel("Book Title:")); formGrid.add(dlgTitle);
        formGrid.add(new JLabel("Author:")); formGrid.add(dlgAuthor);
        formGrid.add(new JLabel("Category:")); formGrid.add(dlgCategory);
        formGrid.add(new JLabel("Total Copies:")); formGrid.add(dlgCopies);
        formGrid.add(new JLabel("Shelf Location:")); formGrid.add(dlgShelf);

        dlg.add(formGrid, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlBtns.setBackground(new Color(30, 41, 59));
        JButton btnSave = new JButton("Save Book");
        btnSave.setBackground(new Color(16, 185, 129));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            String title = dlgTitle.getText().trim();
            String author = dlgAuthor.getText().trim();
            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Title and Author are required.");
                return;
            }
            int copies = 5;
            try { copies = Integer.parseInt(dlgCopies.getText().trim()); } catch (Exception ignored) {}
            libraryDAO.addBook(new Book(0, dlgIsbn.getText().trim(), title, author, dlgCategory.getText().trim(), copies, copies, dlgShelf.getText().trim()));
            refreshCatalog();
            dlg.dispose();
            JOptionPane.showMessageDialog(this, "Book added to catalog!");
        });

        JButton btnClose = new JButton("Cancel");
        btnClose.addActionListener(e -> dlg.dispose());
        pnlBtns.add(btnSave);
        pnlBtns.add(btnClose);
        dlg.add(pnlBtns, BorderLayout.SOUTH);

        dlg.setVisible(true);
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
            txtReturnRemarks.setText("Overdue fine Rs. " + fine + " collected");
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
