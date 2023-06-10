package dev.notalpha.extendedclouds.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class ExtendedCloudsExpectedPlatformImpl {
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
