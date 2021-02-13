// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.LogisticDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.TrapezoidalDistribution;
import ch.ethz.idsc.tensor.red.Entrywise;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.red.Trace;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BenIsraelCohenTest extends TestCase {
  public void testSimple() {
    Distribution distribution = LogisticDistribution.of(1, 5);
    for (int r = 1; r < 5; ++r) {
      Tensor p1 = RandomVariate.of(distribution, 8, r);
      Tensor p2 = RandomVariate.of(distribution, r, 4);
      Tensor design = p1.dot(p2);
      BenIsraelCohen benIsraelCohen = new BenIsraelCohen(design);
      Tensor pinv = benIsraelCohen.of(Chop._08, 100);
      InfluenceMatrix influenceMatrix = new InfluenceMatrixExact(design.dot(pinv));
      Tensor leverages = influenceMatrix.leverages();
      Tolerance.CHOP.requireClose(Total.ofVector(leverages), RealScalar.of(r));
    }
  }

  public void testMathematica() {
    // for the matrix in this example the algorithm PseudoInverse gives
    // a result that deviates from Mathematica's solution significantly
    Tensor matrix = ResourceData.of("/mat/bic1.csv");
    Tensor mathem = ResourceData.of("/mat/bic1pinv.csv");
    BenIsraelCohen benIsraelCohen = new BenIsraelCohen(matrix);
    Tensor pinv = benIsraelCohen.of(Chop._08, 100);
    Tolerance.CHOP.requireClose(pinv, mathem);
  }

  public void testReal() {
    Distribution distribution = TrapezoidalDistribution.of(-3, -1, 1, 3);
    Tensor p1 = RandomVariate.of(distribution, 8, 3);
    Tensor p2 = RandomVariate.of(distribution, 3, 4);
    Tensor matrix = p1.dot(p2);
    BenIsraelCohen benIsraelCohen = new BenIsraelCohen(matrix);
    Tensor refine = benIsraelCohen.of(Chop._12, 100);
    Tensor pinv = PseudoInverse.of(matrix);
    Tolerance.CHOP.requireClose(pinv, refine);
  }

  public void testComplexFullRank() {
    Distribution distribution = TrapezoidalDistribution.of(-3, -1, 1, 3);
    Tensor re = RandomVariate.of(distribution, 5, 3);
    Tensor im = RandomVariate.of(distribution, 5, 3);
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply(re, im);
    BenIsraelCohen benIsraelCohen = new BenIsraelCohen(matrix);
    Tensor refine = benIsraelCohen.of(Chop._12, 100);
    Tensor pinv = PseudoInverse.of(matrix);
    Tolerance.CHOP.requireClose(pinv, refine);
    Tolerance.CHOP.requireClose(refine.dot(matrix), IdentityMatrix.of(3));
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
      BenIsraelCohen benIsraelCohen = new BenIsraelCohen(matrix);
      Tensor refine = benIsraelCohen.of(Chop._12, 100);
      Scalar rank = Trace.of(matrix.dot(refine));
      Tolerance.CHOP.requireClose(rank, RealScalar.of(r));
    }
  }
}
