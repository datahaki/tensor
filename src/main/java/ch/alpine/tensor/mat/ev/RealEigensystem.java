// code corresponds to "org.hipparchus.linear.EigenDecomposition" in Hipparchus project
// adapted by jph
package ch.alpine.tensor.mat.ev;

import java.io.Serializable;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.qr.SchurDecomposition;
import ch.alpine.tensor.nrm.Matrix1Norm;
import ch.alpine.tensor.nrm.Vector1Norm;
import ch.alpine.tensor.num.ReIm;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.Re;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.spa.SparseArray;

/* package */ class RealEigensystem implements Eigensystem, Serializable {
  private static final Scalar DEFAULT_EPSILON = RealScalar.of(1e-12);
  private static final Scalar EPSILON = RealScalar.of(1e-16);
  // ---
  private final Tensor eigenvectors;
  private final Scalar[] eigenvalues;

  public RealEigensystem(Tensor matrix) {
    SchurDecomposition schurDecomposition = SchurDecomposition.of(matrix);
    final int n = matrix.length();
    eigenvalues = ScalarArray.ofVector(Array.zeros(n));
    final Scalar[][] matrixT = ScalarArray.ofMatrix(schurDecomposition.getT());
    {
      Scalar norm = Matrix1Norm.of(matrix);
      for (int i = 0; i < n; ++i)
        if (i == (n - 1) || //
            Scalars.lessThan(Abs.FUNCTION.apply(matrixT[i + 1][i]), norm.multiply(DEFAULT_EPSILON)))
          eigenvalues[i] = matrixT[i][i];
        else {
          Scalar x = matrixT[i + 1][i + 1];
          Scalar p = matrixT[i][i].subtract(x).multiply(RationalScalar.HALF);
          Scalar z = Sqrt.FUNCTION.apply(Abs.FUNCTION.apply(p.multiply(p).add(matrixT[i + 1][i].multiply(matrixT[i][i + 1]))));
          eigenvalues[i] = ComplexScalar.of(x.add(p), z);
          eigenvalues[i + 1] = Conjugate.FUNCTION.apply(eigenvalues[i]);
          ++i;
        }
    }
    final Scalar[][] matrixP = ScalarArray.ofMatrix(schurDecomposition.getUnitary());
    // compute matrix norm
    Scalar norm = matrixT[0][0].zero();
    for (int i = 0; i < n; ++i)
      for (int j = Math.max(i - 1, 0); j < n; ++j)
        norm = norm.add(Abs.FUNCTION.apply(matrixT[i][j]));
    // we can not handle a matrix with zero norm
    if (Scalars.isZero(norm))
      throw new Throw(matrix);
    // Backsubstitute to find vectors of upper triangular form
    Scalar r = RealScalar.ZERO;
    Scalar s = RealScalar.ZERO;
    Scalar z = RealScalar.ZERO;
    for (int idx = n - 1; 0 <= idx; --idx) {
      ReIm reIm = new ReIm(eigenvalues[idx]);
      final Scalar p = reIm.re();
      final Scalar q = reIm.im();
      if (Tolerance.CHOP.isZero(q)) {
        // Real vector
        int l = idx;
        matrixT[idx][idx] = RealScalar.ONE;
        for (int i = idx - 1; 0 <= i; --i) {
          Scalar w = matrixT[i][i].subtract(p);
          r = RealScalar.ZERO;
          for (int j = l; j <= idx; ++j)
            r = r.add(matrixT[i][j].multiply(matrixT[j][idx]));
          if (Scalars.lessThan(Im.FUNCTION.apply(eigenvalues[i]), DEFAULT_EPSILON.negate())) {
            z = w;
            s = r;
          } else {
            l = i;
            if (Scalars.isZero(Im.FUNCTION.apply(eigenvalues[i]))) {
              matrixT[i][idx] = Scalars.nonZero(w) //
                  ? r.divide(w).negate()
                  : r.divide(DEFAULT_EPSILON.multiply(norm)).negate();
            } else {
              // Solve real equations
              Scalar x = matrixT[i][i + 1];
              Scalar y = matrixT[i + 1][i];
              Scalar fq = AbsSquared.FUNCTION.apply(eigenvalues[i].subtract(p));
              Scalar t = x.multiply(s).subtract(z.multiply(r)).divide(fq);
              matrixT[i][idx] = t;
              matrixT[i + 1][idx] = Scalars.lessThan(Abs.FUNCTION.apply(z), Abs.FUNCTION.apply(x)) //
                  ? r.negate().subtract(w.multiply(t)).divide(x)
                  : s.negate().subtract(y.multiply(t)).divide(z);
            }
            // Overflow control
            Scalar t = Abs.FUNCTION.apply(matrixT[i][idx]);
            if (Scalars.lessThan(RealScalar.ONE, Times.of(EPSILON, t, t)))
              // case not covered by tests
              for (int j = i; j <= idx; ++j)
                matrixT[j][idx] = matrixT[j][idx].divide(t);
          }
        }
      } else //
      if (Sign.isNegative(q)) { // Complex vector
        int l = idx - 1;
        // Last vector component imaginary so matrix is triangular
        if (Scalars.lessThan( //
            Abs.FUNCTION.apply(matrixT[idx - 1][idx]), //
            Abs.FUNCTION.apply(matrixT[idx][idx - 1]))) {
          matrixT[idx - 1][idx - 1] = q.divide(matrixT[idx][idx - 1]);
          matrixT[idx - 1][idx] = matrixT[idx][idx].subtract(p).negate().divide(matrixT[idx][idx - 1]);
        } else {
          ReIm c3 = new ReIm(
              ComplexScalar.of(RealScalar.ZERO, matrixT[idx - 1][idx].negate()).divide(ComplexScalar.of(matrixT[idx - 1][idx - 1].subtract(p), q)));
          matrixT[idx - 1][idx - 1] = c3.re();
          matrixT[idx - 1][idx] = c3.im();
        }
        matrixT[idx][idx - 1] = RealScalar.ZERO;
        matrixT[idx][idx] = RealScalar.ONE;
        for (int i = idx - 2; 0 <= i; --i) {
          Scalar ra = RealScalar.ZERO;
          Scalar sa = RealScalar.ZERO;
          for (int j = l; j <= idx; ++j) {
            ra = ra.add(matrixT[i][j].multiply(matrixT[j][idx - 1]));
            sa = sa.add(matrixT[i][j].multiply(matrixT[j][idx]));
          }
          Scalar w = matrixT[i][i].subtract(p);
          if (Scalars.lessThan(Im.FUNCTION.apply(eigenvalues[i]), DEFAULT_EPSILON.negate())) {
            z = w;
            r = ra;
            s = sa;
          } else {
            l = i;
            if (Scalars.isZero(Im.FUNCTION.apply(eigenvalues[i]))) {
              Scalar rsa = ComplexScalar.of(ra, sa);
              ReIm c3 = new ReIm(rsa.negate().divide(ComplexScalar.of(w, q)));
              matrixT[i][idx - 1] = c3.re();
              matrixT[i][idx] = c3.im();
            } else {
              // Solve complex equations
              Scalar x = matrixT[i][i + 1];
              Scalar y = matrixT[i + 1][i];
              Scalar vr = AbsSquared.FUNCTION.apply(eigenvalues[i].subtract(p)).subtract(q.multiply(q));
              Scalar vi = Times.of(RealScalar.TWO, Re.FUNCTION.apply(eigenvalues[i]).subtract(p), q);
              if (Scalars.isZero(vr) && Scalars.isZero(vi))
                // case not covered by tests
                vr = Times.of(EPSILON, norm, Vector1Norm.of(Tensors.of(w, q, x, y, z)));
              {
                Scalar rs = ComplexScalar.of(r, s);
                Scalar rsa = ComplexScalar.of(ra, sa);
                Scalar zq = ComplexScalar.of(z, q);
                ReIm c3 = new ReIm(x.multiply(rs).subtract(zq.multiply(rsa)).divide(ComplexScalar.of(vr, vi)));
                matrixT[i][idx - 1] = c3.re();
                matrixT[i][idx] = c3.im();
              }
              if (Scalars.lessThan(Abs.FUNCTION.apply(z).add(Abs.FUNCTION.apply(q)), Abs.FUNCTION.apply(x))) {
                matrixT[i + 1][idx - 1] = ra.negate().subtract(w.multiply(matrixT[i][idx - 1])).add(q.multiply(matrixT[i][idx])).divide(x);
                matrixT[i + 1][idx] = sa.negate().subtract(w.multiply(matrixT[i][idx])).subtract(q.multiply(matrixT[i][idx - 1])).divide(x);
              } else {
                Scalar rs = ComplexScalar.of(r, s);
                Scalar zq = ComplexScalar.of(z, q);
                Scalar mi = ComplexScalar.of(matrixT[i][idx - 1], matrixT[i][idx]);
                ReIm c3 = new ReIm(rs.negate().subtract(y.multiply(mi)).divide(zq));
                matrixT[i + 1][idx - 1] = c3.re();
                matrixT[i + 1][idx] = c3.im();
              }
            }
            // Overflow control
            Scalar t = Max.of( //
                Abs.FUNCTION.apply(matrixT[i][idx - 1]), //
                Abs.FUNCTION.apply(matrixT[i][idx]));
            if (Scalars.lessThan(RealScalar.ONE, Times.of(EPSILON, t, t)))
              // case not covered by tests
              for (int j = i; j <= idx; ++j) {
                matrixT[j][idx - 1] = matrixT[j][idx - 1].divide(t);
                matrixT[j][idx] = matrixT[j][idx].divide(t);
              }
          }
        }
      }
    }
    // Back transformation to get eigenvectors of original matrix
    for (int j = n - 1; 0 <= j; --j)
      for (int i = 0; i <= n - 1; ++i) {
        z = RealScalar.ZERO;
        for (int k = 0; k <= Math.min(j, n - 1); k++)
          z = z.add(matrixP[i][k].multiply(matrixT[k][j]));
        matrixP[i][j] = z;
      }
    eigenvectors = Transpose.of(Tensors.matrix(matrixP));
  }

  @Override // from Eigensystem
  public Tensor values() {
    return Tensors.of(eigenvalues);
  }

  @Override // from Eigensystem
  public Tensor diagonalMatrix() {
    int n = eigenvalues.length;
    Tensor values = SparseArray.of(RealScalar.ZERO, n, n); // TODO TENSOR zero
    for (int i = 0; i < n; ++i) {
      ReIm reIm = new ReIm(eigenvalues[i]);
      values.set(reIm.re(), i, i);
      if (Scalars.lessThan(DEFAULT_EPSILON, reIm.im()))
        values.set(reIm.im(), i, i + 1);
      else //
      if (Scalars.lessThan(reIm.im(), DEFAULT_EPSILON.negate()))
        values.set(reIm.im(), i, i - 1);
    }
    return values;
  }

  @Override // from Eigensystem
  public Tensor vectors() {
    return eigenvectors;
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("Eigensystem", values(), vectors());
  }
}
