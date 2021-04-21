// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Subsets;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.Objective;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.RegionType;

/** .
 * Algorithm visits all corners the runtime is prohibitive. Only for testing.
 * 
 * "Theorem 4.2 states that if there is a feasible solution, then there is
 * a basic feasible solution. Theorem 4.5 states that if there is an optimum
 * solution, then there is a basic optimum solution. So, to get an optimum
 * solution, we can choose m columns at a time from the matrix A and solve
 * the m simultaneous equations for the n sets of simultaneous m variables.
 * However, this approach requires that n over m equations be solved, which
 * is not practical unless n is small."
 * Reference: Linear and Integer Programming made Easy, 2016 */
/* package */ enum SimplexCorners {
  ;
  public static Tensor of(LinearProgram linearProgram) {
    LinearProgram lp_equality = linearProgram.equality();
    NavigableMap<Scalar, Tensor> navigableMap = of( //
        lp_equality.c, //
        lp_equality.A, //
        lp_equality.b, //
        lp_equality.regionType.equals(RegionType.NON_NEGATIVE));
    if (navigableMap.isEmpty())
      return Tensors.empty();
    Tensor sols = lp_equality.objective.equals(Objective.MIN) //
        ? navigableMap.firstEntry().getValue()
        : navigableMap.lastEntry().getValue();
    return Tensor.of(sols.stream() //
        .map(row -> row.extract(0, lp_equality.variables)) //
        .distinct());
  }

  /** @param c cost vector
   * @param A matrix
   * @param b rhs
   * @param isNonNegative whether to exclude solutions with any negative coordinates
   * @return all solutions */
  public static NavigableMap<Scalar, Tensor> of(Tensor c, Tensor A, Tensor b, boolean isNonNegative) {
    int n = c.length();
    int m = b.length();
    NavigableMap<Scalar, Tensor> map = new TreeMap<>();
    Tensor At = Transpose.of(A);
    for (Tensor subset : Subsets.of(Range.of(0, n), m)) {
      int[] cols = subset.stream() //
          .map(Scalar.class::cast) //
          .map(Scalar::number) //
          .mapToInt(Number::intValue) //
          .toArray();
      Tensor matrix = Transpose.of(Tensor.of(IntStream.of(cols).mapToObj(At::get)));
      try {
        Tensor X = LinearSolve.of(matrix, b);
        if (!isNonNegative || StaticHelper.isNonNegative(X)) {
          Tensor x = Array.zeros(n);
          IntStream.range(0, m).forEach(index -> x.set(X.Get(index), cols[index]));
          Tensor cost = Tensor.of(IntStream.of(cols).mapToObj(c::Get));
          map.merge((Scalar) cost.dot(X), Tensors.of(x), Join::of);
        }
      } catch (Exception exception) {
        // ---
      }
    }
    return map;
  }

  public static void show(NavigableMap<Scalar, Tensor> map) {
    for (Entry<Scalar, Tensor> entry : map.entrySet()) {
      System.out.println("--------------------------------------");
      System.out.println("cost=" + entry.getKey());
      System.out.println(Pretty.of(entry.getValue()));
    }
  }
}
