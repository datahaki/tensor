// code by jph
package ch.ethz.idsc.tensor.lie;

import java.util.Arrays;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class TensorProductTest extends TestCase {
  public void testEmpty() {
    assertEquals(TensorProduct.of(Tensors.empty(), LeviCivitaTensor.of(3)), Tensors.empty());
    assertEquals(TensorProduct.of(Tensors.empty(), Quantity.of(2, "s")), Tensors.empty());
  }

  public void testScalar() {
    Tensor s = TensorProduct.of(Quantity.of(3, "m*s"), Quantity.of(4, "s"));
    assertEquals(s, Quantity.of(12, "s^2*m"));
  }

  public void testVectors() {
    Tensor tensor = TensorProduct.of(Tensors.vector(1, 2, 3), Tensors.vector(-1, 2));
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 2));
  }

  public void testFour1() {
    Tensor tensor = TensorProduct.of(Tensors.vector(1, 2), LeviCivitaTensor.of(3));
    assertEquals(Dimensions.of(tensor), Arrays.asList(2, 3, 3, 3));
  }

  public void testFour2() {
    Tensor tensor = TensorProduct.of(LeviCivitaTensor.of(3), Tensors.vector(1, 2));
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 3, 3, 2));
  }

  public void testScalarTensor() {
    Tensor a = HilbertMatrix.of(3, 4);
    Scalar b = RealScalar.of(3);
    assertEquals(TensorProduct.of(a, b), a.multiply(b));
    assertEquals(TensorProduct.of(b, a), a.multiply(b));
  }

  public void testLength0() {
    assertEquals(TensorProduct.of(), RealScalar.ONE);
  }

  public void testLength1() {
    Tensor x = Tensors.vector(2, 3, 4);
    assertEquals(TensorProduct.of(x), x);
    Tensor y = Tensors.vector(1, 1, 1);
    Tensor r = TensorProduct.of(y);
    assertEquals(r, y);
    r.set(Scalar::zero, Tensor.ALL);
    assertEquals(y, Tensors.vector(1, 1, 1));
    assertEquals(r, Array.zeros(3));
  }

  public void testLength2() {
    Tensor x = Tensors.vector(2, 3, 4);
    Tensor y = Tensors.vector(5, 7, 6);
    Tensor product = TensorProduct.of(x, y);
    Tensor xy = Tensors.fromString("{{10, 14, 12}, {15, 21, 18}, {20, 28, 24}}"); // mathematica
    assertEquals(product, xy);
  }

  public void testLength2b() {
    Tensor x = HilbertMatrix.of(2);
    Tensor y = HilbertMatrix.of(3);
    Tensor product = TensorProduct.of(x, y);
    assertEquals(Dimensions.of(product), Arrays.asList(2, 2, 3, 3));
    assertEquals(product.get(1, 1, 2), Tensors.fromString("{1/9, 1/12, 1/15}"));
  }

  public void testLength3() {
    Tensor x = Tensors.vector(2, 1, 4);
    Tensor y = Tensors.vector(3, -1, 1);
    Tensor z = Tensors.vector(1, -1, -2);
    Tensor skewsy = TensorProduct.of(x, y, z);
    // mathematica
    Tensor xy = Tensors
        .fromString("{{{6, -6, -12}, {-2, 2, 4}, {2, -2, -4}}, {{3, -3, -6}, {-1, 1, 2}, {1, -1, -2}}, {{12, -12, -24}, {-4, 4, 8}, {4, -4, -8}}}");
    assertEquals(skewsy, xy);
  }
}
