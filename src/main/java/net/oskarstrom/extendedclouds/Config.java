package net.oskarstrom.extendedclouds;

public class Config {
    @SuppressWarnings("FieldCanBeLocal")
    public final double cloudRenderDistanceMultiplier = 1;
    public final boolean extendFrustum = true;

    //needs to be above 0
    public double getMultiplier(){
        //noinspection ConstantConditions
        if (cloudRenderDistanceMultiplier <= 0) return 0.1;
        return 4.0;
    }

}
