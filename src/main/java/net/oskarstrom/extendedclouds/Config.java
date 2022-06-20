package net.oskarstrom.extendedclouds;

public class Config {

    public final String _comment_ = "renderDistanceToCloudModifier will set the distance that clouds render, relative to your render distance, 1.0 is render distance, WARNING if this is set too high you might crash";
    @SuppressWarnings("FieldCanBeLocal")
    public final double renderDistanceToCloudModifier = 2;

    //needs to be above 0
    public double getTestedModifier(){
        //noinspection ConstantConditions
        if (renderDistanceToCloudModifier <= 0) return 0.1;
        return renderDistanceToCloudModifier;
    }

}
