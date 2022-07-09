import org.junit.Test;
import storage.ToFile;

import java.io.IOException;
import java.util.Arrays;

public class testStart {
    @Test
    public void firstTest() throws IOException {
        ToFile toFile = new ToFile();
//        toFile.writeToFile(new String[]{"测试写入10", "测试写入12", "测试写入31"});
//        toFile.writeToFile(new String[]{"测试写入13", "测试写入14", "测试写入15"});
        System.out.println(Arrays.toString(toFile.readFromFile().toArray()));
    }
}

