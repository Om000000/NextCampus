package com.college.sms.dao;

import com.college.sms.db.DBConnection;
import com.college.sms.model.Book;
import com.college.sms.model.BookIssue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for Library Management System.
 */
public class LibraryDAO {

    public List<Book> getAllBooks() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT book_id, isbn, title, author, category, total_copies, available_copies, shelf_location "
                   + "FROM books ORDER BY title ASC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapBook(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT book_id, isbn, title, author, category, total_copies, available_copies, shelf_location "
                   + "FROM books WHERE lower(title) LIKE ? OR lower(author) LIKE ? OR lower(category) LIKE ? OR lower(isbn) LIKE ? "
                   + "ORDER BY title ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String p = "%" + (keyword != null ? keyword.trim().toLowerCase() : "") + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            ps.setString(4, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapBook(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Book getBookById(int bookId) {
        String sql = "SELECT book_id, isbn, title, author, category, total_copies, available_copies, shelf_location "
                   + "FROM books WHERE book_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBook(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (isbn, title, author, category, total_copies, available_copies, shelf_location) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, book.getIsbn() != null ? book.getIsbn().trim() : null);
            ps.setString(2, book.getTitle().trim());
            ps.setString(3, book.getAuthor().trim());
            ps.setString(4, book.getCategory().trim());
            ps.setInt(5, book.getTotalCopies());
            ps.setInt(6, book.getAvailableCopies());
            ps.setString(7, book.getShelfLocation() != null ? book.getShelfLocation().trim() : "Section A1");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET isbn=?, title=?, author=?, category=?, total_copies=?, available_copies=?, shelf_location=? "
                   + "WHERE book_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, book.getIsbn() != null ? book.getIsbn().trim() : null);
            ps.setString(2, book.getTitle().trim());
            ps.setString(3, book.getAuthor().trim());
            ps.setString(4, book.getCategory().trim());
            ps.setInt(5, book.getTotalCopies());
            ps.setInt(6, book.getAvailableCopies());
            ps.setString(7, book.getShelfLocation() != null ? book.getShelfLocation().trim() : "Section A1");
            ps.setInt(8, book.getBookId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE book_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized boolean issueBook(int bookId, int studentId, String rollNo, String issueDate, String dueDate, String remarks) {
        String checkBookSql = "SELECT available_copies FROM books WHERE book_id = ?";
        String updateBookSql = "UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ? AND available_copies > 0";
        String insertIssueSql = "INSERT INTO book_issues (book_id, student_id, roll_no, issue_date, due_date, status, remarks) "
                              + "VALUES (?, ?, ?, ?, ?, 'ISSUED', ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                // Check availability
                try (PreparedStatement psCheck = con.prepareStatement(checkBookSql)) {
                    psCheck.setInt(1, bookId);
                    try (ResultSet rs = psCheck.executeQuery()) {
                        if (!rs.next() || rs.getInt("available_copies") <= 0) {
                            con.rollback();
                            return false;
                        }
                    }
                }

                // Decrement copy
                try (PreparedStatement psUp = con.prepareStatement(updateBookSql)) {
                    psUp.setInt(1, bookId);
                    int updated = psUp.executeUpdate();
                    if (updated == 0) {
                        con.rollback();
                        return false;
                    }
                }

                // Insert issue record
                try (PreparedStatement psIns = con.prepareStatement(insertIssueSql)) {
                    psIns.setInt(1, bookId);
                    psIns.setInt(2, studentId);
                    psIns.setString(3, rollNo);
                    psIns.setString(4, issueDate);
                    psIns.setString(5, dueDate);
                    psIns.setString(6, remarks != null ? remarks : "Issued");
                    psIns.executeUpdate();
                }

                con.commit();
                return true;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized boolean returnBook(int issueId, String returnDate, double fineAmount, String remarks) {
        String getIssueSql = "SELECT book_id, status FROM book_issues WHERE issue_id = ?";
        String updateIssueSql = "UPDATE book_issues SET return_date = ?, fine_amount = ?, status = 'RETURNED', remarks = ? WHERE issue_id = ?";
        String updateBookSql = "UPDATE books SET available_copies = available_copies + 1 WHERE book_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int bookId = 0;
                try (PreparedStatement psGet = con.prepareStatement(getIssueSql)) {
                    psGet.setInt(1, issueId);
                    try (ResultSet rs = psGet.executeQuery()) {
                        if (!rs.next() || "RETURNED".equalsIgnoreCase(rs.getString("status"))) {
                            con.rollback();
                            return false;
                        }
                        bookId = rs.getInt("book_id");
                    }
                }

                try (PreparedStatement psUpIssue = con.prepareStatement(updateIssueSql)) {
                    psUpIssue.setString(1, returnDate);
                    psUpIssue.setDouble(2, fineAmount);
                    psUpIssue.setString(3, remarks != null ? remarks : "Returned");
                    psUpIssue.setInt(4, issueId);
                    psUpIssue.executeUpdate();
                }

                try (PreparedStatement psUpBook = con.prepareStatement(updateBookSql)) {
                    psUpBook.setInt(1, bookId);
                    psUpBook.executeUpdate();
                }

                con.commit();
                return true;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<BookIssue> getAllIssues() {
        return queryIssues("SELECT bi.*, b.title as book_title, s.full_name as student_name "
                         + "FROM book_issues bi "
                         + "JOIN books b ON bi.book_id = b.book_id "
                         + "JOIN students s ON bi.student_id = s.student_id "
                         + "ORDER BY bi.issue_id DESC");
    }

    public List<BookIssue> getActiveIssues() {
        return queryIssues("SELECT bi.*, b.title as book_title, s.full_name as student_name "
                         + "FROM book_issues bi "
                         + "JOIN books b ON bi.book_id = b.book_id "
                         + "JOIN students s ON bi.student_id = s.student_id "
                         + "WHERE bi.status != 'RETURNED' "
                         + "ORDER BY bi.due_date ASC");
    }

    public List<BookIssue> getStudentIssues(int studentId) {
        String sql = "SELECT bi.*, b.title as book_title, s.full_name as student_name "
                   + "FROM book_issues bi "
                   + "JOIN books b ON bi.book_id = b.book_id "
                   + "JOIN students s ON bi.student_id = s.student_id "
                   + "WHERE bi.student_id = ? "
                   + "ORDER BY bi.issue_id DESC";
        List<BookIssue> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapIssue(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<BookIssue> getStudentIssuesByRoll(String rollNo) {
        String sql = "SELECT bi.*, b.title as book_title, s.full_name as student_name "
                   + "FROM book_issues bi "
                   + "JOIN books b ON bi.book_id = b.book_id "
                   + "JOIN students s ON bi.student_id = s.student_id "
                   + "WHERE lower(trim(bi.roll_no)) = lower(trim(?)) "
                   + "ORDER BY bi.issue_id DESC";
        List<BookIssue> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapIssue(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static double computeLateFine(String dueDateStr, String returnDateStr, double finePerDay) {
        try {
            LocalDate due = LocalDate.parse(dueDateStr);
            LocalDate ret = (returnDateStr != null && !returnDateStr.isEmpty()) ? LocalDate.parse(returnDateStr) : LocalDate.now();
            if (ret.isAfter(due)) {
                long daysLate = ChronoUnit.DAYS.between(due, ret);
                return daysLate * finePerDay;
            }
        } catch (Exception ignored) {}
        return 0.0;
    }

    private List<BookIssue> queryIssues(String sql) {
        List<BookIssue> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapIssue(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Book mapBook(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("book_id"),
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("category"),
                rs.getInt("total_copies"),
                rs.getInt("available_copies"),
                rs.getString("shelf_location")
        );
    }

    private BookIssue mapIssue(ResultSet rs) throws SQLException {
        return new BookIssue(
                rs.getInt("issue_id"),
                rs.getInt("book_id"),
                rs.getString("book_title"),
                rs.getInt("student_id"),
                rs.getString("student_name"),
                rs.getString("roll_no"),
                rs.getString("issue_date"),
                rs.getString("due_date"),
                rs.getString("return_date"),
                rs.getDouble("fine_amount"),
                rs.getString("status"),
                rs.getString("remarks")
        );
    }
}
