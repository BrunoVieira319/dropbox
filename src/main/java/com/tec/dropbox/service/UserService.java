package com.tec.dropbox.service;

import com.tec.dropbox.dto.UserDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class UserService {

    private FtpService ftpService;

    @Autowired
    public UserService(FtpService ftpService) {
        this.ftpService = ftpService;
    }

    public UserDto connect(String username, String password) throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect("localhost", 2221);
        boolean login = ftpClient.login(username, password);

        if (login) {
            String id = RandomStringUtils.random(20, true, true);
            ftpService.addClient(id, ftpClient);

            UserDto user = new UserDto();
            user.setId(id);
            user.setName(username);
            return user;
        }
        throw new IOException("Não foi possível se conectar ao servidor");
    }

}
