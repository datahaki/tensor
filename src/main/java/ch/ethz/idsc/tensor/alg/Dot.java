// code by jph
package ch.ethz.idsc.tensor.alg;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Tensor;

/** Reference:
 * "Algorithmik", Section 4.2 "Matrizen-Kettenmultiplikation"
 * by Uwe Schoening, 2001
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Dot.html">Dot</a> */
public class Dot {
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
    return new Dot(tensors).product();
  }

  /***************************************************/
  private final Tensor product;
  private final Entry[][] entry;

  /* package */ Dot(Tensor... tensors) {
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
    entry = new Entry[n][n];
    for (int i = 0; i < n; ++i)
      entry[i][i] = new Entry(list.get(i).dimensions, 0, -1);
    for (int l = 1; l <= n - 1; ++l)
      for (int i = 0; i < n - l; ++i) {
        int j = i + l;
        for (int k = i; k <= j - 1; ++k) {
          int cmp = entry[i][k].product(entry[k + 1][j]);
          if (Objects.isNull(entry[i][j]) || cmp < entry[i][j].m)
            entry[i][j] = new Entry(combine(entry[i][k], entry[k + 1][j]), cmp, k);
        }
      }
    product = recur(list, 0, n - 1);
  }

  private Tensor recur(List<Node> list, int i, int j) {
    if (i == j)
      return list.get(i).tensor;
    int k = entry[i][j].k;
    return recur(list, i, k).dot(recur(list, k + 1, j));
  }

  public Tensor product() {
    return product;
  }

  public int multiplications() {
    return entry[0][entry.length - 1].m;
  }

  public List<Integer> dimensions() {
    return entry[0][entry.length - 1].dimensions;
  }

  /***************************************************/
  private static class Node {
    private final Tensor tensor;
    private final List<Integer> dimensions;

    public Node(Tensor tensor, List<Integer> dimensions) {
      this.tensor = tensor;
      this.dimensions = dimensions;
    }
  }

  /* package */ static List<Integer> combine(Entry entry1, Entry entry2) {
    List<Integer> list = new ArrayList<>();
    list.addAll(entry1.dimensions.subList(0, entry1.dimensions.size() - 1));
    list.addAll(entry2.dimensions.subList(1, entry2.dimensions.size()));
    return list;
  }

  /***************************************************/
  private static class Entry {
    private final List<Integer> dimensions;
    private final int m;
    private final int k;

    public Entry(List<Integer> dimensions, int m, int k) {
      this.dimensions = dimensions;
      this.m = m;
      this.k = k;
    }

    public int product(Entry entry) {
      return m + entry.m + Stream.concat(dimensions.stream(), entry.dimensions.stream().skip(1)) //
          .reduce(Math::multiplyExact).get();
    }
  }
}
