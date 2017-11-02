package episcopal;

import episcopal.discrete.DiscreteSample;

public class Observations {
    /**
     * Randomly selects one value from the sample. The probabilities of the values are considered when selecting a value.
     * @param sample Sample containing values to be selected
     * @param <T> Type of the discrete sample values
     * @return Selected value
     */
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
