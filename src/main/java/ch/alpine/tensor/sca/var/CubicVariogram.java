// code by jph
package ch.alpine.tensor.sca.var;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.ply.Polynomial;
import ch.alpine.tensor.sca.pow.Power;

public class CubicVariogram implements ScalarUnaryOperator {
  private final Scalar a;
  private final Scalar b;
  private final Polynomial polynomial;

  public CubicVariogram(Scalar a, Scalar b) {
    this.a = a;
    this.b = b;
    Scalar p0 = Power.of(b, 5).multiply(RealScalar.of(28));
    Scalar p1 = Power.of(b, 4).multiply(RealScalar.of(-35));
    Scalar p3 = Power.of(b, 2).multiply(RealScalar.of(14));
    Scalar p5 = RealScalar.of(-3);
    polynomial = Polynomial.of(Tensors.of(p0, p1, RealScalar.ZERO, p3, RealScalar.ZERO, p5));
  }

  @Override
  public Scalar apply(Scalar r) {
    if (Scalars.lessEquals(b, r))
      return a;
    if (Scalars.lessEquals(b.zero(), r))
      return Times.of(polynomial.apply(r), r, r, a, Power.of(b, -7), RationalScalar.of(1, 4));
    throw new Throw(r);
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("CubicVariogram", a, b);
  }
}
