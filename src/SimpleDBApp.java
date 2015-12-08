import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class SimpleDBApp {
  private static final String DB_ID = "DB-2013-11395";
  private static final String DB_PASSWORD = "DB-2013-11395";
  private static final String DB_URL = "jdbc:mariadb://147.46.15.238:3306/DB-2013-11395";

  private static final String LINE_MENU = "============================================================";
  private static final String LINE_UNIV = "________________________________________________________";
  private static final String FMT_UNIV = "%-3.3s %-22.22s %-8.8s %-5.5s %-6.6s %-7.7s";
  private static final String LINE_STUD = "--------------------------------------------------------";
  private static final String FMT_STUD = "%-9.9s %-22.22s %-10.10s %-12.12s";

  private static Connection conn;
  private static Scanner in;
  private static PreparedStatement stmt00, stmt01;
  private static PreparedStatement stmt10;
  private static PreparedStatement stmt20;
  private static PreparedStatement stmt30, stmt31;
  private static PreparedStatement stmt40, stmt41;
  private static PreparedStatement stmt50, stmt51;
  private static PreparedStatement stmt60, stmt61, stmt62;
  private static PreparedStatement stmt70, stmt72, stmt73, stmt74;
  private static PreparedStatement stmt80;
  private static PreparedStatement stmt90;

  public static void main(String[] args) {
    try {
      init();
      while (true) {
        switch (getAction()) {
          case 1:
            printAllUniv();
            break;
          case 2:
            printAllStud();
            break;
          case 3:
            insertUniv();
            break;
          case 4:
            removeUniv();
            break;
          case 5:
            insertStud();
            break;
          case 6:
            removeStud();
            break;
          case 7:
            apply();
            break;
          case 8:
            printStudByUniv();
            break;
          case 9:
            printUnivByStud();
            break;
          case 10:
          case 11:
          case 12:
            exit();
            break;
          default:
            System.out.println("Invalid action.");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static void init() throws SQLException {
    conn = DriverManager.getConnection(DB_URL, DB_ID, DB_PASSWORD);
    in = new Scanner(System.in);

    stmt00 = conn.prepareStatement("SELECT count(*) FROM university WHERE uid = ?");
    stmt01 = conn.prepareStatement("SELECT count(*) FROM student WHERE sid = ?");

    stmt10 = conn.prepareStatement("SELECT * FROM university");

    stmt20 = conn.prepareStatement("SELECT * FROM student");

    stmt30 = conn.prepareStatement("SELECT max(uid) + 1 FROM university");
    stmt31 = conn.prepareStatement("INSERT INTO university VALUES (?, ?, ?, ?, ?, 0)");

    stmt40 = conn.prepareStatement("DELETE FROM university WHERE uid = ?");
    stmt41 = conn.prepareStatement("DELETE FROM apply WHERE uid = ?");

    stmt50 = conn.prepareStatement("SELECT max(sid) + 1 FROM student");
    stmt51 = conn.prepareStatement("INSERT INTO student VALUES (?, ?, ?, ?)");

    stmt60 = conn.prepareStatement("DELETE FROM student WHERE sid = ?");
    stmt61 = conn.prepareStatement("DELETE FROM apply WHERE sid = ?");
    stmt62 = conn.prepareStatement("UPDATE university SET applied = applied - 1 WHERE uid in (SELECT uid FROM apply WHERE sid = ?)");

    stmt70 = conn.prepareStatement("SELECT ugroup FROM university WHERE uid = ?");
    stmt72 = conn.prepareStatement("SELECT ugroup FROM university WHERE uid in (SELECT uid FROM apply WHERE sid = ?)");
    stmt73 = conn.prepareStatement("INSERT INTO apply VALUES (?, ?)");
    stmt74 = conn.prepareStatement("UPDATE university SET applied = applied + 1 WHERE uid = ?");

    stmt80 = conn.prepareStatement("SELECT * FROM student WHERE sid in (SELECT sid FROM apply WHERE uid = ?)");

    stmt90 = conn.prepareStatement("SELECT * FROM university WHERE uid in (SELECT uid FROM apply WHERE sid = ?)");
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

  private static boolean univExists(int uid) throws SQLException {
    stmt00.setInt(1, uid);
    ResultSet rs = stmt00.executeQuery();
    rs.next();
    return rs.getInt(1) != 0;
  }

  private static boolean studExists(int sid) throws SQLException {
    stmt01.setInt(1, sid);
    ResultSet rs = stmt01.executeQuery();
    rs.next();
    return rs.getInt(1) != 0;
  }

  private static void printUniv(ResultSet rs) throws SQLException {
    System.out.println(LINE_UNIV);
    System.out.println(String.format(FMT_UNIV, "id", "name", "capacity", "group", "weight", "applied"));
    System.out.println(LINE_UNIV);
    while (rs.next())
      System.out.println(String.format(FMT_UNIV, rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), String.format("%.2f", rs.getFloat(5)), rs.getInt(6)));
    System.out.println(LINE_UNIV);
  }

  private static void printStud(ResultSet rs) throws SQLException {
    System.out.println(LINE_STUD);
    System.out.println(String.format(FMT_STUD, "id", "name", "csat_score", "school_score"));
    System.out.println(LINE_STUD);
    while (rs.next())
      System.out.println(String.format(FMT_STUD, rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4)));
    System.out.println(LINE_STUD);
  }

  private static void printAllUniv() throws SQLException {
    printUniv(stmt10.executeQuery());
  }

  private static void printAllStud() throws SQLException {
    printStud(stmt20.executeQuery());
  }

  private static void insertUniv() throws SQLException {
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
    if (uid == 0)
      uid = 1;

    stmt31.setInt(1, uid);
    stmt31.setString(2, name);
    stmt31.setInt(3, capacity);
    stmt31.setString(4, ugroup);
    stmt31.setFloat(5, weight);
    stmt31.executeUpdate();
    System.out.println("A university is successfully inserted.");
  }

  private static void removeUniv() throws SQLException {
    System.out.print("University ID: ");
    int uid = Integer.parseInt(in.nextLine());
    if (!univExists(uid)) {
      System.out.println(String.format("University %d doesn't exist.", uid));
      return;
    }
    try {
      conn.setAutoCommit(false);
      stmt40.setInt(1, uid);
      stmt40.executeUpdate();
      stmt41.setInt(1, uid);
      stmt41.executeUpdate();
      conn.commit();
      System.out.println("A university is successfully deleted.");
    } catch (SQLException e) {
      e.printStackTrace();
      try {
        conn.rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    } finally {
      conn.setAutoCommit(true);
    }
  }

  private static void insertStud() throws SQLException {
    System.out.print("Student name: ");
    String name = in.nextLine();
    if (name.length() > 20) {
      name = name.substring(0, 20);
    }

    System.out.print("CSAT score: ");
    int csat_score = Integer.parseInt(in.nextLine());
    if (csat_score < 0 || csat_score > 400) {
      System.out.println("CSAT score should be between 0 and 400.");
      return;
    }

    System.out.print("High school record score: ");
    int school_score = Integer.parseInt(in.nextLine());
    if (school_score < 0 || school_score > 100) {
      System.out.println("High school records score should be between 0 and 100.");
      return;
    }

    ResultSet rs = stmt50.executeQuery();
    rs.next();
    int sid = rs.getInt(1);
    if (sid == 0)
      sid = 1;

    stmt51.setInt(1, sid);
    stmt51.setString(2, name);
    stmt51.setInt(3, csat_score);
    stmt51.setInt(4, school_score);
    stmt51.executeUpdate();
    System.out.println("A student is successfully inserted.");
  }

  private static void removeStud() throws SQLException {
    System.out.print("Student ID: ");
    int sid = Integer.parseInt(in.nextLine());
    if (!studExists(sid)) {
      System.out.println(String.format("Student %d doesn't exist.", sid));
      return;
    }
    try {
      conn.setAutoCommit(false);
      stmt60.setInt(1, sid);
      stmt60.executeUpdate();
      stmt62.setInt(1, sid);
      stmt62.executeUpdate();
      stmt61.setInt(1, sid);
      stmt61.executeUpdate();
      conn.commit();
      System.out.println("A student is successfully deleted.");
    } catch (SQLException e) {
      e.printStackTrace();
      try {
        conn.rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    } finally {
      conn.setAutoCommit(true);
    }
  }

  private static void apply() throws SQLException {
    ResultSet rs;

    System.out.print("Student ID: ");
    int sid = Integer.parseInt(in.nextLine());
    if (!studExists(sid)) {
      System.out.println(String.format("Student %d doesn't exist.", sid));
      return;
    }

    System.out.print("University ID: ");
    int uid = Integer.parseInt(in.nextLine());
    stmt70.setInt(1, uid);
    rs = stmt70.executeQuery();
    if (!rs.next()) {
      System.out.println(String.format("University %d doesn't exist.", uid));
      return;
    }
    String ugroup = rs.getString(1);

    stmt72.setInt(1, sid);
    rs = stmt72.executeQuery();
    boolean flag = false;
    while (rs.next()) {
      if (rs.getString(1).equals(ugroup)) {
        flag = true;
        break;
      }
    }
    if (flag) {
      System.out.println("A student can apply up to one university per group.");
      return;
    }

    try {
      conn.setAutoCommit(false);
      stmt73.setInt(1, uid);
      stmt73.setInt(2, sid);
      stmt73.executeUpdate();
      stmt74.setInt(1, uid);
      stmt74.executeUpdate();
      conn.commit();
      System.out.println("Successfully made an application.");
    } catch (SQLException e) {
      e.printStackTrace();
      try {
        conn.rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    } finally {
      conn.setAutoCommit(true);
    }
  }

  private static void printStudByUniv() throws SQLException {
    System.out.print("University ID: ");
    int uid = Integer.parseInt(in.nextLine());
    if (!univExists(uid)) {
      System.out.println(String.format("University %d doesn't exist.", uid));
      return;
    }
    stmt80.setInt(1, uid);
    printStud(stmt80.executeQuery());
  }

  private static void printUnivByStud() throws SQLException {
    System.out.print("Student ID: ");
    int sid = Integer.parseInt(in.nextLine());
    if (!studExists(sid)) {
      System.out.println(String.format("Student %d doesn't exist.", sid));
      return;
    }
    stmt90.setInt(1, sid);
    printUniv(stmt90.executeQuery());
  }

  private static void exit() throws SQLException {
    conn.close();
    System.out.println("Bye!");
    System.exit(0);
  }
}
