package com.tec.dropbox.service;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

@Service
public class FtpService {

    private Map<String, FTPClient> clients;

    public FtpService() {
        this.clients = new HashMap<>();
    }

    public void addClient(String id, FTPClient client) throws IOException {
        client.setFileType(FTP.BINARY_FILE_TYPE);
        clients.put(id, client);
    }

    public FTPClient getClient(String id) throws IOException {
        FTPClient client = clients.get(id);
        if (client.isConnected()) {
            return client;
        }
        clients.remove(id);
        throw new IOException("Usuário desconectado");
    }

}
