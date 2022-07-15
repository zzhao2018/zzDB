package IndexEngine.LSM;

import org.apache.log4j.Logger;
import storage.ToFile;
import utils.ConfigLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LSMIndex {
    private ConcurrentHashMap<String, Long> kv = new ConcurrentHashMap<>();
    private ToFile wal = new ToFile();                          // 预写式日志
    private LSMCache cache = new LSMCache();                    // 缓存
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Logger logger = Logger.getLogger(LSMCache.class);
    private long length = 0;


    public LSMIndex() throws IOException {
    }

    public void set(String k, String v) {
        String line = k + "\u0000" + v;
        // 更新预写日志
        try {
            lock.writeLock().lock();

        } catch (Exception e) {
            logger.error("LSMIndex set error, err: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }
        long offset = 0;
        // 更新索引
        cache.insertCache(k, offset);

    }

    // 更新索引
    private void renewIndex(String k, String line) {

    }
}
