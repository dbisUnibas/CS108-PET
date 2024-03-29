package ch.unibas.dmi.dbis.cs108pet.session;

import ch.unibas.dmi.dbis.cs108pet.ui.MainScene;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A class that holds various information about a cs108pet session.
 *
 * @author loris.sauter
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionStorage {
  
  /**
   * The date of when the session was created / closed
   */
  private Date date;
  /**
   * App version -- to prevent version mismatch
   */
  private String version;
  /**
   * catalogue / course
   */
  private String lastUsedDir;
  private boolean enabled = true;
  
  private MainScene.Mode lastOpenMode;
  
  private List<String> lastOpenGroups;
  
  private String lastUsedTemplatingConfig;
  private String lastUsedCatalogueExport;
  
  private UUID lastActiveProgressSummary;
  
  public boolean isEnabled() {
    return enabled;
  }
  
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
  
  public MainScene.Mode getLastOpenMode() {
    return lastOpenMode;
  }
  
  public void setLastOpenMode(MainScene.Mode lastOpenMode) {
    this.lastOpenMode = lastOpenMode;
  }
  
  public List<String> getLastOpenGroups() {
    return lastOpenGroups;
  }
  
  public void setLastOpenGroups(List<String> lastOpenGroups) {
    this.lastOpenGroups = lastOpenGroups;
  }
  
  public UUID getLastActiveProgressSummary() {
    return lastActiveProgressSummary;
  }
  
  public void setLastActiveProgressSummary(UUID lastActiveProgressSummary) {
    this.lastActiveProgressSummary = lastActiveProgressSummary;
  }
  
  private HashMap<String, String> data;
  
  public Date getDate() {
    return date;
  }
  
  public void setDate(Date date) {
    this.date = date;
  }
  
  public String getVersion() {
    return version;
  }
  
  public void setVersion(String version) {
    this.version = version;
  }
  
  public String getLastUsedDir() {
    return lastUsedDir;
  }
  
  public void setLastUsedDir(String lastUsedDir) {
    this.lastUsedDir = lastUsedDir;
  }

  public boolean isValidDir(String lastUsedDir) {
    return Files.isDirectory(Path.of(lastUsedDir));
  }
  
  public SessionStorage() {
    data = new HashMap<>();
  }
  
  public int dataSize() {
    return data.size();
  }
  
  @JsonIgnore
  public boolean isDataEmpty() {
    return data.isEmpty();
  }
  
  public String getData(String key) {
    return data.get(key);
  }
  
  public boolean containsDataKey(String key) {
    return data.containsKey(key);
  }
  
  public String putData(String key, String value) {
    return data.put(key, value);
  }
  
  public void putAllData(Map<? extends String, ? extends String> m) {
    data.putAll(m);
  }
  
  public String removeData(String key) {
    return data.remove(key);
  }
  
  public void clear() {
    data.clear();
  }
  
  public boolean containsValue(String value) {
    return data.containsValue(value);
  }
  
  public Set<String> keySetData() {
    return data.keySet();
  }
  
  public Collection<String> dataValues() {
    return data.values();
  }
  
  public Set<Map.Entry<String, String>> entrySetData() {
    return data.entrySet();
  }
  
  public String getDataOrDefault(String key, String defaultValue) {
    return data.getOrDefault(key, defaultValue);
  }
  
  public String putDataIfAbsent(String key, String value) {
    return data.putIfAbsent(key, value);
  }
  
  public boolean removeData(String key, String value) {
    return data.remove(key, value);
  }
  
  public boolean replaceData(String key, String oldValue, String newValue) {
    return data.replace(key, oldValue, newValue);
  }
  
  public String replaceData(String key, String value) {
    return data.replace(key, value);
  }
  
  public String computeDataIfAbsent(String key, Function<? super String, ? extends String> mappingFunction) {
    return data.computeIfAbsent(key, mappingFunction);
  }
  
  public String computeDataIfPresent(String key, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
    return data.computeIfPresent(key, remappingFunction);
  }
  
  public String computeData(String key, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
    return data.compute(key, remappingFunction);
  }
  
  public String mergeData(String key, String value, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
    return data.merge(key, value, remappingFunction);
  }
  
  public void forEachData(BiConsumer<? super String, ? super String> action) {
    data.forEach(action);
  }
  
  public void replaceAllData(BiFunction<? super String, ? super String, ? extends String> function) {
    data.replaceAll(function);
  }
    
    public String getLastUsedTemplatingConfig() {
        return lastUsedTemplatingConfig;
    }
    
    public void setLastUsedTemplatingConfig(String lastUsedTemplatingConfig) {
        this.lastUsedTemplatingConfig = lastUsedTemplatingConfig;
    }
    
    public String getLastUsedCatalogueExport() {
        return lastUsedCatalogueExport;
    }
    
    public void setLastUsedCatalogueExport(String lastUsedCatalogueExport) {
        this.lastUsedCatalogueExport = lastUsedCatalogueExport;
    }
    
    @Override
    public String toString() {
        return "SessionStorage{" +
            "date=" + date +
            ", version='" + version + '\'' +
            ", lastUsedDir='" + lastUsedDir + '\'' +
            ", enabled=" + enabled +
            ", lastOpenMode=" + lastOpenMode +
            ", lastOpenGroups=" + lastOpenGroups +
            ", lastUsedTemplatingConfig='" + lastUsedTemplatingConfig + '\'' +
            ", lastUsedCatalogueExport='" + lastUsedCatalogueExport + '\'' +
            ", lastActiveProgressSummary=" + lastActiveProgressSummary +
            ", data=" + data +
            '}';
    }
}
