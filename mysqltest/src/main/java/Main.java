import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class Main {

    private static void dbtest() throws Exception {

        // check driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("JDBC driver found");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC driver not found");
            e.printStackTrace();
            return;
        }

        // check connection
        Connection conn = null;
        String dbname = "tal";
        String additional = "?socketFactory=org.newsclub.net.mysql.AFUNIXDatabaseSocketFactoryCJ&junixsocket.file=/var/run/mysqld/mysqld.sock";
        String connString = "jdbc:mysql://localhost:3306/" + dbname + additional;

        System.out.println("connecting to " + connString);
        try {
            conn = DriverManager.getConnection(connString, "root", "root");
            if (conn != null) {
                System.out.println("connection okay");
            } else {
                System.err.println("connection failed");
            }
        } catch (SQLException e) {
            System.err.println("connection failed w/ error");
            e.printStackTrace();
            return;
        }

        // check querying
        String sql = "SHOW DATABASES";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println("Result row: " + rs);
            }


        } catch (SQLException e) {
            System.err.println("query failed w/ error");
            e.printStackTrace();
            return;
        }


    }

    public static void main(String[] args) throws Exception {
        System.out.println("called");
        System.out.println("arguments: " + args.length);
        if (args.length > 0) {
            System.out.println("endpoint 1: " + args[0]);
        } else {
            System.err.println("no endpoint in arguments");
        }
        if (args.length > 1) {
            System.out.println("endpoint 2: " + args[1]);
        } else {
            System.err.println("no second endpoint in arguments");
        }

        System.out.println("executing mysql connection test");

        dbtest();

        System.out.println("all done. exiting");
    }

}
