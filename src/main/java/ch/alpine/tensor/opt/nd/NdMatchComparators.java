// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.Comparator;

import ch.alpine.tensor.Scalars;

/* package */ enum NdMatchComparators implements Comparator<NdMatch<?>> {
  // INCREASING {
  // @Override
  // public int compare(NdMatch<?> o1, NdMatch<?> o2) {
  // return Scalars.compare(o1.distance(), o2.distance());
  // }
  // },
  DECREASING {
    @Override
    public int compare(NdMatch<?> o1, NdMatch<?> o2) {
      return Scalars.compare(o2.distance(), o1.distance());
    }
  };
}
