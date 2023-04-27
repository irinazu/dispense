package com.example.dispense.services;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class DoPartsEntity {
    private HttpEntity<ByteArrayResource> partsEntity;

    public HttpEntity<ByteArrayResource> getPartsEntity(){
        return partsEntity;
    }

    public void generateFileWithJar(String path) {
        File file=new File(path);
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpHeaders parts = new HttpHeaders();
        parts.setContentType(MediaType.MULTIPART_FORM_DATA);
        final ByteArrayResource byteArrayResource = new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return file.getName();
            }
        };
        partsEntity = new HttpEntity<>(byteArrayResource, parts);
    }
}
