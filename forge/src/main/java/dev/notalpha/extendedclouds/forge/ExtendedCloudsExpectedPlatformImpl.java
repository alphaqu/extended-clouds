package dev.notalpha.extendedclouds.forge;

import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ExtendedCloudsExpectedPlatformImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
