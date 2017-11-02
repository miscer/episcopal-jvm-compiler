package episcopal.discrete;

import episcopal.Sample;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Discrete sample contains a set of values with assigned probabilities.
 * @param <T> Type of the values
 */
public class DiscreteSample<T> implements Sample {
    private Map<T, Float> values = new HashMap<>();

    /**
     * Creates a discrete sample containing the specified value with 100% probability.
     * @param value Value
     * @param <T> Type of the value
     * @return Discrete sample with the value
     */
    public static <T> DiscreteSample<T> create(T value) {
      DiscreteSample<T> result = new DiscreteSample<>();
      result.add(value, 1);
      return result;
    }

    /**
     * Adds a new value to the sample with the specified probability
     * @param value Value
     * @param probability Probability of the value
     * @return This instance
     */
    public DiscreteSample<T> add(T value, float probability) {
        values.put(value, get(value) + probability);
        return this;
    }

    /**
     * @param value Value
     * @return Returns the probability of the value, or 0% if it is not in the sample
     */
    public float get(T value) {
    return values.getOrDefault(value, 0f);
    }

    /**
     * @return Set of all values in the sample
     */
    public Set<T> values() {
        return values.keySet();
    }

    /**
     * @return Single value in the sample, or null
     */
    public T single() {
        return values.size() == 1 ? values.keySet().iterator().next() : null;
    }

    @Override
    public String toString() {
        String content = values
                .entrySet().stream()
                .map(entry -> String.format("%s %d%%", entry.getKey(), (int)(entry.getValue() * 100)))
                .collect(Collectors.joining("; "));

        return String.format("DiscreteSample{%s}", content);
    }
}
