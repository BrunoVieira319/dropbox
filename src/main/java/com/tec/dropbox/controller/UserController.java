package com.tec.dropbox.controller;

import com.tec.dropbox.dto.FileDto;
import com.tec.dropbox.dto.UserDto;
import com.tec.dropbox.service.FileService;
import com.tec.dropbox.service.FolderService;
import com.tec.dropbox.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private UserService userService;
    private FileService fileService;
    private FolderService folderService;

    @Autowired
    public UserController(UserService userService, FileService fileService, FolderService folderService) {
        this.userService = userService;
        this.fileService = fileService;
        this.folderService = folderService;
    }

    @PostMapping(value = "/signUp")
    public ResponseEntity signUp(@RequestBody UserDto userDto) {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        return restTemplate.postForEntity("http://localhost:8080/users", userDto, String.class);
    }

    @PostMapping(value = "/login")
    public ResponseEntity login(@RequestBody UserDto user) throws IOException {
        UserDto connected = userService.connect(user.getName(), user.getPassword());
        return new ResponseEntity<>(connected, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/folders")
    public ResponseEntity listDir(@PathVariable String id) throws IOException {
        List<FileDto> files = folderService.listFiles(id);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}/folders/{folderName}")
    public ResponseEntity enterDir(@PathVariable String id, @PathVariable String folderName) throws IOException {
        folderService.changeWorkDir(id, folderName);
        List<FileDto> files = folderService.listFiles(id);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}/folders")
    public ResponseEntity getBackDir(@PathVariable String id) throws IOException {
        folderService.changeToParentDir(id);
        List<FileDto> files = folderService.listFiles(id);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/folders/{dirName}")
    public ResponseEntity createDir(@PathVariable String id, @PathVariable String dirName) throws IOException {
        folderService.createDir(id, dirName);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}/files/{filename}")
    public ResponseEntity downloadFile(
            @PathVariable String id,
            @PathVariable String filename) throws IOException {

        File file = fileService.retrieveFile(id, filename);
        String contentType = Files.probeContentType(file.toPath());

        byte[] bytes = Files.readAllBytes(file.toPath());
        file.delete();

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(file.length())
                .body(new ByteArrayResource(bytes));
    }

    @PostMapping(value = "/{id}/files")
    public ResponseEntity uploadFile(@PathVariable String id, @RequestParam("file") MultipartFile file) throws IOException {
        fileService.storeFile(id, file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/files/{filename}")
    public ResponseEntity deleteFile(@PathVariable String id, @PathVariable String filename) throws IOException {
        fileService.deleteFile(id, filename);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
