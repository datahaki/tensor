// code by jph
package ch.alpine.tensor.mat.re;

import java.util.Random;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.pdf.CategoricalDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Conjugate;

/** Careful: convergence is dubious
 * 
 * Reference:
 * https://en.wikipedia.org/wiki/Kaczmarz_method */
public class KaczmarzIteration {
  private final Tensor matrix;
  private final Tensor b;
  private final Tensor normsq;
  private final Distribution distribution;
  private Tensor x;

  public KaczmarzIteration(Tensor matrix, Tensor b) {
    this.matrix = matrix;
    this.b = b;
    normsq = Tensor.of(matrix.stream().map(Vector2NormSquared::of));
    distribution = CategoricalDistribution.fromUnscaledPDF(normsq);
    x = Array.zeros(Unprotect.dimension1(matrix));
  }

  public Tensor refine(Random random) {
    // TODO for every batch generate discrete sequence with according occurrences
    return refine(RandomVariate.of(distribution, random).number().intValue());
  }

  public Tensor refine(int i) {
    Tensor a = matrix.get(i);
    Tensor ac = a.map(Conjugate.FUNCTION);
    Scalar fac = b.Get(i).subtract(a.dot(x)).divide(normsq.Get(i));
    return x = x.add(ac.multiply(fac));
  }
}
