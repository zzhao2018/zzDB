package storage;

import IndexEngine.LSM.LSMCache;
import org.apache.log4j.Logger;
import utils.ConfigLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 写文件
 */
// TODO 目前明文写入，后续优化反向 - 二进制写入，NIO
public class ToFile {
    private final Logger logger = Logger.getLogger(ToFile.class);
    private final BufferedWriter writer;
    private int buffSize = 0;


    public ToFile(String path) throws IOException {
        writer = Files.newBufferedWriter(
                Paths.get(path),
                StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE
        );
    }

    // 写入文件
    public void writeToFile(String line, boolean imFlush) {
        try {
            this.buffSize++;
            writer.write(line);
            if (imFlush) {
                writer.flush();
            } else if (this.buffSize > 100) {
                writer.flush();
                this.buffSize = 0;
            }
        } catch (IOException e) {
            logger.error("writeToFile error, err: " + e.getMessage());
        }
    }

    public void flush() {
        try {
            this.writer.flush();
        } catch (Exception e) {
            logger.error("flush error, err: " + e.getMessage());
        }
    }

    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }
}
