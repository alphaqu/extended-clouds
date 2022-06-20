package net.oskarstrom.extendedclouds.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static net.oskarstrom.extendedclouds.ExtendedClouds.CONFIG;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow
	private int viewDistance;

	private float oldFogEnd = 0;

	@Inject(
			method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FDDD)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/util/math/MatrixStack;push()V"
			)
	)
	private void fixFoxStart(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double d, double e, double f, CallbackInfo ci) {
		oldFogEnd = RenderSystem.getShaderFogEnd();
		RenderSystem.setShaderFogEnd((viewDistanceModified() * 8) * 10);
	}

	@Inject(
			method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FDDD)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"
			)
	)
	private void fixFoxEnd(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double d, double e, double f, CallbackInfo ci) {
		RenderSystem.setShaderFogEnd(oldFogEnd);
	}

	@ModifyConstant(
			method = "renderClouds(Lnet/minecraft/client/render/BufferBuilder;DDDLnet/minecraft/util/math/Vec3d;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;",
			constant = @Constant(intValue = -3)
	)
	private int fancyForStart(int constant) {
		return -(viewDistanceModified() - 1);
	}

	@ModifyConstant(
			method = "renderClouds(Lnet/minecraft/client/render/BufferBuilder;DDDLnet/minecraft/util/math/Vec3d;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;",
			constant = @Constant(intValue = 4)
	)
	private int fancyForEnd(int constant) {
		return viewDistanceModified();
	}

	@ModifyConstant(
			method = "renderClouds(Lnet/minecraft/client/render/BufferBuilder;DDDLnet/minecraft/util/math/Vec3d;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;",
			constant = @Constant(intValue = -32)
	)
	private int fastForStart(int constant) {
		return -(viewDistanceModified() * 4);
	}

	@ModifyConstant(
			method = "renderClouds(Lnet/minecraft/client/render/BufferBuilder;DDDLnet/minecraft/util/math/Vec3d;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;",
			constant = @Constant(intValue = 32)
	)
	private int fastForEnd(int constant) {
		return (viewDistanceModified() * 4);
	}

	//config support for distance scaling
	private int viewDistanceModified() {
		return (int) (viewDistance * CONFIG.getMultiplier());
	}
}
