// code by jph
package ch.alpine.tensor.sca.ply;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.FoldList;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.lie.TensorProduct;
import ch.alpine.tensor.red.Times;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/CoefficientList.html">CoefficientList</a> */
/* package */ enum CoefficientList {
  ;
  /** The following equality holds up to permutation and numerical precision:
   * <pre>
   * Roots.of(CoefficientList.of(roots)) == roots
   * </pre>
   * 
   * @param roots vector of length 1, 2, or 3
   * @return coefficients of polynomial */
  public static Tensor of(Tensor roots) {
    Scalar first = roots.Get(0);
    Tensor box = roots.stream() //
        .map(CoefficientList::linear) //
        .reduce(TensorProduct::of) //
        .orElseThrow();
    Tensor coeffs = Reverse.of(FoldList.of(Times::of, roots.map(Scalar::zero))).append(first.one().zero());
    Array.stream(Dimensions.of(box)) //
        .forEach(list -> coeffs.set(box.get(list)::add, list.stream().mapToInt(i -> i).sum()));
    return coeffs;
  }

  private static Tensor linear(Tensor scalar) {
    return Tensors.of(scalar.negate(), RealScalar.ONE);
  }
}
