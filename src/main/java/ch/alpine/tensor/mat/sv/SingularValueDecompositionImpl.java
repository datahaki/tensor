// code adapted by jph
package ch.alpine.tensor.mat.sv;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.nrm.Hypot;
import ch.alpine.tensor.red.CopySign;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;

/** implements "thin" svd, i.e. u has the same format as given matrix
 * instead of being square */
/* package */ class SingularValueDecompositionImpl implements SingularValueDecomposition, Serializable {
  private static final int MAX_ITERATIONS = 28;
  // ---
  /** rows x cols */
  private final Tensor u;
  private final Tensor w;
  private final Tensor r;
  /** cols x cols */
  private final Tensor v;

  /** @param init */
  public SingularValueDecompositionImpl(Init init) {
    u = init.u;
    v = init.v;
    w = init.w;
    r = init.r;
    for (int i = w.length() - 1; 0 <= i; --i) {
      for (int iteration = 0; iteration <= MAX_ITERATIONS; ++iteration) {
        int l = levelW(i, init.chop);
        // if (!Unprotect.getUnitUnique(w).equals(Unprotect.getUnitUnique(r)))
        // throw new Throw(w, r);
        if (l == i)
          break;
        if (iteration == MAX_ITERATIONS)
          throw new RuntimeException("no convergence");
        rotateUV(l, i);
      }
      if (Sign.isNegative(w.Get(i))) { // ensure w[i] is positive
        w.set(Scalar::negate, i);
        v.set(Scalar::negate, Tensor.ALL, i);
      }
    }
  }

  @Override // from SingularValueDecomposition
  public Tensor getU() {
    return u;
  }

  @Override // from SingularValueDecomposition
  public Tensor values() {
    return w;
  }

  @Override // from SingularValueDecomposition
  public Tensor getV() {
    return v;
  }

  private int levelW(int k, Chop chop) {
    for (int l = k; l > 0; --l) {
      Scalar rl = r.Get(l);
      if (chop.isZero(rl))
        return l;
      if (chop.isZero(w.Get(l - 1))) {
        Scalar s = rl.one();
        Scalar c = s.zero();
        for (int i = l; i < k + 1; ++i) {
          Scalar f = s.multiply(r.Get(i));
          r.set(c::multiply, i);
          if (chop.isZero(f))
            break;
          Scalar g = w.Get(i);
          Scalar h = Hypot.of(f, g);
          w.set(h, i);
          c = g.divide(h);
          s = f.divide(h).negate();
          rotate(u, c, s, i, l - 1);
        }
        return l;
      }
    }
    return 0;
  }

  private Scalar initf(int l, int i) {
    Scalar x = w.Get(l);
    Scalar y = w.Get(i - 1);
    Scalar z = w.Get(i);
    Scalar p = r.Get(i - 1);
    Scalar h = r.Get(i);
    Scalar hy = h.multiply(y);
    // ((y-z)(y+z)+(p-h)*(p+h))/(2hy)
    Scalar f = y.subtract(z).multiply(y.add(z)).add(p.subtract(h).multiply(p.add(h))).divide(hy.add(hy));
    // ((x-z)(x+z)+(h*(y/(f+-p)-h)))/x
    return x.subtract(z).multiply(x.add(z)).add(h.multiply(y.divide(f.add(CopySign.of(Hypot.withOne(f), f))).subtract(h))).divide(x);
  }

  /** @param l < i
   * @param i > 0 */
  private void rotateUV(int l, int i) {
    Scalar f = initf(l, i);
    Scalar x = w.Get(l);
    Scalar s = x.one(); // without unit
    Scalar c = s; // without unit
    for (int j = l; j < i; ++j) {
      int jp1 = j + 1;
      Scalar p = r.Get(jp1);
      Scalar y = w.Get(jp1);
      Scalar h = s.multiply(p);
      p = c.multiply(p);
      Scalar z = Hypot.of(f, h);
      r.set(z, j);
      c = f.divide(z);
      s = h.divide(z);
      rotate(v, c, s, jp1, j);
      {
        Rotate rotate = new Rotate(p, x, c, s);
        p = rotate.re();
        f = rotate.im();
      }
      h = y.multiply(s);
      y = y.multiply(c);
      z = Hypot.of(f, h);
      w.set(z, j);
      if (Scalars.nonZero(z)) {
        c = f.divide(z);
        s = h.divide(z);
      }
      rotate(u, c, s, jp1, j);
      {
        Rotate rotate = new Rotate(y, p, c, s);
        x = rotate.re();
        f = rotate.im();
      }
    }
    r.set(Scalar::zero, l);
    r.set(f, i);
    w.set(x, i);
  }

  private static void rotate(Tensor m, Scalar c, Scalar s, int i, int j) {
    m.stream().forEach(mk -> {
      Rotate rotate = new Rotate(mk.Get(i), mk.Get(j), c, s);
      mk.set(rotate.re(), i);
      mk.set(rotate.im(), j);
    });
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("SingularValueDecomposition", getU(), values(), getV());
  }
}
