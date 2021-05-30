// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

  @Override // from NdMap
  public Collection<NdMatch<V>> cluster(NdCenterInterface ndCenterInterface, int limit) {
    return list.stream() //
        .map(ndPair -> new NdMatch<>(ndPair.location(), ndPair.value(), ndCenterInterface.distance(ndPair.location()))) //
        .sorted(NdMatchComparators.INCREASING) //
        .limit(limit) //
        .collect(Collectors.toCollection(LinkedList::new));
  }

  @Override
  public Collection<NdMatch<V>> cluster(NdCluster<V> ndCluster) {
    throw new UnsupportedOperationException();
  }
}
