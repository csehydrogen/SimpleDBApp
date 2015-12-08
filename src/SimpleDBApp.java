import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class SimpleDBApp {
  private static final String DB_ID= "DB-2013-11395";
  private static final String DB_PASSWORD= "DB-2013-11395";
  private static final String DB_URL= "jdbc:mariadb://147.46.15.238:3306/DB-2013-11395";

  private static final String LINE_MENU = "============================================================";
  private static final String LINE_UNIV = "________________________________________________________";
  private static final String FMT_UNIV = "%-3.3s %-22.22s %-8.8s %-5.5s %-6.6s %-7.7s";

  private static Connection conn;
  private static Scanner in;
  private static PreparedStatement stmt10;
  private static PreparedStatement stmt30, stmt31;

  public static void main(String[] args) {
    try {
      init();
      while (true) {
        switch (getAction()) {
          case 1: printUniv(); break;
          case 2:
          case 3: insertUniv(); break;
          case 4:
          case 5:
          case 6:
          case 7:
          case 8:
          case 9:
          case 10:
          case 11:
          case 12: exit(); break;
          default:
            System.out.println("Invalid action.");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void init() throws SQLException{
    conn = DriverManager.getConnection(DB_URL, DB_ID, DB_PASSWORD);
    in = new Scanner(System.in);
    stmt10 = conn.prepareStatement("SELECT * FROM university");
    stmt30 = conn.prepareStatement("SELECT max(uid) + 1 FROM university");
    stmt31 = conn.prepareStatement("INSERT INTO university VALUES (?, ?, ?, ?, ?, 0)");
  }

  private static int getAction() {
    System.out.println(LINE_MENU);
    System.out.println("1. print all universities");
    System.out.println("2. print all students");
    System.out.println("3. insert a new university");
    System.out.println("4. remove a university");
    System.out.println("5. insert a new student");
    System.out.println("6. remove a student");
    System.out.println("7. make an application");
    System.out.println("8. print all students who applied for a university");
    System.out.println("9. print all universities a student applied for");
    System.out.println("10. print expected successful applicants of a university");
    System.out.println("11. print universities expected to accept a student");
    System.out.println("12. exit");
    System.out.println(LINE_MENU);
    System.out.print("Select your action: ");
    return Integer.parseInt(in.nextLine());
  }

  private static void printUniv() throws SQLException {
    System.out.println(LINE_UNIV);
    System.out.println(String.format(FMT_UNIV, "id", "name", "capacity", "group", "weight", "applied"));
    System.out.println(LINE_UNIV);
    ResultSet rs = stmt10.executeQuery();
    while (rs.next())
      System.out.println(String.format(FMT_UNIV, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)));
    System.out.println(LINE_UNIV);
  }

  private static void insertUniv() throws SQLException{
    System.out.print("University name: ");
    String name = in.nextLine();
    if (name.length() > 128) {
      name = name.substring(0, 128);
    }

    System.out.print("University capacity: ");
    int capacity = Integer.parseInt(in.nextLine());
    if (capacity < 1) {
      System.out.println("Capacity should be over 0.");
      return;
    }

    System.out.print("University group: ");
    String ugroup = in.nextLine();
    if (!ugroup.equals("A") && !ugroup.equals("B") && !ugroup.equals("C")) {
      System.out.println("Group should be 'A', 'B', or 'C'.");
      return;
    }

    System.out.print("Weight of high school records: ");
    float weight = Float.parseFloat(in.nextLine());
    if (weight < 0) {
      System.out.println("Weight of high school records cannot be negative.");
      return;
    }

    ResultSet rs = stmt30.executeQuery();
    rs.next();
    int uid = rs.getInt(1);
    if (uid == 0) uid = 1;

    stmt31.setInt(1, uid);
    stmt31.setString(2, name);
    stmt31.setInt(3, capacity);
    stmt31.setString(4, ugroup);
    stmt31.setFloat(5, weight);
    stmt31.executeUpdate();
    System.out.println("A university is successfully inserted.");
  }

  private static void exit() throws SQLException {
    conn.close();
    System.out.println("Bye!");
    System.exit(0);
  }
}
