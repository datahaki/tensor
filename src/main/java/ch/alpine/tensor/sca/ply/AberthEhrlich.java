// code by jph
package ch.alpine.tensor.sca.ply;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.itp.FindRoot;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Abs;

/** https://en.wikipedia.org/wiki/Aberth_method
 * 
 * @see FindRoot */
/* package */ class AberthEhrlich {
  private static final int MAX_ITERATIONS = 128;
  private static final Random RANDOM = new SecureRandom();

  /** @param polynomial of degree at least 2
   * @return unsored roots of polynomial
   * @throws Exception if convergence fail */
  public static Tensor of(Polynomial polynomial) {
    AberthEhrlich aberthEhrlich = new AberthEhrlich(polynomial);
    for (int index = 0; index < MAX_ITERATIONS; ++index) {
      Tensor tensor = aberthEhrlich.iterate();
      Scalar err = Total.ofVector(tensor.map(polynomial).map(Abs.FUNCTION));
      if (Tolerance.CHOP.isZero(err))
        return aberthEhrlich.vector;
    }
    throw new Throw(polynomial);
  }

  // ---
  private final Polynomial polynomial;
  private final Polynomial derivative;
  private Tensor vector;

  private AberthEhrlich(Polynomial polynomial) {
    this.polynomial = polynomial;
    derivative = polynomial.derivative();
    Distribution distribution = NormalDistribution.standard();
    vector = Tensors.vector(i -> ComplexScalar.of( //
        RandomVariate.of(distribution, RANDOM), //
        RandomVariate.of(distribution, RANDOM)), polynomial.degree());
  }

  Tensor iterate() {
    Tensor result = vector.copy();
    for (int k = 0; k < vector.length(); ++k) {
      final int fi = k;
      Scalar zk = vector.Get(k);
      Scalar p1 = derivative.apply(zk);
      Scalar p0 = polynomial.apply(zk);
      AtomicInteger atomicInteger = new AtomicInteger();
      Scalar push = vector.map(zk::subtract).stream() //
          .map(Scalar.class::cast) //
          // .filter(s -> !Tolerance.CHOP.isZero(s)) //
          .filter(scalar -> atomicInteger.getAndIncrement() != fi) //
          .map(Scalar::reciprocal) //
          .reduce(Scalar::add) //
          .orElseThrow();
      result.set(p1.divide(p0).subtract(push).reciprocal().negate()::add, k);
    }
    return vector = result;
  }
}
