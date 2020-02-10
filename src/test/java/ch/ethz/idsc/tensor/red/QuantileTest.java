// code by jph
package ch.ethz.idsc.tensor.red;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class QuantileTest extends TestCase {
  public void testMultiple() throws ClassNotFoundException, IOException {
    Tensor vector = Tensors.vector(0, 2, 1, 4, 3);
    ScalarTensorFunction scalarTensorFunction = Serialization.copy(Quantile.of(vector));
    Tensor q = Tensors.fromString("{0, 1/5, 2/5, 3/5, 4/5, 1}").map(scalarTensorFunction);
    Tensor r = Tensors.vector(0, 0, 1, 2, 3, 4);
    assertEquals(q, r);
  }

  public void testScalar() {
    Tensor vector = Tensors.vector(0, 2, 1, 4, 3);
    ScalarTensorFunction scalarTensorFunction = Quantile.of(vector);
    Tensor p = scalarTensorFunction.apply(RealScalar.of(0.71233));
    assertEquals(p, RealScalar.of(3));
  }

  public void testSorted() throws ClassNotFoundException, IOException {
    Tensor vector = Sort.of(Tensors.vector(0, 2, 1, 4, 3));
    ScalarTensorFunction scalarUnaryOperator = Serialization.copy(Quantile.ofSorted(vector));
    Tensor q = Tensors.fromString("{0, 1/5, 2/5, 3/5, 4/5, 1}").map(scalarUnaryOperator);
    Tensor r = Tensors.vector(0, 0, 1, 2, 3, 4);
    assertEquals(q, r);
  }

  public void testBounds() {
    ScalarTensorFunction scalarTensorFunction = Quantile.of(Tensors.vector(0, 2, 1, 4, 3));
    try {
      scalarTensorFunction.apply(RealScalar.of(1.01));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      scalarTensorFunction.apply(RealScalar.of(-0.01));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailSorted() {
    Tensor vector = Tensors.vector(0, 2, 1, 4, 3);
    try {
      Quantile.ofSorted(vector);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testQuantity() {
    Scalar qs1 = Quantity.of(1, "m");
    Scalar qs2 = Quantity.of(4, "m");
    Scalar qs3 = Quantity.of(2, "m");
    Tensor vector = Tensors.of(qs1, qs2, qs3);
    ScalarTensorFunction scalarTensorFunction = Quantile.of(vector);
    assertEquals(scalarTensorFunction.apply(RealScalar.ZERO), qs1);
    assertEquals(scalarTensorFunction.apply(RealScalar.ONE), qs2);
    Scalar qs4 = Quantity.of(2, "s");
    try {
      Sort.of(Tensors.of(qs1, qs4)); // comparison fails
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testLimitTheorem() {
    Random random = new SecureRandom();
    Tensor tensor = Array.of(l -> RealScalar.of(random.nextDouble()), 2000);
    ScalarTensorFunction scalarTensorFunction = Quantile.of(tensor);
    Tensor weight = Tensors.vector(0.76, 0.1, 0.25, 0.5, 0.05, 0.95, 0, 0.5, 0.99, 1);
    Tensor quantile = weight.map(scalarTensorFunction);
    Scalar maxError = Norm.INFINITY.between(quantile, weight);
    assertTrue(Scalars.lessThan(maxError, RealScalar.of(0.125)));
  }

  public void testEmptyFail() {
    try {
      Quantile.of(Tensors.empty());
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailComplex() {
    Tensor tensor = Tensors.vector(-3, 2, 1, 100);
    ScalarTensorFunction scalarTensorFunction = Quantile.of(tensor);
    Tensor weight = Tensors.of(RealScalar.ONE, ComplexScalar.of(1, 2));
    try {
      weight.map(scalarTensorFunction);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailQuantity() {
    Tensor tensor = Tensors.vector(-3, 2, 1, 100);
    ScalarTensorFunction scalarTensorFunction = Quantile.of(tensor);
    try {
      scalarTensorFunction.apply(Quantity.of(0, "m"));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      scalarTensorFunction.apply(Quantity.of(0.2, "m"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailScalar() {
    try {
      Quantile.of(Pi.VALUE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testMatrix() {
    ScalarTensorFunction scalarTensorFunction = Quantile.of(HilbertMatrix.of(3));
    assertEquals(scalarTensorFunction.apply(RealScalar.ZERO), Tensors.fromString("{1/3, 1/4, 1/5}"));
    assertEquals(scalarTensorFunction.apply(RealScalar.ONE), Tensors.fromString("{1, 1/2, 1/3}"));
  }

  public void testFailNull() {
    try {
      Quantile.of(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
