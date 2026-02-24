// code by jph
package ch.alpine.tensor.mat.pi;

import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ArrayReshape;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.LeftNullSpace;

/** operator that maps a vector of length == LS.dimensions()
 * to a tensor in the linear subspace */
public interface LinearSubspace extends TensorUnaryOperator {
  /** @param constraint as homogeneous equations
   * @param size
   * @return */
  static LinearSubspace of(TensorUnaryOperator constraint, List<Integer> size) {
    size.forEach(Integers::requirePositive);
    int cumprod = size.stream().reduce(Math::multiplyExact).orElseThrow();
    TensorUnaryOperator reshape = v -> ArrayReshape.of(v, size);
    Tensor eqs = Tensor.of(IdentityMatrix.stream(cumprod) //
        .map(reshape) //
        .map(constraint) //
        .map(Flatten::of));
    Tensor nullSpace = LeftNullSpace.of(eqs);
    return Tensors.isEmpty(nullSpace) //
        ? new LinearSubspaceNull(size)
        : new LinearSubspaceImpl(nullSpace, reshape.slash(nullSpace));
  }

  /** @param constraint
   * @param size
   * @return */
  static LinearSubspace of(TensorUnaryOperator constraint, int... size) {
    return of(constraint, Integers.asList(size));
  }

  /** @return dimensions of the subspace, equivalent to basis().length() */
  default int dimensions() {
    return basis().length();
  }

  /** @return vector of basis elements, i.e. the result is a tensor
   * with Dimensions: { dimensions() , size() } */
  Tensor basis();

  /** @param v
   * @return least squares projection of v to the subvector space
   * the result satisfies the constraint */
  Tensor projection(Tensor v);
}
