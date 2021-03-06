// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.usr.AssertFail;
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
