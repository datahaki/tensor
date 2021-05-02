// code by jph
package ch.alpine.tensor.mat;

import java.util.Arrays;
import java.util.List;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.MatrixDotTranspose;
import ch.alpine.tensor.lie.MatrixExp;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.pdf.CauchyDistribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class PolarDecompositionTest extends TestCase {
  private static void _check(Tensor matrix, PolarDecomposition polarDecomposition) {
    List<Integer> list = Dimensions.of(matrix);
    int k = list.get(0);
    Tolerance.CHOP.requireClose(Dot.of(polarDecomposition.getS(), polarDecomposition.getR()), matrix);
    Tensor result = polarDecomposition.getR();
    UnitaryMatrixQ.require(result, Chop._06);
    Tensor sym = polarDecomposition.getS();
    assertEquals(Dimensions.of(sym), Arrays.asList(k, k));
    HermitianMatrixQ.require(sym, Chop._06);
    assertTrue(polarDecomposition.toString().startsWith("PolarDecomposition["));
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
      } else {
        // System.out.println("---");
        // System.out.println(Det.of(matrix));
        // System.out.println(Det.of(r1));
        // System.out.println(Pretty.of(r1.map(Round._4)));
        // System.out.println(Pretty.of(r2.map(Round._4)));
      }
    }
  }

  public void testDet1Invariance() {
    int d = 7;
    for (int k = 1; k < d; ++k) {
      Tensor matrix = MatrixExp.of(TensorWedge.of(RandomVariate.of(NormalDistribution.of(0, 0.1), k, k)));
      Tolerance.CHOP.requireClose(Det.of(matrix), RealScalar.ONE);
      PolarDecomposition polarDecomposition = PolarDecomposition.of(matrix);
      _check(matrix, polarDecomposition);
      Tensor r1 = polarDecomposition.getR();
      Tolerance.CHOP.requireClose(Det.of(r1), RealScalar.ONE);
      Tensor result = Orthogonalize.usingSvd(matrix);
      Tolerance.CHOP.requireClose(result, matrix);
    }
  }

  public void testComplex() {
    Tensor matrix = Tensors.fromString("{{1, 0, 1+2*I}, {-3*I, 1, 1}}");
    Tensor mmt = MatrixDotTranspose.of(matrix, Conjugate.of(matrix));
    HermitianMatrixQ.require(mmt);
    PolarDecomposition polarDecomposition = PolarDecomposition.of(matrix);
    Tensor herm = polarDecomposition.getS().map(Tolerance.CHOP);
    HermitianMatrixQ.require(herm);
    Tensor result = polarDecomposition.getR();
    UnitaryMatrixQ.require(result, Chop._06);
    _check(matrix, polarDecomposition);
    // System.out.println(Pretty.of(herm));
  }

  public void testFail() {
    Tensor matrix = RandomVariate.of(CauchyDistribution.standard(), 5, 3);
    AssertFail.of(() -> PolarDecomposition.of(matrix));
  }
}