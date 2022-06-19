package net.oskarstrom.extendedclouds;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.Matrix4f;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ClientEntrypoint implements ClientModInitializer {

    public static Config cloudConfigData;

    public static Matrix4f matrix4fForCloudsOnly = null;

    @Override
    public void onInitializeClient() {
        ec$loadConfig();
    }

    public static void ec$loadConfig() {
        File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "extended-clouds.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (config.exists()) {
            try {
                FileReader fileReader = new FileReader(config);
                cloudConfigData = gson.fromJson(fileReader, Config.class);
                fileReader.close();
                ec$saveConfig();
            } catch (IOException e) {
                cloudConfigData = new Config();
                ec$saveConfig();
            }
        } else {
            cloudConfigData = new Config();
            ec$saveConfig();
        }
    }
    public static void ec$saveConfig() {
        File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "extended-clouds.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!config.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            config.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(config);
            fileWriter.write(gson.toJson(cloudConfigData));
            fileWriter.close();
        } catch (IOException e) {
            //logError("Config file could not be saved", false);
        }
    }

}
