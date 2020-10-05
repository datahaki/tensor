// code by jph
package ch.ethz.idsc.tensor.opt.nd;

import java.util.Comparator;

import ch.ethz.idsc.tensor.Scalars;

/* package */ enum NdEntryComparators implements Comparator<NdEntry<?>> {
  INCREASING {
    @Override
    public int compare(NdEntry<?> o1, NdEntry<?> o2) {
      return Scalars.compare(o1.distance(), o2.distance());
    }
  },
  DECREASING {
    @Override
    public int compare(NdEntry<?> o1, NdEntry<?> o2) {
      return Scalars.compare(o2.distance(), o1.distance());
    }
  };
}
