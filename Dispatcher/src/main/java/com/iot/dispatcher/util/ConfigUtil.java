package com.iot.dispatcher.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

    private static Properties prop = new Properties();

    static {
        try {
            InputStream in = ConfigUtil.class.getClassLoader().getResourceAsStream("dispatcher.properties");
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("load config file err");
        }
    }

    public static Properties getProp() {
        return prop;
    }
}
