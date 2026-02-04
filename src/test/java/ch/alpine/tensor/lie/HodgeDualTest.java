// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.lie.rot.Cross;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.gam.Factorial;

class HodgeDualTest {
  /** @param tensor of rank at least 1
   * @return
   * @throws Exception if tensor is a scalar */
  static Tensor of(Tensor tensor) {
    return HodgeDual.of(tensor, tensor.length());
  }

  static Tensor hodgeDual_of(Tensor tensor, int d) {
    Dimensions dimensions = new Dimensions(tensor);
    if (dimensions.isArray()) {
      int rank = dimensions.list().size();
      // implementation is not efficient
      Tensor product = TensorProduct.of(tensor, LeviCivitaTensor.of(d));
      for (int index = 0; index < rank; ++index)
        product = TensorContract.of(product, 0, rank - index);
      return product.divide(Factorial.of(rank));
    }
    throw new Throw(tensor);
  }

  @Test
  void testVector3() {
    Tensor vector = Tensors.vector(1, 2, 3);
    Tensor matrix = HodgeDual.of(vector, 3);
    assertEquals(matrix, Tensors.fromString("{{0, 3, -2}, {-3, 0, 1}, {2, -1, 0}}"));
    assertEquals(matrix, Cross.skew3(vector).negate());
    assertEquals(matrix, of(vector));
    Tensor checks = HodgeDual.of(matrix, 3);
    assertEquals(matrix, TensorWedge.of(matrix));
    assertEquals(checks, vector);
  }

  @Test
  void testVector4() {
    Tensor vector = Tensors.vector(1, 2, 3, 4);
    Tensor tensor = HodgeDual.of(vector, 4);
    assertEquals(tensor, TensorWedge.of(tensor));
    Tensor checks = HodgeDual.of(tensor, 4);
    assertEquals(checks, vector.negate()); // (-1)^1*(4-1) -> negate is expected
    assertEquals(checks, TensorWedge.of(checks));
  }

  @Test
  void testScalar2() {
    Tensor matrix = HodgeDual.of(RealScalar.of(3), 2);
    assertEquals(matrix, Tensors.fromString("{{0, 3}, {-3, 0}}"));
  }

  @Test
  void testScalar3() {
    Tensor matrix = HodgeDual.of(RealScalar.of(5), 3);
    assertEquals(matrix, Tensors.fromString("{{{0, 0, 0}, {0, 0, 5}, {0, -5, 0}}, {{0, 0, -5}, {0, 0, 0}, {5, 0, 0}}, {{0, 5, 0}, {-5, 0, 0}, {0, 0, 0}}}"));
  }

  @Test
  void testLeviCivitaTensor() {
    for (int d = 1; d < 5; ++d)
      assertEquals(LeviCivitaTensor.of(d), HodgeDual.of(RealScalar.ONE, d));
    for (int d = 1; d < 5; ++d)
      assertEquals(RealScalar.ONE, HodgeDual.of(LeviCivitaTensor.of(d), d));
  }

  @Test
  void testScalar0() {
    Tensor tensor = HodgeDual.of(Pi.HALF, 0);
    assertEquals(tensor, Pi.HALF);
  }

  @Test
  void testScalar1() {
    assertThrows(Throw.class, () -> HodgeDual.of(Tensors.vector(1, 2, 3), 0));
  }

  @Test
  void testNonAlternating() {
    Distribution distribution = DiscreteUniformDistribution.of(-10, 10);
    for (int count = 0; count < 5; ++count) {
      Tensor matrix = RandomVariate.of(distribution, 3, 3);
      assertEquals( //
          of(matrix), //
          hodgeDual_of(matrix, 3));
    }
  }

  @Test
  void testEmpty() {
    assertTrue(new Dimensions(Tensors.empty()).isArray());
    assertThrows(Throw.class, () -> HodgeDual.of(Tensors.empty(), 0));
  }

  @Test
  void testMismatchFail() {
    Tensor vector = Tensors.vector(1, 2, 3);
    assertThrows(IllegalArgumentException.class, () -> HodgeDual.of(vector, 2));
  }

  @Test
  void testNonArrayFail() {
    Tensor vector = Tensors.fromString("{{1, 2}, {3, 4, 5}}");
    assertThrows(Throw.class, () -> HodgeDual.of(vector, 2));
    assertThrows(Throw.class, () -> HodgeDual.of(vector, 3));
  }

  @Test
  void testNonRegularFail() {
    Tensor vector = Array.zeros(2, 3);
    assertThrows(IllegalArgumentException.class, () -> HodgeDual.of(vector, 2));
    assertThrows(IllegalArgumentException.class, () -> HodgeDual.of(vector, 3));
  }

  @Test
  void testNegativeDimFail() {
    assertThrows(IllegalArgumentException.class, () -> of(RealScalar.ONE));
    assertThrows(IllegalArgumentException.class, () -> HodgeDual.of(RealScalar.ONE, -1));
  }
}
