package com.tec.dropbox.service;

import com.tec.dropbox.dto.FileDto;
import com.tec.dropbox.dto.UserDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
        ftpClient.login(username, password);

        String id = RandomStringUtils.random(20, true, true);
        ftpService.addClient(id, ftpClient);

        UserDto user = new UserDto();
        user.setId(id);
        user.setName(username);
        return user;
    }

    public List<FileDto> listFiles(String id) throws IOException {
        List<FileDto> files = new LinkedList<>();
        FTPFile[] ftpFiles = ftpService.getClient(id).listFiles();
        Arrays.stream(ftpFiles)
                .forEach(f -> {
                    FileDto file = new FileDto();
                    file.setName(f.getName());
                    file.setPath("");
                    file.setSize(f.getSize());
                    file.setDirectory(f.isDirectory());

                    files.add(file);
                });
        return files;
    }

    public File downloadFile(String id, String filename) throws IOException {
        File file = new File(System.getProperty("user.home") + "/ftp-temp/" + filename);
        FileOutputStream out = new FileOutputStream(file);
        ftpService.getClient(id).retrieveFile(filename, out);

        return file;
//        byte[] bytes = Files.readAllBytes(fileToDownload.toPath());
//        return new ByteArrayResource(bytes);
    }

    public void createDir(String id, String dirName) throws IOException {
        if (dirName == "root") {
            throw new IllegalArgumentException("Não é possível criar uma pasta com nome root");
        }
        ftpService.getClient(id).makeDirectory(dirName);
    }

    public void changeWorkDir(String id, String dirName) throws IOException {
        ftpService.getClient(id).changeWorkingDirectory("/" + dirName);
    }

    public void changeToParentDir(String id) throws IOException {
        ftpService.getClient(id).changeToParentDirectory();
    }
}
