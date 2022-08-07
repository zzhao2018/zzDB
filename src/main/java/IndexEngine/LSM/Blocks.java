package IndexEngine.LSM;

import java.util.ArrayList;
import java.util.List;

public class Blocks<T> {
    private final List<T> datas = new ArrayList<>();

    public synchronized T get(int loc) {
        return datas.get(loc);
    }

    public synchronized void put(T data) {
        this.datas.add(data);
    }

    public synchronized int size() {
        return this.datas.size();
    }

    @Override
    public String toString() {
        return "Blocks{" +
                "datas=" + datas +
                '}';
    }
}
