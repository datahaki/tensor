package ch.alpine.tensor.opt.nd;

import java.util.Collection;

public interface NdCollector<V> extends NdVisitor<V> {
  /** @return */
  Collection<NdMatch<V>> collection();
}
