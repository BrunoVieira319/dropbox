package com.tec.dropbox.service;

import com.tec.dropbox.dto.UserDto;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    FtpService ftpService;

    @InjectMocks
    UserService userService;

    @Mock
    FTPClient ftpClient;

    @Test
    public void shouldConnectToServer() throws IOException {
        doNothing().when(ftpClient).connect(anyString(), anyInt());
        when(ftpClient.login(anyString(), anyString())).thenReturn(true);
        doNothing().when(ftpService).addClient(anyString(), any(FTPClient.class));

        UserDto connectedUser = userService.connect("João", "123", ftpClient);

        assertEquals("João", connectedUser.getName());
        assertNotNull(connectedUser.getId());
        assertNull(connectedUser.getPassword());
        verify(ftpService, times(1)).addClient(anyString(), eq(ftpClient));
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionIfAuthenticationFails() throws IOException {
        doNothing().when(ftpClient).connect(anyString(), anyInt());
        when(ftpClient.login(anyString(), anyString())).thenReturn(false);

        userService.connect("João", "123", ftpClient);
    }

}
