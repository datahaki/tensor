// code by jph
package ch.alpine.tensor.mat.pd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.PositiveDefiniteMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.CauchyDistribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.pow.Sqrt;

class PolarDecompositionSvdTest {
  @Test
  void testSvd() {
    Random random = new Random(3);
    Tensor matrix = RandomVariate.of(CauchyDistribution.standard(), random, 5, 3);
    PolarDecomposition pd_qs = PolarDecomposition.pu(matrix);
    Tolerance.CHOP.requireClose(pd_qs.getPositiveSemidefinite().dot(pd_qs.getUnitary()), matrix);
    PolarDecomposition pd_kq = PolarDecompositionSvd.pu(matrix);
    Tolerance.CHOP.requireClose(pd_kq.getPositiveSemidefinite().dot(pd_kq.getUnitary()), matrix);
  }

  @Test
  void testStrang() throws ClassNotFoundException, IOException {
    Tensor matrix = Tensors.fromString("{{3, 0}, {4, 5}}");
    PolarDecompositionSvd polarDecompositionSvd = Serialization.copy(PolarDecompositionSvd.up(matrix));
    Tensor q = polarDecompositionSvd.getUnitary();
    OrthogonalMatrixQ.INSTANCE.requireMember(q);
    Tensor qe = Tensors.fromString("{{2, -1}, {1, 2}}").divide(Sqrt.FUNCTION.apply(RealScalar.of(5)));
    Tolerance.CHOP.requireClose(q, qe);
    Tensor s = polarDecompositionSvd.getPositiveSemidefinite();
    Tolerance.CHOP.requireClose(q.dot(s), matrix);
    assertTrue(PositiveDefiniteMatrixQ.ofHermitian(s));
    Tensor se = Tensors.fromString("{{2, 1}, {1, 2}}").multiply(Sqrt.FUNCTION.apply(RealScalar.of(5)));
    Tolerance.CHOP.requireClose(s, se);
    Tolerance.CHOP.requireClose( //
        polarDecompositionSvd.getConjugateTransposeUnitary(), //
        ConjugateTranspose.of(polarDecompositionSvd.getUnitary()));
  }

  @Test
  void testTransposeUsingSvd() {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 4, 3);
    PolarDecompositionSvd polarDecompositionSvd = PolarDecompositionSvd.pu(matrix);
    Tensor result = polarDecompositionSvd.getUnitary();
    OrthogonalMatrixQ.INSTANCE.requireMember(Transpose.of(result));
    assertEquals(Dimensions.of(result), Dimensions.of(matrix));
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 4 })
  void testMatrixExp(int d) {
    Tensor matrix = MatrixExp.of(TensorWedge.of(RandomVariate.of(UniformDistribution.unit(), d, d)));
    OrthogonalMatrixQ.INSTANCE.requireMember(matrix);
    PolarDecompositionSvd polarDecompositionSvd = PolarDecompositionSvd.pu(matrix);
    Tolerance.CHOP.requireClose(matrix, polarDecompositionSvd.getUnitaryWithDetOne());
    Tolerance.CHOP.requireClose(matrix, polarDecompositionSvd.getUnitaryWithDetOne2());
  }

  @Test
  void testDiag() {
    Tensor matrix = DiagonalMatrix.of(2, -2);
    Tensor rdetp1 = DiagonalMatrix.of(1, +1);
    PolarDecompositionSvd polarDecompositionSvd = PolarDecompositionSvd.pu(matrix);
    Tolerance.CHOP.requireClose(rdetp1, polarDecompositionSvd.getUnitaryWithDetOne());
    Tolerance.CHOP.requireClose(rdetp1, polarDecompositionSvd.getUnitaryWithDetOne2());
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 4, 9 })
  void testAlternatives(int d) {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), d, d);
    PolarDecompositionSvd polarDecompositionSvd = PolarDecompositionSvd.pu(matrix);
    Tensor u1 = polarDecompositionSvd.getUnitaryWithDetOne();
    Tensor u2 = polarDecompositionSvd.getUnitaryWithDetOne2();
    Tolerance.CHOP.requireClose(u1, u2);
    OrthogonalMatrixQ.INSTANCE.requireMember(u1);
    OrthogonalMatrixQ.INSTANCE.requireMember(Transpose.of(u1));
    UnitaryMatrixQ.INSTANCE.requireMember(u1);
  }
}
