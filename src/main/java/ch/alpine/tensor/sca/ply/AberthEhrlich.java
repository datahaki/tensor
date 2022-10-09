// code by jph
package ch.alpine.tensor.sca.ply;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.chq.DeterminateScalarQ;
import ch.alpine.tensor.chq.FiniteTensorQ;
import ch.alpine.tensor.itp.FindRoot;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.ComplexDiskUniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Abs;

/** https://en.wikipedia.org/wiki/Aberth_method
 * 
 * @see FindRoot */
public class AberthEhrlich {
  private static final int MAX_ATTEMPTS = 5;
  private static final int MAX_ITERATIONS = 128;
  private static final Random RANDOM = new SecureRandom();

  /** @param polynomial of degree at least 2
   * @return unsorted roots of polynomial
   * @throws Exception if convergence fail */
  public static Tensor of(Polynomial polynomial) {
    return of(polynomial, RANDOM);
  }

  /** @param polynomial of degree at least 2
   * @param random to generate seeds
   * @return unsorted roots of polynomial
   * @throws Exception if convergence fail */
  public static Tensor of(Polynomial polynomial, Random random) {
    Unit unit = polynomial.getUnitDomain();
    Scalar radius = Quantity.of(Roots.bound(polynomial.coeffs().map(Unprotect::withoutUnit)), unit);
    Distribution distribution = ComplexDiskUniformDistribution.of(radius);
    for (int attempt = 0; attempt < MAX_ATTEMPTS; ++attempt)
      try {
        Tensor vector = RandomVariate.of(distribution, random, polynomial.degree());
        AberthEhrlich aberthEhrlich = new AberthEhrlich(polynomial, vector);
        for (int index = 0; index < MAX_ITERATIONS; ++index) {
          vector = aberthEhrlich.iterate();
          for (int k = 0; k < vector.length(); ++k) {
            Scalar cand = vector.Get(k);
            if (DeterminateScalarQ.of(cand)) {
              Scalar abs = Abs.FUNCTION.apply(cand);
              if (Scalars.lessThan(radius, abs))
                vector.set(cand.divide(abs).multiply(radius), k);
            } else {
              vector.set(RandomVariate.of(distribution, random), k);
            }
          }
          FiniteTensorQ.require(vector);
          Tensor eval = vector.map(polynomial);
          FiniteTensorQ.require(eval);
          Scalar err = Total.ofVector(eval.map(Abs.FUNCTION));
          if (Tolerance.CHOP.isZero(err))
            return aberthEhrlich.vector;
        }
      } catch (Exception exception) {
        System.out.println(exception);
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
      AtomicInteger atomicInteger = new AtomicInteger();
      Scalar push = vector.map(zk::subtract).stream() //
          .map(Scalar.class::cast) //
          .filter(scalar -> atomicInteger.getAndIncrement() != fi) //
          .map(Scalar::reciprocal) //
          .reduce(Scalar::add) //
          .orElseThrow();
      result.set(p1.divide(p0).subtract(push).reciprocal().negate()::add, k);
    }
    return vector = result;
  }
}
