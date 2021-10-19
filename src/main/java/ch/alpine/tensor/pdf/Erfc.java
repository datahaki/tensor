// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Exp;
import ch.alpine.tensor.sca.Real;
import ch.alpine.tensor.sca.Sign;

/** Erfc[z] == 1 - Erf[z]
 * 
 * Reference:
 * "Incomplete Gamma Function" in NR, 2007
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Erfc.html">Erfc</a> */
public enum Erfc implements ScalarUnaryOperator {
  FUNCTION;

  private static final Scalar TWO = RealScalar.of(2.0);
  private static final Scalar FOUR = RealScalar.of(4.0);
  private static final Tensor COEFFS = Tensors.vector(-1.3026537197817094, 6.4196979235649026E-1, //
      1.9476473204185836E-2, -9.561514786808631E-3, -9.46595344482036E-4, //
      3.66839497852761E-4, 4.2523324806907E-5, -2.0278578112534E-5, //
      -1.624290004647E-6, 1.303655835580E-6, 1.5626441722E-8, -8.5238095915E-8, //
      6.529054439E-9, 5.059343495E-9, -9.91364156E-10, -2.27365122E-10, //
      9.6467911E-11, 2.394038E-12, -6.886027E-12, 8.94487E-13, 3.13092E-13, //
      -1.12708E-13, 3.81E-16, 7.106E-15, -1.523E-15, -9.4E-17, 1.21E-16, -2.8E-17);

  /** @param z with re[z] non-negative
   * @return */
  private static Scalar evaluate(Scalar z) {
    Scalar t = TWO.divide(TWO.add(z));
    Scalar ty = t.multiply(FOUR).subtract(TWO);
    Scalar d = RealScalar.ZERO;
    Scalar dd = RealScalar.ZERO;
    for (int j = COEFFS.length() - 1; 0 < j; j--) {
      Scalar tmp = d;
      d = ty.multiply(d).subtract(dd).add(COEFFS.Get(j));
      dd = tmp;
    }
    Scalar exponent = RationalScalar.HALF.multiply(ty.multiply(d).add(COEFFS.Get(0))).subtract(dd).subtract(z.multiply(z));
    return t.multiply(Exp.FUNCTION.apply(exponent));
  }

  // ---
  @Override
  public Scalar apply(Scalar z) {
    Scalar re = Real.FUNCTION.apply(z);
    return Sign.isPositiveOrZero(re) //
        ? evaluate(z)
        : TWO.subtract(evaluate(z.negate()));
  }

  /** @param tensor
   * @return tensor with all scalar entries replaced by the evaluation under Erfc */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}