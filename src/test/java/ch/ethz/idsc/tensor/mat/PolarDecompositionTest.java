// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.pdf.CauchyDistribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class PolarDecompositionTest extends TestCase {
  private static void _check(Tensor matrix, PolarDecomposition polarDecomposition) {
    List<Integer> list = Dimensions.of(matrix);
    int k = list.get(0);
    Tolerance.CHOP.requireClose(Dot.of(polarDecomposition.getS(), polarDecomposition.getR()), matrix);
    Tensor result = polarDecomposition.getR();
    OrthogonalMatrixQ.require(result, Chop._06);
    Chop.NONE.requireAllZero(Imag.of(result));
    Tensor sym = polarDecomposition.getS();
    assertEquals(Dimensions.of(sym), Arrays.asList(k, k));
    SymmetricMatrixQ.require(sym);
  }

  public void testRectangle() {
    int n = 5;
    for (int k = 1; k < n; ++k) {
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), k, 5);
      PolarDecomposition polarDecomposition = PolarDecomposition.of(matrix);
      _check(matrix, polarDecomposition);
      Tensor r1 = polarDecomposition.getR();
      Tensor r2 = Orthogonalize.usingSvd(matrix);
      Tolerance.CHOP.requireClose(r1, r2);
    }
  }

  public void testSquare() {
    int d = 7;
    for (int k = 1; k < d; ++k) {
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), k, k);
      PolarDecomposition polarDecomposition = PolarDecomposition.of(matrix);
      _check(matrix, polarDecomposition);
      Tensor r1 = polarDecomposition.getR();
      Tensor r2 = Orthogonalize.usingSvd(matrix);
      if (Sign.isPositive(Det.of(matrix)) && Sign.isPositive(Det.of(r1))) {
        Chop._06.requireClose(r1, r2);
      }
      // else {
      // System.out.println(Det.of(matrix));
      // System.out.println(Det.of(r1));
      // System.out.println(Pretty.of(r1.map(Round._7)));
      // System.out.println(Pretty.of(r2.map(Round._7)));
      // }
    }
  }

  public void testFail() {
    Tensor matrix = RandomVariate.of(CauchyDistribution.standard(), 5, 3);
    AssertFail.of(() -> PolarDecomposition.of(matrix));
  }
}