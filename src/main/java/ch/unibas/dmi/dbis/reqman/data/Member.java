package ch.unibas.dmi.dbis.reqman.data;

/**
 * A {@link Member} is an abstraction of a person, represented by the triple <code>< name, firstName, email ></code>
 *
 * Objects of this type may be added to {@link Group} objects, to
 * link the person with the group.
 *
 * @author loris.sauter
 */
public class Member {
  
  /**
   * The mandatory name of the person.
   * May only be the firstname, if the full name is not important
   */
  private final String name;
  /**
   * The first name of the person
   */
  private String firstName;
  /**
   * The email address of the person
   */
  private String email;
  
  /**
   * Creates a new {@link Member} by the given name
   * @param name The name of the person. May only be the firstname or a username.
   *             Preferably this name is unique, but is not ensured by the framework.
   */
  public Member(String name) {
    this.name = name;
  }
  
  /**
   * Creates a new, {@link Member} with specified name, firstname and email.
   *
   * @param name The name of the person
   * @param firstName The firstname of the person
   * @param email The email address of the person
   */
  public Member(String name, String firstName, String email) {
    this.name = name;
    this.firstName = firstName;
    this.email = email;
  }
  
  /**
   * Returns the name of the person.
   * The returned value is be unique by convention.
   * @return The name of the person
   */
  public String getName() {
    return name;
  }
  
  /**
   * Returns the firstname of the person.
   *
   * @return The firstname of the person
   */
  public String getFirstName() {
    return firstName;
  }
  
  /**
   * Returns the email address of the person
   * @return The email address of the person
   */
  public String getEmail() {
    return email;
  }
  
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Member{");
    sb.append("name='").append(name).append('\'');
    sb.append(", firstName='").append(firstName).append('\'');
    sb.append(", email='").append(email).append('\'');
    sb.append('}');
    return sb.toString();
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    
    Member member = (Member) o;
    
    if (!getName().equals(member.getName())) return false;
    if (getFirstName() != null ? !getFirstName().equals(member.getFirstName()) : member.getFirstName() != null)
      return false;
    return getEmail() != null ? getEmail().equals(member.getEmail()) : member.getEmail() == null;
  }
  
  @Override
  public int hashCode() {
    int result = getName().hashCode();
    result = 31 * result + (getFirstName() != null ? getFirstName().hashCode() : 0);
    result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
    return result;
  }
}
