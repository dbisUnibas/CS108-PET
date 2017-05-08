package ch.unibas.dmi.dbis.reqman.updating;

import ch.unibas.dmi.dbis.reqman.configuration.ConfigUtils;

import java.io.File;
import java.io.IOException;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class GroupUpdater {

    public static final String IN_DIR_KEY = "--in";
    public static final String OUT_DIR_KEY = "--out";
    public static final String CATALOGUE_KEY = "--catalogue";
    public static final String SEPARATOR = "=";
    public static final String OUT_KEY = "-o";
    private final GroupUpdatingStrategy strategy;

    GroupUpdater(GroupUpdatingStrategy strategy) {
        this.strategy = strategy;
    }

    public void perform(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println(getUsage());
        } else if (args.length == 3) {
            // --in and --out specified BUT CHECK IT FIRST
            String in = "", out = "", cat = ""; // have to be assigned, to silence compiler
            try {
                in = findArgument(IN_DIR_KEY, args);
                out = findArgument(OUT_DIR_KEY, args);
                cat = findArgument(CATALOGUE_KEY, args);
            } catch (IllegalArgumentException ex) {
                System.err.println("Could not read the given options \n" + ex.getMessage() + "\n\n" + getUsage());
                System.exit(-1);
            }
            handleBatchUpdate(cat, in, out);
        } else if (args.length == 2) {
            // Only cat and file given.
            throw new UnsupportedOperationException("Not implemented");
        } else if (args.length >= 3) {
            // -o out in specified
            throw new UnsupportedOperationException("Not implemented");
        }
    }

    public String getUsage() {
        return "Updates a reqman group json file from reqman-version " + strategy.fromVersion() +
                " to reqman-version " + strategy.toVersion() + "\n\n" +
                "Usage:\n" +
                "group " + IN_DIR_KEY + SEPARATOR + "<INDIR> " + OUT_DIR_KEY + SEPARATOR + "<OUTDIR> " + CATALOGUE_KEY + SEPARATOR + "<path>" + "\n" +
                "\tupdates all group files in directory <INDIR> and stores the results in the directory <OUTDIR>, keeping the file names.\n\n" +
                "or\n" + "group " + CATALOGUE_KEY + SEPARATOR + "<path>" + "[" + OUT_KEY + " <out>] <file>\n" +
                "\tupdates the <file> group and saves it at the same location with the prefix updtated- or, when option " + OUT_KEY + " is specified, at the location <out>." +
                "\n\nAll paths are assumed to be relative to the current working directory";
    }

    private String findArgument(String key, String[] args) {
        for (String s : args) {
            if (s.startsWith(key)) {
                return s.substring(s.indexOf(SEPARATOR) + 1);
            }
        }
        throw new IllegalArgumentException("Did not find key " + key);
    }

    private void handleBatchUpdate(String catalogue, String inDir, String outDir) throws IOException {
        // inDir and outDir are set for sure!
        File in = new File(ConfigUtils.getCodeSourceLocation().getParent() + File.separator + inDir);
        System.out.println("In dir: " + in.getPath());
        File out = new File(ConfigUtils.getCodeSourceLocation().getParent() + File.separator + outDir);
        System.out.println("Out dir: " + out.getPath());
        if (in.isDirectory() && out.isDirectory()) {
            File catFile = new File(ConfigUtils.getCodeSourceLocation().getParent() + File.separator + catalogue);
            if (!catFile.isFile()) {
                System.err.println("Catalogue must be a file");
                System.exit(-3);
            }
            strategy.setCatalogue(catFile);
            for (File f : in.listFiles()) {
                if (f.isFile()) {
                    if (!strategy.open(f)) {
                        continue;
                    }
                    strategy.update();
                    strategy.save(new File(out.getPath() + f.separator + f.getName()));
                }
            }
            System.out.println("Done.");
            System.exit(0);
        } else {
            System.err.println("With options " + IN_DIR_KEY + ", " + OUT_DIR_KEY + " the arguments must be directories");
            System.exit(-2);
        }

    }
}
