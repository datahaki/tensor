// code by jph
package ch.ethz.idsc.tensor.opt.nd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Tensor;

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
  public void clear() {
    list.clear();
  }

  @Override // from NdMap
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override // from NdMap
  public Collection<NdEntry<V>> cluster(NdCenterInterface ndCenterInterface, int limit) {
    return list.stream() //
        .map(ndPair -> new NdEntry<>(ndPair.location(), ndPair.value(), ndCenterInterface.ofVector(ndPair.location()))) //
        .sorted(NdEntryComparators.INCREASING) //
        .limit(limit) //
        .collect(Collectors.toCollection(LinkedList::new));
  }
}
