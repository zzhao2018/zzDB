import IndexEngine.LSM.LSMCache;
import IndexEngine.LSM.LSMIndex;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

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
    public void testLSMIndex() throws InterruptedException {
        LSMIndex lsmIndex = LSMIndex.getInstance();
        lsmIndex.set("name", "zzh");
        lsmIndex.set("age", "12");
        lsmIndex.set("addr", "earth");
        lsmIndex.set("city", "sum");
        lsmIndex.set("sex", "man");
        System.out.println(lsmIndex.get("name"));
        System.out.println(lsmIndex.get("age"));
        System.out.println(lsmIndex.get("day"));
        System.out.println(lsmIndex.get("city"));
        System.out.println(lsmIndex.get("sex"));
        Thread.sleep(5000);
        System.out.println("=================");
        System.out.println(lsmIndex.get("name"));
        System.out.println(lsmIndex.get("age"));
        System.out.println(lsmIndex.get("day"));
        System.out.println(lsmIndex.get("city"));
        System.out.println(lsmIndex.get("sex"));
    }

    @Test
    public void testLSMThread() {
        TreeMap<String, String> nodes = new TreeMap<>();
        nodes.put("a", "1");
        nodes.put("c", "3");
        nodes.put("k", "2");
        nodes.put("z", "9");
        nodes.put("b", "2");
        nodes.put("d", "4");
        for (Map.Entry<String, String> data : nodes.entrySet()) {
            System.out.println(data.getKey());
//            System.out.println(data.getValue());
        }
    }


}

