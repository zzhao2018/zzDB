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
    private final BufferedOutputStream writer;


    public ToFile(String path, int buffSize) throws IOException {
        writer = new BufferedOutputStream(new FileOutputStream(path), buffSize);
    }

    // 写入文件
    public void writeToFile(byte[] line, boolean imFlush) {
        try {
            writer.write(line);
            if (imFlush) {
                writer.flush();
            }
        } catch (IOException e) {
            logger.error("writeToFile error, err: " + e.getMessage());
        }
    }

    public void close() throws IOException {
        writer.close();
    }
}
