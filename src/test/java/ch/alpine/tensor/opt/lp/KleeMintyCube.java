// code by jph
package ch.alpine.tensor.opt.lp;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.opt.lp.LinearProgram.ConstraintType;
import ch.alpine.tensor.opt.lp.LinearProgram.Objective;
import ch.alpine.tensor.opt.lp.LinearProgram.Variables;
import ch.alpine.tensor.sca.pow.Power;

/** Quote from Wikipedia:
 * 
 * <p>The Klee-Minty cube or Klee-Minty polytope (named after Victor Klee and George J. Minty)
 * is a unit hypercube of variable dimension whose corners have been perturbed.
 * Klee and Minty demonstrated that George Dantzig's simplex algorithm has poor worst-case
 * performance when initialized at one corner of their "squashed cube".
 * 
 * <p>In particular, many optimization algorithms for linear optimization
 * exhibit poor performance when applied to the Klee-Minty cube.
 * In 1973 Klee and Minty showed that Dantzig's simplex algorithm
 * was not a polynomial-time algorithm when applied to their cube.
 * Later, modifications of the Klee-Minty cube have shown poor behavior
 * both for other basis-exchange pivoting algorithms and also
 * for interior-point algorithms.
 * 
 * <p>https://en.wikipedia.org/wiki/Klee%E2%80%93Minty_cube */
/* package */ class KleeMintyCube {
  private static Scalar coefficient(int i, int j) {
    if (i < j)
      return RealScalar.ZERO;
    return i == j ? RealScalar.ONE : Power.of(2, i - j + 1);
  }

  private static final Cache<Integer, KleeMintyCube> CACHE = Cache.of(KleeMintyCube::new, 7);

  public static KleeMintyCube of(int n) {
    return CACHE.apply(n);
  }

  public final LinearProgram linearProgram;
  public final Tensor x; // solution

  private KleeMintyCube(int n) {
    linearProgram = LinearProgram.of(Objective.MAX, Tensors.vector(i -> Power.of(2, n - i - 1), n), //
        ConstraintType.LESS_EQUALS, //
        Tensors.matrix(KleeMintyCube::coefficient, n, n), //
        Tensors.vector(i -> Power.of(5, i + 1), n), //
        Variables.NON_NEGATIVE);
    x = Tensors.vector(i -> i < n - 1 ? RealScalar.ZERO : Power.of(5, n), n);
  }
}
