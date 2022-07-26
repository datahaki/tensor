// code by jph
package ch.alpine.tensor.sca;

import java.util.Objects;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.io.MathematicaFormat;

/** our implementation is <em>not</em> consistent with Mathematica for negative, and complex n.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Mod.html">Mod</a> */
// TODO TENSOR NUM implement https://en.wikipedia.org/wiki/Modular_arithmetic
public class Mod implements ScalarUnaryOperator {
  /** @param n
   * @return remainder on division by n */
  public static Mod function(Scalar n) {
    return function(n, n.zero());
  }

  /** @param n
   * @return remainder on division by n */
  public static Mod function(Number n) {
    return function(RealScalar.of(n));
  }

  /** @param n
   * @param d offset
   * @return remainder on division by n with offset d */
  public static Mod function(Scalar n, Scalar d) {
    if (Scalars.isZero(n))
      throw new Throw(n);
    return new Mod(n, Objects.requireNonNull(d));
  }

  /** @param n
   * @param d offset
   * @return remainder on division by n with offset d */
  public static Mod function(Number n, Number d) {
    return function(RealScalar.of(n), RealScalar.of(d));
  }

  // ---
  private final Scalar n;
  private final Scalar d;

  private Mod(Scalar n, Scalar d) {
    this.n = n;
    this.d = d;
  }

  @Override
  public Scalar apply(Scalar scalar) {
    Scalar loops = Floor.FUNCTION.apply(scalar.subtract(d).divide(n));
    return scalar.subtract(FiniteScalarQ.require(loops).multiply(n));
  }

  @SuppressWarnings("unchecked")
  public <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(this);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.of("Mod", n, d);
  }
}
