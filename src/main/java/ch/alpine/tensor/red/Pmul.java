// code by jph
package ch.alpine.tensor.red;

import java.util.function.BinaryOperator;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** @see Entrywise */
public enum Pmul {
  ;
  private static final BinaryOperator<Tensor> BINARY_OPERATOR = Inner.with((s, t) -> t.multiply(s));

  /** point-wise multiplication of this instance with given tensor.
   * 
   * <p>{@link Dimensions} of <em>this</em> have to match the <em>onset</em> of dimensions of tensor.
   * Tensor::multiply is used on remaining entries in dimensions of tensors exceeding
   * dimensions of this.
   * 
   * <p>For instance,
   * <ul>
   * <li><code>Dimensions.of(this) = [4, 3]</code>, and
   * <li><code>Dimensions.of(tensor) = [4, 3, 5, 2]</code> is feasible.
   * </ul>
   * 
   * <p>pmul is consistent with Mathematica, for instance
   * <pre>
   * {a, b} {{1, 2, 3}, {4, 5, 6}} == {{a, 2 a, 3 a}, {4 b, 5 b, 6 b}}
   * Dimensions[Array[1 &, {2, 3}] Array[1 &, {2, 3, 4}]] == {2, 3, 4}
   * </pre>
   * 
   * <p>If this is a vector, then pmul is the faster equivalent to the dot product
   * with a diagonal matrix
   * <pre>
   * vector.pmul(tensor) == DiagonalMatrix[vector].tensor
   * </pre>
   * 
   * <p>If this is a scalar, then pmul is equivalent to scalar multiplication
   * <pre>
   * scalar.pmul(tensor) == tensor.multiply(scalar)
   * </pre>
   * 
   * @param tensor
   * @return this element-wise multiply input tensor. */
  public static Tensor of(Tensor a, Tensor b) {
    return BINARY_OPERATOR.apply(a, b);
  }

  public static TensorUnaryOperator operator(Tensor a) {
    return v -> of(a, v);
  }
}
