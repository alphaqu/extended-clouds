package net.oskarstrom.extendedclouds.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.oskarstrom.extendedclouds.ClientEntrypoint.cloudConfigData;
import static net.oskarstrom.extendedclouds.ClientEntrypoint.matrix4fForCloudsOnly;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin{

	@Shadow
	protected abstract double getFov(Camera cam, float delta, boolean changeFov);
	@Final
	@Shadow
	private Camera camera;
	@Final
	@Shadow
	private MinecraftClient client;
	@Shadow
	private float zoom;
	@Shadow
	private float zoomX;
	@Shadow
	private float zoomY;
	@Shadow
	public abstract float method_32796();

	@Inject(method = "renderWorld", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Matrix4f;)V",
			shift = At.Shift.BEFORE))
	private void injected(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
		//this code is a copy of the construction of the vanilla projection matrix but has had it's distance extended
		//this will be stored and only used for clouds later
		MatrixStack matrixStack = new MatrixStack();
		double d = this.getFov(this.camera, tickDelta, true);
		matrixStack.peek().getPositionMatrix().multiply(this.getLongerBasicProjectionMatrix(d));
		matrix4fForCloudsOnly = matrixStack.peek().getPositionMatrix();
	}

	public Matrix4f getLongerBasicProjectionMatrix(double fov) {
		MatrixStack matrixStack = new MatrixStack();
		matrixStack.peek().getPositionMatrix().loadIdentity();
		if (this.zoom != 1.0F) {
			matrixStack.translate((double)this.zoomX, (double)(-this.zoomY), 0.0D);
			matrixStack.scale(this.zoom, this.zoom, 1.0F);
		}
		matrixStack.peek().getPositionMatrix().multiply(Matrix4f.viewboxMatrix(fov, (float)this.client.getWindow().getFramebufferWidth() / (float)this.client.getWindow().getFramebufferHeight(), 0.05F, (float) (this.method_32796()*cloudConfigData.getTestedModifier())));
		return matrixStack.peek().getPositionMatrix();
	}
}
