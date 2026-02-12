// code by jph
// https://de.wikipedia.org/wiki/Elliptische_Kurve
package ch.alpine.tensor.jet;

import java.math.BigInteger;
import java.util.List;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.api.GroupInterface;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ZeroDefectArrayQ;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.num.BinaryPower;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.ply.Polynomial;
import ch.alpine.tensor.sca.pow.Sqrt;

/** using exact arithmetics to perform group operations on an elliptic curve of the from
 * 
 * y^2 == x^3 + a*x + b
 * 
 * https://bugs.java.com/bugdatabase/JDK-8374189/description */
public class EllipticCurve extends ZeroDefectArrayQ implements GroupInterface<Tensor>, ScalarUnaryOperator {
  public static final Tensor NEUTRAL = Tensors.unmodifiableEmpty();

  /** Hint: also works for parameters of type {@link Quantity}
   * 
   * @param a in exact precision
   * @param b in exact precision
   * @return */
  public static EllipticCurve of(Scalar a, Scalar b) {
    EllipticCurve ellipticCurve = new EllipticCurve(a, b);
    if (Scalars.isZero(ellipticCurve.discriminant()))
      throw new Throw(a, b);
    return ellipticCurve;
  }

  /** @param a
   * @param b
   * @return */
  public static EllipticCurve of(long a, long b) {
    return of(RealScalar.of(a), RealScalar.of(b));
  }

  /** https://en.wikipedia.org/wiki/Montgomery_curve
   * 
   * @param A
   * @param B
   * @return */
  public static EllipticCurve montgomery(Scalar A, Scalar B) {
    Scalar one = A.one();
    Scalar A2 = A.multiply(A);
    Scalar B2 = B.multiply(B);
    Scalar P03 = Scalars.add().raise(one, 3);
    Scalar P02 = Scalars.add().raise(one, 2);
    Scalar P09 = Scalars.add().raise(one, 9);
    Scalar P27 = Scalars.add().raise(one, 27);
    return of( //
        P03.subtract(A2).divide(P03.multiply(B2)), //
        Times.of(P02, A2, A).subtract(P09.multiply(A)).divide(Times.of(P27, B2, B)));
  }

  public static boolean isNeutral(Tensor p) {
    return Tensors.isEmpty(p);
  }

  // ---
  private final Polynomial polynomial;
  private final Polynomial derivative;
  private final Scalar discriminant;
  private final BinaryPower<Tensor> binaryPower = new BinaryPower<>(this);

  private EllipticCurve(Scalar a, Scalar b) {
    super(1, Chop.NONE); // vector in exact precision
    Scalar one = a.one();
    polynomial = Polynomial.of(Tensors.of(b, a, Unprotect.zero_negateUnit(a).multiply(b), one));
    derivative = polynomial.derivative();
    Scalar P04 = Scalars.add().raise(one, 4);
    Scalar P27 = Scalars.add().raise(one, 27);
    Scalar N16 = Scalars.add().raise(one, -16);
    discriminant = Times.of(P04, a, a, a).add(Times.of(P27, b, b)).multiply(N16);
  }

  @Override // from ZeroDefectArrayQ
  protected boolean isArrayWith(List<Integer> list) {
    int n = list.getFirst();
    return n == 0 //
        || n == 2;
  }

  @Override // from ZeroDefectArrayQ
  public Tensor defect(Tensor p) {
    if (isNeutral(p))
      return RealScalar.ZERO;
    Scalar xp = ExactScalarQ.require(p.Get(0));
    Scalar yp = ExactScalarQ.require(p.Get(1));
    return polynomial.apply(xp).subtract(yp.multiply(yp));
  }

  /** @return y^2 == polynomial(x) */
  public Polynomial polynomial() {
    return polynomial;
  }

  /** @param p
   * @param exponent
   * @return p + p + ... + p as sum consisting of exponent terms */
  public Tensor raise(Tensor p, BigInteger exponent) {
    return binaryPower.raise(p, exponent);
  }

  @PackageTestAccess
  Tensor raise(Tensor p, long exponent) {
    return raise(p, BigInteger.valueOf(exponent));
  }

  /** Quote from Wikipedia:
   * The real graph of a non-singular curve has two components if its
   * discriminant is positive, and one component if it is negative.
   * 
   * @return */
  public Scalar discriminant() {
    return discriminant;
  }

  @Override // from ScalarUnaryOperator
  public Scalar apply(Scalar x) {
    return Sqrt.FUNCTION.apply(polynomial().apply(x));
  }

  @Override // from GroupInterface
  public Tensor neutral(Tensor element) {
    return NEUTRAL;
  }

  @Override // from GroupInterface
  public Tensor invert(Tensor element) {
    if (isNeutral(element))
      return NEUTRAL;
    require(element);
    return Tensors.of(element.Get(0), element.Get(1).negate());
  }

  @Override // from GroupInterface
  public Tensor combine(Tensor p, Tensor q) {
    require(p);
    require(q);
    if (isNeutral(p))
      return q;
    if (isNeutral(q))
      return p;
    Scalar px = p.Get(0);
    Scalar py = p.Get(1);
    Scalar qx = q.Get(0);
    Scalar qy = q.Get(1);
    boolean xsame = px.equals(qx);
    if (xsame && py.equals(qy.negate()))
      return NEUTRAL;
    Scalar s = xsame //
        ? derivative.apply(px).divide(py.add(qy))
        : py.subtract(qy).divide(px.subtract(qx));
    Scalar rx = s.multiply(s).subtract(px.add(qx));
    Scalar ry = px.subtract(rx).multiply(s).subtract(py);
    return require(Tensors.of(rx, ry));
  }

  /** @param x
   * @return
   * @throws Exception if x cannot be completed to a point on the curve */
  public Tensor complete(Scalar x) {
    return require(Tensors.of(x, apply(x)));
  }

  @Override // from Object
  public int hashCode() {
    return polynomial.hashCode();
  }

  @Override // from Object
  public boolean equals(Object object) {
    return object instanceof EllipticCurve ellipticCurve //
        && ellipticCurve.polynomial.equals(polynomial);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("EllipticCurve", polynomial);
  }
}
