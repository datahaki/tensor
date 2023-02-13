// code by jph
package ch.alpine.tensor.lie;

import java.util.List;
import java.util.stream.Stream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Join;

/** implementation was designed to be consistent with Mathematica
 * 
 * in addition, the tensor library allows scalar input
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/KroneckerProduct.html">KroneckerProduct</a> */
public enum KroneckerProduct {
  ;
  /** @param a with array structure
   * @param b with array structure
   * @return */
  public static Tensor of(Tensor a, Tensor b) {
    Dimensions dim_a = new Dimensions(a);
    if (!dim_a.isArray())
      throw new Throw(a);
    Dimensions dim_b = new Dimensions(b);
    if (!dim_b.isArray())
      throw new Throw(b);
    List<Integer> list = Stream.concat( //
        dim_a.list().stream(), //
        dim_b.list().stream()).toList();
    Tensor product = TensorProduct.of(a, b);
    if (2 < list.size())
      if (list.size() % 2 == 0) {
        int half = list.size() / 2;
        for (int i = 0; i < half; ++i)
          product = Join.of(half - 1, product.stream().toList()); // general algorithm
      } else {
        if (dim_a.list().size() == 1 && dim_b.list().size() == 2) // special case: vector (X) matrix
          return Join.of(0, product.stream().toList());
        if (dim_a.list().size() == 2 && dim_b.list().size() == 1) // special case: matrix (X) vector
          return Tensor.of(product.stream().map(s -> Join.of(0, s.stream().toList())));
      }
    return product;
  }
}
