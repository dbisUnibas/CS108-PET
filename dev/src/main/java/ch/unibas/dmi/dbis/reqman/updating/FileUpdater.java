package ch.unibas.dmi.dbis.reqman.updating;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class FileUpdater {

    public static final String GROUP_OPTION = "group";
    private GroupUpdater updater;

    FileUpdater() {

    }

    FileUpdater(GroupUpdater updater) {
        this.updater = updater;
    }

    public static void main(String[] args) {
        FileUpdater updater = new FileUpdater();
        if (args.length < 1) {
            System.out.println(updater.getUsage());
        } else {
            try {
                switch (args[0]) {
                    case GROUP_OPTION:
                        ArrayList<String> argList = new ArrayList<>(Arrays.asList(args));
                        argList.remove(0);
                        updater.setUpdater(new GroupUpdater(new GroupAllProgressToAsessedStrategy()));
                        updater.perform(argList.toArray(new String[0]));
                        break;
                    default:
                        System.out.println("Not implemented yet");
                }
            } catch (Throwable t) {
                System.err.println("An exception occured: " + t.getMessage() + "\n");
                t.printStackTrace();
            }

        }


    }

    public GroupUpdater getUpdater() {
        return updater;
    }

    public void setUpdater(GroupUpdater updater) {
        this.updater = updater;
    }

    private void perform(String[] args) throws IOException {
        if (updater != null) {
            updater.perform(args);
        }
    }

    private String getBasicUsage() {
        return "CLI program to update ReqMan savefiles (groups or catalogues) from a version to another.";
    }

    private String getDetailedUsage() {
        if (updater != null) {
            return "Usage: java -jar updater.jar group <group-args>\n\n" +
                    "With <group-args>: \n" +
                    updater.getUsage();
        } else {
            return "No usage yet";
        }
    }

    private String getUsage() {
        return getBasicUsage() + "\n" + getDetailedUsage();
    }

}
