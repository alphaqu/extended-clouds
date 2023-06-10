package dev.notalpha.extendedclouds;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ExtendedClouds {
    public static Config CONFIG;
    public static final Logger LOG = LoggerFactory.getLogger("extended-clouds");

    public static void init() {
        loadConfig();
    }


    public static void loadConfig() {
        File config = new File(ExtendedCloudsExpectedPlatform.getConfigDirectory().toFile(), "extended-clouds.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (config.exists()) {
            try {
                FileReader fileReader = new FileReader(config);
                ExtendedClouds.CONFIG = gson.fromJson(fileReader, Config.class);
                fileReader.close();
                saveConfig();
                return;
            } catch (IOException exception) {
                LOG.error("Creating new config because of reading error.", exception);
            }
        }

        ExtendedClouds.CONFIG = new Config();
        saveConfig();
    }

    public static void saveConfig() {
        File config = new File(ExtendedCloudsExpectedPlatform.getConfigDirectory().toFile(), "extended-clouds.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!config.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            config.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(config);
            fileWriter.write(gson.toJson(ExtendedClouds.CONFIG));
            fileWriter.close();
        } catch (IOException e) {
            LOG.error("Log file could not be saved", e);
        }
    }

}
