package storage;

import utils.ConfigLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FromFile {
    public List<String> readFromFile(String path) {
        ArrayList<String> lines = new ArrayList();
        try {
            File file = new File(path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            br.close();
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return lines;
    }
}
