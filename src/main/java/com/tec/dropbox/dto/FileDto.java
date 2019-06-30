package com.tec.dropbox.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDto {

    private String name;
    private String path;
    private boolean isDirectory;
    private long size;

}
