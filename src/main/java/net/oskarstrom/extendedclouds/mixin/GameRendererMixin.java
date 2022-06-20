package net.oskarstrom.extendedclouds.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import static net.oskarstrom.extendedclouds.ExtendedClouds.CONFIG;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Inject(method = "method_32796", at = @At("RETURN"), cancellable = true)
	private void extend_distance(CallbackInfoReturnable<Float> cir) {
		if (CONFIG.extendFrustum) {
			cir.setReturnValue((float) (cir.getReturnValue() * CONFIG.getMultiplier()));
		}
	}
}
