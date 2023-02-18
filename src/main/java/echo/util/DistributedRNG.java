package echo.util;

import com.megacrit.cardcrawl.random.Random;

import java.util.HashMap;
import java.util.Map;

// From https://stackoverflow.com/a/20329901/10501401
public class DistributedRNG {

    private final Map<Integer, Float> distribution;
    private float distSum;

    public DistributedRNG() {
        distribution = new HashMap<>();
    }

    public void addNumber(int value, float distribution) {
        if (this.distribution.get(value) != null) {
            distSum -= this.distribution.get(value);
        }
        this.distribution.put(value, distribution);
        distSum += distribution;
    }

    public int getDistributedRandomNumber(Random rng) {
        double rand = rng.random();
        double ratio = 1.0f / distSum;
        double tempDist = 0;
        for (Integer i : distribution.keySet()) {
            tempDist += distribution.get(i);
            if (rand / ratio <= tempDist) {
                return i;
            }
        }
        return 0;
    }
}