package utils;

import lombok.Getter;

import java.io.*;
import java.util.Properties;

@Getter
public class ConfigLoader {
    private final String walFilePath;
    private final String indexFilePath;
    private final String dataFilePath;
    private final Integer cacheSize;
    private final Integer interval;
    private final Integer flushDiskInterval;
    private final Integer indexLruCacheSize;
    private final Float indexLruCacheLoadFactor;
    private final static ConfigLoader configLoader = new ConfigLoader();

    private ConfigLoader() {
        int innerInterval;
        Properties properties = new Properties();
        InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 初始化变量
        walFilePath = properties.getProperty("walFilePath");
        indexFilePath = properties.getProperty("indexFilePath");
        dataFilePath = properties.getProperty("dataFilePath");
        cacheSize = Integer.parseInt(properties.getProperty("cacheSize", "10"));
        flushDiskInterval = Integer.parseInt(properties.getProperty("flushDiskInterval", "1000"));
        indexLruCacheSize = Integer.parseInt(properties.getProperty("indexLruCacheSize", "100"));
        indexLruCacheLoadFactor = Float.parseFloat(properties.getProperty("indexLruCacheLoadFactor", "0.75"));
        innerInterval = Integer.parseInt(properties.getProperty("interval", "3"));
        if (innerInterval <= 0) {
            innerInterval = 3;
        }
        interval = innerInterval;
    }

    public static ConfigLoader getInstance() {
        return configLoader;
    }

}
