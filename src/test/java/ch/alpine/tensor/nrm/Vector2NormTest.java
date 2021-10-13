// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Vector2NormTest extends TestCase {
  public void testScalar() {
    assertEquals(Vector2Norm.of(Tensors.fromString("{0}")), RealScalar.ZERO);
    assertEquals(Vector2Norm.of(Tensors.fromString("{-3.90512}")), Scalars.fromString("3.90512"));
    assertEquals(Vector2Norm.of(Tensors.fromString("{-3/7}")), Scalars.fromString("3/7"));
    Scalar rs = Vector2Norm.of(Tensors.of(ComplexScalar.of(RealScalar.ONE, RealScalar.of(2)))); // <- sqrt(5)
    assertEquals(rs, Scalars.fromString("2.23606797749979"));
    assertEquals(Vector2NormSquared.of(Tensors.of(Scalars.fromString("-3/7"))), Scalars.fromString("9/49"));
  }

  public void testVector1() {
    Tensor A = Tensors.vectorDouble(new double[] { 2, 1.5, 3 });
    assertEquals(Vector2Norm.of(A), Scalars.fromString("3.905124837953327"));
  }

  public void testVector2() {
    Tensor A = Tensors.of(ComplexScalar.of( //
        RealScalar.ONE, RealScalar.of(2)), DoubleScalar.of(1.5));
    assertEquals(Vector2Norm.of(A), Scalars.fromString("2.6925824035672523"));
    Tensor a = Tensors.vector(2, 3, 4);
    assertEquals(Vector2NormSquared.of(a), Dot.of(a, a));
  }

  public void testVector3() {
    Tensor A = Tensors.of(ComplexScalar.of(1, 2), DoubleScalar.of(1.5));
    assertEquals(Vector2Norm.of(A), DoubleScalar.of(2.6925824035672523)); // 2.69258
  }

  public void testExact() {
    Scalar two = Vector2Norm.of(Tensors.vector(1, 1, 1, 1));
    ExactScalarQ.require(two);
    assertEquals(two, RealScalar.of(2));
  }

  public void testQuantity() {
    Tensor vec = Tensors.of( //
        Quantity.of(3, "m^2"), //
        Quantity.of(0, "s*rad"), //
        Quantity.of(-4, "m^2"), //
        RealScalar.ZERO //
    );
    assertEquals(Vector2Norm.of(vec), Quantity.of(5, "m^2"));
  }

  public void testQuantityMixed() {
    Tensor vec = Tensors.fromString("{0[m^2], 0[s*rad], 1}");
    assertEquals(Vector2Norm.of(vec), RealScalar.ONE);
  }
}
