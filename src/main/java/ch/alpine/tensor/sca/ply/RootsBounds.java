// code by jph
package ch.alpine.tensor.sca.ply;

import java.util.concurrent.atomic.AtomicInteger;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Drop;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.nrm.Vector1Norm;
import ch.alpine.tensor.nrm.VectorInfinityNorm;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;

/** Reference:
 * https://en.wikipedia.org/wiki/Geometrical_properties_of_polynomial_roots */
public enum RootsBounds {
  CAUCHY {
    @Override
    public Scalar of(Tensor coeffs) {
      return VectorInfinityNorm.of(Drop.tail(coeffs, 1).divide(Last.of(coeffs))).add(RealScalar.ONE);
    }
  },
  LAGRANGE1 {
    @Override
    public Scalar of(Tensor coeffs) {
      return Max.of(Vector1Norm.of(Drop.tail(coeffs, 1).divide(Last.of(coeffs))), RealScalar.ONE);
    }
  },
  LAGRANGE2 {
    @Override
    public Scalar of(Tensor coeffs) {
      int last = coeffs.length() - 1;
      AtomicInteger atomicInteger = new AtomicInteger(last);
      return Total.ofVector(Tensor.of(Drop.tail(coeffs, 1).divide(Last.of(coeffs)).stream() //
          .map(Scalar.class::cast) //
          .map(Abs.FUNCTION) //
          .map(ratio -> Power.of(ratio, RationalScalar.of(1, atomicInteger.getAndDecrement()))) //
          .sorted() //
          .skip(last - 2)));
    }
  },
  FUJIWARA {
    @Override
    public Scalar of(Tensor coeffs) {
      int last = coeffs.length() - 1;
      AtomicInteger atomicInteger = new AtomicInteger(coeffs.length() - 1);
      Scalar max = Drop.tail(coeffs, 1).divide(Last.of(coeffs)).stream() //
          .map(Scalar.class::cast) //
          .map(Abs.FUNCTION) //
          .map(ratio -> atomicInteger.get() == last ? ratio.multiply(RationalScalar.HALF) : ratio) //
          .map(ratio -> Power.of(ratio, RationalScalar.of(1, atomicInteger.getAndDecrement()))) //
          .reduce(Max::of) //
          .orElseThrow();
      return max.add(max);
    }
  },
  /** never better than SUN_HSIEH2 */
  SUN_HSIEH1 {
    @Override
    public Scalar of(Tensor coeffs) {
      coeffs = Drop.tail(coeffs, 1).divide(Last.of(coeffs));
      Scalar a = VectorInfinityNorm.of(coeffs);
      Scalar b = Abs.FUNCTION.apply(Last.of(coeffs)).subtract(RealScalar.ONE);
      Scalar d1 = b.add(Sqrt.FUNCTION.apply(b.multiply(b).add(a.multiply(RealScalar.of(4)))));
      return RealScalar.ONE.add(d1);
    }
  },
  /** really competitive for polynomials of large degree */
  SUN_HSIEH2 {
    @Override
    public Scalar of(Tensor coeffs) {
      coeffs = Drop.tail(coeffs, 1).divide(Last.of(coeffs));
      Scalar a = VectorInfinityNorm.of(coeffs);
      Scalar hi = Abs.FUNCTION.apply(coeffs.Get(coeffs.length() - 1));
      Scalar lo = Abs.FUNCTION.apply(coeffs.Get(coeffs.length() - 2));
      Tensor help = Tensors.of( //
          a.negate(), //
          RealScalar.ONE.subtract(hi).subtract(lo), //
          RealScalar.TWO.subtract(hi), RealScalar.ONE);
      Scalar d2 = Last.of(Roots.of(help));
      return RealScalar.ONE.add(d2);
    }
  },;

  public abstract Scalar of(Tensor coeffs);
}
