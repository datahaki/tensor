// code corresponds to "org.hipparchus.linear.SchurTransformer" in Hipparchus project
// adapted by jph
package ch.alpine.tensor.mat.qr;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.nrm.Hypot;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.EqualsReduce;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Sqrt;

/** Quote from SchurTransformer:
 * "A m &times; m matrix A can be written as the product of three matrices:
 * A = P . T . P' with P an orthogonal matrix and T an quasi-triangular
 * matrix. Both P and T are m &times; m matrices."
 * 
 * For the special case of symmetric matrices the result corresponds to
 * {@link Eigensystem#ofSymmetric(Tensor)}
 * 
 * Implementation works for matrices consisting of scalars of type {@link Quantity}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/SchurDecomposition.html">SchurDecomposition</a> */
public class SchurDecomposition implements Serializable {
  /** Maximum allowed iterations for convergence of the transformation. */
  private static final int MAX_ITERATIONS = 100;
  private static final Scalar EPSILON = RealScalar.of(1 - Math.nextDown(1.0));
  private static final Chop CHOP = Chop.below(Math.nextUp(1 - Math.nextDown(1.0)));

  public static SchurDecomposition of(Tensor matrix) {
    return new SchurDecomposition(HessenbergDecomposition.of(matrix));
  }

  // ---
  private final Scalar zero;
  private final Scalar[][] hmt;
  private final Scalar[][] pam;

  private SchurDecomposition(HessenbergDecomposition hessenbergDecomposition) {
    zero = EqualsReduce.zero(hessenbergDecomposition.getH());
    hmt = ScalarArray.ofMatrix(hessenbergDecomposition.getH());
    pam = ScalarArray.ofMatrix(hessenbergDecomposition.getUnitary());
    // ---
    final int n = hmt.length;
    // compute matrix norm
    final Scalar norm = getNorm();
    // shift information
    final ShiftInfo shift = new ShiftInfo();
    shift.exShift = zero;
    // Outer loop over eigenvalue index
    int iteration = 0;
    int iu = n - 1;
    while (0 <= iu) {
      // Look for single small sub-diagonal element
      final int il = findSmallSubDiagonalElement(iu, norm);
      // Check for convergence
      if (il == iu) {
        // One root found
        hmt[iu][iu] = hmt[iu][iu].add(shift.exShift);
        --iu;
        iteration = 0;
      } else //
      if (il == iu - 1) {
        // Two roots found
        Scalar p = hmt[iu - 1][iu - 1].subtract(hmt[iu][iu]).divide(RealScalar.TWO);
        Scalar q = p.multiply(p).add(hmt[iu][iu - 1].multiply(hmt[iu - 1][iu]));
        hmt[iu][iu] = hmt[iu][iu].add(shift.exShift);
        hmt[iu - 1][iu - 1] = hmt[iu - 1][iu - 1].add(shift.exShift);
        if (Sign.isPositiveOrZero(q)) {
          Scalar z = Sqrt.FUNCTION.apply(Abs.FUNCTION.apply(q)); // TODO TENSOR ABS should be obsolete
          z = Sign.isPositiveOrZero(p) //
              ? p.add(z)
              : p.subtract(z);
          final Scalar x = hmt[iu][iu - 1];
          final Scalar s = Abs.FUNCTION.apply(x).add(Abs.FUNCTION.apply(z));
          p = x.divide(s);
          q = z.divide(s);
          final Scalar r = Hypot.of(p, q);
          p = p.divide(r);
          q = q.divide(r);
          // Row modification
          for (int j = iu - 1; j < n; ++j) {
            Scalar c = hmt[iu - 1][j];
            hmt[iu - 1][j] = q.multiply(c).add(p.multiply(hmt[iu][j]));
            hmt[iu][j] = q.multiply(hmt[iu][j]).subtract(p.multiply(c));
          }
          // Column modification
          for (int i = 0; i <= iu; ++i) {
            Scalar c = hmt[i][iu - 1];
            hmt[i][iu - 1] = q.multiply(c).add(p.multiply(hmt[i][iu]));
            hmt[i][iu] = q.multiply(hmt[i][iu]).subtract(p.multiply(c));
          }
          // Accumulate transformations
          for (int i = 0; i <= n - 1; ++i) {
            Scalar c = pam[i][iu - 1];
            pam[i][iu - 1] = q.multiply(c).add(p.multiply(pam[i][iu]));
            pam[i][iu] = q.multiply(pam[i][iu]).subtract(p.multiply(c));
          }
        }
        iu -= 2;
        iteration = 0;
      } else {
        // No convergence yet
        computeShift(il, iu, iteration, shift);
        // stop transformation after too many iterations
        ++iteration;
        if (MAX_ITERATIONS < iteration)
          throw new Throw();
        // the initial houseHolder vector for the QR step
        final Scalar[] hVec = new Scalar[3]; // {RealScalar.ZERO,RealScalar.ZERO,RealScalar.ZERO};
        final int im = initQRStep(il, iu, shift, hVec);
        performDoubleQRStep(il, im, iu, shift, hVec);
      }
    }
  }

  private Scalar getNorm() {
    Scalar norm = zero;
    for (int i = 0; i < hmt.length; ++i)
      // as matrix T is (quasi-)triangular, also take the sub-diagonal element into account
      for (int j = Math.max(i - 1, 0); j < hmt.length; ++j)
        norm = norm.add(Abs.FUNCTION.apply(hmt[i][j]));
    return norm;
  }

  private static class ShiftInfo {
    /** x shift info */
    Scalar x;
    /** y shift info */
    Scalar y;
    /** w shift info */
    Scalar w;
    /** Indicates an exceptional shift. */
    Scalar exShift;

    Scalar warp(Scalar s) {
      return x.subtract(w.divide(((y.subtract(x).divide(RealScalar.TWO).add(s)))));
    }
  }

  private int findSmallSubDiagonalElement(final int startIdx, final Scalar norm) {
    int l = startIdx;
    while (0 < l) {
      Scalar s = Abs.FUNCTION.apply(hmt[l - 1][l - 1]).add(Abs.FUNCTION.apply(hmt[l][l]));
      if (Scalars.isZero(s))
        s = norm;
      if (Scalars.lessThan(Abs.FUNCTION.apply(hmt[l][l - 1]), EPSILON.multiply(s)))
        break;
      --l;
    }
    return l;
  }

  private void computeShift(final int l, final int idx, final int iteration, final ShiftInfo shift) {
    // Form shift
    shift.x = hmt[idx][idx];
    shift.y = shift.w = shift.x.zero();
    if (l < idx) {
      shift.y = hmt[idx - 1][idx - 1];
      shift.w = hmt[idx][idx - 1].multiply(hmt[idx - 1][idx]);
    }
    // Wilkinson's original ad hoc shift
    if (iteration == 10) {
      shift.exShift = shift.exShift.add(shift.x);
      for (int i = 0; i <= idx; ++i)
        hmt[i][i] = hmt[i][i].subtract(shift.x);
      final Scalar s = Abs.FUNCTION.apply(hmt[idx][idx - 1]).add(Abs.FUNCTION.apply(hmt[idx - 1][idx - 2]));
      shift.x = RealScalar.of(0.75).multiply(s);
      shift.y = RealScalar.of(0.75).multiply(s);
      shift.w = RealScalar.of(-0.4375).multiply(s).multiply(s);
    }
    // MATLAB's new ad hoc shift
    if (iteration == 30) {
      Scalar s = shift.y.subtract(shift.x).divide(RealScalar.TWO);
      s = s.multiply(s).add(shift.w);
      if (Sign.isPositive(s)) {
        s = Sqrt.FUNCTION.apply(s);
        if (Scalars.lessThan(shift.y, shift.x))
          s = s.negate();
        s = shift.warp(s);
        for (int i = 0; i <= idx; ++i)
          hmt[i][i] = hmt[i][i].subtract(s);
        shift.exShift = shift.exShift.add(s);
        shift.x = shift.y = shift.w = RealScalar.of(0.964);
      }
    }
  }

  private int initQRStep(int il, final int iu, final ShiftInfo shift, Scalar[] hVec) {
    // Look for two consecutive small sub-diagonal elements
    int im = iu - 2;
    while (il <= im) {
      final Scalar z = hmt[im][im];
      final Scalar r = shift.x.subtract(z);
      Scalar s = shift.y.subtract(z);
      hVec[0] = r.multiply(s).subtract(shift.w).divide(hmt[im + 1][im]).add(hmt[im][im + 1]);
      hVec[1] = hmt[im + 1][im + 1].subtract(z).subtract(r).subtract(s);
      hVec[2] = hmt[im + 2][im + 1];
      if (im == il)
        break;
      final Scalar lhs = Abs.FUNCTION.apply(hmt[im][im - 1]).multiply(Abs.FUNCTION.apply(hVec[1]).add(Abs.FUNCTION.apply(hVec[2])));
      final Scalar rhs = Abs.FUNCTION.apply(hVec[0])
          .multiply(Abs.FUNCTION.apply(hmt[im - 1][im - 1]).add(Abs.FUNCTION.apply(z)).add(Abs.FUNCTION.apply(hmt[im + 1][im + 1])));
      if (Scalars.lessThan(lhs, EPSILON.multiply(rhs)))
        break;
      --im;
    }
    return im;
  }

  private void performDoubleQRStep(final int il, final int im, final int iu, final ShiftInfo shift, final Scalar[] hVec) {
    final int n = hmt.length;
    Scalar p = hVec[0];
    Scalar q = hVec[1];
    Scalar r = hVec[2];
    for (int k = im; k <= iu - 1; ++k) {
      boolean notlast = k != iu - 1;
      if (k != im) {
        p = hmt[k][k - 1];
        q = hmt[k + 1][k - 1];
        r = notlast //
            ? hmt[k + 2][k - 1]
            : zero;
        shift.x = Abs.FUNCTION.apply(p).add(Abs.FUNCTION.apply(q)).add(Abs.FUNCTION.apply(r));
        if (CHOP.isZero(shift.x)) // related to EPSILON (see commented line above)
          continue;
        p = p.divide(shift.x);
        q = q.divide(shift.x);
        r = r.divide(shift.x);
      }
      Scalar s = Hypot.ofVector(Tensors.of(p, q, r));
      if (Sign.isNegative(p))
        s = s.negate();
      if (Scalars.nonZero(s)) {
        if (k != im) {
          hmt[k][k - 1] = s.negate().multiply(shift.x);
        } else if (il != im) {
          hmt[k][k - 1] = hmt[k][k - 1].negate();
        }
        p = p.add(s);
        shift.x = p.divide(s);
        shift.y = q.divide(s);
        Scalar z = r.divide(s);
        q = q.divide(p);
        r = r.divide(p);
        // Row modification
        for (int j = k; j < n; ++j) {
          p = hmt[k][j].add(q.multiply(hmt[k + 1][j]));
          if (notlast) {
            p = p.add(r.multiply(hmt[k + 2][j]));
            hmt[k + 2][j] = hmt[k + 2][j].subtract(p.multiply(z));
          }
          hmt[k][j] = hmt[k][j].subtract(p.multiply(shift.x));
          hmt[k + 1][j] = hmt[k + 1][j].subtract(p.multiply(shift.y));
        }
        // Column modification
        for (int i = 0; i <= Math.min(iu, k + 3); ++i) {
          p = shift.x.multiply(hmt[i][k]).add(shift.y.multiply(hmt[i][k + 1]));
          if (notlast) {
            p = p.add(z.multiply(hmt[i][k + 2]));
            hmt[i][k + 2] = hmt[i][k + 2].subtract(p.multiply(r));
          }
          hmt[i][k] = hmt[i][k].subtract(p);
          hmt[i][k + 1] = hmt[i][k + 1].subtract(p.multiply(q));
        }
        // Accumulate transformations
        final int high = hmt.length - 1;
        for (int i = 0; i <= high; ++i) {
          p = shift.x.multiply(pam[i][k]).add(shift.y.multiply(pam[i][k + 1]));
          if (notlast) {
            p = p.add(z.multiply(pam[i][k + 2]));
            pam[i][k + 2] = pam[i][k + 2].subtract(p.multiply(r));
          }
          pam[i][k] = pam[i][k].subtract(p);
          pam[i][k + 1] = pam[i][k + 1].subtract(p.multiply(q));
        }
      } // (s != 0)
    } // k loop
    // clean up pollution due to round-off errors
    for (int i = im + 2; i <= iu; ++i) {
      hmt[i][i - 2] = zero; // hmt[i][i - 2].zero();
      if (im + 2 < i)
        hmt[i][i - 3] = zero; // hmt[i][i - 3].zero();
    }
  }

  /** @return
   * @see UnitaryMatrixQ */
  public Tensor getUnitary() {
    return Tensors.matrix(pam);
  }

  /** @return quasi upper triangular matrix, i.e.
   * along the diagonal there are either 1x1 blocks or 2x2 blocks
   * the latter of which are 2x2 rotation matrices */
  public Tensor getT() {
    return Tensors.matrix(hmt);
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("SchurDecomposition", getUnitary(), getT());
  }
}
