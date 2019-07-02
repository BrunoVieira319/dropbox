package com.tec.dropbox.service;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FtpServiceTest {

    @Mock
    FTPClient ftpClient;

    @Test
    public void shouldAddFtpClient() throws IOException {
        when(ftpClient.setFileType(anyInt())).thenReturn(true);
        when(ftpClient.isConnected()).thenReturn(true);

        FtpService ftpService = new FtpService();
        ftpService.addClient("1234", ftpClient);

        assertEquals(ftpClient, ftpService.getClient("1234"));
    }

    @Test(expected = IOException.class)
    public void shouldRemoveClientAndThrowExceptionIfItIsNotConnected() throws IOException {
        when(ftpClient.setFileType(anyInt())).thenReturn(true);
        when(ftpClient.isConnected()).thenReturn(false);

        FtpService ftpService = new FtpService();
        ftpService.addClient("1234", ftpClient);

        ftpService.getClient("1234");
    }
}
