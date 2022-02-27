// code by jph
package ch.alpine.tensor.mat.ev;

import java.util.Random;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.BasisTransform;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import junit.framework.TestCase;

public class JacobiRealTest extends TestCase {
  public void testRandom() {
    Random random = new Random(1);
    Distribution distribution = UniformDistribution.of(-10, 10);
    for (int d = 2; d < 10; ++d)
      for (int count = 0; count < 5; ++count) {
        Tensor matrix = Symmetrize.of(RandomVariate.of(distribution, random, d, d));
        Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
        Tensor diagon = DiagonalMatrix.with(eigensystem.values());
        Tensor m1 = BasisTransform.ofMatrix(diagon, eigensystem.vectors());
        Tolerance.CHOP.requireClose(m1, matrix);
      }
  }
}
