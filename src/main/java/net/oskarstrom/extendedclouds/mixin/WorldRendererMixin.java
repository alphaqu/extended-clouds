package net.oskarstrom.extendedclouds.mixin;

import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;
import net.oskarstrom.extendedclouds.CloudRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow private int viewDistance;

	@Shadow private @Nullable CloudRenderMode lastCloudsRenderMode;

	@Inject(
			method = "renderClouds(Lnet/minecraft/client/render/BufferBuilder;DDDLnet/minecraft/util/math/Vec3d;)V",
			at = @At(value = "HEAD"),
			cancellable = true
	)
	private void renderStuff(BufferBuilder builder, double x, double y, double z, Vec3d color, CallbackInfo ci) {
		CloudRenderer.renderClouds(builder, x, y, z, color, 0.8f, viewDistance, this.lastCloudsRenderMode == CloudRenderMode.FANCY);
		ci.cancel();
	}
}
