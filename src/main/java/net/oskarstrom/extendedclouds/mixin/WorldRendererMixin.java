package net.oskarstrom.extendedclouds.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static net.oskarstrom.extendedclouds.ExtendedClouds.CONFIG;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
	@Shadow
	private int viewDistance;
	private static final float OFFSET = 1f / 256f;

	@Shadow
	@Nullable
	private CloudRenderMode lastCloudRenderMode;
	@Shadow
	private boolean cloudsDirty;
	@Shadow
	@Nullable
	private VertexBuffer cloudsBuffer;

	@Shadow
	protected abstract BufferBuilder.BuiltBuffer renderClouds(BufferBuilder builder, double x, double y, double z, Vec3d color);

	@Shadow
	@Nullable
	private ClientWorld world;
	@Shadow
	private int ticks;
	@Shadow
	private int lastCloudsBlockX;
	@Shadow
	private int lastCloudsBlockY;
	@Shadow
	private int lastCloudsBlockZ;
	@Shadow private Vec3d lastCloudsColor;
	@Shadow @Final private MinecraftClient client;
	@Shadow @Final private static Identifier CLOUDS;
	private float oldFogEnd = 0;

	// Async clouds
	@Nullable
	private Future<BufferBuilder.BuiltBuffer> cloudBuildTask = null;
	private final BufferBuilder cloudBufferBuilder = new BufferBuilder(1000);
	private final ExecutorService cloudMesher = Executors.newSingleThreadExecutor(r -> new Thread(r, "CloudMesher"));
	private static final float CLOUD_SIZE = 12.0F;
	private int cloudsMeshX = 0;
	private int cloudsMeshY = 0;
	private int cloudsMeshZ = 0;
	private int cloudMeshTargetX = 0;
	private int cloudMeshTargetY = 0;
	private int cloudMeshTargetZ = 0;



	@Overwrite
	public void renderClouds(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double xOffset, double yOffset, double zOffset) {
		float cloudHeight = this.world.getDimensionEffects().getCloudsHeight();
		if (!Float.isNaN(cloudHeight)) {
			RenderSystem.disableCull();
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.depthMask(true);

			// calculate position
			double tickOffset = ((float)this.ticks + tickDelta) * 0.03F;
			double x = (xOffset + tickOffset) / CLOUD_SIZE;
			double y = cloudHeight - (float)yOffset + 0.33F;
			double z = zOffset / CLOUD_SIZE + 0.33000001311302185;

			// calculate mesh position
			float fracX = (float)(x - (double)MathHelper.floor(x));
			float fracY = (float)(y / 4.0 - (double)MathHelper.floor(y / 4.0)) * 4.0F;
			float fracZ = (float)(z - (double)MathHelper.floor(z));
			Vec3d color = this.world.getCloudsColor(tickDelta);

			// check if clouds should be remeshed
			int posX = (int)Math.floor(x);
			int posY = (int)Math.floor(y / 4.0);
			int posZ = (int)Math.floor(z);
			if (posX != this.lastCloudsBlockX || posY != this.lastCloudsBlockY || posZ != this.lastCloudsBlockZ || this.client.options.getCloudRenderModeValue() != this.lastCloudRenderMode || this.lastCloudsColor.squaredDistanceTo(color) > 2.0E-4) {
				this.lastCloudsBlockX = posX;
				this.lastCloudsBlockY = posY;
				this.lastCloudsBlockZ = posZ;
				this.lastCloudsColor = color;
				this.lastCloudRenderMode = this.client.options.getCloudRenderModeValue();
				this.cloudsDirty = true;
			}

			if (this.cloudBuildTask != null) {
				if (cloudBuildTask.isDone()) {
					try {
						if (this.cloudsBuffer != null) {
							this.cloudsBuffer.close();
						}

						this.cloudsBuffer = new VertexBuffer();
						BufferBuilder.BuiltBuffer builtBuffer = cloudBuildTask.get();
						this.cloudsBuffer.bind();
						this.cloudsBuffer.upload(builtBuffer);
						VertexBuffer.unbind();

						this.cloudsMeshX = this.cloudMeshTargetX;
						this.cloudsMeshY = this.cloudMeshTargetY;
						this.cloudsMeshZ = this.cloudMeshTargetZ;
						this.cloudBuildTask = null;
					} catch (InterruptedException | ExecutionException e) {
						throw new RuntimeException(e);
					}
				}
			} else {
				if (this.cloudsDirty) {
					this.cloudMeshTargetX = posX;
					this.cloudMeshTargetY = posY;
					this.cloudMeshTargetZ = posZ;
					this.cloudBuildTask = this.cloudMesher.submit(() -> {
						this.cloudBufferBuilder.clear();
						return this.renderClouds(this.cloudBufferBuilder, x, y, z, color);
					});
					this.cloudsDirty = false;
				}
			}


			RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
			RenderSystem.setShaderTexture(0, CLOUDS);
			BackgroundRenderer.setFogBlack();
			matrices.push();
			matrices.scale(CLOUD_SIZE, 1.0F, CLOUD_SIZE);
			matrices.translate((-fracX) + (this.cloudsMeshX - posX), fracY - ((this.cloudsMeshY - posY) * 4.0), (-fracZ) + (this.cloudsMeshZ - posZ));
			if (this.cloudsBuffer != null) {
				this.cloudsBuffer.bind();
				int u = this.lastCloudRenderMode == CloudRenderMode.FANCY ? 0 : 1;

				for(int v = u; v < 2; ++v) {
					if (v == 0) {
						RenderSystem.colorMask(false, false, false, false);
					} else {
						RenderSystem.colorMask(true, true, true, true);
					}

					ShaderProgram shader = RenderSystem.getShader();
					this.cloudsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shader);
				}

				VertexBuffer.unbind();
			}

			matrices.pop();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.enableCull();
			RenderSystem.disableBlend();
		}
	}

	@Inject(
			method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/util/math/MatrixStack;push()V"
			)
	)
	private void fixFoxStart(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double d, double e, double f, CallbackInfo ci) {
		oldFogEnd = RenderSystem.getShaderFogEnd();
		RenderSystem.setShaderFogEnd((viewDistanceModified() * 8) * 12);
	}

	@Inject(
			method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V",
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
