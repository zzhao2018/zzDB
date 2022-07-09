package IndexEngine;

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

public class HashIndex {
    private ConcurrentHashMap<String, Long> kv = new ConcurrentHashMap<>();
    private ToFile toFile = new ToFile();
    private AtomicInteger begin = new AtomicInteger();
    private AtomicInteger length = new AtomicInteger();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private ConcurrentLinkedQueue<String> cache = new ConcurrentLinkedQueue<>();


    public HashIndex() throws IOException {
    }

    public void set(String k, String v) {
        String line = k + "\u0000" + v;
        // 更新索引
        renewIndex(k, line);
        setToFile(line);
        try {
            lock.writeLock().lock();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    // 更新索引
    private void renewIndex(String k, String line) {

    }

    // 落盘
    private void setToFile(String line) {
        // 大于阈值，写入文件，否则写入cache
        cache.add(line);
        if (cache.size() > ConfigLoader.CACHE_SIZE) {
            try {
                this.lock.writeLock().lock();
                toFile.writeToFile(cache);
                cache.clear();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.lock.writeLock().unlock();
            }
        } else {
            cache.add(line);
        }
    }
}
