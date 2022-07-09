package utils;

import lombok.Data;

import java.io.*;
import java.util.Properties;


public class ConfigLoader {
    public final static String DATA_FILE_PATH;
    public final static Integer CACHE_SIZE;

    static {
        Properties properties = new Properties();
        InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream("config/config.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 初始化变量
        DATA_FILE_PATH = properties.getProperty("filepath");
        CACHE_SIZE = Integer.parseInt(properties.getProperty("cacheSize", "10"));
    }

}
