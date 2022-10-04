// code by jph
package ch.alpine.tensor.tmp;

import ch.alpine.tensor.red.Entrywise;

/* package */ enum StaticHelper {
  ;
  public static final Entrywise COPY_SECOND = Entrywise.with((s1, s2) -> s2);
}
