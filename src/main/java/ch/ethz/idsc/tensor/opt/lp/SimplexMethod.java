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
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.TensorMap;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.num.Boole;
import ch.ethz.idsc.tensor.red.ArgMax;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.sca.Sign;

/** traditional simplex algorithm that performs poorly on Klee-Minty cube */
/* package */ class SimplexMethod {
  /** @param c
   * @param A
   * @param b
   * @param simplexPivot
   * @return */
  public static Tensor of(Tensor c, Tensor A, Tensor b, SimplexPivot simplexPivot) {
    int m = b.length();
    int n = c.length();
    // System.out.println(m + " x " + n);
    SimplexMethod simplexMethod;
    {
      // Tensor D = DiagonalMatrix.of(b.map(UnitStep.function));
      // IdentityMatrix.of(m)
      // System.out.println(Pretty.of(A));
      Tensor tab = Join.of(1, A, IdentityMatrix.of(m), Partition.of(b, 1));
      // System.out.println(Pretty.of(tab));
      Tensor row = Tensors.vector(i -> Boole.of(n <= i && i < n + m), n + m + 1);
      // System.out.println(row);
      for (int index = 0; index < m; ++index) // make all entries in bottom row zero
        row = row.subtract(tab.get(index));
      row.set(RealScalar.ZERO, n + m); // set bottom corner to 0
      tab.append(row);
      simplexMethod = new SimplexMethod(tab, Range.of(n, n + m), simplexPivot); // phase 1
    }
    Tensor tab = Join.of(1, //
        TensorMap.of(row -> row.extract(0, n), simplexMethod.tab.extract(0, m), 1), //
        Partition.of(simplexMethod.tab.get(Tensor.ALL, n + m).extract(0, m), 1));
    tab.append(Append.of(c, RealScalar.ZERO)); // set bottom corner to 0
    // System.out.println(Pretty.of(tab));
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
    n = Unprotect.dimension1(tab) - 1;
    if (isOutsideRange(ind, n))
      throw TensorRuntimeException.of(ind);
    while (true) {
      // System.out.println(Pretty.of(tab));
      Tensor c = Tensor.of(tab.get(m).stream().limit(n));
      final int j = ArgMin.of(withoutUnits(c));
      if (Sign.isNegative(c.Get(j))) {
        { // check if unbounded
          int argmax = ArgMax.of(withoutUnits(tab.get(Tensor.ALL, j).extract(0, m)));
          Sign.requirePositive(tab.Get(argmax, j)); // otherwise problem unbounded
        }
        int p = simplexPivot.get(tab, j, n);
        ind.set(RealScalar.of(j), p);
        // System.out.println(ind);
        tab.set(tab.get(p).divide(tab.Get(p, j)), p); // normalize
        for (int i = 0; i < tab.length(); ++i)
          if (i != p)
            tab.set(tab.get(i).subtract(tab.get(p).multiply(tab.Get(i, j))), i);
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
