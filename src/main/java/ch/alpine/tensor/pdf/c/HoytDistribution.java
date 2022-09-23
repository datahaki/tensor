// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.MeanInterface;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.VarianceInterface;

/** CDF requires MarcumQ
 * Mean requires EllipticE */
/* package */ class HoytDistribution implements Distribution, //
    PDF, MeanInterface, VarianceInterface, Serializable {
  public static Distribution of(Scalar q, Scalar w) {
    return new HoytDistribution(q, w);
  }

  public static Distribution of(Number q, Number w) {
    return of(RealScalar.of(q), RealScalar.of(w));
  }

  // ---
  private final Scalar q;
  private final Scalar w;

  private HoytDistribution(Scalar q, Scalar w) {
    this.q = q;
    this.w = w;
  }

  @Override
  public Scalar at(Scalar x) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Scalar mean() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Scalar variance() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("HoytDistribution", q, w);
  }
}
