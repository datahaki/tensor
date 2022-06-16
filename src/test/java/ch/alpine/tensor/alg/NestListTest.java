// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.ex.MatrixPower;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.tri.Cos;

class NestListTest {
  @Test
  void testLength() {
    Tensor list = NestList.of(Cos::of, RealScalar.ONE, 4);
    assertEquals(list.length(), 5);
  }

  private static Tensor _clear(Tensor a) {
    a.set(RealScalar.ZERO, 0);
    a.append(RealScalar.ONE);
    return Tensors.vector(1, 2, 3);
  }

  @Test
  void testProduct() {
    Tensor tensor = NestList.of(RealScalar.of(3)::multiply, RealScalar.ONE, 5);
    assertEquals(tensor.toString(), "{1, 3, 9, 27, 81, 243}");
  }

  @Test
  void testClear() {
    Tensor t = Tensors.vector(1, 2, 3);
    Tensor x = Tensors.vector(1, 2, 3);
    Tensor list = NestList.of(NestListTest::_clear, x, 3);
    assertEquals(list, Tensors.of(t, t, t, t));
  }

  @Test
  void testReferences() {
    Tensor vector = Tensors.vector(1, 2, 3);
    Tensor list = NestList.of(f -> f, vector, 0);
    vector.set(RealScalar.ZERO, 0);
    assertEquals(list, Tensors.fromString("{{1, 2, 3}}"));
  }

  @Test
  void testZero() {
    Tensor vector = NestList.of(Cos::of, RealScalar.ONE, 0);
    assertEquals(vector, Tensors.vector(1));
  }

  @Test
  void testNullOperator() {
    assertEquals(NestList.of(null, RealScalar.ONE, 0), Tensors.vector(1));
  }

  @Test
  void testMatrixPower() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 4);
    Tensor matrix = RandomVariate.of(distribution, 3, 3);
    Tensor list = NestList.of(matrix::dot, IdentityMatrix.of(3), 5);
    for (int index = 0; index < list.length(); ++index)
      assertEquals(list.get(index), MatrixPower.of(matrix, index));
  }

  @Test
  void testFailNull() {
    assertThrows(NullPointerException.class, () -> NestList.of(Cos::of, null, 0));
  }

  @Test
  void testFailNegative() {
    assertThrows(IllegalArgumentException.class, () -> NestList.of(Cos::of, RealScalar.ONE, -1));
    assertThrows(IllegalArgumentException.class, () -> NestList.of(Cos::of, RealScalar.ONE, -2));
  }
}
