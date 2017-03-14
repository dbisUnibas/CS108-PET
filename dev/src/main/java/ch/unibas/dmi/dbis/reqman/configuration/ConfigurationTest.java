package ch.unibas.dmi.dbis.reqman.configuration;

import ch.unibas.dmi.dbis.reqman.common.JSONUtils;

import java.io.IOException;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class ConfigurationTest {

    public static void main(String[] args) throws IOException {
        String complete = "{\n" +
                "\t\t\"extension\": \"html\",\n" +
                "\t\t\"templates\": {\n" +
                "\t\t\t\"requirement\": \"requirement.html\",\n" +
                "\t\t\t\"milestone\": \"milestone.html\",\n" +
                "\t\t\t\"catalogue\": \"catalogue.txt\",\n" +
                "\t\t\t\"progress\": \"progress.asfd\",\n" +
                "\t\t\t\"group-milestone\": \"groupMS.md\",\n" +
                "\t\t\t\"group\": \"g.template\"\n" +
                "\t\t}\n" +
                "\t}";

        String incomplete = "{\n" +
                "\t\t\t\"extension\": \"tex\",\n" +
                "\t\t\t\"templates\":{\n" +
                "\t\t\t\t\"requirement\": \"req.tex\"\n" +
                "\t\t\t}}";

        TemplatingConfiguration cConfig = JSONUtils.readFromString(complete, TemplatingConfiguration.class);

        cConfig.validateTemplatesAndFix();
        System.out.println("Complete: \n"+cConfig.toString());

        TemplatingConfiguration fixed = JSONUtils.readFromString(incomplete, TemplatingConfiguration.class);

        fixed.validateTemplatesAndFix();
        System.out.println("Fixed: \n"+fixed.toString());
    }
}
