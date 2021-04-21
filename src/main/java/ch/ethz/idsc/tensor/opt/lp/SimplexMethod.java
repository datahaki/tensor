// MATLAB code by Nasser M. Abbasi
// http://12000.org/my_notes/simplex/index.htm
// adapted by jph
package ch.ethz.idsc.tensor.opt.lp;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Append;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.ArrayFlatten;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.red.ArgMax;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Sign;

/** traditional simplex algorithm that performs poorly on Klee-Minty cube */
/* package */ class SimplexMethod {
  /** @param c
   * @param A
   * @param b
   * @param simplexPivot
   * @return x with A.x == b so that c.x is minimal */
  public static Tensor of(Tensor c, Tensor A, Tensor b, SimplexPivot simplexPivot) {
    int m = b.length();
    int n = c.length();
    SimplexMethod simplexMethod = new SimplexMethod(ArrayFlatten.of(new Tensor[][] { //
        { A, IdentityMatrix.of(m), Partition.of(b, 1) }, //
        { Tensors.of(Total.of(A).negate()), Array.zeros(1, m), Tensors.of(Tensors.of(Total.ofVector(b).zero())) }, //
    }), Range.of(n, n + m), simplexPivot); // phase 1
    Tensor tab = Tensor.of(simplexMethod.tab.stream().limit(m) //
        .map(row -> row.extract(0, n).append(Last.of(row))));
    /* set bottom corner to 0, column generally does not have uniform unit */
    tab.append(Append.of(c, RealScalar.ZERO));
    return new SimplexMethod(tab, simplexMethod.ind, simplexPivot).getX(); // phase 2
  }

  /***************************************************/
  private final Tensor tab; // (m+1) x (n+1)
  private final Tensor ind; // vector of length m
  private final int m;
  private final int n;

  private SimplexMethod(Tensor tab, Tensor ind, SimplexPivot simplexPivot) {
    this.tab = tab;
    this.ind = ind;
    m = tab.length() - 1;
    n = Unprotect.dimension1Hint(tab) - 1;
    if (isOutsideRange(ind, n))
      throw TensorRuntimeException.of(ind);
    while (true) {
      /* the tests pass for "c = tab.get(m)" as well!? */
      Tensor c = tab.get(m).extract(0, n);
      int j = ArgMin.of(withoutUnits(c));
      if (Sign.isNegative(c.Get(j))) {
        { // check if unbounded
          int argmax = ArgMax.of(withoutUnits(tab.get(Tensor.ALL, j).extract(0, m)));
          Sign.requirePositive(tab.Get(argmax, j)); // otherwise problem unbounded
        }
        int p = simplexPivot.get(tab, j, n);
        ind.set(RealScalar.of(j), p);
        tab.set(row -> row.divide(row.Get(j)), p); // normalize
        Tensor tab_p = tab.get(p);
        for (int i = 0; i < tab.length(); ++i)
          if (i != p)
            tab.set(row -> row.subtract(tab_p.multiply(row.Get(j))), i);
      } else
        break;
    }
  }

  private Tensor getX() {
    Tensor x = Array.zeros(n);
    for (int index = 0; index < ind.length(); ++index)
      x.set(tab.Get(index, n), ind.Get(index).number().intValue());
    return x;
  }

  // helper function to check consistency
  private static boolean isOutsideRange(Tensor ind, int n) {
    return ind.stream() //
        .map(Scalar.class::cast) //
        .map(Scalars::intValueExact) //
        .anyMatch(i -> n <= i);
  }

  // helper function
  private static Tensor withoutUnits(Tensor vector) {
    return vector.map(Unprotect::withoutUnit);
  }
}
