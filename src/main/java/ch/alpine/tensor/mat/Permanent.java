// code by jph
package ch.alpine.tensor.mat;

import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.lie.Permutations;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Permanent.html">Permanent</a> */
public enum Permanent {
  ;
  /** @param matrix square
   * @return permanent of given matrix */
  public static Scalar of(Tensor matrix) {
    int n = Integers.requireEquals(matrix.length(), Unprotect.dimension1(matrix));
    return Permutations.stream(Range.of(0, n)) //
        .map(Primitives::toIntArray) //
        .map(sigma -> IntStream.range(0, n) //
            .mapToObj(i -> matrix.Get(i, sigma[i])) //
            .reduce(Scalar::multiply).orElseThrow())
        .reduce(Scalar::add).orElseThrow();
  }
}
