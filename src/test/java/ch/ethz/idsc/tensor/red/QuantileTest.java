// code by jph
package ch.ethz.idsc.tensor.red;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.OrderedQ;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class QuantileTest extends TestCase {
  public void testMultiple() throws ClassNotFoundException, IOException {
    Tensor vector = Tensors.vector(0, 2, 1, 4, 3);
    ScalarUnaryOperator scalarUnaryOperator = Serialization.copy(Quantile.of(vector));
    Tensor q = Tensors.fromString("{0, 1/5, 2/5, 3/5, 4/5, 1}").map(scalarUnaryOperator);
    Tensor r = Tensors.vector(0, 0, 1, 2, 3, 4);
    assertEquals(q, r);
  }

  public void testScalar() {
    Tensor vector = Tensors.vector(0, 2, 1, 4, 3);
    ScalarUnaryOperator scalarUnaryOperator = Quantile.of(vector);
    Tensor p = scalarUnaryOperator.apply(RealScalar.of(0.71233));
    assertEquals(p, RealScalar.of(3));
  }

  public void testSorted() throws ClassNotFoundException, IOException {
    Tensor vector = Sort.of(Tensors.vector(0, 2, 1, 4, 3));
    ScalarUnaryOperator scalarUnaryOperator = Serialization.copy(Quantile.ofSorted(vector));
    Tensor q = Tensors.fromString("{0, 1/5, 2/5, 3/5, 4/5, 1}").map(scalarUnaryOperator);
    Tensor r = Tensors.vector(0, 0, 1, 2, 3, 4);
    assertEquals(q, r);
  }

  public void testBounds() {
    ScalarUnaryOperator scalarUnaryOperator = Quantile.of(Tensors.vector(0, 2, 1, 4, 3));
    AssertFail.of(() -> scalarUnaryOperator.apply(RealScalar.of(1.01)));
    AssertFail.of(() -> scalarUnaryOperator.apply(RealScalar.of(-0.01)));
  }

  public void testFailSorted() {
    Tensor vector = Tensors.vector(0, 2, 1, 4, 3);
    AssertFail.of(() -> Quantile.ofSorted(vector));
  }

  public void testQuantity() {
    Scalar qs1 = Quantity.of(1, "m");
    Scalar qs2 = Quantity.of(4, "m");
    Scalar qs3 = Quantity.of(2, "m");
    Tensor vector = Tensors.of(qs1, qs2, qs3);
    ScalarUnaryOperator scalarUnaryOperator = Quantile.of(vector);
    assertEquals(scalarUnaryOperator.apply(RealScalar.ZERO), qs1);
    assertEquals(scalarUnaryOperator.apply(RealScalar.ONE), qs2);
    Scalar qs4 = Quantity.of(2, "s");
    AssertFail.of(() -> Sort.of(Tensors.of(qs1, qs4))); // comparison fails
  }

  public void testLimitTheorem() {
    Random random = new SecureRandom();
    Tensor tensor = Array.of(l -> RealScalar.of(random.nextDouble()), 2000);
    ScalarUnaryOperator scalarUnaryOperator = Quantile.of(tensor);
    Tensor weight = Tensors.vector(0.76, 0.1, 0.25, 0.5, 0.05, 0.95, 0, 0.5, 0.99, 1);
    Tensor quantile = weight.map(scalarUnaryOperator);
    Scalar maxError = Norm.INFINITY.between(quantile, weight);
    assertTrue(Scalars.lessThan(maxError, RealScalar.of(0.125)));
  }

  public void testDistribution() {
    ScalarUnaryOperator suo = Quantile.of(UniformDistribution.of(5, 10));
    AssertFail.of(() -> suo.apply(RationalScalar.of(-1, 5)));
    assertEquals(suo.apply(RationalScalar.of(0, 5)), RealScalar.of(5));
    assertEquals(suo.apply(RationalScalar.of(1, 5)), RealScalar.of(6));
    assertEquals(suo.apply(RationalScalar.of(2, 5)), RealScalar.of(7));
    assertEquals(suo.apply(RationalScalar.of(5, 5)), RealScalar.of(10));
    AssertFail.of(() -> suo.apply(RationalScalar.of(+6, 5)));
  }

  public void testEmptyFail() {
    AssertFail.of(() -> Quantile.of(Tensors.empty()));
  }

  public void testFailComplex() {
    Tensor tensor = Tensors.vector(-3, 2, 1, 100);
    ScalarUnaryOperator scalarUnaryOperator = Quantile.of(tensor);
    Tensor weight = Tensors.of(RealScalar.ONE, ComplexScalar.of(1, 2));
    AssertFail.of(() -> weight.map(scalarUnaryOperator));
  }

  public void testFailQuantity() {
    Tensor tensor = Tensors.vector(-3, 2, 1, 100);
    ScalarUnaryOperator scalarUnaryOperator = Quantile.of(tensor);
    AssertFail.of(() -> scalarUnaryOperator.apply(Quantity.of(0, "m")));
    AssertFail.of(() -> scalarUnaryOperator.apply(Quantity.of(0.2, "m")));
  }

  public void testFailScalar() {
    AssertFail.of(() -> Quantile.of(Pi.VALUE));
  }

  public void testMatrixFail() {
    Tensor matrix = Reverse.of(IdentityMatrix.of(7));
    OrderedQ.require(matrix);
    AssertFail.of(() -> Quantile.of(matrix));
    AssertFail.of(() -> Quantile.ofSorted(matrix));
  }

  public void testEmptyVectorFail() {
    AssertFail.of(() -> Quantile.of(Tensors.empty()));
    AssertFail.of(() -> Quantile.ofSorted(Tensors.empty()));
  }

  public void testFailNull() {
    AssertFail.of(() -> Quantile.of((Tensor) null));
    AssertFail.of(() -> Quantile.of((Distribution) null));
  }
}
