// code adapted from chatgpt
package ch.alpine.tensor.num;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;

class ExtendedGCDN {
  // Result holder for 2-variable extended GCD
  private record EGCDResult(Scalar gcd, Scalar x, Scalar y) {
  }

  // Standard extended GCD for two numbers
  static EGCDResult egcd(Scalar a, Scalar b) {
    if (Scalars.isZero(b))
      return new EGCDResult(a, a.one(), a.zero());
    QuotientRemainder qr = QuotientRemainder.of(a, b);
    EGCDResult r = egcd(b, qr.remainder());
    Scalar x = r.y;
    Scalar y = r.x.subtract(qr.quotient().multiply(r.y));
    return new EGCDResult(r.gcd, x, y);
  }

  // Extended GCD for n numbers
  // Returns gcd, and fills coeff[] such that sum(coeff[i] * a[i]) = gcd
  static Scalar extendedGCD(Tensor a, Tensor coeff) {
    int n = a.length();
    Scalar g = a.Get(0);
    for (int i = 1; i < n; i++) {
      EGCDResult r = egcd(g, a.Get(i));
      // Scale previous coefficients
      for (int j = 0; j < i; j++)
        coeff.set(r.x::multiply, j);
      coeff.set(r.y, i);
      g = r.gcd;
    }
    return g;
  }

  // Example usage
  static void main() {
    Tensor a = Tensors.vector(30, 18, 27, 100);
    Tensor coeff = UnitVector.of(a.length(), 0);
    Scalar g = extendedGCD(a, coeff);
    IO.println("gcd = " + g);
    IO.println("coefficients = " + coeff);
    // Verify
    IO.println("verification sum = " + a.dot(coeff));
  }
}
