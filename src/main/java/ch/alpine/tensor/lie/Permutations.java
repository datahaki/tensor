// code by jph
package ch.alpine.tensor.lie;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ScalarQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.alg.Tuples;

/** implementation is consistent with Mathematica
 * 
 * <p>Example:
 * <pre>
 * Permutations.of({1, 2, 1}) == {{1, 2, 1}, {1, 1, 2}, {2, 1, 1}}
 * </pre>
 * 
 * <p>For Scalar input the function is not defined
 * Permutations.of(5) // throws an exception
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Permutations.html">Permutations</a>
 * 
 * @see Signature
 * @see Sort
 * @see Tuples */
public class Permutations {
  /** @param tensor that is not a {@link Scalar}
   * @return
   * @throws Exception if given tensor is a scalar */
  public static Tensor of(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    List<Tensor> list = new LinkedList<>();
    new Permutations(list::add).init(tensor);
    return Unprotect.using(list);
  }

  /** @param tensor that is not a {@link Scalar}
   * @return
   * @throws Exception if given tensor is a scalar */
  public static Stream<Tensor> stream(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    Builder<Tensor> builder = Stream.builder();
    new Permutations(builder).init(tensor);
    return builder.build();
  }

  // ---
  private final Consumer<Tensor> consumer;

  private Permutations(Consumer<Tensor> consumer) {
    this.consumer = consumer;
  }

  private void init(Tensor tensor) {
    recur(Tensors.empty(), tensor);
  }

  private void recur(Tensor ante, Tensor post) {
    int length = post.length();
    if (length == 0)
      consumer.accept(ante);
    else {
      Set<Tensor> set = new HashSet<>();
      for (int index = 0; index < length; ++index) {
        Tensor key = Tensor.of(Stream.concat( //
            post.stream().limit(index), //
            post.stream().skip(index + 1)));
        if (set.add(Sort.of(key))) // TODO the use of sort here is unfortunate
          recur(Append.of(ante, post.get(index)), key);
      }
    }
  }
}
