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
        lsmCache.insertCache("key1", "val1");
        lsmCache.insertCache("key2", "val2");
        lsmCache.insertCache("key3", "val3");
        lsmCache.insertCache("key4", "val4");
        lsmCache.insertCache("key5", "val5");
        lsmCache.insertCache("key2", "val6");
        lsmCache.insertCache("key7", "val7");
        lsmCache.insertCache("key8", "val8");
        lsmCache.insertCache("key9", "val9");

        System.out.println(lsmCache.getVal("key1"));
        System.out.println(lsmCache.getVal("key2"));
        System.out.println(lsmCache.getVal("key4"));
        System.out.println(lsmCache.getVal("key8"));
        System.out.println(lsmCache.getVal("key9"));
    }

    @Test
    public void testLSMIndex() throws IOException {
        LSMIndex lsmIndex = new LSMIndex();
        lsmIndex.set("name", "zzh");
        lsmIndex.set("age", "12");
        lsmIndex.set("addr", "earth");
        lsmIndex.set("city", "sum");
        lsmIndex.set("sex", "man");
        System.out.println(lsmIndex.get("name"));
        System.out.println(lsmIndex.get("age"));
        System.out.println(lsmIndex.get("day"));
        System.out.println(lsmIndex.get("city"));

    }
}

