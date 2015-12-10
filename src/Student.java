
public class Student {
  private int sid;
  private String name;
  private int csat_score;
  private int school_score;

  public Student(int sid, String name, int csat_score, int school_score) {
    this.sid = sid;
    this.name = name;
    this.csat_score = csat_score;
    this.school_score = school_score;
  }

  public int getSid() {
    return sid;
  }

  public String getName() {
    return name;
  }

  public int getCsatScore() {
    return csat_score;
  }

  public int getSchoolScore() {
    return school_score;
  }

  public boolean hasSameScore(Student that) {
    return csat_score == that.csat_score && school_score == that.school_score;
  }
}
