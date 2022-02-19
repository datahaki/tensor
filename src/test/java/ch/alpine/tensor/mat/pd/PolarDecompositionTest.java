// code by jph
package ch.alpine.tensor.mat.pd;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.Orthogonalize;
import ch.alpine.tensor.mat.PositiveSemidefiniteMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.CauchyDistribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.Sqrt;
import junit.framework.TestCase;

public class PolarDecompositionTest extends TestCase {
  private static void _check(Tensor matrix, PolarDecomposition polarDecomposition) {
    List<Integer> list = Dimensions.of(matrix);
    int k = list.get(0);
    Tolerance.CHOP.requireClose(Dot.of(polarDecomposition.getPositiveSemidefinite(), polarDecomposition.getUnitary()), matrix);
    Tensor result = polarDecomposition.getUnitary();
    UnitaryMatrixQ.require(result, Chop._06);
    Tensor sym = polarDecomposition.getPositiveSemidefinite();
    assertEquals(Dimensions.of(sym), Arrays.asList(k, k));
    HermitianMatrixQ.require(sym, Chop._06);
    assertTrue(polarDecomposition.toString().startsWith("PolarDecomposition["));
    boolean hermitian = PositiveSemidefiniteMatrixQ.ofHermitian(polarDecomposition.getPositiveSemidefinite());
    assertTrue(hermitian);
  }

  public void testRectangle() {
    Random random = new Random(2);
    int n = 5;
    for (int k = 1; k < n; ++k) {
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), random, k, 5);
      PolarDecomposition polarDecomposition = PolarDecomposition.su(matrix);
      _check(matrix, polarDecomposition);
      Tensor r1 = polarDecomposition.getUnitary();
      Tensor r2 = Orthogonalize.usingSvd(matrix);
      Tolerance.CHOP.requireClose(r1, r2);
    }
  }

  public void testSquare() {
    Random random = new Random(3);
    int d = 7;
    for (int k = 1; k < d; ++k) {
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), random, k, k);
      PolarDecomposition polarDecomposition = PolarDecomposition.su(matrix);
      _check(matrix, polarDecomposition);
      Tensor r1 = polarDecomposition.getUnitary();
      Tensor r2 = Orthogonalize.usingSvd(matrix);
      if (Sign.isPositive(Det.of(matrix)) && Sign.isPositive(Det.of(r1))) {
        Tolerance.CHOP.requireClose(r1, r2);
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
    Random random = new Random(5);
    int d = 7;
    for (int k = 1; k < d; ++k) {
      Tensor matrix = MatrixExp.of(TensorWedge.of(RandomVariate.of(NormalDistribution.of(0, 0.1), random, k, k)));
      Tolerance.CHOP.requireClose(Det.of(matrix), RealScalar.ONE);
      PolarDecomposition polarDecomposition = PolarDecomposition.su(matrix);
      _check(matrix, polarDecomposition);
      Tensor r1 = polarDecomposition.getUnitary();
      Tolerance.CHOP.requireClose(Det.of(r1), RealScalar.ONE);
      Tensor result = Orthogonalize.usingSvd(matrix);
      Tolerance.CHOP.requireClose(result, matrix);
    }
  }

  public void testStrang() throws ClassNotFoundException, IOException {
    Tensor matrix = Tensors.fromString("{{3, 0}, {4, 5}}");
    PolarDecomposition polarDecomposition = Serialization.copy(PolarDecomposition.su(matrix));
    Tensor s = polarDecomposition.getPositiveSemidefinite().multiply(Sqrt.FUNCTION.apply(RealScalar.of(5)));
    Tensor expect = Tensors.fromString("{{6, 3}, {3, 14}}");
    // TODO strang p67 says otherwise
    Tolerance.CHOP.requireClose(s, expect);
  }

  public void testComplex() {
    Tensor matrix = Tensors.fromString("{{1, 0, 1+2*I}, {-3*I, 1, 1}}");
    Tensor mmt = MatrixDotTranspose.of(matrix, Conjugate.of(matrix));
    HermitianMatrixQ.require(mmt);
    PolarDecomposition polarDecomposition = PolarDecomposition.su(matrix);
    Tensor herm = polarDecomposition.getPositiveSemidefinite().map(Tolerance.CHOP);
    HermitianMatrixQ.require(herm);
    Tensor result = polarDecomposition.getUnitary();
    UnitaryMatrixQ.require(result, Chop._06);
    _check(matrix, polarDecomposition);
    // System.out.println(Pretty.of(herm));
  }

  public void testSvd() {
    Random random = new Random(3);
    Tensor matrix = RandomVariate.of(CauchyDistribution.standard(), random, 5, 3);
    PolarDecomposition pd_qs = PolarDecomposition.su(matrix);
    Tolerance.CHOP.requireClose(pd_qs.getPositiveSemidefinite().dot(pd_qs.getUnitary()), matrix);
    SvdSu pd_kq = new SvdSu(SingularValueDecomposition.of(matrix));
    Tolerance.CHOP.requireClose(pd_kq.getPositiveSemidefinite().dot(pd_kq.getUnitary()), matrix);
  }
}