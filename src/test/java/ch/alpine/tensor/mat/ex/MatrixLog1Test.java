// code by jph
package ch.alpine.tensor.mat.ex;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;

class MatrixLog1Test {
  @Test
  public void test1x1() {
    for (int count = 0; count < 10; ++count) {
      Tensor x = RandomVariate.of(UniformDistribution.of(-10, 6), 1, 1);
      Tensor exp = MatrixExp.of(x);
      Tensor log = MatrixLog._of(exp);
      Tensor cmp = MatrixLog1.of(exp);
      Chop._04.requireClose(log, cmp);
    }
  }

  @Test
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(MatrixLog1.class.getModifiers()));
  }
}
