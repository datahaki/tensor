// code by jph
package ch.alpine.tensor.alg;

import java.util.ArrayList;
import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Lists;

/** In order to set a matrix/array as a block within a larger matrix/array one can
 * simply use the following pattern:
 * <pre>
 * Tensor tensor = Array.zeros(8, 8);
 * tensor.block(List.of(2, 2), List.of(4, 4)).set(HilbertMatrix.of(4), Tensor.ALL, Tensor.ALL);
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ArrayPad.html">ArrayPad</a> */
public enum ArrayPad {
  ;
  /** @param tensor
   * @param ante number of zeros to be prepended for each dimension of tensor
   * @param post number of zeros to be appended for each dimension of tensor
   * @return tensor padded with zeros */
  public static Tensor of(Tensor tensor, List<Integer> ante, List<Integer> post) {
    List<Integer> dimensions = Dimensions.of(tensor);
    for (int index = 0; index < dimensions.size(); ++index)
      dimensions.set(index, ante.get(index) + dimensions.get(index) + post.get(index));
    return of(tensor, dimensions, ante, post);
  }

  // helper function
  private static Tensor of(Tensor tensor, List<Integer> dimensions, List<Integer> ante, List<Integer> post) {
    int rank = dimensions.size();
    List<Integer> copy = new ArrayList<>(dimensions);
    copy.set(0, ante.get(0));
    Tensor a = Array.zeros(copy);
    copy.set(0, post.get(0));
    Tensor b = Array.zeros(copy);
    if (1 == rank)
      return Join.of(0, a, tensor, b);
    List<Integer> _copy = Lists.rest(copy);
    List<Integer> _ante = Lists.rest(ante);
    List<Integer> _post = Lists.rest(post);
    return Join.of(0, a, Tensor.of(tensor.stream().map(entry -> of(entry, _copy, _ante, _post))), b);
  }
}
