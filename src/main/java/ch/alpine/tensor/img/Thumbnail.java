// code by jph
package ch.alpine.tensor.img;

import java.util.List;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.red.Max;

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
    List<Integer> list = Dimensions.of(tensor);
    Scalar scalar = Max.of(RationalScalar.of(size, list.get(0)), RationalScalar.of(size, list.get(1)));
    Tensor resize = ImageResize.of(tensor, scalar);
    List<Integer> crop = Dimensions.of(resize);
    int ofs0 = (crop.get(0) - size) / 2;
    int ofs1 = (crop.get(1) - size) / 2;
    return Tensor.of(resize.stream().skip(ofs0).limit(size) //
        .map(row -> Tensor.of(row.stream().skip(ofs1).limit(size))));
  }
}
