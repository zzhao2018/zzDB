package IndexEngine.LSM;

import org.apache.log4j.Logger;
import storage.ToFile;
import utils.ConfigLoader;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LSMIndex {
    private final Logger logger = Logger.getLogger(LSMCache.class);

    private final ToFile wal = new ToFile(ConfigLoader.getInstance().getWalFilePath());                          // 预写式日志
    private final LSMCache cache = new LSMCache();                    // 缓存
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();


    public LSMIndex() throws IOException {
    }

    public void set(String k, String v) {
        String line = k + "\u0000" + v;
        long lineOffset;
        // 更新预写日志
        try {
            lock.writeLock().lock();
            line = String.format("%s\n", line);
            wal.writeToFile(line, true);
        } catch (Exception e) {
            logger.error("LSMIndex set error, err: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }
        // 更新索引
        logger.debug(String.format("get key:%s", k));
        cache.insertCache(k, v);
    }

    public String get(String key) {
        String val = this.cache.getVal(key);
        if (val != null) {
            return val;
        } else {
            // todo 从文件中获取

        }
        return null;
    }
}
