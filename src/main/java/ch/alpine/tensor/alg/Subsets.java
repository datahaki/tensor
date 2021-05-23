// code by jph
package ch.alpine.tensor.alg;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import ch.alpine.tensor.ScalarQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.num.Binomial;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Subsets.html">Subsets</a>
 * 
 * @see Tuples
 * @see Binomial */
public class Subsets {
  /** @param tensor of length n, not a scalar
   * @param k non-negative
   * @return tensor of length n choose k
   * @throws Exception if k is negative */
  public static Tensor of(Tensor tensor, int k) {
    ScalarQ.thenThrow(tensor);
    List<Tensor> list = new LinkedList<>();
    new Subsets(list::add).init(tensor, k);
    return Unprotect.using(list);
  }

  /** @param tensor of length n, not a scalar
   * @param k non-negative
   * @return stream of tensors of length n choose k
   * @throws Exception if k is negative */
  public static Stream<Tensor> stream(Tensor tensor, int k) {
    ScalarQ.thenThrow(tensor);
    Builder<Tensor> builder = Stream.builder();
    new Subsets(builder).init(tensor, k);
    return builder.build();
  }

  /***************************************************/
  private final Consumer<Tensor> consumer;

  private Subsets(Consumer<Tensor> consumer) {
    this.consumer = consumer;
  }

  private void init(Tensor tensor, int k) {
    recur(Tensors.empty(), tensor, k);
  }

  private void recur(Tensor ante, Tensor tensor, int k) {
    if (k == 0)
      consumer.accept(ante);
    else {
      --k;
      int limit = tensor.length() - k;
      for (int index = 0; index < limit;)
        recur(Append.of(ante, tensor.get(index)), Tensor.of(tensor.stream().skip(++index)), k);
    }
  }
}
