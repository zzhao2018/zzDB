import org.junit.Test;
import storage.ToFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class testStart {
    @Test
    public void firstTest() throws IOException {
        ToFile toFile = new ToFile();
        ConcurrentLinkedQueue list = new ConcurrentLinkedQueue<String>();
        list.add("测试1");
        list.add("测试2");
        list.add("测试3");
        toFile.writeToFile(list);
        System.out.println(Arrays.toString(toFile.readFromFile().toArray()));
    }
}

