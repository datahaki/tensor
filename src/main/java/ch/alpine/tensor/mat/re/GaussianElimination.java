// code by jph
package ch.alpine.tensor.mat.re;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.qty.LenientAdd;

/** Gaussian elimination is the most important algorithm of all time.
 * 
 * <p>Quote from Wikipedia:
 * The method of Gaussian elimination appears in the Chinese mathematical text Chapter
 * Eight Rectangular Arrays of The Nine Chapters on the Mathematical Art. Its use is
 * illustrated in eighteen problems, with two to five equations. The first reference to
 * the book by this title is dated to 179 CE, but parts of it were written as early as
 * approximately 150 BCE. It was commented on by Liu Hui in the 3rd century.
 * The method in Europe stems from the notes of Isaac Newton. In 1670, he wrote that all
 * the algebra books known to him lacked a lesson for solving simultaneous equations,
 * which Newton then supplied. Cambridge University eventually published the notes as
 * Arithmetica Universalis in 1707 long after Newton left academic life. The notes were
 * widely imitated, which made (what is now called) Gaussian elimination a standard lesson
 * in algebra textbooks by the end of the 18th century. Carl Friedrich Gauss in 1810
 * devised a notation for symmetric elimination that was adopted in the 19th century by
 * professional hand computers to solve the normal equations of least-squares problems.
 * The algorithm that is taught in high school was named for Gauss only in the 1950s as
 * a result of confusion over the history of the subject.
 * 
 * GaussianElimination is public for the rare case when the solution to a linear system
 * together with the determinant of the matrix are required.
 * 
 * @see LinearSolve */
public class GaussianElimination extends AbstractReduce {
  /** @param matrix square and invertible
   * @param b tensor with first dimension identical to size of matrix
   * @param pivot
   * @throws Throw if matrix m is singular */
  public static Tensor of(Tensor matrix, Tensor b, Pivot pivot) {
    return new GaussianElimination(matrix, b, pivot).solve();
  }

  // ---
  private final Tensor[] rhs;

  /** @param matrix square and invertible
   * @param b tensor with first dimension identical to size of matrix
   * @param pivot
   * @throws Exception if matrix is singular
   * @throws Exception if dimensions mismatch */
  public GaussianElimination(Tensor matrix, Tensor b, Pivot pivot) {
    super(matrix, pivot);
    rhs = b.stream().toArray(Tensor[]::new);
    int n = Integers.requireEquals(lhs.length, lhs[0].length()); // matrix is square
    Integers.requireEquals(n, rhs.length); // rhs matches dimensions of lhs
    for (int c0 = 0; c0 < n; ++c0) {
      pivot(c0, c0);
      Scalar piv = lhs[ind(c0)].Get(c0);
      if (Scalars.isZero(piv))
        throw Throw.of(matrix, piv);
      eliminate(c0, piv);
    }
  }

  private void eliminate(int c0, Scalar piv) {
    int ic0 = ind(c0);
    for (int c1 = c0 + 1; c1 < lhs.length; ++c1) { // deliberately without parallel
      int ic1 = ind(c1);
      Scalar fac = lhs[ic1].Get(c0).divide(piv).negate();
      lhs[ic1] = lhs[ic1].add(lhs[ic0].multiply(fac));
      rhs[ic1] = LenientAdd.of(rhs[ic1], rhs[ic0].multiply(fac));
    }
  }

  /** @return x with m.dot(x) == b */
  public Tensor solve() {
    Tensor[] sol = new Tensor[rhs.length];
    for (int c0 = sol.length - 1; 0 <= c0; --c0) {
      int ic0 = ind(c0);
      Tensor sum = rhs[ic0];
      for (int c1 = c0 + 1; c1 < rhs.length; ++c1)
        sum = LenientAdd.of(sum, sol[c1].multiply(lhs[ic0].Get(c1).negate()));
      sol[c0] = sum.divide(lhs[ic0].Get(c0));
    }
    return Unprotect.byRef(sol);
  }
}
