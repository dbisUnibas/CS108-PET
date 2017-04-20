package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;
import ch.unibas.dmi.dbis.reqman.configuration.ConfigUtils;
import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Group;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class OpenBackupsTask extends ManagementTask<List<OpenBackupsTask.BackupObject>> {


    public static class BackupObject{
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("BackupObject{");
            sb.append("cat=").append(cat);
            sb.append(", catalogue=").append(catalogue);
            sb.append(", group=").append(group);
            sb.append(", location=").append(location);
            sb.append('}');
            return sb.toString();
        }

        final boolean cat;

        private BackupObject(boolean catalogue) {
            this.cat = catalogue;
        }

        static BackupObject createCatalogueBackup(Catalogue cat, File location){
            BackupObject obj = new BackupObject(true);
            obj.setCatalogue(cat);
            obj.setLocation(location);
            return obj;
        }

        static BackupObject createGroupBackup(Group group){
            BackupObject obj = new BackupObject(false);
            obj.setGroup(group);
            return obj;
        }

        private Catalogue catalogue = null;
        private Group group = null;
        private File location = null;

        public boolean isCatalogue(){
            return cat;
        }


        public Catalogue getCatalogue() {
            return catalogue;
        }

        private void setCatalogue(Catalogue catalogue) {
            this.catalogue = catalogue;
        }

        public Group getGroup() {
            return group;
        }

        private void setGroup(Group group) {
            this.group = group;
        }

        public File getLocation() {
            return location;
        }

        private void setLocation(File location) {
            this.location = location;
        }
    }

    @Override
    protected List<BackupObject> call() throws Exception {
        List<BackupObject> list = new ArrayList<>();
        File dir = ConfigUtils.getCodeSourceLocation().getParentFile();
        if (dir.isDirectory()) {
            File[] files = dir.listFiles(EntityManager.BACKUP_FILTER);
            updateAll("Early initialization done.", 0.1);
            for (int i = 1; i <= files.length; ++i) {
                File file = files[i - 1];
                LOGGER.info("Found .backup file: " + file.getPath());
                Map<String, Object> backupObj = JSONUtils.readFromJSONFile(file);
                boolean hasCatalogueKey = backupObj.containsKey(EntityManager.CATALOGUE_KEY);
                boolean hasGroupKey = backupObj.containsKey(EntityManager.GROUP_KEY);

                if (hasCatalogueKey && hasGroupKey) {
                    if (backupObj.get(EntityManager.CATALOGUE_KEY) instanceof String) {
                        File catFile = new File((String) backupObj.get(EntityManager.CATALOGUE_KEY));

                        OpenCatalogueTask openCatalogueTask = new OpenCatalogueTask(catFile);
                        Thread th = new Thread(openCatalogueTask);
                        th.setDaemon(true);
                        th.start();
                        // TODO Check if catalogue signature matches current signature
                        Catalogue cat = openCatalogueTask.get(); // Intentionally blocking!
                        LOGGER.debug("Catalogue: "+cat);
                        list.add(BackupObject.createCatalogueBackup(cat, catFile));
                        updateAll("Loaded required catalogue", 0.4);

                        if (backupObj.get(EntityManager.GROUP_KEY) instanceof String) {
                            String groupFile = (String) backupObj.get(EntityManager.GROUP_KEY);
                            Group g = JSONUtils.readFromString(groupFile, Group.class);
                            list.add(BackupObject.createGroupBackup(g));
                            updateAll("msg", 0.1);
                            LOGGER.info("Successfully opened group" + g.getName() + " from backupfile " + file.getName());
                            boolean deletSucc = file.delete();
                            if (deletSucc) {
                                LOGGER.info("Removed backup file: " + file.getName());
                            }
                        }
                    } else {
                        String message = "Expected catalogue property to be of type String.";
                        throw LOGGER.throwing(Level.ERROR, new RuntimeException(message));
                    }
                } else {
                    throw LOGGER.throwing(Level.ERROR, new RuntimeException("Found .backup file without expected structure: " + backupObj.toString()));
                }
            }
        }
        return list;
    }


}