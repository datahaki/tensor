// code by jph
package ch.alpine.tensor.lie;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.red.Nest;

/** Careful:
 * the implementation of the tensor library is not consistent with Mathematica
 * 
 * See the test scope (KroneckerProductTest) for an implementation that attempts
 * to be consistent but was dismissed in favor of simplicity, instead.
 * 
 * Naturally, the implementations are consistent when both input parameters are
 * matrices.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/KroneckerProduct.html">KroneckerProduct</a> */
public enum KroneckerProduct {
  ;
  /** @param a with array structure
   * @param b with array structure
   * @return
   * @throws Exception if a or b are not arrays, or their tensor product has odd rank */
  public static Tensor of(Tensor a, Tensor b) {
    Dimensions dim_a = new Dimensions(a);
    Dimensions dim_b = new Dimensions(b);
    int n = dim_a.rank() + dim_b.rank();
    int half = n / 2;
    if (dim_a.isArray() && //
        dim_b.isArray() && //
        n % 2 == 0)
      return Nest.of(tensor -> Join.of(half - 1, tensor.stream().toList()), TensorProduct.of(a, b), half);
    throw new Throw(a, b);
  }
}
