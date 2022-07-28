package IndexEngine.LSM;

import storage.ToFile;
import utils.ConfigLoader;

import java.io.IOException;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 一个节点只能开一个后台进程，后续优化
 */
public class LSMBackend implements Runnable {
    private final LSMCache lsmCache;
    private final ToFile dataFile = new ToFile(ConfigLoader.getInstance().getDataFilePath());
    private final ToFile indexFile = new ToFile(ConfigLoader.getInstance().getIndexFilePath());


    public LSMBackend(LSMCache lsmCache) throws IOException {
        this.lsmCache = lsmCache;
    }


    @Override
    public void run() {
        TreeMap<String, String> node = this.lsmCache.peekCache();
        if (node != null) {
            // 刷数据
            writeToFile(node, ConfigLoader.getInstance().getInterval());
            // 删除节点
            this.lsmCache.popCache();
        }
    }

    /**
     * @param node     节点
     * @param interval 间隔距离，索引可以是稀疏矩阵
     */
    private void writeToFile(TreeMap<String, String> node, int interval) {
        String line;
        int lineLen;
        String index;
        int offset = 0;
        int inter_interval = 0;
        for (String key : node.keySet()) {
            // 构造line
            line = key + "\u0000" + node.get(key);
            lineLen = line.length();
            lineLen = String.valueOf(lineLen).length() + lineLen;
            line = String.format("%d\u0000%s", lineLen, line);
            this.dataFile.writeToFile(line, false);
            // 计算出offset后，构造index
            if (inter_interval % interval == 0) {
                index = key + "\u0000" + offset + "\n";
                this.indexFile.writeToFile(index, true);
            }
            // 更新数据
            inter_interval++;
            offset = offset + lineLen;
        }
        this.indexFile.flush();
        this.dataFile.flush();
    }
}