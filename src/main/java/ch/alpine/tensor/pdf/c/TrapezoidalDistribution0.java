// code by clruch, jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.pdf.CentralMomentInterface;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.ply.Polynomial;
import ch.alpine.tensor.sca.pow.Sqrt;

/* package */ class TrapezoidalDistribution0 extends AbstractContinuousDistribution //
    implements CentralMomentInterface, Serializable {
  private final Clip clip;
  private final Scalar a;
  private final Scalar b;
  private final Scalar c;
  private final Scalar d;
  private final Scalar alpha_inv;
  private final Scalar alpha;
  private final Scalar yB;
  private final Scalar yC;
  private final Scalar mean;

  public TrapezoidalDistribution0(Scalar a, Scalar b, Scalar c, Scalar d) {
    clip = Clips.interval(a, d);
    this.a = a;
    this.b = clip.requireInside(b);
    this.c = clip.requireInside(c);
    this.d = d;
    alpha_inv = d.subtract(a).add(c.subtract(b));
    this.alpha = alpha_inv.reciprocal();
    yB = p_lessThan(b);
    yC = p_lessThan(c);
    mean = moment(false, 1);
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    Scalar value = _p_lessThan(x);
    return x.one().multiply(value);
  }

  private Scalar _p_lessThan(Scalar x) {
    if (Scalars.lessThan(x, a))
      return RealScalar.ZERO;
    if (Scalars.lessThan(x, b)) {
      Scalar term1 = RealScalar.ONE.divide(b.subtract(a));
      Scalar term2 = x.subtract(a).multiply(x.subtract(a));
      return alpha.multiply(term1).multiply(term2);
    }
    if (Scalars.lessEquals(x, c)) {
      Scalar term2 = x.subtract(a).add(x.subtract(b));
      return alpha.multiply(term2);
    }
    if (Scalars.lessThan(x, d)) {
      Scalar term1 = RealScalar.ONE.divide(d.subtract(c));
      Scalar term2 = d.subtract(x).multiply(d.subtract(x));
      return RealScalar.ONE.subtract(alpha.multiply(term1).multiply(term2));
    }
    return RealScalar.ONE;
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    Scalar value = _at(x);
    return x.one().multiply(value);
  }

  private Scalar _at(Scalar x) {
    if (clip.isInside(x)) { // support is [a, d]
      Scalar two_alpha = alpha.add(alpha);
      if (Scalars.lessThan(x, b)) {
        Scalar term = x.subtract(a).divide(b.subtract(a));
        return two_alpha.multiply(term);
      }
      if (Scalars.lessEquals(x, c))
        return two_alpha;
      // here is case c < x <= d
      Scalar term = d.subtract(x).divide(d.subtract(c));
      return two_alpha.multiply(term);
    }
    return alpha.zero();
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return mean;
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return moment(true, 2);
  }

  @Override // from CentralMomentInterface
  public Scalar centralMoment(int order) {
    return moment(true, order);
  }

  private Scalar moment(boolean centered, int order) {
    ScalarUnaryOperator n_mean = centered //
        ? s -> s.subtract(mean)
        : s -> s;
    List<Optional<Scalar>> list = List.of( //
        contrib(a, b, n_mean, order), //
        contrib(b, c, n_mean, order), //
        contrib(c, d, n_mean, order));
    return list.stream() //
        .flatMap(Optional::stream) //
        .reduce(Scalar::add) //
        .orElseThrow();
  }

  private Optional<Scalar> contrib(Scalar lo, Scalar hi, ScalarUnaryOperator map, int order) {
    if (lo.equals(hi))
      return Optional.empty();
    Tensor domain = Tensors.of(lo, hi);
    Tensor xdata = domain.map(map);
    Tensor ydata = domain.map(this::at);
    return Optional.of(Polynomial.fit(xdata, ydata, 1).moment(order, xdata.Get(0), xdata.Get(1)));
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar protected_quantile(Scalar p) {
    if (Scalars.lessEquals(p, yB)) // y <= yB
      return Sqrt.FUNCTION.apply(alpha_inv.multiply(b.subtract(a)).multiply(p)).add(a);
    // yB < y <= yC
    if (Scalars.lessEquals(p, yC))
      return p.multiply(alpha_inv).add(a).add(b).multiply(RationalScalar.HALF);
    // yC < y
    return d.subtract(Sqrt.FUNCTION.apply( //
        RealScalar.ONE.subtract(p).multiply(alpha_inv).multiply(d.subtract(c))));
  }
}
