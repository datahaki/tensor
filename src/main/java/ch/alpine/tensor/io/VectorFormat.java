// code by jph
package ch.alpine.tensor.io;

import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** VectorFormat is a file format for import and export of tensors
 * 
 * VectorFormat is specific to the tensor library
 * and does not have an equivalent in Mathematica */
public enum VectorFormat {
  ;
  /** @param tensor of rank at least 1
   * @return stream of strings where each string encodes a {@link Tensor}
   * @throws Exception if given tensor is a {@link Scalar} */
  public static Stream<String> of(Tensor tensor) {
    return tensor.stream().map(Tensor::toString);
  }

  /** @param stream of strings where each string encodes a {@link Scalar}
   * @return vector of scalars parsed from strings in given stream */
  public static Tensor parse(Stream<String> stream) {
    return Tensor.of(stream.map(Tensors::fromString));
  }
}
