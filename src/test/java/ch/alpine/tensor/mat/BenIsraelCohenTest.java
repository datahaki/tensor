// code by jph
package ch.alpine.tensor.mat;

import java.util.Random;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.MatrixQ;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.mat.gr.InfluenceMatrixQ;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.LogisticDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TrapezoidalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.red.Trace;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class BenIsraelCohenTest extends TestCase {
  public void testQuantity() {
    Distribution distribution = LogisticDistribution.of(1, 5);
    ScalarUnaryOperator suo = QuantityMagnitude.singleton("K^1/2*m^-1");
    for (int r = 1; r < 5; ++r) {
      Tensor p1 = RandomVariate.of(distribution, 8, r);
      Tensor p2 = RandomVariate.of(distribution, r, 4).map(s -> Quantity.of(s, "m*K^-1/2"));
      Tensor design = p1.dot(p2);
      Tensor pinv = BenIsraelCohen.of(design);
      suo.apply(pinv.Get(0, 0));
      InfluenceMatrixQ.require(design.dot(pinv), Chop._10); // 1e-12 does not always work
    }
  }

  public void testMathematica() {
    Tensor matrix = ResourceData.of("/mat/bic1.csv");
    Tensor mathem = ResourceData.of("/mat/bic1pinv.csv");
    Tensor pinv = BenIsraelCohen.of(matrix);
    Tolerance.CHOP.requireClose(pinv, mathem);
    SingularValueDecomposition svd = SingularValueDecomposition.of(matrix);
    Chop._08.requireClose(pinv, PseudoInverse.of(svd));
  }

  public void testMixedUnitsSquare() {
    Tensor matrix = Tensors.fromString( //
        "{{-4/5[m^-2], 3/10[m^-1*rad^-1]}, {3/10[m^-1*rad^-1], -1/20[rad^-2]}}");
    Tensor inv1 = Inverse.of(matrix);
    Tensor pinv = BenIsraelCohen.of(matrix);
    Tolerance.CHOP.requireClose(inv1, pinv);
  }

  public void testMixedUnitsGeneral() {
    Tensor matrix = Tensors.fromString( //
        "{{-4/5[m], 3/10[m], 1/2[m]}, {3[s], -2[s], 1[s]}}");
    Tensor pinv1 = BenIsraelCohen.of(matrix);
    Tensor pinv2 = BenIsraelCohen.of(Transpose.of(matrix));
    Tolerance.CHOP.requireClose(Transpose.of(pinv1), pinv2);
    InfluenceMatrixQ.require(pinv1.dot(matrix));
  }

  public void testMixedUnitsFail() {
    Tensor matrix = Tensors.fromString( //
        "{{-4/5[kg], 3/10[m], 1/2[m]}, {3[s], -2[s], 1[s]}}");
    AssertFail.of(() -> BenIsraelCohen.of(matrix));
  }

  public void testZeros() {
    Tensor refine = BenIsraelCohen.of(Array.zeros(4, 3));
    assertEquals(refine, Array.zeros(3, 4));
  }

  public void testEpsilonNonFail() {
    BenIsraelCohen.of(Tensors.fromString("{{1E-300}}"));
  }

  public void testReal() {
    Random random = new Random(3);
    Distribution distribution = TrapezoidalDistribution.of(-3, -1, 1, 3);
    Tensor p1 = RandomVariate.of(distribution, random, 8, 3);
    Tensor p2 = RandomVariate.of(distribution, random, 3, 4);
    Tensor matrix = p1.dot(p2);
    Tensor refine = BenIsraelCohen.of(matrix);
    Chop._09.requireClose(PseudoInverse.of(matrix), refine);
    InfluenceMatrixQ.require(refine.dot(matrix));
  }

  public void testExceedIters() {
    Tensor matrix = ResourceData.of("/mat/bic_fail.csv");
    MatrixQ.require(matrix);
    AssertFail.of(() -> BenIsraelCohen.of(matrix));
  }

  public void testComplexFullRank() {
    Distribution distribution = TrapezoidalDistribution.of(-3, -1, 1, 3);
    Tensor re = RandomVariate.of(distribution, 5, 3);
    Tensor im = RandomVariate.of(distribution, 5, 3);
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply(re, im);
    Tensor refine = BenIsraelCohen.of(matrix);
    Tolerance.CHOP.requireClose(refine.dot(matrix), IdentityMatrix.of(3));
    Tensor pinvtr = BenIsraelCohen.of(Transpose.of(matrix));
    Tolerance.CHOP.requireClose(Transpose.of(pinvtr), refine);
  }

  public void testComplexRankDeficient() {
    Distribution distribution = TrapezoidalDistribution.of(-3, -1, 1, 3);
    for (int r = 1; r < 5; ++r) {
      Tensor p1 = Entrywise.with(ComplexScalar::of).apply( //
          RandomVariate.of(distribution, 5, r), //
          RandomVariate.of(distribution, 5, r));
      Tensor p2 = Entrywise.with(ComplexScalar::of).apply( //
          RandomVariate.of(distribution, r, 4), //
          RandomVariate.of(distribution, r, 4));
      Tensor matrix = p1.dot(p2);
      Tensor refine = BenIsraelCohen.of(matrix);
      Scalar rank = Trace.of(matrix.dot(refine));
      Tolerance.CHOP.requireClose(rank, RealScalar.of(r));
    }
  }

  public void testAbsurd() {
    AssertFail.of(() -> BenIsraelCohen.of(Tensors.fromString("{{NaN}}")));
    AssertFail.of(() -> BenIsraelCohen.of(Tensors.fromString("{{Infinity}}")));
    AssertFail.of(() -> BenIsraelCohen.of(Tensors.fromString("{{1.7976931348623157e+308}}")));
  }
}
