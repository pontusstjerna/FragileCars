package se.nocroft.util;

import java.io.*;

/**
 * Created by pontu on 2016-04-06.
 */
public class CfgParser {
    public static String STD_PATH = "model/data/config.txt";

    private BufferedReader reader;
    private String cfg = "";

    public CfgParser(String filePath) {
        loadFile(filePath);
    }

    public int readInt(String varName) {
        return Integer.valueOf(readValue(varName));
    }

    public long readLong(String varName) {
        return Long.valueOf(readValue(varName));
    }

    public double readDouble(String varName) {
        return Double.valueOf(readValue(varName));
    }

    public String readString(String varName) {
        return readValue(varName);
    }

    public boolean readBoolean(String varName) {
        return Boolean.valueOf(readValue(varName));
    }

    public Class readClass(String varName) throws ClassNotFoundException {
        return Class.forName(readValue(varName));
    }

    private String readValue(String varName) {
        for (int i = 0; i < cfg.length(); i++) { // Read the whole string
            if (cfg.charAt(i) == '#') { // Search for #
                if (cfg.regionMatches(false, i + 1, varName, 0, varName.length())) { // See if name matches
                    String value = "";

                    // Current index + # + varName + "
                    int j = i + 1 + varName.length() + 2;
                    while (cfg.charAt(j) != '"') {
                        value += cfg.charAt(j);
                        j++;
                    }
                    return value;
                }
            }
        }

        System.out.println("Found no variable named " + varName + "!");
        return null;
    }

    private void loadFile(String filePath) {
        try {

            // This is seriously hacking. Java... sigh.
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream is = classLoader.getResourceAsStream(filePath);
            reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                cfg += line;
            }
        } catch (IOException e) {
            System.out.println("Unable to load file: " + filePath);
        }
    }
}
