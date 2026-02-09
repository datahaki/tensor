// code by jph
package ch.alpine.tensor.mat.pd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.MatrixDotConjugateTranspose;
import ch.alpine.tensor.mat.PositiveSemidefiniteMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;

class PolarDecompositionTest {
  private static void _check(Tensor matrix, PolarDecomposition polarDecomposition) {
    List<Integer> list = Dimensions.of(matrix);
    int k = list.get(0);
    Tolerance.CHOP.requireClose(Dot.of(polarDecomposition.getPositiveSemidefinite(), polarDecomposition.getUnitary()), matrix);
    Tensor result = polarDecomposition.getUnitary();
    new UnitaryMatrixQ(Chop._06).requireMember(result);
    Tensor sym = polarDecomposition.getPositiveSemidefinite();
    assertEquals(Dimensions.of(sym), Arrays.asList(k, k));
    new HermitianMatrixQ(Chop._06).requireMember(sym);
    assertTrue(polarDecomposition.toString().startsWith("PolarDecomposition["));
    boolean hermitian = PositiveSemidefiniteMatrixQ.ofHermitian(polarDecomposition.getPositiveSemidefinite());
    assertTrue(hermitian);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 3, 4 })
  void testRectangle(int k) {
    Random random = new Random(2);
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), random, k, 5);
    PolarDecomposition polarDecomposition = PolarDecomposition.pu(matrix);
    _check(matrix, polarDecomposition);
    Tensor r1 = polarDecomposition.getUnitary();
    Tensor r2 = Orthogonalize.usingSvd(matrix);
    Tolerance.CHOP.requireClose(r1, r2);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 3, 4, 6 })
  void testSquare(int k) {
    Random random = new Random(3);
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), random, k, k);
    PolarDecomposition polarDecomposition = PolarDecomposition.pu(matrix);
    _check(matrix, polarDecomposition);
    Tensor r1 = polarDecomposition.getUnitary();
    Tensor r2 = Orthogonalize.usingSvd(matrix);
    assumeTrue(Sign.isPositive(Det.of(matrix)) && Sign.isPositive(Det.of(r1)));
    Tolerance.CHOP.requireClose(r1, r2);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 3, 4, 6 })
  void testDet1Invariance(int k) {
    Random random = new Random(5);
    Tensor matrix = MatrixExp.of(TensorWedge.of(RandomVariate.of(NormalDistribution.of(0, 0.1), random, k, k)));
    Tolerance.CHOP.requireClose(Det.of(matrix), RealScalar.ONE);
    PolarDecomposition polarDecomposition = PolarDecomposition.pu(matrix);
    _check(matrix, polarDecomposition);
    Tensor r1 = polarDecomposition.getUnitary();
    Tolerance.CHOP.requireClose(Det.of(r1), RealScalar.ONE);
    Tensor result = Orthogonalize.usingSvd(matrix);
    Tolerance.CHOP.requireClose(result, matrix);
  }

  @Test
  void testComplex() {
    Tensor matrix = Tensors.fromString("{{1, 0, 1+2*I}, {-3*I, 1, 1}}");
    Tensor mmt = MatrixDotConjugateTranspose.self(matrix);
    HermitianMatrixQ.INSTANCE.requireMember(mmt);
    PolarDecomposition polarDecomposition = PolarDecomposition.pu(matrix);
    Tensor herm = polarDecomposition.getPositiveSemidefinite().maps(Tolerance.CHOP);
    HermitianMatrixQ.INSTANCE.requireMember(herm);
    Tensor result = polarDecomposition.getUnitary();
    new UnitaryMatrixQ(Chop._06).requireMember(result);
    _check(matrix, polarDecomposition);
  }
}
