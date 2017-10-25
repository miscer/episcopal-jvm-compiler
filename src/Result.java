import java.util.*;
import java.util.stream.Collectors;

public class Result<T> {
    private Map<T, Float> values = new HashMap<>();

    public static <T> Result<T> create(T value) {
      Result<T> result = new Result<>();
      result.add(value, 1);
      return result;
    }

    public Result<T> add(T value, float probability) {
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

        return String.format("Result{%s}", content);
    }
}
