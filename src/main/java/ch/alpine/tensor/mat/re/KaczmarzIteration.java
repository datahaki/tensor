// code by jph
package ch.alpine.tensor.mat.re;

import java.util.Random;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.mat.pi.LeastSquares;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.CategoricalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.sca.Conjugate;

/** Experiments have shown that the iteration is most suitable
 * for system of equations with many more variables than equations,
 * i.e. where matrix rows << cols.
 * 
 * In that case, the iteration is expected to converge to
 * LeastSquares.of(matrix, b)
 * 
 * Careful:
 * Many iterations may be needed for an accurate solution.
 * When exact precision is used the result consists of lengthy fractions.
 * 
 * Reference:
 * https://en.wikipedia.org/wiki/Kaczmarz_method 
 * 
 * @see LeastSquares */
public class KaczmarzIteration {
  public static KaczmarzIteration of(Tensor matrix, Tensor b) {
    Tensor mat = Tensors.empty();
    Tensor rhs = b.copy();
    for (int i = 0; i < b.length(); ++i) {
      Tensor row = matrix.get(i);
      Scalar norm = Vector2Norm.of(row);
      mat.append(row.divide(norm));
      rhs.set(norm::under, i);
    }
    return new KaczmarzIteration(mat, rhs);
  }

  private final Tensor[] rows;
  private final Tensor b;
  private final Tensor normsq;
  private final Distribution distribution;
  private Tensor x;

  public KaczmarzIteration(Tensor matrix, Tensor b) {
    rows = matrix.stream().toArray(Tensor[]::new);
    this.b = b;
    normsq = Tensor.of(matrix.stream().map(Vector2NormSquared::of));
    distribution = CategoricalDistribution.fromUnscaledPDF(normsq);
    // TODO TENSOR TEST for mixed units
    int m = Unprotect.dimension1(matrix);
    x = Tensors.vector(i -> Quantity.of(0, //
        QuantityUnit.of(b.Get(0)).add(QuantityUnit.of(matrix.Get(0, i)).negate())), m);
    // System.out.println(x);
  }
  // TODO also relaxation parameter

  /** fixed rate convergence
   * 
   * @param i
   * @return */
  public Tensor refine(int i) {
    Tensor a = rows[i];
    Tensor ac = a.map(Conjugate.FUNCTION);
    // LenientAdd.of(a, ac);
    // TODO TENSOR does this dot product need lenient add?
    Scalar fac = b.Get(i).subtract(a.dot(x)).divide(normsq.Get(i));
    return x = x.add(ac.multiply(fac));
  }

  public Tensor refine() {
    Tensor x = null;
    for (int i = 0; i < b.length(); ++i)
      x = refine(i);
    return x;
  }

  /** Careful: in our experiments the randomization slows down convergence rate (!)
   * 
   * @param random
   * @return */
  public Tensor refine(Random random) {
    // QUEST TENSOR MAT for every batch generate discrete sequence with according occurrences
    return refine(RandomVariate.of(distribution, random).number().intValue());
  }
}
