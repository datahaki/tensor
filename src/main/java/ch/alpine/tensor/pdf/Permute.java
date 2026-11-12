// code by jph
package ch.alpine.tensor.pdf;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.num.PermutationCycles;
import ch.alpine.tensor.num.RandomPermutation;

/** Permute is useful when the same permutation needs to be applied to several tensors,
 * for instance an xdata and ydata vector.
 * 
 * Permute can help test if a function output undergoes the same permutation as the input.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/Combinatorica/ref/Permute.html">Permute</a>
 * 
 * @see PermutationCycles */
public class Permute implements TensorUnaryOperator {
  /** @param sigma
   * @return */
  public static TensorUnaryOperator of(Tensor sigma) {
    return new Permute(Integers.requirePermutation(Primitives.toIntArray(sigma)));
  }

  public static TensorUnaryOperator of(int[] sigma) {
    return new Permute(sigma);
  }

  /** @param n
   * @param randomGenerator
   * @return */
  public static TensorUnaryOperator range(int n, RandomGenerator randomGenerator) {
    return of(RandomPermutation.of(n, randomGenerator));
  }

  /** @param n
   * @return */
  public static TensorUnaryOperator range(int n) {
    return range(n, ThreadLocalRandom.current());
  }

  // ---
  private final int[] sigma;

  /** @param sigma for instance {0, 1, 4, 2, 3} */
  private Permute(int[] sigma) {
    this.sigma = sigma;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Integers.requireEquals(sigma.length, tensor.length());
    return Tensor.of(Arrays.stream(sigma).mapToObj(tensor::get));
  }
}
