// code by jph
package ch.alpine.tensor.mat.ex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;

class MatrixLog1Test {
  @Test
  void test1x1() {
    for (int count = 0; count < 10; ++count) {
      Tensor x = RandomVariate.of(UniformDistribution.of(-10, 6), 1, 1);
      Tensor exp = MatrixExp.of(x);
      Tensor log = MatrixLog._of(exp);
      Tensor cmp = MatrixLog1.of(exp);
      Chop._04.requireClose(log, cmp);
    }
  }

  @Test
  void testSparse() {
    for (int n = 1; n < 5; ++n)
      assertEquals(MatrixLog.of(IdentityMatrix.sparse(n)), MatrixLog.of(IdentityMatrix.of(n)));
  }

  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(MatrixLog1.class.getModifiers()));
  }
}
