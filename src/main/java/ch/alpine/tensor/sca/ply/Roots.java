// code by jph
package ch.alpine.tensor.sca.ply;

import java.util.Comparator;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.Re;

/** Not entirely consistent with Mathematica for the case
 * Mathematica::Roots[a == 0, x] == false
 * Tensor::Roots[a == 0, x] == {}
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Roots.html">Roots</a>
 * 
 * @see Polynomial */
public enum Roots {
  ;
  /** attempts to find all roots of a polynomial
   * 
   * <pre>
   * Roots.of(coeffs).map(Polynomial.of(coeffs)) == {0, 0, ...}
   * </pre>
   * 
   * @param coeffs of polynomial, for instance {a, b, c, d} represents
   * cubic polynomial a + b*x + c*x^2 + d*x^3
   * @return roots of polynomial as vector with length of that of coeffs minus one.
   * if the roots do not have imaginary part, the roots vector is sorted.
   * @throws Exception given coeffs vector is empty
   * @throws Exception if roots cannot be determined */
  public static Tensor of(Tensor coeffs) {
    Tensor roots = unsorted(coeffs);
    try {
      return Tensor.of(roots.stream().map(Scalar.class::cast).sorted(ComplexComparator.INSTANCE));
    } catch (Exception exception) {
      // ---
    }
    return roots;
  }

  /** @param coeffs of polynomial
   * @return roots of polynomial as vector with length of that of coeffs minus one */
  private static Tensor unsorted(Tensor coeffs) {
    while (Scalars.isZero(Last.of(coeffs)))
      coeffs = coeffs.extract(0, coeffs.length() - 1);
    int degree = coeffs.length() - 1;
    switch (degree) {
    case 0:
      return Tensors.empty();
    case 1: // a + b ** x == 0
      return RootsDegree1.of(coeffs);
    default:
    }
    if (Scalars.isZero(coeffs.Get(0))) {
      Tensor roots = unsorted(coeffs.extract(1, coeffs.length()));
      return roots.append(roots.Get(0).zero());
    }
    return switch (degree) {
    case 2 -> RootsDegree2.of(coeffs); // a + b*x + c*x^2 == 0
    case 3 -> RootsDegree3.of(coeffs); // a + b*x + c*x^2 + d*x^3 == 0
    default -> throw new Throw(coeffs);
    };
  }

  /* package */ enum ComplexComparator implements Comparator<Scalar> {
    INSTANCE;

    @Override // from Comparator
    public int compare(Scalar scalar1, Scalar scalar2) {
      int cmp = Scalars.compare(Re.of(scalar1), Re.of(scalar2));
      return cmp == 0 //
          ? Scalars.compare(Im.of(scalar1), Im.of(scalar2))
          : cmp;
    }
  }
}
