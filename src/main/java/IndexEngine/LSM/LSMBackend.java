package IndexEngine.LSM;

import storage.ToFile;
import utils.ConfigLoader;

import java.io.IOException;
import java.util.TreeMap;

public class LSMBackend implements Runnable {
    private final LSMCache lsmCache;
    private final ToFile dataFile = new ToFile(ConfigLoader.getInstance().getDataFilePath());
    private final ToFile indexFile = new ToFile(ConfigLoader.getInstance().getIndexFilePath());


    public LSMBackend(LSMCache lsmCache) throws IOException {
        this.lsmCache = lsmCache;
    }


    @Override
    public void run() {
        TreeMap<String, String> node = this.lsmCache.getCache();
        if (node != null) {

        }

    }
}
