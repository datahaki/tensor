// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.OrderedQ;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.nrm.VectorInfinityNorm;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;

class QuantileTest {
  @Test
  void testMultiple() throws ClassNotFoundException, IOException {
    Tensor vector = Tensors.vector(0, 2, 1, 4, 3);
    ScalarUnaryOperator scalarUnaryOperator = Serialization.copy(Quantile.of(vector));
    Tensor q = Tensors.fromString("{0, 1/5, 2/5, 3/5, 4/5, 1}").map(scalarUnaryOperator);
    Tensor r = Tensors.vector(0, 0, 1, 2, 3, 4);
    assertEquals(q, r);
  }

  @Test
  void testScalar() {
    Tensor vector = Tensors.vector(0, 2, 1, 4, 3);
    ScalarUnaryOperator scalarUnaryOperator = Quantile.of(vector);
    Tensor p = scalarUnaryOperator.apply(RealScalar.of(0.71233));
    assertEquals(p, RealScalar.of(3));
  }

  @Test
  void testSorted() throws ClassNotFoundException, IOException {
    Tensor vector = Sort.of(Tensors.vector(0, 2, 1, 4, 3));
    ScalarUnaryOperator scalarUnaryOperator = Serialization.copy(Quantile.ofSorted(vector));
    Tensor q = Tensors.fromString("{0, 1/5, 2/5, 3/5, 4/5, 1}").map(scalarUnaryOperator);
    Tensor r = Tensors.vector(0, 0, 1, 2, 3, 4);
    assertEquals(q, r);
  }

  @Test
  void testBounds() {
    ScalarUnaryOperator scalarUnaryOperator = Quantile.of(Tensors.vector(0, 2, 1, 4, 3));
    assertThrows(IndexOutOfBoundsException.class, () -> scalarUnaryOperator.apply(RealScalar.of(1.01)));
    assertThrows(IndexOutOfBoundsException.class, () -> scalarUnaryOperator.apply(RealScalar.of(-0.01)));
  }

  @Test
  void testFailSorted() {
    Tensor vector = Tensors.vector(0, 2, 1, 4, 3);
    assertThrows(Throw.class, () -> Quantile.ofSorted(vector));
  }

  @Test
  void testQuantity() {
    Scalar qs1 = Quantity.of(1, "m");
    Scalar qs2 = Quantity.of(4, "m");
    Scalar qs3 = Quantity.of(2, "m");
    Tensor vector = Tensors.of(qs1, qs2, qs3);
    ScalarUnaryOperator scalarUnaryOperator = Quantile.of(vector);
    assertEquals(scalarUnaryOperator.apply(RealScalar.ZERO), qs1);
    assertEquals(scalarUnaryOperator.apply(RealScalar.ONE), qs2);
    Scalar qs4 = Quantity.of(2, "s");
    assertThrows(Throw.class, () -> Sort.of(Tensors.of(qs1, qs4))); // comparison fails
  }

  @Test
  void testLimitTheorem() {
    Tensor tensor = Array.of(_ -> RealScalar.of(ThreadLocalRandom.current().nextDouble()), 2000);
    ScalarUnaryOperator scalarUnaryOperator = Quantile.of(tensor);
    Tensor weight = Tensors.vector(0.76, 0.1, 0.25, 0.5, 0.05, 0.95, 0, 0.5, 0.99, 1);
    Tensor quantile = weight.map(scalarUnaryOperator);
    Scalar maxError = VectorInfinityNorm.between(quantile, weight);
    assertTrue(Scalars.lessThan(maxError, RealScalar.of(0.125)));
  }

  @Test
  void testDistribution() {
    ScalarUnaryOperator suo = Quantile.of(UniformDistribution.of(5, 10));
    assertThrows(Throw.class, () -> suo.apply(RationalScalar.of(-1, 5)));
    assertEquals(suo.apply(RationalScalar.of(0, 5)), RealScalar.of(5));
    assertEquals(suo.apply(RationalScalar.of(1, 5)), RealScalar.of(6));
    assertEquals(suo.apply(RationalScalar.of(2, 5)), RealScalar.of(7));
    assertEquals(suo.apply(RationalScalar.of(5, 5)), RealScalar.of(10));
    assertThrows(Throw.class, () -> suo.apply(RationalScalar.of(+6, 5)));
  }

  @Test
  void testEmptyFail() {
    assertThrows(IllegalArgumentException.class, () -> Quantile.of(Tensors.empty()));
  }

  @Test
  void testFailComplex() {
    Tensor tensor = Tensors.vector(-3, 2, 1, 100);
    ScalarUnaryOperator scalarUnaryOperator = Quantile.of(tensor);
    Tensor weight = Tensors.of(RealScalar.ONE, ComplexScalar.of(1, 2));
    assertThrows(Throw.class, () -> weight.map(scalarUnaryOperator));
  }

  @Test
  void testFailQuantity() {
    Tensor tensor = Tensors.vector(-3, 2, 1, 100);
    ScalarUnaryOperator scalarUnaryOperator = Quantile.of(tensor);
    assertThrows(Throw.class, () -> scalarUnaryOperator.apply(Quantity.of(0, "m")));
    assertThrows(Throw.class, () -> scalarUnaryOperator.apply(Quantity.of(0.2, "m")));
  }

  @Test
  void testFailScalar() {
    assertThrows(Throw.class, () -> Quantile.of(Pi.VALUE));
  }

  @Test
  void testMatrixFail() {
    Tensor matrix = Reverse.of(IdentityMatrix.of(7));
    OrderedQ.require(matrix);
    assertThrows(Throw.class, () -> Quantile.of(matrix));
    assertThrows(Throw.class, () -> Quantile.ofSorted(matrix));
  }

  @Test
  void testEmptyVectorFail() {
    assertThrows(IllegalArgumentException.class, () -> Quantile.of(Tensors.empty()));
    assertThrows(IllegalArgumentException.class, () -> Quantile.ofSorted(Tensors.empty()));
  }

  @Test
  void testFailNull() {
    assertThrows(NullPointerException.class, () -> Quantile.of((Tensor) null));
    assertThrows(NullPointerException.class, () -> Quantile.of((Distribution) null));
  }
}
