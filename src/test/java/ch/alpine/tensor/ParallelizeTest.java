// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.ExponentialDistribution;
import ch.alpine.tensor.usr.AssertFail;

public class ParallelizeTest {
  @Test
  public void testDotSimple() {
    Tensor a = Tensors.vector(1, 2, 3);
    Tensor b = Tensors.vector(-3, 4, -8);
    assertEquals(a.dot(b), Parallelize.dot(a, b));
  }

  @Test
  public void testDotEmpty() {
    Tensor a = Tensors.empty();
    Tensor b = Tensors.empty();
    assertEquals(a.dot(b), Parallelize.dot(a, b));
  }

  @Test
  public void testMatMat() {
    Tensor a = RandomVariate.of(ExponentialDistribution.standard(), 3, 3);
    Tensor b = HilbertMatrix.of(3, 4);
    Tensor m1 = Parallelize.dot(a, b);
    Tensor m2 = a.dot(b);
    assertEquals(m1, m2);
  }

  @Test
  public void testMatrix() {
    Tensor a = Parallelize.matrix((i, j) -> RationalScalar.of(i, j + 1), 30, 30);
    Tensor b = Tensors.matrix((i, j) -> RationalScalar.of(i, j + 1), 30, 30);
    assertEquals(a, b);
  }

  @Test
  public void testDotFail() {
    AssertFail.of(() -> Parallelize.dot(RealScalar.ONE, RealScalar.ZERO));
    AssertFail.of(() -> Parallelize.dot(Tensors.vector(1, 2, 3), HilbertMatrix.of(4)));
  }
}
