// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** Horner's scheme improves speed and stability for the numeric evaluation of large polynomials
 * 
 * <p>Quote from Wikipedia:
 * These methods named after the British mathematician William George Horner,
 * although they were known before him by Paolo Ruffini, six hundred years earlier,
 * by the Chinese mathematician Qin Jiushao and seven hundred years earlier,
 * by the Persian mathematician Sharaf al-Din al-Ṭusi.
 * 
 * <p>https://en.wikipedia.org/wiki/Horner%27s_method */
/* package */ class HornerScheme implements ScalarUnaryOperator {
  private final Scalar head;
  private final Tensor rest;

  // careful: the coeffs are in reversed order in comparison to Polynomial
  public HornerScheme(Tensor coeffs) {
    head = coeffs.Get(0);
    rest = coeffs.extract(1, coeffs.length());
  }

  @Override
  public final Scalar apply(Scalar scalar) {
    Scalar total = head;
    total.multiply(scalar); // consistency check
    for (Tensor entry : rest)
      total = total.multiply(scalar).add(entry); // a + b ** x + c ** x ** x
    // total = scalar.multiply(total).add(entry); // a + x ** b + x ** x ** c
    return total;
  }
}
