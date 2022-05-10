// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.qty.Quantity;

class TensorProductTest {
  @Test
  public void testEmpty() {
    assertEquals(TensorProduct.of(Tensors.empty(), LeviCivitaTensor.of(3)), Tensors.empty());
    assertEquals(TensorProduct.of(Tensors.empty(), Quantity.of(2, "s")), Tensors.empty());
  }

  @Test
  public void testScalar() {
    Tensor s = TensorProduct.of(Quantity.of(3, "m*s"), Quantity.of(4, "s"));
    assertEquals(s, Quantity.of(12, "s^2*m"));
  }

  @Test
  public void testVectors() {
    Tensor tensor = TensorProduct.of(Tensors.vector(1, 2, 3), Tensors.vector(-1, 2));
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 2));
  }

  @Test
  public void testFour1() {
    Tensor tensor = TensorProduct.of(Tensors.vector(1, 2), LeviCivitaTensor.of(3));
    assertEquals(Dimensions.of(tensor), Arrays.asList(2, 3, 3, 3));
  }

  @Test
  public void testFour2() {
    Tensor tensor = TensorProduct.of(LeviCivitaTensor.of(3), Tensors.vector(1, 2));
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 3, 3, 2));
  }

  @Test
  public void testScalarTensor() {
    Tensor a = HilbertMatrix.of(3, 4);
    Scalar b = RealScalar.of(3);
    assertEquals(TensorProduct.of(a, b), a.multiply(b));
    assertEquals(TensorProduct.of(b, a), a.multiply(b));
  }

  @Test
  public void testLength0() {
    assertEquals(TensorProduct.of(), RealScalar.ONE);
  }

  @Test
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

  @Test
  public void testLength2() {
    Tensor x = Tensors.vector(2, 3, 4);
    Tensor y = Tensors.vector(5, 7, 6);
    Tensor product = TensorProduct.of(x, y);
    Tensor xy = Tensors.fromString("{{10, 14, 12}, {15, 21, 18}, {20, 28, 24}}"); // mathematica
    assertEquals(product, xy);
  }

  @Test
  public void testLength2b() {
    Tensor x = HilbertMatrix.of(2);
    Tensor y = HilbertMatrix.of(3);
    Tensor product = TensorProduct.of(x, y);
    assertEquals(Dimensions.of(product), Arrays.asList(2, 2, 3, 3));
    assertEquals(product.get(1, 1, 2), Tensors.fromString("{1/9, 1/12, 1/15}"));
  }

  @Test
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
