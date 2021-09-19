// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.ArrayList;
import java.util.List;

import ch.alpine.tensor.Tensor;

/** class for verification of {@link NdTreeMap} */
public class NdListMap<V> implements NdMap<V> {
  private final List<NdPair<V>> list = new ArrayList<>();

  @Override // from NdMap
  public void add(Tensor location, V value) {
    list.add(new NdPair<>(location, value));
  }

  @Override // from NdMap
  public int size() {
    return list.size();
  }

  @Override // from NdMap
  public boolean isEmpty() {
    return list.isEmpty();
  }

  @Override
  public void visit(NdVisitor<V> ndVisitor) {
    list.stream().forEach(ndVisitor::consider);
  }
}
