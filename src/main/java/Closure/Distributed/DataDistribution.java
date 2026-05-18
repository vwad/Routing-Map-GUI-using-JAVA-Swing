package Closure.Distributed;

import java.util.List;
import java.util.Random;

public class DataDistribution implements Distribution {
    private Random random = new Random();
    private List<Integer> data;

    public DataDistribution(List<Integer> data)
    {
        this.data = data;
    }

    @Override
    public int getRandom() {
        int index = random.nextInt(data.size());
        return data.get(index);
    }
}
