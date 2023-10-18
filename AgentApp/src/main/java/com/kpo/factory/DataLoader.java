package com.kpo.factory;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public interface DataLoader<T> {
     default JSONObject getJSON(String pathToFile) {
        File file;
        JSONObject data;
        try {
            file = getFile(pathToFile);
            String line = new String(Files.readAllBytes(file.toPath()));
            line = line.replace("\t", "").replace("\n", "");
            data = line.isEmpty() ? new JSONObject() : new JSONObject(line);
        } catch (RuntimeException | IOException e) {
            System.out.printf("Error %s in %s file. Not correct JSON format: %s\n", e, pathToFile, e.getMessage());
            return null;
        }
        return data;
    }

    default File getFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("File not found");
        }
        return file;
    }

    Map<Integer, T> load();
}
