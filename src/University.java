
public class University {
  private int uid;
  private String name;
  private int capacity;
  private String ugroup;
  private float weight;
  private int applied;

  public University(int uid, String name, int capacity, String ugroup, float weight, int applied) {
    this.uid = uid;
    this.name = name;
    this.capacity = capacity;
    this.ugroup = ugroup;
    this.weight = weight;
    this.applied = applied;
  }

  public int getUid() {
    return uid;
  }

  public String getName() {
    return name;
  }

  public int getCapacity() {
    return capacity;
  }

  public String getUgroup() {
    return ugroup;
  }

  public float getWeight() {
    return weight;
  }

  public int getApplied() {
    return applied;
  }
}
