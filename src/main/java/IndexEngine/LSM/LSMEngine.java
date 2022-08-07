package IndexEngine.LSM;

import org.apache.log4j.Logger;
import storage.ToFile;
import utils.ConfigLoader;

import java.io.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LSMEngine {
    private final Logger logger = Logger.getLogger(LSMCache.class);
    private static LSMEngine lsmEngine = null;
    private final ToFile wal = new ToFile(ConfigLoader.getInstance().getWalFilePath(), 1024);                          // 预写式日志
    private final LSMCache cache = new LSMCache();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Blocks<Long> blocks = new Blocks<>();   //TODO 后续写zk
    private final LRUCache<String, BlockOffsetPair> lruCache = new LRUCache<>(
            ConfigLoader.getInstance().getIndexLruCacheSize(),
            ConfigLoader.getInstance().getIndexLruCacheLoadFactor()
    );

    static {
        try {
            lsmEngine = new LSMEngine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LSMEngine getInstance() {
        return lsmEngine;
    }


    private LSMEngine() throws IOException {
        // 开启刷cache后台线程
        LSMBackend lsmBackend = new LSMBackend(cache, blocks);
        Thread thread = new Thread(lsmBackend);
        thread.start();
    }

    /**
     * TODO 1.优化\n问题  2.优化锁粒度为行锁
     *
     * @param k 键
     * @param v 值
     */
    public void set(String k, String v) {
        String line = k + "\u0000" + v + "\n";
        // 更新预写日志
        try {
            lock.writeLock().lock();
            wal.writeToFile(line.getBytes(), true);
        } catch (Exception e) {
            logger.error("lsmEngine set error, err: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }
        // 更新索引
        logger.debug(String.format("get key:%s", k));
        cache.insertCache(k, v);
    }

    public String get(String key) {
        try {
            String val = this.cache.getVal(key);
            if (val != null) {
                return val;
            } else {
                return getFromFile(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("get key error, err: " + e.getMessage());
        }
        return null;
    }

    // TODO 删除
    public void delete(String key) {

    }


    /**
     * 从文件中获取数据，
     *
     * @param key
     * @return
     * @throws IOException
     */
    public synchronized String getFromFile(String key) throws IOException {
        // 1. 获取offset
        BlockOffsetPair pair = getOffset(key);
        if (pair == null) {
            return null;
        }
        // 2. 查询文件
        return checkFile(pair);
    }

    /**
     * 根据index获取原始数据
     *
     * @param pair
     * @return
     */
    private String checkFile(BlockOffsetPair pair) throws IOException {
        File file = new File(ConfigLoader.getInstance().getDataFilePath() + "." + pair.getBlock());
        try (RandomAccessFile br = new RandomAccessFile(file, "r")) {
            // 获取长度
            byte[] rowLenByt = new byte[10];
            br.seek(pair.getOffset().intValue());
            br.read(rowLenByt, 0, 5);
            int colLen = Integer.parseInt(new String(rowLenByt).trim());
            // 获取内容
            byte[] data = new byte[colLen + 1];
            br.seek(pair.getOffset().intValue() + 6);
            br.read(data, 0, colLen);
            return new String(data);
        }
    }


    /**
     * 获取偏移量
     *
     * @param key
     * @return
     * @throws IOException
     */
    private BlockOffsetPair getOffset(String key) throws IOException {
        BlockOffsetPair pair = this.lruCache.get(key);
        if (pair == null) {
            // 加载文件
            pair = loadFromIndexFile(key);
        }
        return pair;
    }

    /**
     * 按顺序获取key对应的offset
     *
     * @param key
     * @return
     * @throws IOException
     */
    private BlockOffsetPair loadFromIndexFile(String key) throws IOException {
        // 1. 逆序遍历indexs，加载文件
        int size = this.blocks.size();
        for (int index = size - 1; index >= 0; index--) {
            Long block = blocks.get(index);
            if (block == null) {
                return null;
            }
            // 2. 遍历文件每一行，判断cache是否存在，不存在更新
            File file = new File(ConfigLoader.getInstance().getIndexFilePath() + "." + block);
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] pairs = line.trim().split("\u0000");
                    String pairsKey = pairs[0];
                    if (this.lruCache.get(pairsKey) == null) {
                        Long pairsOffset = Long.parseLong(pairs[1]);
                        BlockOffsetPair pair = new BlockOffsetPair(pairsOffset, block);
                        this.lruCache.set(pairsKey, pair);
                        // 3. 判断key是否匹配，匹配返回
                        if (pairsKey.equals(key)) {
                            return pair;
                        }
                    }
                }
            }
        }
        return null;
    }

}
