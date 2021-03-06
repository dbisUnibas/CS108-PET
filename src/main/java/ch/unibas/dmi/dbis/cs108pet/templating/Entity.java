package ch.unibas.dmi.dbis.cs108pet.templating;

import java.util.Map;
import java.util.TreeMap;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
class Entity<E> {
  
  
  private final String name;
  private Map<String, Field<E, ?>> fields = new TreeMap<>();
  
  Entity(String name, Field<E, ?>... fields) {
    this.name = name;
    if (fields != null) {
      for (Field<E, ?> field : fields) {
        this.fields.put(field.getName(), field);
      }
    }
  }
  
  public boolean hasField(String name) {
    return fields.containsKey(name);
  }
  
  public Field<E, ?> getFieldForName(String name) {
    return fields.get(name);
  }
  
  public String getEntityName() {
    return name;
  }
  
}
