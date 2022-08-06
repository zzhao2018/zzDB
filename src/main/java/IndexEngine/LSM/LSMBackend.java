package IndexEngine.LSM;

import lombok.SneakyThrows;
import storage.ToFile;
import utils.ConfigLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * 一个节点只能开一个后台进程，后续优化
 */
public class LSMBackend implements Runnable {
    private final LSMCache lsmCache;
    private final List<Long> blocks = new ArrayList<>();   //TODO 后续写zk
    private final LRUCache<String, BlockOffsetPair> lruCache = new LRUCache<>(
            ConfigLoader.getInstance().getIndexLruCacheSize(),
            ConfigLoader.getInstance().getIndexLruCacheLoadFactor()
    );

    public LSMBackend(LSMCache lsmCache) throws IOException {
        this.lsmCache = lsmCache;
    }


    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            TreeMap<String, String> node = this.lsmCache.peekCache();
            if (node != null) {
                // 刷数据
                writeToFile(node, ConfigLoader.getInstance().getInterval());
                // 删除节点
                this.lsmCache.popCache();
            }
            Thread.sleep(ConfigLoader.getInstance().getFlushDiskInterval());
        }
    }

    /**
     * @param node     节点
     * @param interval 间隔距离，索引可以是稀疏矩阵
     */
    // TODO 目前是所有key均保存磁盘，后续优化为稀疏数组
    private void writeToFile(TreeMap<String, String> node, int interval) throws IOException {
        String line, index;
        int lineLen, offset = 0;
        ToFile[] files = initFile();
        ToFile dataFile = files[0];
        ToFile indexFile = files[1];
        for (String key : node.keySet()) {
            // 构造line
            line = key + "\u0000" + node.get(key);
            lineLen = line.length();
            lineLen = String.valueOf(lineLen).length() + lineLen;
            line = String.format("%d\u0000%s", lineLen, line);
            dataFile.writeToFile(line, false);
            // 计算出offset后，构造index
            index = key + "\u0000" + offset + "\n";
            indexFile.writeToFile(index, true);
            // 更新数据
            offset = offset + lineLen;
        }
        indexFile.flush();
        dataFile.flush();
        indexFile.close();
        dataFile.close();
    }

    // 初始化
    private ToFile[] initFile() throws IOException {
        long timeNow = System.currentTimeMillis();
        blocks.add(timeNow);
        String dataPath = ConfigLoader.getInstance().getDataFilePath() + "." + timeNow;
        String indexPath = ConfigLoader.getInstance().getIndexFilePath() + "." + timeNow;
        ToFile[] files = new ToFile[2];
        files[0] = new ToFile(dataPath);
        files[1] = new ToFile(indexPath);
        return files;
    }

    /**
     * 获取key对应的val
     *
     * @param key
     * @return
     * @throws IOException
     */
    public String get(String key) throws IOException {
        // 1. 获取offset
        BlockOffsetPair pair = getOffset(key);
        // 2. 查询文件
        return checkFile(pair);
    }

    /**
     * 根据index获取原始数据
     *
     * @param pair
     * @return
     */
    private String checkFile(BlockOffsetPair pair) {
        return null;
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
            File file = new File(ConfigLoader.getInstance().getDataFilePath() + "." + block);
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