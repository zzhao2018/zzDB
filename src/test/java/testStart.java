import IndexEngine.LSM.LSMCache;
import IndexEngine.LSM.LSMEngine;
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
        LSMEngine lsmIndex = LSMEngine.getInstance();
        lsmIndex.set("name", "测试");
        lsmIndex.set("age", "123456789101112");
        lsmIndex.set("addr", "地球");
        lsmIndex.set("city", "太阳");
        lsmIndex.set("name", "新名字");
        lsmIndex.set("sex", "man");
        lsmIndex.set("sex", "woman");
        lsmIndex.set("addr", "太阳系");
        lsmIndex.set("city", "银河系");
        Thread.sleep(5000);
        System.out.println("=================");
        System.out.println("name:" + lsmIndex.get("name"));
        System.out.println("age:" + lsmIndex.get("age"));
        System.out.println("addr:" + lsmIndex.get("addr"));
        System.out.println("city:" + lsmIndex.get("city"));
        System.out.println("sex:" + lsmIndex.get("sex"));
        System.out.println("sex:" + lsmIndex.get("test"));
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

