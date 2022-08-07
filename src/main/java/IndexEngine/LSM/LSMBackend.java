package IndexEngine.LSM;

import lombok.SneakyThrows;
import storage.ToFile;
import utils.ConfigLoader;

import java.io.*;
import java.util.TreeMap;

/**
 * 一个节点只能开一个后台进程，后续优化
 */
public class LSMBackend implements Runnable {
    private final LSMCache lsmCache;
    private final Blocks<Long> blocks;   //TODO 后续写zk

    public LSMBackend(LSMCache lsmCache, Blocks<Long> blocks) {
        this.lsmCache = lsmCache;
        this.blocks = blocks;
    }


    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            TreeMap<String, String> node = this.lsmCache.peekCache();
            if (node != null) {
                // 初始化
                long timeNow = System.currentTimeMillis();
                ToFile[] files = initFile(timeNow);
                // 刷数据
                writeToFile(files, node, ConfigLoader.getInstance().getInterval());
                // 删除节点
                blocks.put(timeNow);
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
    private void writeToFile(ToFile[] files, TreeMap<String, String> node, int interval) throws IOException {
        String line, index;
        int lineLen, offset = 0;
        ToFile dataFile = files[0];
        ToFile indexFile = files[1];
        for (String key : node.keySet()) {
            // 构造line
            line = node.get(key);
            lineLen = line.getBytes().length;
            line = String.format("%5d\u0001%s", lineLen, line);
            byte[] lineBytes = line.getBytes();
            dataFile.writeToFile(lineBytes, false);
            // 计算出offset后，构造index
            index = (key + "\u0000" + offset + "\n");
            indexFile.writeToFile(index.getBytes(), false);
            // 更新数据
            offset = offset + lineBytes.length;
        }
        indexFile.close();
        dataFile.close();
    }

    // 初始化
    private ToFile[] initFile(long block) throws IOException {
        String dataPath = ConfigLoader.getInstance().getDataFilePath() + "." + block;
        String indexPath = ConfigLoader.getInstance().getIndexFilePath() + "." + block;
        ToFile[] files = new ToFile[2];
        files[0] = new ToFile(dataPath, 1024);
        files[1] = new ToFile(indexPath, 1024);
        return files;
    }
}