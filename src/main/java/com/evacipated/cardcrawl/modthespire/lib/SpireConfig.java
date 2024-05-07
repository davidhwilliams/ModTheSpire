package com.evacipated.cardcrawl.modthespire.lib;

import java.io.*;
import java.util.Properties;

public class SpireConfig
{
    private final static String EXTENSION = "properties";
    private Properties properties;
    private File file;
    private String filePath;

    public static String makeFilePath(String modName, String fileName)
    {
        return makeFilePath(modName, fileName, EXTENSION);
    }

    public static String makeFilePath(String modName, String fileName, String ext)
    {
        String dirPath;
        if (modName == null) {
            dirPath = ConfigUtils.CONFIG_DIR + File.separator;
        } else {
            dirPath = ConfigUtils.CONFIG_DIR + File.separator
                + modName + File.separator;
        }
        String filePath = dirPath + fileName + "." + ext;
        File dir = new File(dirPath);
        dir.mkdirs();

        return filePath;
    }

    public SpireConfig(String modName, String fileName) throws IOException
    {
        this(modName, fileName, new Properties());
    }

    public SpireConfig(String modName, String fileName, Properties defaultProperties) throws IOException
    {
        properties = new Properties(defaultProperties);

        filePath = makeFilePath(modName, fileName);

        file = new File(filePath);
        file.createNewFile();
        load();
    }

    public void load() throws IOException
    {
        properties.load(new FileInputStream(file));
    }

    public void save() throws IOException
    {
        properties.store(new FileOutputStream(file), null);
    }

    public boolean has(String key)
    {
        return properties.containsKey(key);
    }

    public void remove(String key)
    {
        properties.remove(key);
    }

    public void clear()
    {
        properties.clear();
    }

    public String getString(String key)
    {
        return properties.getProperty(key);
    }

    public String getString(String key, String defaultValue)
    {
        return properties.getProperty(key, defaultValue);
    }

    public void setString(String key, String value)
    {
        properties.setProperty(key, value);
    }

    public boolean getBool(String key)
    {
        return Boolean.parseBoolean(getString(key));
    }

    public boolean getBool(String key, boolean defaultValue)
    {
        if (has(key)) {
            return getBool(key);
        }
        return defaultValue;
    }

    public void setBool(String key, boolean value)
    {
        setString(key, Boolean.toString(value));
    }

    public int getInt(String key)
    {
        return Integer.parseInt(getString(key));
    }

    public int getInt(String key, int defaultValue) {
        if (has(key)) {
            return getInt(key);
        }
        return defaultValue;
    }

    public void setInt(String key, int value)
    {
        setString(key, Integer.toString(value));
    }

    public float getFloat(String key)
    {
        return Float.parseFloat(getString(key));
    }

    public float getFloat(String key, float defaultValue)
    {
        if (has(key)) {
            return getFloat(key);
        }
        return defaultValue;
    }

    public void setFloat(String key, float value)
    {
        setString(key, Float.toString(value));
    }
}
