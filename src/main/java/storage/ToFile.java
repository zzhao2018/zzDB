package storage;

import utils.ConfigLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ToFile {
    BufferedWriter writer;

    public ToFile(String path) throws IOException {
        writer = Files.newBufferedWriter(
                Paths.get(path),
                StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE
        );
    }

    public void writeToFile(String line) {
        try {
            writer.write(line);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(ConcurrentLinkedQueue<String> lines) {
        try {
            writer.write(String.join("\n", lines));
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> readFromFile() {
        List<String> lines = new ArrayList<>();
        try {
            File file = new File(ConfigLoader.getInstance().getDataFilePath());
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }

    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }
}
