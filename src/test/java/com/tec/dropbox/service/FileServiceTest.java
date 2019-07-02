package com.tec.dropbox.service;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileServiceTest {

    @Mock
    FtpService ftpService;

    @InjectMocks
    FileService fileService;

    @Mock
    FTPClient ftpClient;

    @Test
    public void shouldRetrieveFile() throws IOException {
        when(ftpService.getClient(anyString())).thenReturn(ftpClient);
        when(ftpClient.retrieveFile(anyString(), any(OutputStream.class))).thenReturn(true);

        File file = fileService.retrieveFile("1234", "test.txt");

        assertEquals("test.txt", file.getName());
        assertTrue(file.isFile());

        file.delete();
    }

}
