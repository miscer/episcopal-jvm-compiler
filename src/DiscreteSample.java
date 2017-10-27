import java.util.*;
import java.util.stream.Collectors;

public class DiscreteSample<T> {
    private Map<T, Float> values = new HashMap<>();

    public static <T> DiscreteSample<T> create(T value) {
      DiscreteSample<T> result = new DiscreteSample<>();
      result.add(value, 1);
      return result;
    }

    public DiscreteSample<T> add(T value, float probability) {
    values.put(value, get(value) + probability);
    return this;
    }

    public float get(T value) {
    return values.getOrDefault(value, 0f);
    }

    public Set<T> values() {
      return values.keySet();
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
