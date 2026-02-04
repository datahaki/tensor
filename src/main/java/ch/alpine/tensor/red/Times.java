// code by jph
package ch.alpine.tensor.red;

import java.util.Arrays;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.api.TensorBinaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** Hint: implementation covers the Hadamard product, i.e. entrywise/pointwise product
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Times.html">Times</a> */
public enum Times {
  ;
  private static final TensorBinaryOperator BINARY_OPERATOR = Inner.with((s, t) -> t.multiply(s));

  /** point-wise multiplication of two tensors.
   * 
   * <p>{@link Dimensions} of tensor a have to match the <em>onset</em> of dimensions of tensor b.
   * Tensor::multiply is used on remaining entries in dimensions of tensors exceeding
   * dimensions of tensor a.
   * 
   * <p>For instance,
   * <ul>
   * <li><code>Dimensions.of(a) = [4, 3]</code>, and
   * <li><code>Dimensions.of(b) = [4, 3, 5, 2]</code> is feasible.
   * </ul>
   * 
   * <p>Times is consistent with Mathematica, for instance
   * <pre>
   * {x, y} {{1, 2, 3}, {4, 5, 6}} == {{x, 2 x, 3 x}, {4 y, 5 y, 6 y}}
   * Dimensions[Array[1 &, {2, 3}] Array[1 &, {2, 3, 4}]] == {2, 3, 4}
   * </pre>
   * 
   * <p>If this is a vector, then Times is the faster equivalent to the dot product
   * with a diagonal matrix
   * <pre>
   * Times[vector, tensor] == DiagonalMatrix[vector].tensor
   * </pre>
   * 
   * <p>If tensor a is a scalar, then Times is equivalent to scalar multiplication
   * <pre>
   * Times[scalar, tensor] == tensor.multiply(scalar)
   * </pre>
   * 
   * @param a
   * @param b
   * @return see description above
   * @see Entrywise#mul() */
  public static Tensor of(Tensor a, Tensor b) {
    return BINARY_OPERATOR.apply(a, b);
  }

  /** @param a
   * @return */
  public static TensorUnaryOperator operator(Tensor a) {
    return v -> of(a, v);
  }

  /** function computes the product of a sequence of {@link Scalar}s
   * 
   * @param scalars
   * @return product of scalars, or {@link RealScalar#ONE} if no scalars are present */
  public static Scalar of(Scalar... scalars) {
    return Arrays.stream(scalars) //
        .reduce(Scalar::multiply) //
        .orElse(RealScalar.ONE);
  }
}
