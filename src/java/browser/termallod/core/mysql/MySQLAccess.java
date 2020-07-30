/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core.mysql;
import browser.termallod.core.sparql.SparqlGenerator;
import browser.termallod.core.termbase.TermDetail;
import browser.termallod.core.termbase.Termbase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author elahi SELECT * FROM `en_A_B_term` WHERE 1 DROP table `en_A_B_term`;
 * //command line location to /opt/lampp/bin/mysql -u root -p
 * //jdbc:mariadb://localhost:3306/revmgt?localSocket=/var/run/mysqld/mysqld.sock
 * //command line location to /opt/lampp/bin/mysql -u root -p
 */
public class MySQLAccess implements DataBaseConst {

    private Connection conn = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public MySQLAccess() throws Exception {
        this.connectDataBaseUnix();
    }
    
     private void connectDataBaseUnix() throws Exception {

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
        /*String sql = "SHOW DATABASES";

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
        }*/


    }


    
    /*public void connectDataBaseUnix() throws Exception {

        try {
            //Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://" + host + ":" + port + "/test?localSocket=/var/run/mysqld/mysqld.sock";
            String user = "root";
            String password = "";
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successfull!!");
        } catch (Exception e) {
            System.out.println("jdbc:mysql://" + host + ":" + port + "/test?localSocket=/var/run/mysqld/mysqld.sock");
            System.out.println("mysql database connection failed!!:" + e.getMessage());
        }
    }*/
    public void createTermTable(String tableName) throws Exception {

        try {
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE " + tableName + " "
                    + "(id VARCHAR(255) not NULL, "
                    + " term VARCHAR(4000), "
                    + " originalUrl VARCHAR(4000), "
                    + " alternativeUrl VARCHAR(255), "
                    + " reliabilityCode VARCHAR(255), "
                    + " administrativeStatus VARCHAR(255), "
                    + " subjectField VARCHAR(255), "
                    + " subjectDescription VARCHAR(255), "
                    + " reference VARCHAR(255), "
                    + " language VARCHAR(255), "
                    + " pair VARCHAR(255), "
                    + " PRIMARY KEY ( id ))";

            stmt.executeUpdate(sql);
            System.out.println("Created " + tableName + " table in given database...");

        } catch (Exception e) {
            System.out.println("create table is failed!!!!");
        }
    }

    public void deleteTable(String tableName) throws Exception {

        try {
            Statement stmt = conn.createStatement();
            String sql = "Drop table " + tableName;
            stmt.executeUpdate(sql);
            System.out.println("delete " + tableName + " table in given database...");

        } catch (Exception e) {
            System.out.println("delete data to " + tableName + " is failed!!!!");
        }

    }

    public void insertDataTermTable(String tableName, Termbase languageTerms, Integer limit) {
        String language = "";
        String pair = "";
       
        try {
            Integer index = 1;

            for (String url : languageTerms.getTerms().keySet()) {
                TermDetail termInfo = languageTerms.getTerms().get(url);
                index = index + 1;

                if (!(limit == -1)) {
                    if (index > limit) {
                        break;
                    }
                }

                String id = index.toString();
                String query = " insert into " + tableName
                        + " (id, term, originalUrl, alternativeUrl, reliabilityCode, "
                        + " administrativeStatus, subjectField, subjectDescription, reference,"
                        + " language, pair)"
                        + " values (?,?,?,?,?,?,?,?,?,?,?)";

                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setString(1, id);
                preparedStmt.setString(2, termInfo.getTermOrg());
                preparedStmt.setString(3, termInfo.getTermUrl());
                preparedStmt.setString(4, termInfo.getAlternativeUrl());
                preparedStmt.setString(5, termInfo.getReliabilityCode());
                preparedStmt.setString(6, termInfo.getAdministrativeStatus());
                preparedStmt.setString(7, termInfo.getSubjectId());
                preparedStmt.setString(8, termInfo.getSubjectDescription());
                preparedStmt.setString(9, termInfo.getReferenceID());
                preparedStmt.setString(10, language);
                preparedStmt.setString(11, pair);
                preparedStmt.execute();
            }
            System.out.println("insert to " + tableName + " table is successful!!...");

        } catch (Exception e) {
            System.out.println("insert data to " + tableName + " is failed!!!!");
        }
    }

    public Integer insertDataTermTable(String myTermTable, Termbase otherTermTable, String matchedTermTable) throws SQLException, Exception {
        Integer index = 0;

        try {

            Statement stmt = conn.createStatement();
            String query = " SELECT term, originalUrl"
                    + " FROM " + myTermTable;

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                //String language = rs.getString("language");
                String term = rs.getString("term");
                String orginalUrl = rs.getString("originalUrl");
                TermDetail myterm = new TermDetail(term, orginalUrl);
                //System.out.println(term + " " + orginalUrl + " ");
                if (otherTermTable.getTerms().containsKey(term)) {
                    TermDetail othertermInfo = otherTermTable.getTerms().get(term);
                    TermDetail otherTerm = new TermDetail(term, othertermInfo.getTermUrl());
                    //System.out.println(term + " " + orginalUrl + " " + othertermInfo.getTermUrl());
                    index++;
                    insertDataLinkTable(myterm, otherTerm, matchedTermTable, index);
                }
            }
            System.out.println("insert to " + matchedTermTable + " table is successful!!...");

        } catch (Exception e) {
            System.out.println("insert data to " + matchedTermTable + " is failed!!!!");
        }

        return index;
    }

    public void createLinkingTable(String tableName) throws Exception {

        try {
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE " + tableName + " "
                    + "(id VARCHAR(255) not NULL, "
                    + " termOrg VARCHAR(4000), "
                    + " term VARCHAR(4000), "
                    + " myTermUrl VARCHAR(255), "
                    + " myTermAlterUrl VARCHAR(255), "
                    + " otherTermUrl VARCHAR(255), "
                    + " otherTermAlterUrl VARCHAR(255), "
                    + " PRIMARY KEY ( id ))";

            stmt.executeUpdate(sql);
            System.out.println("Created " + tableName + " table in given database...");

        } catch (Exception e) {
            System.out.println("create table is failed!!...");
        }

    }

    public void insertDataLinkTable(TermDetail myTerminology, TermDetail linkTerminology, String linkTableName, Integer index) {

        try {

            String query = " insert into link (id, termOrg, term, myTermUrl, myTermAlterUrl, otherTermUrl, otherTermAlterUrl)"
                    + " values (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, index);
            preparedStmt.setString(2, myTerminology.getTermOrg());
            preparedStmt.setString(3, myTerminology.getTermDecrpt());
            preparedStmt.setString(4, myTerminology.getTermUrl());
            preparedStmt.setString(5, myTerminology.getAlternativeUrl());
            preparedStmt.setString(6, linkTerminology.getTermUrl());
            preparedStmt.setString(7, linkTerminology.getAlternativeUrl());
            preparedStmt.execute();

        } catch (Exception e) {
            System.out.println("insert data to " + linkTableName + " is failed!!!!");
            System.err.println(e.getMessage());
        }

    }
    
    public void readTermTable(String termTableName) throws SQLException, Exception {
        System.out.println("running read table command");
      
        try {

            Statement stmt = conn.createStatement();
            String query = " SELECT id, term, originalUrl"
                    + " FROM " + termTableName;

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                //String language = rs.getString("language");
                int id = rs.getInt("id");
                String term = rs.getString("term");
                String orginalUrl = rs.getString("originalUrl");

                System.out.println(id + " " + term + " " + orginalUrl);
            }
            System.out.println("reading " + termTableName + " table is successful!!...");

        } catch (Exception e) {
            System.out.println("reading data to " + termTableName + " is failed!!!!");
        }

    }
    
    public List<TermDetail> readMatchedTermTable(String termTableName) throws SQLException, Exception {
        System.out.println("running read table command");
        List<TermDetail> termInfos=new ArrayList<TermDetail>();
      
        try {

            Statement stmt = conn.createStatement();
            String query = " SELECT termOrg, term, myTermUrl"
                    + " FROM " + termTableName;

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                //String language = rs.getString("language");
                String termOrg= rs.getString("termOrg");
                String term = rs.getString("term");
                String myTermOrginalUrl = rs.getString("myTermUrl");
                String OtherTermOrginalUrl = rs.getString("otherTermUrl");                
                System.out.println(termOrg + " " + term + " " + myTermOrginalUrl+" "+OtherTermOrginalUrl);
                TermDetail terminfo=new TermDetail(termOrg,myTermOrginalUrl,"",OtherTermOrginalUrl);
                termInfos.add(terminfo);
                //SparqlGenerator.linkSparqlGenerator(termOrg,myTermOrginalUrl,"other",OtherTermOrginalUrl);
            }
            System.out.println("reading " + termTableName + " table is successful!!...");

        } catch (Exception e) {
            System.out.println("reading data to " + termTableName + " is failed!!!!");
        }
       return termInfos;
    }


    public void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {

        }
    }
}
