package org.kafka.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

public class PropertyFileReader {
    private static final Logger logger = Logger.getLogger(PropertyFileReader.class);
    private static Properties prop = new Properties();

    public PropertyFileReader() {
    }

    public static Properties readPropertyFile() throws Exception {
        if (prop.isEmpty()) {
            InputStream input = PropertyFileReader.class.getClassLoader().getResourceAsStream("iot-kafka.properties");

            try {
                prop.load(input);
            } catch (IOException var5) {
                logger.error(var5);
                throw var5;
            } finally {
                if (input != null) {
                    input.close();
                }

            }
        }

        return prop;
    }
}
