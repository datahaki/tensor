// code by jph
package ch.alpine.tensor.mat.pi;

import java.io.Serializable;
import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.ArrayReshape;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.LeftNullSpace;

// TODO TENSOR an empty LinearSubspace could also implement this interface
public class LinearSubspace implements Serializable {
  /** @param constraint as homogeneous equations
   * @param size
   * @return */
  public static LinearSubspace of(TensorUnaryOperator constraint, List<Integer> size) {
    size.forEach(Integers::requirePositive);
    int cumprod = size.stream().reduce(Math::multiplyExact).orElseThrow();
    TensorUnaryOperator reshape = v -> ArrayReshape.of(v, size);
    Tensor eqs = Tensor.of(IdentityMatrix.stream(cumprod) //
        .map(reshape) //
        .map(constraint) //
        .map(Flatten::of));
    Tensor nullSpace = LeftNullSpace.of(eqs);
    if (Tensors.nonEmpty(nullSpace))
      return new LinearSubspace(nullSpace, Tensor.of(nullSpace.stream().map(reshape)));
    throw new Throw(eqs);
  }

  /** @param constraint
   * @param size
   * @return */
  public static LinearSubspace of(TensorUnaryOperator constraint, int... size) {
    return of(constraint, Integers.asList(size));
  }

  // ---
  /** pinv has dimensions rows x cols with rows <= cols */
  private final Tensor pinv;
  private final Tensor basis;

  private LinearSubspace(Tensor nullSpace, Tensor basis) {
    this.pinv = PseudoInverse.of(Transpose.of(nullSpace));
    this.basis = basis;
  }

  public Tensor basis() {
    return basis;
  }

  public int dimensions() {
    return basis.length();
  }

  /** @param v
   * @return least squares projection */
  public Tensor projection(Tensor v) {
    return pinv.dot(Flatten.of(v)).dot(basis);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("LinearSubspace", basis);
  }
}
