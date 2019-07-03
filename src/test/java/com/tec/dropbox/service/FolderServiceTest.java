package com.tec.dropbox.service;

import com.tec.dropbox.dto.FileDto;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FolderServiceTest {

    @Mock
    FtpService ftpService;

    @InjectMocks
    FolderService folderService;

    @Mock
    FTPClient ftpClient;

    public FTPFile[] createFiles() {
        FTPFile[] files = new FTPFile[3];

        IntStream.range(0, 3).forEach(i -> {
                    FTPFile file = new FTPFile();
                    file.setName("file:" + i);
                    file.setSize(i);
                    file.setType(0);

                    files[i] = file;
                }
        );
        return files;
    }

    @Test
    public void shouldListFilesInCurrentFolder() throws IOException {
        when(ftpService.getClient(anyString())).thenReturn(ftpClient);
        when(ftpClient.listFiles()).thenReturn(createFiles());

        List<FileDto> files = folderService.listFilesInWorkDir("1234abcd");
        assertEquals(3, files.size());

        FileDto firstFile = files.get(0);
        assertEquals("file:0", firstFile.getName());
        assertEquals(0, firstFile.getSize());
        assertFalse(firstFile.isDirectory());
        assertEquals("", firstFile.getPath());

        verify(ftpService, times(1)).getClient(anyString());
        verify(ftpClient, times(1)).listFiles();
    }

    @Test
    public void shouldCreateNewFolder() throws IOException {
        when(ftpService.getClient(anyString())).thenReturn(ftpClient);
        when(ftpClient.makeDirectory(eq("testFolder"))).thenReturn(true);

        boolean result = folderService.createDir("1234abcd", "testFolder");

        assertTrue(result);
        verify(ftpService, times(1)).getClient(anyString());
        verify(ftpClient, times(1)).makeDirectory(anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateFolderWithName_root_() throws IOException {
        folderService.createDir("1234abcd", "root");
    }

    @Test
    public void shouldEnterInFolder() throws IOException {
        when(ftpService.getClient(anyString())).thenReturn(ftpClient);
        when(ftpClient.changeWorkingDirectory(eq("/testFolder"))).thenReturn(true);

        boolean result = folderService.changeWorkDir("1234abcd", "testFolder");

        assertTrue(result);
        verify(ftpService, times(1)).getClient(anyString());
        verify(ftpClient, times(1)).changeWorkingDirectory(anyString());
    }

    @Test
    public void shouldGetBackToPreviousFolder() throws IOException {
        when(ftpService.getClient(anyString())).thenReturn(ftpClient);
        when(ftpClient.changeToParentDirectory()).thenReturn(true);

        boolean result = folderService.changeToParentDir("1234abcd");

        assertTrue(result);
        verify(ftpService, times(1)).getClient(anyString());
        verify(ftpClient, times(1)).changeToParentDirectory();
    }
}
