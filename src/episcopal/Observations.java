package episcopal;

import episcopal.discrete.DiscreteSample;

public class Observations {
    public static <T> T select(DiscreteSample<T> sample) {
        double targetWeight = Math.random();
        double totalWeight = 0;

        for (T value : sample.values()) {
            totalWeight += sample.get(value);

            if (totalWeight >= targetWeight) {
                return value;
            }
        }

        throw new RuntimeException("Failed to select a sample from distribution");
    }
}
