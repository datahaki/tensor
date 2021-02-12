// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.PseudoInverse;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.LogisticDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.TrapezoidalDistribution;
import ch.ethz.idsc.tensor.red.Entrywise;
import ch.ethz.idsc.tensor.red.Trace;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BenIsraelCohenTest extends TestCase {
  public void testSimple() {
    Distribution distribution = LogisticDistribution.of(1, 5);
    for (int r = 1; r < 5; ++r) {
      Tensor p1 = RandomVariate.of(distribution, 8, r);
      Tensor p2 = RandomVariate.of(distribution, r, 4);
      Tensor matrix = p1.dot(p2);
      Tensor pinv = PseudoInverse.of(matrix);
      Tensor refine = BenIsraelCohen.refine(matrix, pinv);
      Chop._08.requireClose(pinv, refine);
    }
  }

  public void testReal() {
    Distribution distribution = TrapezoidalDistribution.of(-3, -1, 1, 3);
    Tensor p1 = RandomVariate.of(distribution, 8, 3);
    Tensor p2 = RandomVariate.of(distribution, 3, 4);
    Tensor matrix = p1.dot(p2);
    Tensor refine = BenIsraelCohen.of(matrix, Chop._12, 100);
    Tensor pinv = PseudoInverse.of(matrix);
    Tolerance.CHOP.requireClose(pinv, refine);
  }

  public void testComplexFullRank() {
    Distribution distribution = TrapezoidalDistribution.of(-3, -1, 1, 3);
    Tensor re = RandomVariate.of(distribution, 5, 3);
    Tensor im = RandomVariate.of(distribution, 5, 3);
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply(re, im);
    Tensor refine = BenIsraelCohen.of(matrix, Chop._12, 100);
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
      Tensor refine = BenIsraelCohen.of(matrix, Chop._12, 100);
      Scalar rank = Trace.of(matrix.dot(refine));
      Tolerance.CHOP.requireClose(rank, RealScalar.of(r));
    }
  }
}
