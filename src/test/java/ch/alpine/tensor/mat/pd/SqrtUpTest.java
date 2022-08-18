// code by jph
package ch.alpine.tensor.mat.pd;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.PositiveSemidefiniteMatrixQ;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;

class SqrtUpTest {
  @Test
  void testRectangle() {
    Random random = new Random(2);
    for (int n = 1; n < 6; ++n) {
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), random, n, n);
      PolarDecomposition polarDecomposition = new SqrtUp(matrix);
      Tensor s = polarDecomposition.getPositiveSemidefinite();
      Tensor u = polarDecomposition.getUnitary();
      Tensor tensor = polarDecomposition.getUnitary().dot(s);
      PositiveSemidefiniteMatrixQ.ofHermitian(s);
      UnitaryMatrixQ.require(u);
      Chop._10.requireClose(tensor, matrix);
    }
  }
}
