// code by jph
package ch.ethz.idsc.tensor.alg;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;

/** Reference:
 * "Algorithmik", Section 4.2 "Matrizen-Kettenmultiplikation"
 * by Uwe Schoening, 2001
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Dot.html">Dot</a> */
public enum Dot {
  ;
  /** @param tensor
   * @param v's
   * @return ( ... ( ( m . v[0] ) . v[1] ). ... ) . v[end-1] */
  public static Tensor of(Tensor tensor, Tensor... v) {
    if (v.length == 0)
      return tensor.copy();
    for (int index = 0; index < v.length; ++index)
      tensor = tensor.dot(v[index]);
    return tensor;
  }

  /** @param tensors
   * @return result as {@link #of(Tensor, Tensor...)} but with the guarantee that
   * minimal number of multiplications is used */
  public static Tensor minimal(Tensor... tensors) {
    if (tensors.length == 1)
      return tensors[0].copy();
    List<Node> list = new ArrayList<>();
    for (Tensor tensor : tensors)
      list.add(new Node(tensor, Dimensions.of(tensor)));
    // handle rank 1 tensors
    int index = 0;
    while (index < list.size()) {
      Node node = list.get(index);
      int rank = node.dimensions.size();
      if (rank == 1) {
        if (index == 0) {
          if (index + 1 < list.size()) {
            Node next = list.get(index + 1);
            Tensor nrep = node.tensor.dot(next.tensor);
            list.set(index, new Node(nrep, Dimensions.of(nrep)));
            list.remove(index + 1);
          } else
            break;
        } else {
          Node prev = list.get(index - 1);
          Tensor nrep = prev.tensor.dot(node.tensor);
          list.set(index - 1, new Node(nrep, Dimensions.of(nrep)));
          list.remove(index);
          --index;
        }
      } else
        ++index;
    }
    int n = list.size();
    int[][] m = new int[n][n];
    int[][] K = new int[n][n];
    for (int l = 1; l <= n - 1; ++l) {
      for (int i = 0; i < n - l; ++i) {
        int j = i + l;
        m[i][j] = Integer.MAX_VALUE;
        for (int k = i; k <= j - 1; ++k) {
          int pi = list.get(i).dimensions.get(0);
          int pk = list.get(k).dimensions.get(1);
          int pj = list.get(j).dimensions.get(1);
          int cmp = m[i][k] + m[k + 1][j] + pi * pk * pj;
          if (cmp < m[i][j]) {
            m[i][j] = cmp;
            K[i][j] = k;
          }
        }
      }
    }
    return recur(list, K, 0, n - 1);
  }

  private static Tensor recur(List<Node> list, int[][] K, int i, int j) {
    if (i == j)
      return list.get(i).tensor;
    int m = K[i][j];
    return recur(list, K, i, m).dot(recur(list, K, m + 1, j));
  }

  private static class Node {
    private final Tensor tensor;
    private final List<Integer> dimensions;

    public Node(Tensor tensor, List<Integer> dimensions) {
      this.tensor = tensor;
      this.dimensions = dimensions;
    }
  }
}
