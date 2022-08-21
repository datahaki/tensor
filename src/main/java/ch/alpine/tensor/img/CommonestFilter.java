// code by jph
package ch.alpine.tensor.img;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.red.Commonest;

/** Careful:
 * in case of a tie, Mathematica picks a random "winner" whereas the tensor lib picks
 * the element that was encountered "first".
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/CommonestFilter.html">CommonestFilter</a> */
public enum CommonestFilter {
  ;
  /** @param tensor of arbitrary rank but not a scalar
   * @param radius non-negative integer
   * @return filtered version of input tensor with same {@link Dimensions};
   * for radius == 0 the function returns a copy of the given tensor
   * @throws Exception if given tensor is a scalar
   * @throws Exception if given radius is negative */
  public static Tensor of(Tensor tensor, int radius) {
    return ImageFilter.of(tensor, radius, CommonestFilter::flatten);
  }

  // helper function
  private static Tensor flatten(Tensor tensor) {
    return Commonest.of(Flatten.of(tensor)).get(0);
  }
}
