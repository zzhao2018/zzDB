import IndexEngine.LSM.LSMCache;
import IndexEngine.LSM.LSMIndex;
import org.junit.Test;
import storage.ToFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class testStart {
    @Test
    public void testCache() throws IOException {
        LSMCache lsmCache = new LSMCache();
        lsmCache.insertCache("key1", 1l);
        lsmCache.insertCache("key2", 2l);
        lsmCache.insertCache("key3", 3l);
        lsmCache.insertCache("key4", 4l);
        lsmCache.insertCache("key5", 5l);
        System.out.println(lsmCache.getActiveCache());
        while (lsmCache.immuCacheNum() > 0) {
            System.out.println(lsmCache.getCache());
        }
    }

    @Test
    public void testLSMIndex() throws IOException {
        LSMIndex lsmIndex = new LSMIndex();
        lsmIndex.set("name", "zzh");
        lsmIndex.set("age", "12");
        lsmIndex.set("addr", "earth");
        lsmIndex.set("city", "sum");
        lsmIndex.set("sex", "man");
        lsmIndex.printf();
    }
}

