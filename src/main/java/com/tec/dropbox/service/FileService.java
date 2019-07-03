package com.tec.dropbox.service;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class FileService {

    private FtpService ftpService;

    @Autowired
    public FileService(FtpService ftpService) {
        this.ftpService = ftpService;
    }

    public File retrieveFile(String id, String filename) throws IOException {
        File file = new File(System.getProperty("user.home") + "/ftp-tmp/" + filename);
        ftpService.getClient(id)
                .retrieveFile(filename, new FileOutputStream(file));

        return file;
    }

    public void storeFile(String id, MultipartFile file) throws IOException {
        FTPClient client = ftpService.getClient(id);
        client.storeFile(file.getOriginalFilename(), file.getInputStream());
    }

    public void deleteFile(String id, String filename) throws IOException {
        ftpService.getClient(id).deleteFile(filename);
    }
}
