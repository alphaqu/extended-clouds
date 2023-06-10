package dev.notalpha.extendedclouds;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public class ExtendedCloudsExpectedPlatform {
	@ExpectPlatform
	public static Path getConfigDirectory() {
		throw new AssertionError();
	}
}
