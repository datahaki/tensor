// MATLAB code by Nasser M. Abbasi
// http://12000.org/my_notes/simplex/index.htm
// adapted by jph
package ch.alpine.tensor.opt.lp;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ArrayFlatten;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.alg.Partition;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.ext.ArgMax;
import ch.alpine.tensor.ext.ArgMin;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.qty.LenientAdd;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Sign;

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
    /* set bottom corner to 0, column generally does not have uniform unit */
    return new SimplexMethod(Tensor.of(simplexMethod.tab.stream().limit(m) //
        .map(row -> row.extract(0, n).append(Last.of(row)))) //
        .append(Append.of(c, RealScalar.ZERO)), //
        simplexMethod.ind, simplexPivot).getX(); // phase 2
  }

  /** @param linearProgram
   * @param simplexPivot
   * @return */
  public static Tensor of(LinearProgram linearProgram, SimplexPivot simplexPivot) {
    Scalar c_zero = Total.ofVector(linearProgram.c.map(Scalar::zero));
    Tensor tab = ArrayFlatten.of(new Tensor[][] { //
        { linearProgram.A, Partition.of(linearProgram.b, 1) }, //
        { Tensors.of(linearProgram.minObjective()), Tensors.of(Tensors.of(c_zero)) } });
    Tensor ind = Range.of(linearProgram.var_count(), linearProgram.c.length());
    return new SimplexMethod(tab, ind, simplexPivot).getX();
  }

  // ---
  private final Tensor tab; // (m+1) x (n+1)
  private final Tensor ind; // vector of length m
  // private final m;
  private final int n;

  private SimplexMethod(Tensor tab, Tensor ind, SimplexPivot simplexPivot) {
    this.tab = tab;
    this.ind = ind;
    int m = tab.length() - 1;
    n = Unprotect.dimension1Hint(tab) - 1;
    if (!StaticHelper.isInsideRange(ind, n) || ind.length() != m)
      throw new Throw(ind);
    while (true) {
      /* the tests pass for "c = tab.get(m)" as well!? */
      Tensor c = tab.get(m).extract(0, n);
      int j = ArgMin.of(withoutUnits(c)); // "entering variable"
      if (Sign.isNegative(c.Get(j))) {
        { // check if unbounded
          int argmax = ArgMax.of(withoutUnits(tab.get(Tensor.ALL, j).extract(0, m)));
          Sign.requirePositive(tab.Get(argmax, j)); // otherwise problem unbounded
        }
        int p = simplexPivot.get(tab, j, n); // "leaving variable"
        ind.set(RealScalar.of(j), p);
        tab.set(row -> row.divide(row.Get(j)), p); // normalize
        Tensor tab_p = tab.get(p);
        for (int i = 0; i < tab.length(); ++i)
          if (i != p)
            tab.set(row -> LenientAdd.of(row, tab_p.multiply(row.Get(j).negate())), i);
      } else
        /* "[...] if we have all c_j >= 0 in the tableau in the minimization
         * problem, then the current value of z is optimum."
         * Reference:
         * "Linear and Integer Programming made Easy"
         * by T.C. Hu, Andrew B. Kahng, 2016 */
        break;
    }
  }

  private Tensor getX() {
    Tensor x = Array.zeros(n);
    for (int index = 0; index < ind.length(); ++index)
      x.set(tab.Get(index, n), ind.Get(index).number().intValue());
    return x;
  }

  // helper function
  private static Tensor withoutUnits(Tensor vector) {
    return vector.map(Unprotect::withoutUnit);
  }
}
