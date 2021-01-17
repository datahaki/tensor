// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Subsets;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.mat.LinearSolve;

/** algorithm visits all corners
 * the runtime is prohibitive
 * only for testing */
/* package */ enum SimplexCorners {
  ;
  /** @param c
   * @param A
   * @param b
   * @return all non-negative solutions */
  public static NavigableMap<Scalar, Tensor> minEquals(Tensor c, Tensor A, Tensor b, boolean isNonNegative) {
    List<Integer> list = Dimensions.of(A);
    int m = b.length();
    int n = c.length();
    if (!list.equals(Arrays.asList(m, n)))
      throw TensorRuntimeException.of(c, A, b);
    NavigableMap<Scalar, Tensor> map = new TreeMap<>();
    Tensor At = Transpose.of(A);
    for (Tensor subset : Subsets.of(Range.of(0, n), m)) {
      int[] cols = subset.stream() //
          .map(Scalar.class::cast) //
          .map(Scalar::number) //
          .mapToInt(Number::intValue) //
          .toArray();
      Tensor matrix = Tensors.reserve(m);
      Tensor cost = Tensors.reserve(m);
      for (int col : cols) {
        matrix.append(At.get(col));
        cost.append(c.get(col));
      }
      try {
        Tensor X = LinearSolve.of(Transpose.of(matrix), b);
        if (!isNonNegative || LinearProgramming.isNonNegative(X)) {
          Scalar key = (Scalar) cost.dot(X);
          if (!map.containsKey(key))
            map.put(key, Tensors.empty());
          Tensor x = Array.zeros(n);
          int index = 0;
          for (int col : cols) {
            x.set(X.get(index), col);
            ++index;
          }
          map.get(key).append(x);
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
