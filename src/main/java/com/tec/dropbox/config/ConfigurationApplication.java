package com.tec.dropbox.config;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ConfigurationApplication {

    public ConfigurationApplication() {
        createFolderForDownloadedFiles();
    }

    public void createFolderForDownloadedFiles() {
        Path path = Paths.get(System.getProperty("user.home") + "/ftp-tmp");
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
