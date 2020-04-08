// code by jph
package ch.ethz.idsc.tensor.pdf;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.mat.Tolerance;
import junit.framework.TestCase;

public class ErfiTest extends TestCase {
  public void testSimple() {
    Scalar result = Erfi.FUNCTION.apply(ComplexScalar.of(1.2, 3.4));
    Scalar expect = ComplexScalar.of(4.9665206621382625E-6, 1.0000035775250082);
    Tolerance.CHOP.requireClose(result, expect);
  }
}
