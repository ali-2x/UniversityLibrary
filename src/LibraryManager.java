import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import javax.swing.*;
import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class LibraryManager {
    Scanner in = null;
    Connection conn = null;
    // Database Host
    final String databaseHost = "orasrv1.comp.hkbu.edu.hk";
    // Database Port
    final int databasePort = 1521;
    // Database name
    final String database = "pdborcl.orasrv1.comp.hkbu.edu.hk";
    final String proxyHost = "faith.comp.hkbu.edu.hk";
    final int proxyPort = 22;
    final String forwardHost = "localhost";
    int forwardPort;
    Session proxySession = null;
    boolean noException = true;

    // JDBC connecting host
    String jdbcHost;
    // JDBC connecting port
    int jdbcPort;

    String[] options = { // if you want to add an option, append to the end of
            // this array
            "search a book (by ISBN)", "borrow a book (by student-no, call_no)", "return a book (by student-no, call_no)",
            "reserve a book (by student-no, call_no)", "renew a book (by student-no, call_no)",
            "exit"};

    boolean getYESorNO(String message) {
        JPanel panel = new JPanel();
        panel.add(new JLabel(message));
        JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        JDialog dialog = pane.createDialog(null, "Question");
        dialog.setVisible(true);
        boolean result = JOptionPane.YES_OPTION == (int) pane.getValue();
        dialog.dispose();
        return result;
    }

    String[] getUsernamePassword(String title) {
        JPanel panel = new JPanel();
        final TextField usernameField = new TextField();
        final JPasswordField passwordField = new JPasswordField();
        panel.setLayout(new GridLayout(2, 2));
        panel.add(new JLabel("Username"));
        panel.add(usernameField);
        panel.add(new JLabel("Password"));
        panel.add(passwordField);
        JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {
            private static final long serialVersionUID = 1L;

            @Override
            public void selectInitialValue() {
                usernameField.requestFocusInWindow();
            }
        };
        JDialog dialog = pane.createDialog(null, title);
        dialog.setVisible(true);
        dialog.dispose();
        return new String[]{usernameField.getText(), new String(passwordField.getPassword())};
    }

    /**
     * Login the proxy.
     *
     * @return boolean
     */
    public boolean loginProxy() {
        if (getYESorNO("Using ssh tunnel or not?")) { // if using ssh tunnel
            String[] namePwd = getUsernamePassword("Login cs lab computer");
            String sshUser = namePwd[0];
            String sshPwd = namePwd[1];
            try {
                proxySession = new JSch().getSession(sshUser, proxyHost, proxyPort);
                proxySession.setPassword(sshPwd);
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                proxySession.setConfig(config);
                proxySession.connect();
                proxySession.setPortForwardingL(forwardHost, 0, databaseHost, databasePort);
                forwardPort = Integer.parseInt(proxySession.getPortForwardingL()[0].split(":")[0]);
            } catch (JSchException e) {
                e.printStackTrace();
                return false;
            }
            jdbcHost = forwardHost;
            jdbcPort = forwardPort;
        } else {
            jdbcHost = databaseHost;
            jdbcPort = databasePort;
        }
        return true;
    }

    /**
     * Login the oracle system. Change this function under instruction.
     *
     * @return boolean
     */
    public boolean loginDB() {
        String username = "e1234567";//Replace e1234567 to your username
        String password = "e1234567";//Replace e1234567 to your password

        /* Do not change the code below */
        if (username.equalsIgnoreCase("e1234567") || password.equalsIgnoreCase("e1234567")) {
            String[] namePwd = getUsernamePassword("Login sqlplus");
            username = namePwd[0];
            password = namePwd[1];
        }
        String URL = "jdbc:oracle:thin:@" + jdbcHost + ":" + jdbcPort + "/" + database;

        try {
            System.out.println("Logging " + URL + " ...");
            conn = DriverManager.getConnection(URL, username, password);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Show the options. If you want to add one more option, put into the
     * options array above.
     */
    public void showOptions() {
        System.out.println("Please choose following option:");
        for (int i = 0; i < options.length; ++i) {
            System.out.println("(" + (i + 1) + ") " + options[i]);
        }
    }

    /**
     * Run the manager
     */
    public void run() {
        while (noException) {
            showOptions();
            String line = in.nextLine();
            if (line.equalsIgnoreCase("exit"))
                return;
            int choice = -1;
            try {
                choice = Integer.parseInt(line);
            } catch (Exception e) {
                System.out.println("This option is not available");
                continue;
            }
            if (!(choice >= 1 && choice <= options.length)) {
                System.out.println("This option is not available");
                continue;
            }
            if (options[choice - 1].equals("search a book (by ISBN)")) {
                bookSearchByISBN();
            } else if (options[choice - 1].equals("borrow a book (by student-no, call_no)")) {
                bookBorrow();
            } else if (options[choice - 1].equals("return a book (by student-no, call_no)")) {
                bookReturn();
            } else if (options[choice - 1].equals("reserve a book (by student-no, call_no)")) {
                bookReserve();
            } else if (options[choice - 1].equals("renew a book (by student-no, call_no)")) {
                bookRenew();
            } else if (options[choice - 1].equals("exit")) {
                break;
            }
        }
    }

    private void bookSearchByISBN() {

        System.out.println("Please input the ISBN of the book you want to search:");
        String line = in.nextLine();
        line = line.trim();
        if (line.equalsIgnoreCase("exit"))
            return;
        bookSearch(line);
    }

    private void bookSearch(String ISBN) {
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT Title, Author, Amount, Location FROM Book WHERE ISBN = '" + ISBN + "' AND Amount > 0";
            ResultSet rs = stm.executeQuery(sql);

            if (!rs.next()) {
                System.out.println("The book is not available");
                return;
            }

            String[] heads = {"Title", "Author", "Amount", "Location"};
            for (int i = 0; i < 4; ++i) {                // the book will have 4 attributes
                try {
                    System.out.println(heads[i] + " : " + rs.getString(i + 1));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            noException = false;
        }
    }

    private void printBookInfo(String call) {
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM BOOK WHERE CALL_NO = '" + call + "'";
            ResultSet rs = stm.executeQuery(sql);
            if (!rs.next()) {
                rs.close();
                stm.close();
                return;
            }
            String[] heads = {"Call_no", "ISBN", "Title", "Author", "Amount", "Location"};
            for (int i = 0; i < 6; ++i) { // book table 6 attributes
                try {
                    System.out.println(heads[i] + " : " + rs.getString(i + 1)); // attribute
                    // id
                    // starts
                    // with
                    // 1

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            rs.close();
            stm.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
            noException = false;
        }
    }

    private void printBookRenewInfo() {
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM RENEW";
            ResultSet rs = stm.executeQuery(sql);
            String[] heads = {"Student-Number", "Renewed Book"};
            while (rs.next()) {
                for (int i = 0; i < 2; ++i) {
                    try {
                        System.out.println(heads[i] + " : " + rs.getString(i + 1)); // attribute
                        // id
                        // starts
                        // with
                        // 1
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("---------------------------------");
            }
            rs.close();
            stm.close();

        } catch (SQLException e1) {
            e1.printStackTrace();
            noException = false;
        }
    }

    private void printBookReserveInfo() {
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM RESERVE";
            ResultSet rs = stm.executeQuery(sql);
            String[] heads = {"Student-Number", "Reserved_book", "Request date"};
            while (rs.next()) {
                for (int i = 0; i < 3; ++i) {
                    try {
                        System.out.println(heads[i] + " : " + rs.getString(i + 1));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("---------------------------------");
            }
            rs.close();
            stm.close();

        } catch (SQLException e1) {
            e1.printStackTrace();
            noException = false;
        }
    }

    private void bookReserve() {
        System.out.println("Please input the student_no, book that you want to make the reservation request for:");
        String line = in.nextLine();

        if (line.equalsIgnoreCase("exit"))
            return;
        String[] values = line.split(",");

        if (values.length < 2) {
            System.out.println("The value number is expected to be 2");
            return;
        }
        for (int i = 0; i < values.length; ++i)
            values[i] = values[i].trim();

        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT AMOUNT FROM BOOK WHERE Call_no = '" + values[1] + "'";
            ResultSet rs = stm.executeQuery(sql);
            rs.next();
            if (rs.getInt(1) > 0) {
                System.out.println("The book is now available. No reservation is required");
                rs.close();
                stm.close();
                return;
            }
            rs.close();
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
            noException = false;
        }

        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM Borrow WHERE Borrower = '" + values[0] + "'" + "AND Book = '" + values[1] + "'";
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next()) {
                System.out.println("Reserving a borrowed book is not allowed");
                rs.close();
                stm.close();
                return;
            }
            rs.close();
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
            noException = false;
        }

        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM Reserve WHERE Student_number = '" + values[0] + "'";
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next()) {
                System.out.println("Multiple reservations are not allowed");
                rs.close();
                stm.close();
                return;
            }
            rs.close();
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
            noException = false;
        }

        try {
            Statement stm = conn.createStatement();
            String sql = "INSERT INTO RESERVE VALUES(" + "'" + values[0] + "', " + "'" + values[1] + "'," + "to_date(SYSDATE , 'dd/mon/yyyy'))";
            stm.executeUpdate(sql);
            stm.close();
            System.out.println("The reservation succeeded.");
            printBookReserveInfo();
        } catch (SQLException e) {
            e.printStackTrace();
            noException = false;
        }
    }

    private void bookRenew() {
        System.out.println("Please input the student_no, book you would like to renew:");
        String line = in.nextLine();

        if (line.equalsIgnoreCase("exit"))
            return;
        String[] values = line.split(",");

        if (values.length < 2) {
            System.out.println("The value number is expected to be 2");
            return;
        }
        for (int i = 0; i < values.length; ++i)
            values[i] = values[i].trim();
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM BORROW WHERE BORROWER = " + "'" + values[0] + "' AND BOOK = " + "'" + values[1] +
                    "' AND Due_date - Borrow_date = 42";
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next()) {
                System.out.println("This book cannot be renewed again.");
                rs.close();
                stm.close();
                return;
            }
            rs.close();
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("failed to renew book " + line);
            noException = false;
        }
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM BORROW WHERE BORROWER = " + "'" + values[0] + "' AND Due_date < SYSDATE";
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next()) {
                System.out.println("You have at least one overdue book, you cannot make a new borrowing");
                rs.close();
                stm.close();
                return;
            }
            rs.close();
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("failed to renew book " + line);
            noException = false;
        }

        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM RESERVE WHERE Reserved_book = '" + values[1] + "'";
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next()) {
                System.out.println("The book is reserved by another student.");
                rs.close();
                stm.close();
                return;
            }
            rs.close();
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("failed to renew book " + line);
            noException = false;
        }
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM BORROW WHERE SYSDATE - Borrow_date < 14"
                    + " AND Borrower = " + "'" + values[0] + "' AND Book = " + "'" + values[1] + "'";
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next()) {
                System.out.println("The renewal is not yet available.");
                rs.close();
                stm.close();
                return;
            }
            rs.close();
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("failed to renew book " + line);
            noException = false;
        }

        try {
            Statement stm = conn.createStatement();
            String sql = "INSERT INTO Renew VALUES(" + "'" + values[0] + "', " +
                    "'" + values[1] + "')";
            stm.executeUpdate(sql);
            stm.close();
            System.out.println("The borrowed book is renewed successfully ");
            printBookRenewInfo();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("failed to renew book " + line);
            noException = false;
        }
    }

    private void bookReturn() {
        System.out.println("Please input the student_no, book you would like to return:");
        String line = in.nextLine();

        if (line.equalsIgnoreCase("exit"))
            return;
        String[] values = line.split(",");

        if (values.length < 2) {
            System.out.println("The value number is expected to be 2");
            return;
        }
        for (int i = 0; i < values.length; ++i)
            values[i] = values[i].trim();

        try {
            Statement stm = conn.createStatement();
            String sql = "DELETE FROM Borrow WHERE BORROWER='" + values[0] + "' AND BOOK='" + values[1] + "'";
            stm.executeUpdate(sql);
            stm.close();
            System.out.println("succeed to return book ");
            printBookInfo(values[1]);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("failed to return book " + line);
            noException = false;
        }
    }

    private void bookBorrow() {
        System.out.println("Please input the student_no, book you would like to borrow:");
        String line = in.nextLine();

        if (line.equalsIgnoreCase("exit"))
            return;
        String[] values = line.split(",");

        if (values.length < 2) {
            System.out.println("The value number is expected to be 2");
            return;
        }
        for (int i = 0; i < values.length; ++i)
            values[i] = values[i].trim();
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT AMOUNT FROM BOOK WHERE Call_no = " + "'" + values[1] + "'";
            ResultSet rs = stm.executeQuery(sql);
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("The book is not available at present.");
                rs.close();
                stm.close();
                return;
            }
            rs.close();
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("failed to borrow book " + line);
            noException = false;
        }
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM BORROW WHERE Borrower = " + "'" + values[0] + "'";
            ResultSet rs = stm.executeQuery(sql);
            int noOfBorrowedBooks = 0;
            while (rs.next()) {
                noOfBorrowedBooks++;
            }
            if (noOfBorrowedBooks > 4) {
                System.out.println("You have already borrowed 5 books.");
                rs.close();
                stm.close();
                return;
            }
            rs.close();
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("failed to borrow book " + line);
            noException = false;
        }
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM BORROW WHERE BORROWER = " + "'" + values[0] + "' AND Due_date < SYSDATE";
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next()) {
                System.out.println("You have at least one overdue book, you cannot make a new borrowing.");
                rs.close();
                stm.close();
                return;
            }
            rs.close();
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("failed to borrow book " + line);
            noException = false;
        }
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM Reserve WHERE Reserved_book = " + "'" + values[1] + "'";
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next()) {
                rs.close();
                stm.close();
                Statement stm1 = conn.createStatement();
                sql = "(SELECT * FROM Reserve WHERE Reserved_book = " + "'" + values[1] + "'"
                        + ") MINUS (SELECT * FROM Reserve WHERE Reserved_book = " + "'" + values[1] + "'"
                        + " AND Student_number <> '" + values[0] + "')";
                ResultSet rs1 = stm1.executeQuery(sql);
                if (!rs1.next()) {
                    System.out.println("The book is reserved by another student.");
                    rs1.close();
                    stm1.close();
                    return;
                }
                rs1.close();
                stm1.close();
            } else {
                rs.close();
                stm.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("failed to borrow book " + line);
            noException = false;
        }
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM RESERVE R1, RESERVE R2 WHERE R1.reserved_book = " + "'" + values[1] + "' "
                    + "AND R1.reserved_book = R2.reserved_book AND R1.student_number = '" + values[0] + "' "
                    + "AND R1.student_number <> R2.student_number AND R1.request_date > R2.request_date";
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next()) {
                System.out.println("You are not the first one to reserve this book.");
                rs.close();
                stm.close();
                return;
            }
            rs.close();
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("failed to borrow book " + line);
            noException = false;
        }

        try {
            Statement stm = conn.createStatement();
            String sql = "INSERT INTO BORROW VALUES(" + "'" + values[0] + "', " + "'" + values[1] + "', " + "to_date(SYSDATE, 'dd/mon/yyyy'), " + "to_date(SYSDATE+28, 'dd/mon/yyyy'))";
            stm.executeUpdate(sql);
            stm.close();
            System.out.println("The borrowing succeeded ");
            printBookInfo(values[1]);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("failed to borrow book " + line);
            noException = false;
        }
    }

    public void close() {
        System.out.println("Thanks for using this manager! Bye...");
        try {
            if (conn != null)
                conn.close();
            if (proxySession != null) {
                proxySession.disconnect();
            }
            in.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LibraryManager() {
        System.out.println("Welcome to use this manager!");
        in = new Scanner(System.in);
    }

    public static void main(String[] args) {
        LibraryManager manager = new LibraryManager();
        if (!manager.loginProxy()) {
            System.out.println("Login proxy failed, please re-examine your username and password!");
            return;
        }
        if (!manager.loginDB()) {
            System.out.println("Login database failed, please re-examine your username and password!");
            return;
        }
        System.out.println("Login succeed!");
        try {
            manager.run();
        } finally {
            manager.close();
        }
    }
}
