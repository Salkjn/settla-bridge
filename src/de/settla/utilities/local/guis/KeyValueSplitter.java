package de.settla.utilities.local.guis;


import org.bukkit.ChatColor;

/**
 * Created by dennisheckmann on 28.02.17.
 */
public class KeyValueSplitter {

    private String splitter = ": ";

    public String getSplitter() {
        return this.splitter;
    }

    public String getLine(String key, String value) {
        return key + getSplitter() + value;
    }

    public void setSplitter(String splitter) {
        this.splitter = splitter;
    }

    public String getValue(String combined) {
        return getStrippedLine(returnIndexIfExists(combined, 1));
    }

    public String getKey(String combined) {
        return getStrippedLine(returnIndexIfExists(combined, 0));
    }

    private String returnIndexIfExists(String combined, int index) {
        String[] parts = combined.split(getSplitter());
        if (parts.length == 2)
            return parts[index];
        return null;
    }

    private static String getStrippedLine(String line) {
        return ChatColor.stripColor(line);
    }

}
