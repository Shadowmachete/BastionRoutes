package com.shadowmachete.bastionroutes.utils;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileUtils {
    public static String readFile(Path file) throws IOException {
        loadDefaultIfNotPresent(file);

        byte[] bytes = Files.readAllBytes(file);
        return new String(bytes);
    }

    public static void writeFile(Path file, String content) throws IOException {
        createFileIfNotPresent(file, "");

        Files.write(file, content.getBytes());
    }

    public static void createFileIfNotPresent(Path file, String emptyFileContent) throws IOException {
        Path parent = file.getParent();
        if (parent != null) Files.createDirectories(parent);

        try {
            Files.createFile(file);
            Files.write(file, emptyFileContent.getBytes());
        } catch (FileAlreadyExistsException ignored) {
        }
    }

    public static void loadDefaultIfNotPresent(Path file) throws IOException {
        if (!Files.exists(file)) {
            Path parent = file.getParent();
            if (parent != null) Files.createDirectories(parent);

            try {
                Files.copy(
                        Objects.requireNonNull(FileUtils.class.getResourceAsStream("/config/default_routes.json")),
                        file
                );
            } catch (FileAlreadyExistsException ignored) {
            }
        }
    }

    public static void createDefaultPath() throws IOException {
        Files.createDirectories(getDefaultPath());
    }

    public static Path getDefaultPath() {
        return FabricLoader.getInstance().getGameDir();
    }

    public static Path getSavePath() {
        return getDefaultPath().resolve("config");
    }
}
