package com.nidhi.projectyt;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public enum Config {

    PRESENT_ADDRESS,
    QUERY_STRING,
    NEW_STRING,
    RESULTS_OF_VIDEOS,
    YT_PROPERTIES_FILENAME,
    QUEUE_A,
    QUEUE_B;

    private static final Logger LOG = LogManager.getLogger(Config.class);
    private static Map<String, String> map = new HashMap<>();
    private static final String C_FILE_NAME = "config.json";

    static {
        try {
            File file = new File(C_FILE_NAME);
            if (!file.exists()) {
                LOG.fatal("ALERT! FILE NOT FOUND: " + file.getAbsolutePath());
                file = new File("../" + C_FILE_NAME);
                if (!file.exists()) {
                    LOG.fatal("ALERT! FILE NOT FOUND: " + file.getAbsolutePath());
                    System.exit(-1);
                }
            }
            LOG.debug("Config file found in: " + file.getAbsolutePath());

            ObjectMapper om = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            map = om.readValue(file, new TypeReference<Map<String, String>>() {
            });

            LOG.debug("Configuration:\n" + om.writeValueAsString(map));
        } catch (IOException ex) {
            LOG.fatal(ex);
            System.exit(-1);
        }
    }

    public String getString() {
        return map.get(name());
    }

    public Integer getInteger() {
        return Integer.parseInt(map.get(name()));
    }

    public Long getLong() {
        return Long.parseLong(map.get(name()));
    }

    public Float getFloat() {
        return Float.parseFloat(map.get(name()));
    }

    public Boolean getBoolean() {
        return Boolean.parseBoolean(map.get(name()));
    }

    public Object get() {
        return map.get(name());
    }

}
