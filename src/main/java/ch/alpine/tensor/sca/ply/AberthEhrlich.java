// code by jph
package ch.alpine.tensor.sca.ply;

import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.DeterminateScalarQ;
import ch.alpine.tensor.chq.FiniteTensorQ;
import ch.alpine.tensor.ext.Int;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.VectorInfinityNorm;
import ch.alpine.tensor.opt.fnd.FindRoot;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.ComplexDiskUniformDistribution;
import ch.alpine.tensor.sca.Abs;

/** https://en.wikipedia.org/wiki/Aberth_method
 * 
 * @see FindRoot */
public class AberthEhrlich {
  private static final int MAX_ATTEMPTS = 5;
  private static final int MAX_ITERATIONS = 32;

  /** @param polynomial of degree at least 2
   * @return unsorted roots of polynomial
   * @throws Exception if convergence fail */
  public static Tensor of(Polynomial polynomial) {
    return of(polynomial, ThreadLocalRandom.current());
  }

  /** @param polynomial of degree at least 2
   * @param randomGenerator to generate seeds
   * @return unsorted roots of polynomial
   * @throws Exception if convergence fail */
  public static Tensor of(Polynomial polynomial, RandomGenerator randomGenerator) {
    Scalar radius = Roots.bound(polynomial.coeffs());
    Distribution distribution = ComplexDiskUniformDistribution.of(radius);
    for (int attempt = 0; attempt < MAX_ATTEMPTS; ++attempt)
      try {
        Tensor vector = RandomVariate.of(distribution, randomGenerator, polynomial.degree());
        AberthEhrlich aberthEhrlich = new AberthEhrlich(polynomial, vector);
        for (int iteration = 0; iteration < MAX_ITERATIONS; ++iteration) {
          vector = aberthEhrlich.iterate();
          for (int k = 0; k < vector.length(); ++k) {
            Scalar cand = vector.Get(k);
            if (DeterminateScalarQ.of(cand)) {
              Scalar abs = Abs.FUNCTION.apply(cand);
              if (Scalars.lessThan(radius, abs))
                vector.set(cand.divide(abs).multiply(radius), k);
            } else {
              vector.set(RandomVariate.of(distribution, randomGenerator), k);
            }
          }
          FiniteTensorQ.require(vector);
          Tensor eval = vector.map(polynomial);
          FiniteTensorQ.require(eval);
          Scalar err = VectorInfinityNorm.of(eval);
          if (Tolerance.CHOP.isZero(err)) {
            return aberthEhrlich.vector;
          }
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    throw new Throw(polynomial);
  }

  // ---
  private final Polynomial polynomial;
  private final Polynomial derivative;
  private Tensor vector;

  /** @param polynomial
   * @param vector initial guess */
  public AberthEhrlich(Polynomial polynomial, Tensor vector) {
    this.polynomial = polynomial;
    this.derivative = polynomial.derivative();
    this.vector = vector;
  }

  public Tensor iterate() {
    Tensor result = vector.copy();
    for (int k = 0; k < vector.length(); ++k) {
      final int fi = k;
      Scalar zk = vector.Get(k);
      Scalar p1 = derivative.apply(zk);
      Scalar p0 = polynomial.apply(zk);
      Int i = new Int();
      Scalar push = vector.map(zk::subtract).stream() //
          .map(Scalar.class::cast) //
          .filter(_ -> i.getAndIncrement() != fi) //
          .map(Scalar::reciprocal) //
          .reduce(Scalar::add) //
          .orElseThrow();
      result.set(p1.divide(p0).subtract(push).reciprocal().negate()::add, k);
    }
    return vector = result;
  }
}
