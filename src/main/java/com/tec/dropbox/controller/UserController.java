package com.tec.dropbox.controller;

import com.tec.dropbox.dto.FileDto;
import com.tec.dropbox.dto.UserDto;
import com.tec.dropbox.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
        List<FileDto> files = userService.listFiles(id);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}/folders/{folderName}")
    public ResponseEntity enterDir(@PathVariable String id, @PathVariable String folderName) throws IOException {
        userService.changeWorkDir(id, folderName);
        List<FileDto> files = userService.listFiles(id);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}/folders")
    public ResponseEntity getBackDir(@PathVariable String id) throws IOException {
        userService.changeToParentDir(id);
        List<FileDto> files = userService.listFiles(id);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/folders/{dirName}")
    public ResponseEntity createDir(@PathVariable String id, @PathVariable String dirName) throws IOException {
        userService.createDir(id, dirName);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}/file/{filename}")
    public ResponseEntity downloadFile(
            @PathVariable String id,
            @PathVariable String filename) throws IOException {

        File file = userService.downloadFile(id, filename);
        String contentType = Files.probeContentType(file.toPath());

        byte[] bytes = Files.readAllBytes(file.toPath());

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(file.length())
                .body(new ByteArrayResource(bytes));
    }

}
