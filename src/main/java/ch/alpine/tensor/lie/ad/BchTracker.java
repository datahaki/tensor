package ch.alpine.tensor.lie.ad;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.TensorComparator;

/* package */ class BchTracker {
  private final Map<Tensor, List<String>> map = new HashMap<>();

  public void add(Tensor key, String value) {
    int cmp = TensorComparator.INSTANCE.compare(key.negate(), key);
    if (cmp < 0)
      key = key.negate();
    if (!map.containsKey(key))
      map.put(key, new LinkedList<>());
    map.get(key).add(value);
  }

  public void dup() {
    System.out.println("---");
    map.values().stream() //
        .filter(list -> 1 < list.size()) //
        .forEach(l -> System.out.println(l));
  }
}
