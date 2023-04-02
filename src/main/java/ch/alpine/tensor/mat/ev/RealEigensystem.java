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
import ch.alpine.tensor.red.EqualsReduce;
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
  private final Scalar zero;
  private final Tensor eigenvectors;
  private final Scalar[] eigenvalues;

  public RealEigensystem(Tensor matrix) {
    zero = EqualsReduce.zero(matrix);
    SchurDecomposition schurDecomposition = SchurDecomposition.of(matrix);
    final int n = matrix.length();
    eigenvalues = ScalarArray.ofVector(Array.zeros(n));
    final Scalar[][] matrixT = ScalarArray.ofMatrix(schurDecomposition.getT());
    ComplexWrap cmplxWrp = new ComplexWrap(matrixT);
    {
      Scalar norm = Matrix1Norm.of(matrix);
      for (int i = 0; i < n; ++i)
        if (i == (n - 1) || //
            Scalars.lessThan(Abs.FUNCTION.apply(matrixT[i + 1][i]), norm.multiply(DEFAULT_EPSILON)))
          eigenvalues[i] = matrixT[i][i];
        else {
          Scalar x0 = matrixT[i + 0][i + 0];
          Scalar x1 = matrixT[i + 1][i + 1];
          Scalar half = x1.subtract(x0).multiply(RationalScalar.HALF);
          Scalar zr = x0.add(half); // mean
          Scalar zi = Sqrt.FUNCTION.apply(Abs.FUNCTION.apply(half.multiply(half).add(matrixT[i + 1][i].multiply(matrixT[i][i + 1]))));
          eigenvalues[i] = ComplexScalar.of(zr, zi);
          eigenvalues[i + 1] = Conjugate.FUNCTION.apply(eigenvalues[i]);
          ++i;
        }
    }
    final Scalar[][] matrixP = ScalarArray.ofMatrix(schurDecomposition.getUnitary());
    // compute matrix norm
    Scalar norm = zero;
    for (int i = 0; i < n; ++i)
      for (int j = Math.max(i - 1, 0); j < n; ++j)
        norm = norm.add(Abs.FUNCTION.apply(matrixT[i][j]));
    // we can not handle a matrix with zero norm
    if (Scalars.isZero(norm))
      throw new Throw(matrix);
    // Backsubstitute to find vectors of upper triangular form
    for (int idx = n - 1; 0 <= idx; --idx) {
      Scalar zr = RealScalar.ZERO;
      Scalar zi = RealScalar.ZERO;
      Scalar z = RealScalar.ZERO;
      final Scalar p = Re.FUNCTION.apply(eigenvalues[idx]);
      final Scalar q = Im.FUNCTION.apply(eigenvalues[idx]);
      if (Tolerance.CHOP.isZero(q)) {
        // Real vector
        int l = idx;
        matrixT[idx][idx] = RealScalar.ONE;
        for (int i = idx - 1; 0 <= i; --i) {
          Scalar w = matrixT[i][i].subtract(p);
          zr = zero;
          for (int j = l; j <= idx; ++j)
            zr = zr.add(matrixT[i][j].multiply(matrixT[j][idx]));
          Scalar chopped = Tolerance.CHOP.apply(Im.FUNCTION.apply(eigenvalues[i]));
          if (Sign.isNegative(chopped)) {
            z = w;
            zi = zr;
          } else {
            l = i;
            if (Scalars.isZero(Im.FUNCTION.apply(eigenvalues[i])))
              matrixT[i][idx] = Scalars.nonZero(w) //
                  ? zr.divide(w).negate()
                  : zr.divide(DEFAULT_EPSILON.multiply(norm)).negate();
            else {
              // Solve real equations
              Scalar x = matrixT[i][i + 1];
              Scalar y = matrixT[i + 1][i];
              Scalar fq = AbsSquared.FUNCTION.apply(eigenvalues[i].subtract(p));
              Scalar t = x.multiply(zi).subtract(z.multiply(zr)).divide(fq);
              matrixT[i][idx] = t;
              matrixT[i + 1][idx] = Scalars.lessThan(Abs.FUNCTION.apply(z), Abs.FUNCTION.apply(x)) //
                  ? zr.negate().subtract(w.multiply(t)).divide(x)
                  : zi.negate().subtract(y.multiply(t)).divide(z);
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
        {
          ReIm reIm = new ReIm(Scalars.lessThan( //
              Abs.FUNCTION.apply(matrixT[idx - 1][idx]), //
              Abs.FUNCTION.apply(matrixT[idx][idx - 1])) //
                  ? eigenvalues[idx].subtract(matrixT[idx][idx]).divide(matrixT[idx][idx - 1])
                  : matrixT[idx - 1][idx].divide(eigenvalues[idx].subtract(matrixT[idx - 1][idx - 1])));
          matrixT[idx - 1][idx] = reIm.re();
          matrixT[idx - 1][idx - 1] = reIm.im();
        }
        matrixT[idx][idx - 1] = RealScalar.ZERO;
        matrixT[idx][idx] = RealScalar.ONE;
        for (int i = idx - 2; 0 <= i; --i) {
          Scalar ra = zero;
          Scalar sa = zero;
          for (int j = l; j <= idx; ++j) {
            ra = ra.add(matrixT[i][j].multiply(matrixT[j][idx - 1]));
            sa = sa.add(matrixT[i][j].multiply(matrixT[j][idx]));
          }
          Scalar w = matrixT[i][i].subtract(p);
          Scalar chopped = Tolerance.CHOP.apply(Im.FUNCTION.apply(eigenvalues[i]));
          if (Sign.FUNCTION.apply(chopped).equals(RealScalar.of(-1))) {
            z = w;
            zr = ra;
            zi = sa;
          } else {
            l = i;
            Scalar rsa = ComplexScalar.of(ra, sa);
            Scalar wq = ComplexScalar.of(w, q);
            if (Scalars.isZero(Im.FUNCTION.apply(eigenvalues[i]))) {
              cmplxWrp.set(i, idx, rsa.divide(wq).negate());
            } else {
              // Solve complex equations
              Scalar x = matrixT[i][i + 1];
              Scalar y = matrixT[i + 1][i];
              Scalar vr = AbsSquared.FUNCTION.apply(eigenvalues[i].subtract(p)).subtract(q.multiply(q));
              Scalar vi = Times.of(RealScalar.TWO, Re.FUNCTION.apply(eigenvalues[i]).subtract(p), q);
              if (Scalars.isZero(vr) && Scalars.isZero(vi))
                // case not covered by tests
                vr = Times.of(EPSILON, norm, Vector1Norm.of(Tensors.of(w, q, x, y, z)));
              Scalar zq = ComplexScalar.of(z, q);
              {
                Scalar rs = ComplexScalar.of(zr, zi);
                Scalar vz = ComplexScalar.of(vr, vi);
                cmplxWrp.set(i, idx, x.multiply(rs).subtract(zq.multiply(rsa)).divide(vz));
              }
              Scalar cc = ComplexScalar.of(matrixT[i][idx - 1], matrixT[i][idx]);
              if (Scalars.lessThan(Abs.FUNCTION.apply(z).add(Abs.FUNCTION.apply(q)), Abs.FUNCTION.apply(x)))
                cmplxWrp.set(i + 1, idx, wq.multiply(cc).add(rsa).divide(x).negate());
              else {
                Scalar rs = ComplexScalar.of(zr, zi);
                cmplxWrp.set(i + 1, idx, rs.add(y.multiply(cc)).divide(zq).negate());
              }
            }
            // Overflow control
            Scalar t = Max.of( //
                Abs.FUNCTION.apply(matrixT[i][idx - 1]), //
                Abs.FUNCTION.apply(matrixT[i][idx]));
            if (Scalars.lessThan(RealScalar.ONE, Times.of(EPSILON, t, t)))
              // case not covered by tests
              for (int j = i; j <= idx; ++j)
                cmplxWrp.set(j, idx, ComplexScalar.of(matrixT[j][idx - 1], matrixT[j][idx]).divide(t));
          }
        }
      }
    }
    // Back transformation to get eigenvectors of original matrix
    // at this point matrixT is upper triangular matrix but lower diagonal entries may still have unit
    for (int j = 1; j < n; ++j)
      for (int i = 0; i < j; ++i)
        matrixT[j][i] = matrixT[j][i].one().zero();
    eigenvectors = Transpose.of(Tensors.matrix(matrixP).dot(Tensors.matrix(matrixT)));
  }

  @Override // from Eigensystem
  public Tensor values() {
    return Tensors.of(eigenvalues);
  }

  @Override // from Eigensystem
  public Tensor diagonalMatrix() {
    int n = eigenvalues.length;
    Tensor values = SparseArray.of(zero, n, n);
    for (int i = 0; i < n; ++i) {
      ReIm reIm = new ReIm(eigenvalues[i]);
      values.set(reIm.re(), i, i);
      Scalar chopped = Tolerance.CHOP.apply(reIm.im());
      if (Sign.isPositive(chopped))
        values.set(reIm.im(), i, i + 1);
      else //
      if (Sign.isNegative(chopped))
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
