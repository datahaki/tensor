// code by jph
package ch.alpine.tensor.img;

import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.red.Min;

/** implementation deviates from Mathematica in case input is grayscale image
 * 
 * inspired by
 * <a href="https://reference.wolfram.com/language/ref/Thumbnail.html">Thumbnail</a> */
public enum Thumbnail {
  ;
  /** @param tensor
   * @param size strictly positive
   * @return square image with dimensions size x size */
  public static Tensor of(Tensor tensor, int size) {
    Integers.requirePositive(size);
    // TODO implementation has trouble if tensor is smaller than size
    List<Integer> list = Dimensions.of(tensor);
    int min = Min.of(list.get(0), list.get(1));
    int ofs0 = (list.get(0) - min) / 2;
    int ofs1 = (list.get(1) - min) / 2;
    Tensor square = Tensor.of(tensor.stream().skip(ofs0).limit(min) //
        .map(row -> Tensor.of(row.stream().skip(ofs1).limit(min))));
    return ImageResize.of(square, size, size);
  }
}
