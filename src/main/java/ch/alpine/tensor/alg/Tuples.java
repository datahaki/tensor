// code by jph
package ch.alpine.tensor.alg;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.Permutations;

/** implementation consistent with Mathematica except in special case n == 0
 * Tensor-Lib.::Tuples[{a, b, c}, 0] == {}
 * Mathematica::Tuples[{a, b, c}, 0] == {{}}
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Tuples.html">Tuples</a>
 * 
 * @see Array
 * @see Permutations
 * @see Subsets */
public enum Tuples {
  ;
  /** Example:
   * Tuples.of(Tensors.vector(3, 4, 5), 2) gives
   * {{3, 3}, {3, 4}, {3, 5}, {4, 3}, {4, 4}, {4, 5}, {5, 3}, {5, 4}, {5, 5}}
   * 
   * @param tensor of length k
   * @param n non-negative
   * @return tensor with k ^ n elements
   * @throws Exception if n is negative */
  public static Tensor of(Tensor tensor, int n) {
    Tensor array = Array.of(list -> Tensor.of(list.stream().map(tensor::get)), Collections.nCopies(n, tensor.length()));
    return n <= 1 //
        ? array
        : Tensor.of(Flatten.stream(array, n - 1));
  }

  /** Example:
   * Tuples.of( {0, 1, 2}, {5, 6}, {8, 9} );
   * gives the following result of length == 3 * 2 * 2 == 12
   * {{0, 5, 8}, {0, 5, 9}, {0, 6, 8}, {0, 6, 9}, {1, 5, 8}, {1, 5, 9}, ...
   * {1, 6, 8}, {1, 6, 9}, {2, 5, 8}, {2, 5, 9}, {2, 6, 8}, {2, 6, 9}}
   * 
   * @param tensors
   * @return */
  public static Tensor of(Tensor... tensors) {
    List<Integer> dims = Stream.of(tensors).map(Tensor::length).toList();
    Tensor tensor = Tensors.reserve(dims.stream().reduce(Math::multiplyExact).orElseThrow());
    Array.forEach(list -> tensor.append(Tensor.of(IntStream.range(0, list.size()) //
        .mapToObj(i -> tensors[i].get(list.get(i))))), dims);
    return tensor;
  }
}
