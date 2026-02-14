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
  void testIdentity() {
    Tensor matrix = DiagonalMatrix.full(Tensors.vector(1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
    assertEquals(IdentityMatrix.of(10), matrix);
  }

  @Test
  void testIdentitySparse() {
    Tensor matrix = DiagonalMatrix.sparse(Tensors.vector(1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
    assertEquals(IdentityMatrix.of(10), matrix);
  }

  @Test
  void testMisc1() {
    Tensor matrix = DiagonalMatrix.of(-2, 3, -4);
    assertEquals(Det.of(matrix).number(), 2 * 3 * 4);
    ExactTensorQ.require(matrix);
  }

  @Test
  void testDiagonalMatrix() {
    Tensor m1 = DiagonalMatrix.full(Tensors.vectorDouble(12, 3.2, 0.32));
    Tensor m2 = DiagonalMatrix.sparse(Tensors.vectorDouble(12, 3.2, 0.32));
    Tensor m3 = DiagonalMatrix.of(12.0, 3.2, 0.32);
    assertEquals(m1, m2);
    assertEquals(m1, m3);
  }

  @Test
  void testMisc2() {
    Tensor matrix = DiagonalMatrix.of( //
        RealScalar.of(-2), RealScalar.of(3), RealScalar.of(-4));
    ExactTensorQ.require(matrix);
    assertEquals(Det.of(matrix).number(), 2 * 3 * 4);
  }

  @Test
  void testMisc3() {
    Tensor tensor = RealScalar.of(-2);
    assertThrows(Exception.class, () -> DiagonalMatrix.full(tensor));
    assertThrows(Exception.class, () -> DiagonalMatrix.sparse(tensor));
  }

  @Test
  void testQuantityFail() {
    Scalar qs1 = Quantity.of(1, "m");
    Scalar qs2 = Quantity.of(2, "s");
    Tensor vec = Tensors.of(qs1, qs2);
    // assertThrows(Exception.class, () -> DiagonalMatrix.with(vec));
    Tensor matrix = DiagonalMatrix.full(vec);
    ExactTensorQ.require(matrix);
    assertEquals(matrix, Tensors.fromString("{{1[m], 0[m]}, {0[s], 2[s]}}"));
  }

  @Test
  void testQuantity() {
    Scalar qs1 = Quantity.of(1, "m");
    Scalar qs2 = Quantity.of(2, "m");
    Tensor vec = Tensors.of(qs1, qs2);
    Tensor expect = Tensors.fromString("{{1[m], 0[m]}, {0[m], 2[m]}}");
    Tensor m1 = DiagonalMatrix.full(vec);
    ExactTensorQ.require(m1);
    assertEquals(m1, expect);
    Tensor m2 = DiagonalMatrix.sparse(vec);
    ExactTensorQ.require(m2);
    assertEquals(m2, expect);
  }

  @Test
  void testFailScalar() {
    Tensor matrix = DiagonalMatrix.sparse(RealScalar.ONE);
    assertEquals(matrix, Tensors.fromString("{{1}}"));
    assertThrows(Exception.class, () -> DiagonalMatrix.full(RealScalar.ONE));
    assertThrows(Exception.class, () -> DiagonalMatrix.sparse(RealScalar.ONE));
  }

  @Test
  void testFailNonVector() {
    Tensor tensor = Tensors.fromString("{1, 2, {3}}");
    assertThrows(ClassCastException.class, () -> DiagonalMatrix.full(tensor));
    assertThrows(ClassCastException.class, () -> DiagonalMatrix.sparse(tensor));
  }

  @Test
  void testFailEmpty() {
    assertThrows(Exception.class, () -> DiagonalMatrix.full(Tensors.empty()));
    assertThrows(Exception.class, () -> DiagonalMatrix.sparse(Tensors.empty()));
  }

  @Test
  void testFailScalarEmpty() {
    assertThrows(Exception.class, () -> DiagonalMatrix.of(new Scalar[] {}));
  }

  @Test
  void testFailNumberEmpty() {
    assertThrows(Exception.class, () -> DiagonalMatrix.of(new Number[] {}));
  }
}
