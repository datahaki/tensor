// code by jph
package ch.alpine.tensor.mat.pd;

import java.util.Random;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.PositiveSemidefiniteMatrixQ;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class SqrtUpTest extends TestCase {
  public void testRectangle() {
    Random random = new Random(2);
    for (int n = 1; n < 6; ++n) {
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), random, n, n);
      PolarDecomposition polarDecomposition = new SqrtUp(matrix);
      Tensor s = polarDecomposition.getPositiveSemidefinite();
      Tensor u = polarDecomposition.getUnitary();
      Tensor tensor = polarDecomposition.getUnitary().dot(s);
      PositiveSemidefiniteMatrixQ.ofHermitian(s);
      // System.out.println(Pretty.of(u.map(Round._3)));
      UnitaryMatrixQ.require(u);
      Chop._10.requireClose(tensor, matrix);
    }
  }
}
