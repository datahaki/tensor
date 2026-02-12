// code by jph
package ch.alpine.tensor.mat.pd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
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
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.PositiveSemidefiniteMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.CauchyDistribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Sqrt;

class PolarDecompositionSqrtTest {
  private static void _check(Tensor matrix, PolarDecomposition polarDecomposition) {
    List<Integer> list = Dimensions.of(matrix);
    int k = list.get(0);
    Tolerance.CHOP.requireClose(Dot.of(polarDecomposition.getUnitary(), polarDecomposition.getPositiveSemidefinite()), matrix);
    Tensor result = polarDecomposition.getUnitary();
    new UnitaryMatrixQ(Chop._06).require(result);
    Tensor sym = polarDecomposition.getPositiveSemidefinite();
    assertEquals(Dimensions.of(sym), Arrays.asList(k, k));
    new HermitianMatrixQ(Chop._06).require(sym);
    assertTrue(polarDecomposition.toString().startsWith("PolarDecomposition["));
    boolean hermitian = PositiveSemidefiniteMatrixQ.ofHermitian(polarDecomposition.getPositiveSemidefinite());
    assertTrue(hermitian);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4 })
  void testRectangle(int k) {
    Random random = new Random(1);
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), random, k, 5);
    PolarDecomposition polarDecomposition = PolarDecomposition.up(matrix);
    Chop._06.requireClose(Dot.of(polarDecomposition.getUnitary(), polarDecomposition.getPositiveSemidefinite()), matrix);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 6 })
  void testSquare(int k) {
    Random random = new Random(3);
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), random, k, k);
    PolarDecomposition polarDecomposition = PolarDecomposition.up(matrix);
    _check(matrix, polarDecomposition);
    Tensor r1 = polarDecomposition.getUnitary();
    Tensor r2 = Orthogonalize.usingSvd(matrix);
    assumeTrue(Sign.isPositive(Det.of(matrix)) && Sign.isPositive(Det.of(r1)));
    Tolerance.CHOP.requireClose(r1, r2);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 6 })
  void testDet1Invariance(int k) {
    Random random = new Random(5);
    Tensor matrix = MatrixExp.of(TensorWedge.of(RandomVariate.of(NormalDistribution.of(0, 0.1), random, k, k)));
    Tolerance.CHOP.requireClose(Det.of(matrix), RealScalar.ONE);
    PolarDecomposition polarDecomposition = PolarDecomposition.up(matrix);
    _check(matrix, polarDecomposition);
    Tensor r1 = polarDecomposition.getUnitary();
    Tolerance.CHOP.requireClose(Det.of(r1), RealScalar.ONE);
    Tensor result = Orthogonalize.usingSvd(matrix);
    Tolerance.CHOP.requireClose(result, matrix);
  }

  @Test
  void testStrang() throws ClassNotFoundException, IOException {
    Tensor matrix = Tensors.fromString("{{3, 0}, {4, 5}}");
    PolarDecomposition polarDecomposition = Serialization.copy(PolarDecomposition.up(matrix));
    Tensor expect = Tensors.fromString("{{2, 1}, {1, 2}}").multiply(Sqrt.FUNCTION.apply(RealScalar.of(5)));
    Tolerance.CHOP.requireClose(polarDecomposition.getPositiveSemidefinite(), expect);
  }
  // public void testComplex() {
  // Tensor matrix = Tensors.fromString("{{1, 0, 1+2*I}, {-3*I, 1, 1}}");
  // Tensor mmt = Conjugate.of(matrix).dot(matrix );
  // HermitianMatrixQ.require(mmt);
  // PolarDecomposition polarDecomposition = PolarDecomposition.us(matrix);
  // Tensor herm = polarDecomposition.getPositiveSemidefinite().map(Tolerance.CHOP);
  // HermitianMatrixQ.require(herm);
  // Tensor result = polarDecomposition.getUnitary();
  // UnitaryMatrixQ.require(result, Chop._06);
  // _check(matrix, polarDecomposition);
  // }

  @Test
  void testSvd() {
    Random random = new Random(3);
    Tensor matrix = RandomVariate.of(CauchyDistribution.standard(), random, 5, 3);
    PolarDecomposition pd_qs = PolarDecomposition.up(matrix);
    Tolerance.CHOP.requireClose(pd_qs.getUnitary().dot(pd_qs.getPositiveSemidefinite()), matrix);
  }
}
