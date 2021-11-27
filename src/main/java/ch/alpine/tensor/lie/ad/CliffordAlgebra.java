// code by jph
package ch.alpine.tensor.lie.ad;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Ordering;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.Subsets;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.spa.SparseArray;

/** geometric algebra
 * 
 * <p>Remark:
 * Cl(0, 1) is algebra-isomorphic to the complex scalars
 * Cl(0, 2) is algebra-isomorphic to the quaternions
 * 
 * <p>Careful:
 * the memory use of the representation of Cl(p, q) in the implementation is in
 * the order of (2 ^ (p+q)) ^ 3.
 * 
 * <p>Reference:
 * https://en.wikipedia.org/wiki/Clifford_algebra */
public class CliffordAlgebra implements Serializable {
  private static final Scalar[] SIGN = { //
      RealScalar.ONE, //
      RealScalar.ONE.negate() };

  /** @param p non-negative
   * @param q non-negative
   * @return Cl(p, q) */
  public static CliffordAlgebra of(int p, int q) {
    return new CliffordAlgebra(p, q);
  }

  /** @param p non-negative
   * @return Cl(p, 0) */
  public static CliffordAlgebra positive(int p) {
    return of(p, 0);
  }

  /** @param q non-negative
   * @return Cl(0, q) */
  public static CliffordAlgebra negative(int q) {
    return of(0, q);
  }

  // ---
  private final int signature_p;
  private final Tensor gp;
  /** vector */
  private final Tensor reverse;
  private final int[] offset;

  private CliffordAlgebra(int p, int q) {
    this.signature_p = p;
    int n = p + q;
    Tensor range = Range.of(0, n);
    int m = 1 << n;
    offset = new int[n + 2];
    List<Tensor> list = new ArrayList<>(m);
    Map<Tensor, Integer> map = new HashMap<>();
    for (int k = 0; k <= n; ++k) {
      Tensor subsets = Subsets.of(range, k);
      offset[k + 1] = offset[k] + subsets.length();
      for (Tensor perm : subsets) {
        list.add(perm);
        map.put(perm, map.size());
      }
    }
    Integers.requireEquals(list.size(), m);
    gp = Array.sparse(m, m, m);
    for (int i = 0; i < m; ++i)
      for (int j = 0; j < m; ++j) {
        SignedSubset signedSubset = new SignedSubset(Join.of(list.get(i), list.get(j)));
        gp.set(signedSubset.sign(), map.get(signedSubset.normal), j, i);
      }
    reverse = Tensor.of(list.stream() //
        .map(Reverse::of) //
        .map(SignedSubset::new) //
        .map(SignedSubset::sign));
  }

  /** @param x
   * @param y
   * @return geometric product of multivectors x and y */
  public Tensor gp(Tensor x, Tensor y) {
    return gp.dot(VectorQ.require(x)).dot(VectorQ.require(y));
  }

  /** @param x
   * @param y
   * @return commutator product of multivectors of x and y */
  public Tensor cp(Tensor x, Tensor y) {
    return gp(x, y).subtract(gp.dot(y).dot(x)).multiply(RationalScalar.HALF);
  }

  /** @return geometric product as tensor of rank 3
   * @see SparseArray */
  public Tensor gp() {
    return gp.copy();
  }

  /** @return commutator product tensor of rank 3 that satisfies {@link JacobiIdentity}
   * @see SparseArray */
  public Tensor cp() {
    return gp.subtract(Transpose.of(gp, 0, 2, 1)).multiply(RationalScalar.HALF);
  }

  /** @param x multivector
   * @return
   * @throws Exception if x is not a vector */
  public Tensor reverse(Tensor x) {
    return Times.of(VectorQ.require(x), reverse);
  }

  public Tensor grade(Tensor x, int grade) {
    Tensor y = x.copy();
    IntStream.range(0, offset[grade]).forEach(index -> y.set(Scalar::zero, index));
    IntStream.range(offset[grade + 1], gp.length()).forEach(index -> y.set(Scalar::zero, index));
    return y;
  }

  /** @param x multivector
   * @return
   * @throws Exception if x cannot be inverted */
  public Tensor reciprocal(Tensor x) {
    return LinearSolve.of(gp.dot(x), UnitVector.of(gp.length(), 0));
  }

  /** @param x multivector
   * @return */
  public Tensor exp(Tensor x) {
    return MatrixExp.of(gp.dot(x)).get(Tensor.ALL, 0);
  }

  private class SignedSubset {
    private final Scalar sign;
    private final Tensor normal;

    /** @param indices for instance {5, 2, 3, 0, 3, 5} */
    public SignedSubset(Tensor indices) {
      int[] ordering = Ordering.INCREASING.of(indices);
      int parity = Integers.parity(ordering);
      Deque<Scalar> deque = new ArrayDeque<>();
      for (int index : ordering) {
        Scalar scalar = indices.Get(index);
        if (scalar.equals(deque.peekLast())) { // peekLast returns null if deque is empty
          Scalar duplicate = deque.pollLast(); // check for sign in scalar product 0, ..., n - 1
          if (signature_p <= duplicate.number().intValue())
            parity ^= 1;
        } else
          deque.add(scalar);
      }
      sign = SIGN[parity];
      normal = Tensor.of(deque.stream());
    }

    public Scalar sign() {
      return sign;
    }
  }
}
