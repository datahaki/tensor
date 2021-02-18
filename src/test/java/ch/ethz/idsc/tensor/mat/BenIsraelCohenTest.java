// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.LogisticDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.TrapezoidalDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.red.Entrywise;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.red.Trace;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
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
      InfluenceMatrix influenceMatrix = new InfluenceMatrixExact(design.dot(pinv));
      Tensor leverages = influenceMatrix.leverages();
      Chop._09.requireClose(Total.ofVector(leverages), RealScalar.of(r));
    }
  }

  public void testMathematica() {
    Tensor matrix = ResourceData.of("/mat/bic1.csv");
    Tensor mathem = ResourceData.of("/mat/bic1pinv.csv");
    Tensor pinv = BenIsraelCohen.of(matrix);
    Tolerance.CHOP.requireClose(pinv, mathem);
    SingularValueDecomposition svd = SingularValueDecomposition.of(matrix);
    Chop._08.requireClose(pinv, PseudoInverse.of(svd));
    // for the matrix in this example the algorithm PseudoInverse gives
    // a result that deviates from Mathematica's solution significantly
    // TODO investigate why QR decomp does not work well here!?
  }

  public void testZeros() {
    Tensor refine = BenIsraelCohen.of(Array.zeros(4, 3));
    assertEquals(refine, Array.zeros(3, 4));
  }

  public void testEpsilonNonFail() {
    BenIsraelCohen.of(Tensors.fromString("{{1E-300}}"));
  }

  public void testReal() {
    Distribution distribution = TrapezoidalDistribution.of(-3, -1, 1, 3);
    Tensor p1 = RandomVariate.of(distribution, 8, 3);
    Tensor p2 = RandomVariate.of(distribution, 3, 4);
    Tensor matrix = p1.dot(p2);
    Tensor refine = BenIsraelCohen.of(matrix);
    Tolerance.CHOP.requireClose(PseudoInverse.of(matrix), refine);
    InfluenceMatrix influenceMatrix = new InfluenceMatrixExact(refine.dot(matrix));
    Chop._09.requireClose(Total.ofVector(influenceMatrix.leverages()), RealScalar.of(3));
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
