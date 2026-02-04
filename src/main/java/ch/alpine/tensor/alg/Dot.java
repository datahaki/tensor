// code by jph
package ch.alpine.tensor.alg;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.PackageTestAccess;

/** Implementation is based on the dynamic programming solution in the reference
 * "Algorithmik", Section 4.2 "Matrizen-Kettenmultiplikation"
 * by Uwe Schoening, 2001
 * 
 * <p>The implementation was extended to work for tensors of arbitrary rank.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Dot.html">Dot</a> */
public class Dot {
  /** @param tensors non-empty array
   * @return (...((tensors[0].tensors[1]).tensors[2]) ... ).tensors[n-1]
   * with the guarantee that minimal number of multiplications is used
   * @throws Exception if tensors is empty array */
  public static Tensor of(Tensor... tensors) {
    if (tensors.length == 1)
      return tensors[0].copy();
    return new Dot(tensors).product();
  }

  // ---
  private final Tensor product;
  private final Entry[][] entry;

  @PackageTestAccess
  Dot(Tensor... tensors) {
    List<Node> list = Arrays.stream(tensors) //
        .map(tensor -> new Node(tensor, Dimensions.of(tensor))) //
        .collect(Collectors.toList());
    for (int index = 0; index < list.size() && 1 < list.size();) {
      Node node = list.get(index);
      if (node.dimensions.size() == 1) // handle rank 1 tensors
        if (index == 0)
          list.set(0, node.dot(list.remove(1)));
        else {
          list.remove(index);
          --index; // can decrement index since 0 < index
          list.set(index, list.get(index).dot(node));
        }
      else
        ++index;
    }
    int n = list.size();
    entry = new Entry[n][];
    for (int i = 0; i < n; ++i) {
      entry[i] = new Entry[n - i];
      entry[i][0] = new Entry(list.get(i).dimensions, 0, -1);
    }
    for (int l = 1; l < n; ++l)
      for (int i = 0; i < n - l; ++i)
        for (int k = 0; k < l; ++k) {
          int kp1 = k + 1;
          int cmp = entry[i][k].product(entry[i + kp1][l - kp1]);
          if (Objects.isNull(entry[i][l]) || cmp < entry[i][l].m)
            entry[i][l] = new Entry(combine(entry[i][k].dimensions, entry[i + kp1][l - kp1].dimensions), cmp, k + i);
        }
    product = recur(list, 0, n - 1);
  }

  private Tensor recur(List<Node> list, int i, int j) {
    if (i == j)
      return list.get(i).tensor;
    int k = entry[i][j - i].k;
    return recur(list, i, k).dot(recur(list, k + 1, j));
  }

  public Tensor product() {
    return product;
  }

  /** @return count of multiplications excluding dots with vectors */
  public int multiplications() {
    return entry[0][entry.length - 1].m;
  }

  /** @return dimensions of result of dot product */
  public List<Integer> dimensions() {
    return entry[0][entry.length - 1].dimensions;
  }

  /** @param dimensions1 {..., tail}
   * @param dimensions2 {head, ...}
   * @return
   * @throws Exception if tail and head are not equal */
  public static List<Integer> combine(List<Integer> dimensions1, List<Integer> dimensions2) {
    Integers.requireEquals(dimensions1.getLast(), dimensions2.getFirst());
    return Stream.concat( //
        dimensions1.stream().limit(dimensions1.size() - 1), //
        dimensions2.stream().skip(1)).collect(Collectors.toList());
  }

  private record Node(Tensor tensor, List<Integer> dimensions) {
    public Node dot(Node node) {
      return new Node(tensor.dot(node.tensor), combine(dimensions, node.dimensions));
    }
  }

  private record Entry(List<Integer> dimensions, int m, int k) {
    public int product(Entry entry) {
      return m + entry.m + Stream.concat(dimensions.stream(), entry.dimensions.stream().skip(1)) //
          .reduce(Math::multiplyExact).orElseThrow();
    }
  }
}
