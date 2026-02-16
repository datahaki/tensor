// code by jph
package ch.alpine.tensor.sca.ply;

import java.util.concurrent.atomic.AtomicBoolean;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Drop;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.ext.Int;
import ch.alpine.tensor.nrm.Vector1Norm;
import ch.alpine.tensor.nrm.VectorInfinityNorm;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;

/** {@link #LAGRANGE2} as well as {@link #FUJIWARA} work for coefficients
 * of type {@link Quantity}.
 * 
 * Reference:
 * https://en.wikipedia.org/wiki/Geometrical_properties_of_polynomial_roots
 * 
 * @see AberthEhrlich */
public enum RootsBounds {
  /** Cauchy's bound was not found to be competitive */
  CAUCHY {
    @Override
    Scalar ofMonic(Tensor monic) {
      return VectorInfinityNorm.of(monic).add(RealScalar.ONE); // norm of vector with mixed units fails
    }
  },
  /** does not work for coefficients with mixed unit */
  LAGRANGE1 {
    @Override
    Scalar ofMonic(Tensor monic) {
      return Max.of(Vector1Norm.of(monic), RealScalar.ONE); // norm of vector with mixed units fails
    }
  },
  /** LAGRANGE2 works for coefficients of type {@link Quantity} */
  LAGRANGE2 {
    @Override
    Scalar ofMonic(Tensor monic) {
      int last = monic.length();
      Int i = new Int(last);
      return Total.ofVector(Tensor.of(monic.stream() //
          .map(Scalar.class::cast) //
          .map(Abs.FUNCTION) //
          .map(ratio -> Power.of(ratio, Rational.of(1, i.getAndDecrement()))) //
          .sorted() //
          .skip(last - 2)));
    }
  },
  /** FUJIWARA works for coefficients of type {@link Quantity} */
  FUJIWARA {
    @Override
    Scalar ofMonic(Tensor monic) {
      AtomicBoolean atomicBoolean = new AtomicBoolean();
      Int i = new Int(monic.length());
      return monic.stream() //
          .map(Scalar.class::cast) //
          .map(Abs.FUNCTION) //
          .map(ratio -> atomicBoolean.getAndSet(true) ? ratio : ratio.multiply(Rational.HALF))
          .map(ratio -> Power.of(ratio, Rational.of(1, i.getAndDecrement()))) //
          .reduce(Max::of) //
          .map(max -> max.add(max)) //
          .orElseThrow();
    }
  },
  /** never better than {@link #SUN_HSIEH2} */
  SUN_HSIEH1 {
    @Override
    Scalar ofMonic(Tensor monic) {
      Scalar a = VectorInfinityNorm.of(monic); // norm of vector with mixed units fails
      Scalar b = Abs.FUNCTION.apply(Last.of(monic)).subtract(RealScalar.ONE);
      Scalar d1 = b.add(Sqrt.FUNCTION.apply(b.multiply(b).add(a.multiply(_4))));
      return RealScalar.ONE.add(d1);
    }
  },
  /** really competitive for polynomials of large degree */
  SUN_HSIEH2 {
    @Override
    Scalar ofMonic(Tensor monic) {
      Scalar a = VectorInfinityNorm.of(monic); // norm of vector with mixed units fails
      Scalar hi = Abs.FUNCTION.apply(monic.Get(monic.length() - 1));
      Scalar lo = Abs.FUNCTION.apply(monic.Get(monic.length() - 2));
      Tensor help = Tensors.of( //
          a.negate(), //
          RealScalar.ONE.subtract(hi).subtract(lo), //
          RealScalar.TWO.subtract(hi), //
          RealScalar.ONE);
      return RealScalar.ONE.add(Last.of(Roots.of(help)));
    }
  };

  private static final Scalar _4 = RealScalar.of(4);

  /** @param coeffs of polynomial, for instance {a, b, c, d} represents
   * cubic polynomial a + b*x + c*x^2 + d*x^3
   * @return upper bound on absolute value of any root of given polynomial */
  public final Scalar of(Tensor coeffs) {
    return ofMonic(Drop.tail(coeffs, 1).divide(Last.of(coeffs)));
  }

  /** for a polynomial with coefficient vector {a, b, c, d}
   * the function ofMonic is called with vector {a/d, b/d, c/d},
   * i.e. the tailing d/d == 1 is omitted.
   * 
   * coefficients of monic polynomial might have mixed units
   * for instance {-3/4[s^3], -1/2[s^2], -3/4[s]}
   * 
   * @param monic vector
   * @return upper bound on absolute value of any root of given polynomial */
  /* package */ abstract Scalar ofMonic(Tensor monic);
}
