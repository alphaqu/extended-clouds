package net.oskarstrom.extendedclouds;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ExtendedClouds implements ClientModInitializer {

    public static Config CONFIG;

    @Override
    public void onInitializeClient() {
        loadConfig();
    }

    public static void loadConfig() {
        File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "extended-clouds.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (config.exists()) {
            try {
                FileReader fileReader = new FileReader(config);
                ExtendedClouds.CONFIG = gson.fromJson(fileReader, Config.class);
                fileReader.close();
                saveConfig();
                return;
            } catch (IOException ignored) {}
        }

        ExtendedClouds.CONFIG = new Config();
        saveConfig();
    }
    public static void saveConfig() {
        File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "extended-clouds.json");
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
            //logError("Config file could not be saved", false);
        }
    }

}
