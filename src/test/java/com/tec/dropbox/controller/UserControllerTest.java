package com.tec.dropbox.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tec.dropbox.dto.FileDto;
import com.tec.dropbox.dto.UserDto;
import com.tec.dropbox.service.FileService;
import com.tec.dropbox.service.FolderService;
import com.tec.dropbox.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    FolderService folderService;

    @MockBean
    FileService fileService;

    @Test
    public void shouldDoLogin() throws Exception {
        UserDto user = createUserDtoForTesting();
        when(userService.connect(anyString(), anyString(), any())).thenReturn(user);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.name", is("João")));

        verify(userService, times(1)).connect(anyString(), anyString(), any());
    }

    @Test
    public void shouldListFilesInWorkingDirectory() throws Exception {
        List<FileDto> files = createListOfFileDtoForTesting();
        when(folderService.listFilesInWorkDir(eq("userId"))).thenReturn(files);

        mockMvc.perform(get("/users/userId/files"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(folderService, times(1)).listFilesInWorkDir(eq("userId"));
    }

    @Test
    public void shouldEnterInAnyFolder() throws Exception {
        List<FileDto> files = createListOfFileDtoForTesting();
        when(folderService.listFilesInWorkDir(eq("userId"))).thenReturn(files);
        when(folderService.changeWorkDir(eq("userId"), anyString())).thenReturn(true);

        mockMvc.perform(patch("/users/userId/folders/anyFolder"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(folderService, times(1)).changeWorkDir(eq("userId"), anyString());
    }

    @Test
    public void shouldGetBackToPreviousFolder() throws Exception {
        List<FileDto> files = createListOfFileDtoForTesting();
        when(folderService.listFilesInWorkDir(eq("userId"))).thenReturn(files);
        when(folderService.changeToParentDir(eq("userId"))).thenReturn(true);

        mockMvc.perform(patch("/users/userId/folders"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(folderService, times(1)).changeToParentDir(eq("userId"));
    }

    @Test
    public void shouldCreateNewFolderInWorkingDirectory() throws Exception {
        when(folderService.createDir(eq("userId"), anyString())).thenReturn(true);

        mockMvc.perform(post("/users/userId/folders/folderName"))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(folderService, times(1)).createDir(eq("userId"), anyString());
    }

    private List<FileDto> createListOfFileDtoForTesting() {
        FileDto file = new FileDto();
        file.setName("test.txt");
        file.setPath("");
        file.setSize(1);
        file.setDirectory(false);

        List<FileDto> files = new ArrayList<>();
        files.add(file);
        files.add(file);
        return files;
    }

    private UserDto createUserDtoForTesting() {
        UserDto user = new UserDto();
        user.setId("1234abcd");
        user.setName("João");
        user.setPassword("1234");
        return user;
    }
}
