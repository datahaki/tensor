// code by jph
package ch.alpine.tensor.sca.erf;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;
import junit.framework.TestCase;

public class ErfiTest extends TestCase {
  public void testSimple() {
    Scalar result = Erfi.FUNCTION.apply(ComplexScalar.of(1.2, 3.4));
    Scalar expect = ComplexScalar.of(4.9665206621382625E-6, 1.0000035775250082);
    Tolerance.CHOP.requireClose(result, expect);
  }

  public void testOf() {
    Tensor vector = Tensors.vector(1, 2, 3, 4);
    Tolerance.CHOP.requireClose(vector.map(Erfi.FUNCTION), Erfi.of(vector));
  }
}
