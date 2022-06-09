// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.qty.Quantity;

class Vector2NormTest {
  @Test
  public void testScalar() {
    assertEquals(Vector2Norm.of(Tensors.fromString("{0}")), RealScalar.ZERO);
    assertEquals(Vector2Norm.of(Tensors.fromString("{-3.90512}")), Scalars.fromString("3.90512"));
    assertEquals(Vector2Norm.of(Tensors.fromString("{-3/7}")), Scalars.fromString("3/7"));
    Scalar rs = Vector2Norm.of(Tensors.of(ComplexScalar.of(RealScalar.ONE, RealScalar.of(2)))); // <- sqrt(5)
    assertEquals(rs, Scalars.fromString("2.23606797749979"));
    assertEquals(Vector2NormSquared.of(Tensors.of(Scalars.fromString("-3/7"))), Scalars.fromString("9/49"));
  }

  @Test
  public void testVector1() {
    Tensor A = Tensors.vectorDouble(new double[] { 2, 1.5, 3 });
    assertEquals(Vector2Norm.of(A), Scalars.fromString("3.905124837953327"));
  }

  @Test
  public void testVector2() {
    Tensor A = Tensors.of(ComplexScalar.of( //
        RealScalar.ONE, RealScalar.of(2)), DoubleScalar.of(1.5));
    assertEquals(Vector2Norm.of(A), Scalars.fromString("2.6925824035672523"));
    Tensor a = Tensors.vector(2, 3, 4);
    assertEquals(Vector2NormSquared.of(a), Dot.of(a, a));
  }

  @Test
  public void testVector3() {
    Tensor A = Tensors.of(ComplexScalar.of(1, 2), DoubleScalar.of(1.5));
    assertEquals(Vector2Norm.of(A), DoubleScalar.of(2.6925824035672523)); // 2.69258
  }

  @Test
  public void testExact() {
    Scalar two = Vector2Norm.of(Tensors.vector(1, 1, 1, 1));
    ExactScalarQ.require(two);
    assertEquals(two, RealScalar.of(2));
  }

  @Test
  public void testQuantity() {
    Tensor vec = Tensors.of( //
        Quantity.of(3, "m^2"), //
        Quantity.of(-4, "m^2"));
    assertEquals(Vector2Norm.of(vec), Quantity.of(5, "m^2"));
  }

  @Test
  public void testQuantityFail() {
    Tensor vec = Tensors.of( //
        Quantity.of(3, "m^2"), //
        Quantity.of(-4, "m^2"), //
        RealScalar.ZERO //
    );
    assertThrows(TensorRuntimeException.class, () -> Vector2Norm.of(vec));
  }

  @Test
  public void testQuantityMixedFail1() {
    Tensor vector = Tensors.fromString("{0[m^2], 0[s*rad]}");
    assertThrows(TensorRuntimeException.class, () -> Vector2Norm.of(vector));
  }

  @Test
  public void testQuantityMixedFail2() {
    Tensor vector = Tensors.fromString("{0[m^2], 0[m]}");
    assertThrows(TensorRuntimeException.class, () -> Vector2Norm.of(vector));
  }

  @Test
  public void testUnity() {
    Tensor vector = Tensors.fromString("{0, 0, 1}");
    assertEquals(Vector2Norm.of(vector), RealScalar.ONE);
  }
}
