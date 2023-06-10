package dev.notalpha.extendedclouds.fabric;


import dev.notalpha.extendedclouds.ExtendedClouds;
import net.fabricmc.api.ClientModInitializer;

public class ExtendedCloudsFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ExtendedClouds.init();
    }
}
