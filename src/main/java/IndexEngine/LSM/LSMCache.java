package IndexEngine.LSM;

import org.apache.log4j.Logger;
import utils.ConfigLoader;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * LSMCache lsm树缓存
 * 接收写请求时，会写到
 */
public class LSMCache {
    private Logger logger = Logger.getLogger(LSMCache.class);

    private TreeMap<String, String> activeMemtable = new TreeMap<>();
    private ConcurrentLinkedDeque<TreeMap<String, String>> immuMemtables = new ConcurrentLinkedDeque<>();
    private ReentrantReadWriteLock activeLock = new ReentrantReadWriteLock();
    private long cnt = 0;

    // 接收写请求，线程安全
    public void insertCache(String key, String val) {
        try {
            activeLock.writeLock().lock();
            activeMemtable.put(key, val);
            cnt++;
            if (cnt >= ConfigLoader.getInstance().getCacheSize()) {
                immuMemtables.offer(activeMemtable);
                activeMemtable = new TreeMap<>();
                cnt = 0;
            }
        } catch (Exception e) {
            logger.error("LSMCache insertCache error, err: " + e.getMessage());
        } finally {
            activeLock.writeLock().unlock();
        }
    }

    // 查cache流程
    public String getVal(String key) {
        String val;
        try {
            activeLock.readLock().lock();
            val = this.activeMemtable.get(key);
            if (val != null) {
                return val;
            }
        } finally {
            activeLock.readLock().unlock();
        }
        Iterator<TreeMap<String, String>> iterator = this.immuMemtables.descendingIterator();
        while (iterator.hasNext()) {
            TreeMap<String, String> node = iterator.next();
            val = node.get(key);
            if (val != null) {
                return val;
            }
        }
        return null;
    }

    // 后台拉取full cache写入
    public TreeMap<String, String> getCache() {
        return immuMemtables.poll();
    }

    // 获取full cache数量
    public int immuCacheNum() {
        return immuMemtables.size();
    }
}
