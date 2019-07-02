package com.tec.dropbox.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigurationApplicationTest {

    @Test
    public void shouldCreateFolderForDownloadingFiles() throws IOException {
        new ConfigurationApplication();
        Path folderPath = Paths.get(System.getProperty("user.home") + "/ftp-tmp");
        assertTrue(Files.exists(folderPath));

        try {
            Files.delete(folderPath);
        } catch (DirectoryNotEmptyException e) {
            System.out.println(String.format("Does not be possible delete folder %s, it is not empty", folderPath));
        }
    }

}
