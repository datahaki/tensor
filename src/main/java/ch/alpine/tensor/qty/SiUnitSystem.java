// code by jph
package ch.alpine.tensor.qty;

import ch.alpine.tensor.io.ResourceData;

/** Initialization On Demand Holder Idiom */
/* package */ enum SiUnitSystem {
  INSTANCE;

  /** Cached field instance. */
  final UnitSystem unitSystem = UnitSystemInflator.of(ResourceData.properties("/ch/alpine/tensor/qty/si.properties"));
}