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
    private final static ConfigLoader configLoader = new ConfigLoader();

    private ConfigLoader() {
        Properties properties = new Properties();
        InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties");
        System.out.println(in);
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
    }

    public static ConfigLoader getInstance() {
        return configLoader;
    }

}
