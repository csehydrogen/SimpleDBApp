import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleDBApp {
  private static final String DB_ID= "DB-2013-11395";
  private static final String DB_PASSWORD= "DB-2013-11395";
  private static final String DB_URL= "jdbc:mariadb://147.46.15.238:3306/DB-2013-11395";

  public static void main(String[] args) {
    try {
      Connection conn = DriverManager.getConnection(DB_URL, DB_ID, DB_PASSWORD);
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
