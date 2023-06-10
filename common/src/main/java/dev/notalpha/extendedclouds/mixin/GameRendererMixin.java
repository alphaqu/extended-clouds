package dev.notalpha.extendedclouds.mixin;

import dev.notalpha.extendedclouds.ExtendedClouds;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Inject(method = "method_32796", at = @At("RETURN"), cancellable = true)
	private void extend_distance(CallbackInfoReturnable<Float> cir) {
		if (ExtendedClouds.CONFIG.extendFrustum) {
			cir.setReturnValue((float) (cir.getReturnValue() * ExtendedClouds.CONFIG.getMultiplier()));
		}
	}
}
