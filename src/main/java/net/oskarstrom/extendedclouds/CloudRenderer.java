package net.oskarstrom.extendedclouds;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CloudRenderer {
	public static final float SCALE = 1 / 256.0f;
	public static final float OFFSET = 1 / 1024.0f;

	private static int color(Vec3d color, float alpha, float darkness) {
		int red = (int) ((color.x * darkness) * 255.0);
		int green = (int) ((color.y * darkness) * 255.0);
		int blue = (int) ((color.z * darkness) * 255.0);
		return ColorHelper.Argb.getArgb((int) (alpha * 255.0), red, green, blue);
	}

	public static void renderClouds(BufferBuilder builder, double xPos, double yPos, double zPos, Vec3d color, float alpha, int renderDistance, boolean fancy) {
		float xOffset = (float) MathHelper.floor(xPos) * SCALE;
		float zOffset = (float) MathHelper.floor(zPos) * SCALE;
		RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
		builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
		float y = (float) Math.floor(yPos / 4.0) * 4.0f;
		if (fancy) {
			for (int chunkX = -(renderDistance - 1); chunkX <= renderDistance; ++chunkX) {
				for (int chunkZ = -(renderDistance - 1); chunkZ <= renderDistance; ++chunkZ) {
					float x = chunkX * 8;
					float z = chunkZ * 8;

					float u1 = (x + 0.0f) * SCALE + xOffset;
					float u2 = (x + 8.0f) * SCALE + xOffset;
					float v1 = (z + 8.0f) * SCALE + zOffset;
					float v2 = (z + 0.0f) * SCALE + zOffset;

					if (y > -5.0f) {
						int argb = color(color, alpha, 0.7f);
						builder.vertex(x + 0.0f, y + 0.0f, z + 8.0f).texture(u1, v1).color(argb).normal(0.0f, -1.0f, 0.0f).next();
						builder.vertex(x + 8.0f, y + 0.0f, z + 8.0f).texture(u2, v1).color(argb).normal(0.0f, -1.0f, 0.0f).next();
						builder.vertex(x + 8.0f, y + 0.0f, z + 0.0f).texture(u2, v2).color(argb).normal(0.0f, -1.0f, 0.0f).next();
						builder.vertex(x + 0.0f, y + 0.0f, z + 0.0f).texture(u1, v2).color(argb).normal(0.0f, -1.0f, 0.0f).next();
					}

					if (y <= 5.0f) {
						int argb = color(color, alpha, 1.0f);
						builder.vertex(x + 0.0f, y + 4.0f - OFFSET, z + 8.0f).texture(u1, v1).color(argb).normal(0.0f, 1.0f, 0.0f).next();
						builder.vertex(x + 8.0f, y + 4.0f - OFFSET, z + 8.0f).texture(u2, v1).color(argb).normal(0.0f, 1.0f, 0.0f).next();
						builder.vertex(x + 8.0f, y + 4.0f - OFFSET, z + 0.0f).texture(u2, v2).color(argb).normal(0.0f, 1.0f, 0.0f).next();
						builder.vertex(x + 0.0f, y + 4.0f - OFFSET, z + 0.0f).texture(u1, v2).color(argb).normal(0.0f, 1.0f, 0.0f).next();
					}

					if (chunkX > -1) {
						int argb = color(color, alpha, 0.9f);
						for (int i = 0; i < 8; ++i) {
							builder.vertex(x + (float) i + 0.0f, y + 0.0f, z + 8.0f).texture((x + (float) i + 0.5f) * SCALE + xOffset, v1).color(argb).normal(-1.0f, 0.0f, 0.0f).next();
							builder.vertex(x + (float) i + 0.0f, y + 4.0f, z + 8.0f).texture((x + (float) i + 0.5f) * SCALE + xOffset, v1).color(argb).normal(-1.0f, 0.0f, 0.0f).next();
							builder.vertex(x + (float) i + 0.0f, y + 4.0f, z + 0.0f).texture((x + (float) i + 0.5f) * SCALE + xOffset, v2).color(argb).normal(-1.0f, 0.0f, 0.0f).next();
							builder.vertex(x + (float) i + 0.0f, y + 0.0f, z + 0.0f).texture((x + (float) i + 0.5f) * SCALE + xOffset, v2).color(argb).normal(-1.0f, 0.0f, 0.0f).next();
						}
					}

					if (chunkX <= 1) {
						int argb = color(color, alpha, 0.9f);
						for (int i = 0; i < 8; ++i) {
							builder.vertex(x + (float) i + 1.0f - OFFSET, y + 0.0f, z + 8.0f).texture((x + (float) i + 0.5f) * SCALE + xOffset, v1).color(argb).normal(1.0f, 0.0f, 0.0f).next();
							builder.vertex(x + (float) i + 1.0f - OFFSET, y + 4.0f, z + 8.0f).texture((x + (float) i + 0.5f) * SCALE + xOffset, v1).color(argb).normal(1.0f, 0.0f, 0.0f).next();
							builder.vertex(x + (float) i + 1.0f - OFFSET, y + 4.0f, z + 0.0f).texture((x + (float) i + 0.5f) * SCALE + xOffset, v2).color(argb).normal(1.0f, 0.0f, 0.0f).next();
							builder.vertex(x + (float) i + 1.0f - OFFSET, y + 0.0f, z + 0.0f).texture((x + (float) i + 0.5f) * SCALE + xOffset, v2).color(argb).normal(1.0f, 0.0f, 0.0f).next();
						}
					}

					if (chunkZ > -1) {
						int argb = color(color, alpha, 0.8f);
						for (int i = 0; i < 8; ++i) {
							builder.vertex(x + 0.0f, y + 4.0f, z + (float) i + 0.0f).texture(u1, (z + (float) i + 0.5f) * SCALE + zOffset).color(argb).normal(0.0f, 0.0f, -1.0f).next();
							builder.vertex(x + 8.0f, y + 4.0f, z + (float) i + 0.0f).texture(u2, (z + (float) i + 0.5f) * SCALE + zOffset).color(argb).normal(0.0f, 0.0f, -1.0f).next();
							builder.vertex(x + 8.0f, y + 0.0f, z + (float) i + 0.0f).texture(u2, (z + (float) i + 0.5f) * SCALE + zOffset).color(argb).normal(0.0f, 0.0f, -1.0f).next();
							builder.vertex(x + 0.0f, y + 0.0f, z + (float) i + 0.0f).texture(u1, (z + (float) i + 0.5f) * SCALE + zOffset).color(argb).normal(0.0f, 0.0f, -1.0f).next();
						}
					}

					if (chunkZ <= 1) {
						int argb = color(color, alpha, 0.8f);
						for (int i = 0; i < 8; ++i) {
							builder.vertex(x + 0.0f, y + 4.0f, z + (float) i + 1.0f - OFFSET).texture(u1, (z + (float) i + 0.5f) * SCALE + zOffset).color(argb).normal(0.0f, 0.0f, 1.0f).next();
							builder.vertex(x + 8.0f, y + 4.0f, z + (float) i + 1.0f - OFFSET).texture(u2, (z + (float) i + 0.5f) * SCALE + zOffset).color(argb).normal(0.0f, 0.0f, 1.0f).next();
							builder.vertex(x + 8.0f, y + 0.0f, z + (float) i + 1.0f - OFFSET).texture(u2, (z + (float) i + 0.5f) * SCALE + zOffset).color(argb).normal(0.0f, 0.0f, 1.0f).next();
							builder.vertex(x + 0.0f, y + 0.0f, z + (float) i + 1.0f - OFFSET).texture(u1, (z + (float) i + 0.5f) * SCALE + zOffset).color(argb).normal(0.0f, 0.0f, 1.0f).next();
						}
					}
				}
			}
		} else {
			int argb = color(color, alpha, 1.0f);
			int dist = (renderDistance * 8);
			for (int ah = -dist; ah < dist; ah += dist) {
				for (int ai = -dist; ai < dist; ai += dist) {
					builder.vertex(ah, 		 y, ai + dist).texture((float) (ah) * SCALE + xOffset, (float) (ai + dist) * SCALE + zOffset).color(argb).normal(0.0f, -1.0f, 0.0f).next();
					builder.vertex(ah + dist, y, ai + dist).texture((float) (ah + dist) * SCALE + xOffset, (float) (ai + dist) * SCALE + zOffset).color(argb).normal(0.0f, -1.0f, 0.0f).next();
					builder.vertex(ah + dist, y, ai		).texture((float) (ah + dist) * SCALE + xOffset, (float) (ai) * SCALE + zOffset).color(argb).normal(0.0f, -1.0f, 0.0f).next();
					builder.vertex(ah,        y, ai		).texture((float) (ah) * SCALE + xOffset, (float) (ai) * SCALE + zOffset).color(argb).normal(0.0f, -1.0f, 0.0f).next();
				}
			}
		}
	}
}
