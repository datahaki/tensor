// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.sca.Chop;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Tolerance.html">Tolerance</a> */
public enum Tolerance {
  ;
  /** default threshold below which to consider:
   * a singular value as zero,
   * values equal to determine symmetry,
   * etc. */
  public static final Chop CHOP = Chop._12;
}
