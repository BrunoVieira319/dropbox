package com.tec.dropbox.service;

import com.tec.dropbox.dto.FileDto;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class FolderService {

    private FtpService ftpService;

    @Autowired
    public FolderService(FtpService ftpService) {
        this.ftpService = ftpService;
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
