// code by jph
package ch.ethz.idsc.tensor.red;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NormTest extends TestCase {
  public void testOneInfNorm1() {
    Tensor a = Tensors.vector(3, -4);
    assertEquals(Norm._1.of(a), Scalars.fromString("7"));
    assertEquals(Norm.INFINITY.of(a), Scalars.fromString("4"));
  }

  public void testOneInfNorm2() {
    Tensor a = Tensors.vector(1, 2);
    Tensor b = Tensors.vector(3, 4);
    Tensor c = Tensors.of(a, b);
    assertEquals(Norm._1.of(c), Scalars.fromString("6"));
    assertEquals(Norm.INFINITY.of(c), Scalars.fromString("7"));
  }

  public void testOneInfNorm3() {
    Tensor a = Tensors.vector(1, 2, 8);
    Tensor b = Tensors.vector(3, 4, 2);
    Tensor c = Tensors.of(a, b);
    assertEquals(Norm._1.ofMatrix(c), Scalars.fromString("10"));
    assertEquals(Norm.INFINITY.ofMatrix(c), Scalars.fromString("11"));
  }

  public void testQuantity() {
    for (int count = 0; count < 5; ++count)
      for (int n = 1; n < 6; ++n) {
        Tensor x = RandomVariate.of(NormalDistribution.standard(), n, n).map(s -> Quantity.of(s, "m"));
        Scalar n1 = Norm._1.ofMatrix(x);
        Scalar n2 = Norm._2.ofMatrix(x);
        Scalar ni = Norm.INFINITY.ofMatrix(x);
        assertTrue(Scalars.lessEquals(n2, Max.of(n1, ni)));
      }
  }

  private static void _checkExactZero(Scalar norm) {
    ExactScalarQ.require(norm);
    assertEquals(norm, RealScalar.ZERO);
  }

  public void testZero() {
    for (Norm norm : Norm.values()) {
      _checkExactZero(norm.ofVector(Array.zeros(1)));
      _checkExactZero(norm.ofVector(Array.zeros(5)));
    }
  }

  public void testEmptyFail() {
    AssertFail.of(() -> Norm._1.of(Tensors.empty()));
  }

  public void testScalarFail() {
    AssertFail.of(() -> Norm._1.of(RealScalar.ONE));
  }

  public void testUnstructuredFail() {
    AssertFail.of(() -> Norm._1.of(Tensors.fromString("{{1, 2}, {3}}")));
  }
}
