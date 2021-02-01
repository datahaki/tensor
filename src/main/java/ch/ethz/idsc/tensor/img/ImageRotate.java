// code by jph
package ch.ethz.idsc.tensor.img;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.ext.Integers;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ImageRotate.html">ImageRotate</a> */
public enum ImageRotate {
  ;
  /** @param tensor of rank at least 2
   * @return counter-clockwise rotation, i.e. +90 degree */
  public static Tensor of(Tensor tensor) {
    int rows = tensor.length();
    int cols = cols(tensor);
    int r = cols - 1;
    return Tensors.matrix((i, j) -> tensor.get(j, r - i), cols, rows);
  }

  /** @param tensor of rank at least 2
   * @return clockwise rotation, i.e. -90 degree */
  public static Tensor cw(Tensor tensor) {
    int rows = tensor.length();
    int cols = cols(tensor);
    int r = rows - 1;
    return Tensors.matrix((i, j) -> tensor.get(r - j, i), cols, rows);
  }

  /** @param tensor of rank at least 2
   * @return rotation by 180 degree */
  public static Tensor _180(Tensor tensor) {
    int rows = tensor.length();
    int cols = cols(tensor);
    int r = rows - 1;
    int s = cols - 1;
    return Tensors.matrix((i, j) -> tensor.get(r - i, s - j), rows, cols);
  }

  private static int cols(Tensor tensor) {
    return Integers.requirePositive(Unprotect.dimension1(tensor));
  }
}
