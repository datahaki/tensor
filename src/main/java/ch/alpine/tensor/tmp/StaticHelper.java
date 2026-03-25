// code by jph
package ch.alpine.tensor.tmp;

import ch.alpine.tensor.red.Entrywise;

enum StaticHelper {
  ;
  public static final Entrywise COPY_SECOND = Entrywise.with((_, s2) -> s2);
}
