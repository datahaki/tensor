// code by jph
package ch.ethz.idsc.tensor.alg;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.MatrixPower;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Cos;
import junit.framework.TestCase;

public class NestListTest extends TestCase {
  public void testLength() {
    Tensor list = NestList.of(Cos::of, RealScalar.ONE, 4);
    assertEquals(list.length(), 5);
  }

  private static Tensor _clear(Tensor a) {
    a.set(RealScalar.ZERO, 0);
    a.append(RealScalar.ONE);
    return Tensors.vector(1, 2, 3);
  }

  public void testProduct() {
    Tensor tensor = NestList.of(RealScalar.of(3)::multiply, RealScalar.ONE, 5);
    assertEquals(tensor.toString(), "{1, 3, 9, 27, 81, 243}");
  }

  public void testClear() {
    Tensor t = Tensors.vector(1, 2, 3);
    Tensor x = Tensors.vector(1, 2, 3);
    Tensor list = NestList.of(NestListTest::_clear, x, 3);
    assertEquals(list, Tensors.of(t, t, t, t));
  }

  public void testReferences() {
    Tensor vector = Tensors.vector(1, 2, 3);
    Tensor list = NestList.of(f -> f, vector, 0);
    vector.set(RealScalar.ZERO, 0);
    assertEquals(list, Tensors.fromString("{{1, 2, 3}}"));
  }

  public void testZero() {
    Tensor vector = NestList.of(Cos::of, RealScalar.ONE, 0);
    assertEquals(vector, Tensors.vector(1));
  }

  public void testNullOperator() {
    assertEquals(NestList.of(null, RealScalar.ONE, 0), Tensors.vector(1));
  }

  public void testMatrixPower() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 4);
    Tensor matrix = RandomVariate.of(distribution, 3, 3);
    Tensor list = NestList.of(matrix::dot, IdentityMatrix.of(3), 5);
    for (int index = 0; index < list.length(); ++index)
      assertEquals(list.get(index), MatrixPower.of(matrix, index));
  }

  public void testFailNull() {
    try {
      NestList.of(Cos::of, null, 0);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailNegative() {
    try {
      NestList.of(Cos::of, RealScalar.ONE, -1);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      NestList.of(Cos::of, RealScalar.ONE, -2);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
