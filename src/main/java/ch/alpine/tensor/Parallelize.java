// code by jph
package ch.alpine.tensor;

import java.util.function.BiFunction;
import java.util.stream.IntStream;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Parallelize.html">Parallelize</a> */
public enum Parallelize {
  ;
  /** dot product between two given tensors
   * 
   * <p>parallel stream processing can accelerate the computation of the dot-product
   * however, for small vectors or matrices, serial computation is faster.
   * 
   * <p>A simple heuristic can determine whether parallel processing is beneficial:
   * For vector dot vector, use parallel if the vector length is greater than 200.
   * For square matrix dot vector, use parallel if the matrix size exceeds 20.
   * For dotting a pair of square matrices, use parallel if the matrix size exceeds 6.
   * 
   * @param lhs
   * @param rhs
   * @return lhs.dot(rhs) */
  public static Tensor dot(Tensor lhs, Tensor rhs) {
    int length = lhs.length();
    if (length == 0 || lhs.get(0) instanceof Scalar) { // quick hint whether lhs is a vector
      if (length != rhs.length())
        throw TensorRuntimeException.of(lhs, rhs);
      return IntStream.range(0, length).parallel() //
          .mapToObj(index -> rhs.get(index).multiply(lhs.Get(index))) //
          .reduce(Tensor::add) //
          .orElse(RealScalar.ZERO);
    }
    return Tensor.of(lhs.stream().parallel().map(entry -> entry.dot(rhs)));
  }

  /** parallel matrix construction, special case of
   * Mathematica::ParallelTable
   * Mathematica::ParallelArray
   * 
   * @param biFunction
   * @param rows
   * @param cols
   * @return (rows x cols)-matrix with (i, j)th-entry == bifunction.apply(i, j) */
  public static Tensor matrix(BiFunction<Integer, Integer, ? extends Tensor> biFunction, int rows, int cols) {
    return Tensor.of(IntStream.range(0, rows).parallel().mapToObj( //
        i -> Tensor.of(IntStream.range(0, cols).mapToObj(j -> biFunction.apply(i, j)))));
  }
}
