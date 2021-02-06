// code by jph
package ch.ethz.idsc.tensor.itp;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class BernsteinBasisTest extends TestCase {
  public void testSimple() {
    Tensor actual = BernsteinBasis.of(5, RationalScalar.of(2, 3));
    Tensor expect = Tensors.fromString("{1/243, 10/243, 40/243, 80/243, 80/243, 32/243}");
    assertEquals(actual, expect);
    assertEquals(Total.of(actual), RealScalar.ONE);
  }

  public void testSimpleReverse() {
    Tensor actual = BernsteinBasis.of(5, RationalScalar.of(1, 3));
    Tensor expect = Reverse.of(Tensors.fromString("{1/243, 10/243, 40/243, 80/243, 80/243, 32/243}"));
    assertEquals(actual, expect);
    assertEquals(Total.of(actual), RealScalar.ONE);
  }

  public void testNegFail() {
    assertEquals(BernsteinBasis.of(0, RationalScalar.of(2, 3)), Tensors.vector(1));
    AssertFail.of(() -> BernsteinBasis.of(-1, RationalScalar.of(2, 3)));
  }
}
