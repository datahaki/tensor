// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.qty.Quantity;

class DiagonalMatrixTest {
  @Test
  public void testIdentity() {
    Tensor matrix = DiagonalMatrix.with(Tensors.vector(1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
    assertEquals(IdentityMatrix.of(10), matrix);
  }

  @Test
  public void testMisc1() {
    Tensor matrix = DiagonalMatrix.of(-2, 3, -4);
    assertEquals(Det.of(matrix).number(), 2 * 3 * 4);
    ExactTensorQ.require(matrix);
  }

  @Test
  public void testDiagonalMatrix() {
    Tensor m1 = DiagonalMatrix.with(Tensors.vectorDouble(12, 3.2, 0.32));
    Tensor m2 = DiagonalMatrix.of(12, 3.2, 0.32);
    assertEquals(m1, m2);
  }

  @Test
  public void testMisc2() {
    Tensor matrix = DiagonalMatrix.of( //
        RealScalar.of(-2), RealScalar.of(3), RealScalar.of(-4));
    ExactTensorQ.require(matrix);
    assertEquals(Det.of(matrix).number(), 2 * 3 * 4);
  }

  @Test
  public void testMisc3() {
    Tensor tensor = RealScalar.of(-2);
    assertThrows(IllegalArgumentException.class, () -> DiagonalMatrix.with(tensor));
  }

  @Test
  public void testQuantity() {
    Scalar qs1 = Quantity.of(1, "m");
    Scalar qs2 = Quantity.of(2, "s");
    Tensor vec = Tensors.of(qs1, qs2);
    Tensor matrix = DiagonalMatrix.with(vec);
    ExactTensorQ.require(matrix);
    assertEquals(matrix.toString(), "{{1[m], 0[m]}, {0[s], 2[s]}}");
  }

  @Test
  public void testFailScalar() {
    Tensor matrix = DiagonalMatrix.of(RealScalar.ONE);
    assertEquals(matrix.toString(), "{{1}}");
    assertThrows(IllegalArgumentException.class, () -> DiagonalMatrix.with(RealScalar.ONE));
  }

  @Test
  public void testFailNonVector() {
    assertThrows(ClassCastException.class, () -> DiagonalMatrix.with(Tensors.fromString("{1, 2, {3}}")));
  }

  @Test
  public void testFailEmpty() {
    assertThrows(IllegalArgumentException.class, () -> DiagonalMatrix.with(Tensors.empty()));
  }

  @Test
  public void testFailScalarEmpty() {
    assertThrows(IllegalArgumentException.class, () -> DiagonalMatrix.of(new Scalar[] {}));
  }

  @Test
  public void testFailNumberEmpty() {
    assertThrows(IllegalArgumentException.class, () -> DiagonalMatrix.of(new Number[] {}));
  }
}
