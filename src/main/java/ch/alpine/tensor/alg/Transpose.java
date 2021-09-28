// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ScalarQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.sca.Conjugate;

/** Transpose is consistent with Mathematica::Transpose
 * 
 * <p>Transpose does <b>not</b> conjugate the elements.
 * For that purpose, use {@link Conjugate}, or {@link ConjugateTranspose}.
 * 
 * <p>related to MATLAB::permute
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Transpose.html">Transpose</a> */
public enum Transpose {
  ;
  /** function is used to transpose a matrix
   * 
   * Remark:
   * if the input tensor is a matrix, function Transpose.of(tensor)
   * is identical albeit faster than Transpose.of(tensor, 1, 0).
   * 
   * <p>The function also operates on matrices with tensors as entries. Example:
   * <pre>
   * Transpose.of({{1, {2, 2}}, {{3}, 4}, {5, {6}}}) == {{1, {3}, 5}, {{2, 2}, 4, {6}}}
   * </pre>
   * 
   * <p>Special cases that are consistent with Mathematica:
   * <pre>
   * Transpose[{{}, {}}] == {}
   * Transpose[{}] throws an Exception
   * </pre>
   * 
   * @param tensor with rank at least 2
   * @return tensor with the two first dimensions transposed and the remaining dimensions left as-is
   * @throws Exception if input is a vector or scalar */
  public static Tensor of(Tensor tensor) {
    int length = Unprotect.dimension1(tensor);
    if (length == Scalar.LENGTH)
      throw TensorRuntimeException.of(tensor);
    return Tensors.vector(i -> tensor.get(Tensor.ALL, i), length);
  }

  /** transpose according to permutation sigma.
   * function conforms to Mathematica::Transpose except for scalar input:
   * <pre>
   * Tensor::Transpose[scalar, {}] == scalar
   * Mathematica::Transpose[scalar, {}] is undefined
   * </pre>
   * 
   * @param tensor with array structure
   * @param sigma is a permutation with sigma.length == rank of tensor
   * @return */
  public static Tensor of(Tensor tensor, int... sigma) {
    if (!ArrayQ.ofRank(tensor, sigma.length))
      throw TensorRuntimeException.of(tensor);
    if (ScalarQ.of(tensor))
      return tensor;
    Scalar[] data = tensor.flatten(-1).map(Scalar.class::cast).toArray(Scalar[]::new);
    Size size = Size.of(Dimensions.of(tensor));
    // indices 0,1,2,... are mapped to location of values in Flatten[tensor]
    return ArrayReshape.of(size.stream(sigma).mapToObj(i -> data[i]), size.permute(sigma).size());
  }

  /** generalization of {@link #of(Tensor, Integer...)} as function
   * only requires that tensor has array structure up to sigma.length
   * 
   * {@link #nonArray(Tensor, Integer...)} is typically a bit slower than
   * {@link #of(Tensor, Integer...)}
   * 
   * @param tensor with array structure up to sigma.length
   * @param sigma is a permutation
   * @return
   * @throws Exception */
  public static Tensor nonArray(Tensor tensor, int... sigma) {
    Tensor _sigma = Tensors.vectorInt(sigma);
    if (Sort.of(_sigma).equals(Range.of(0, sigma.length))) // assert that sigma is a permutation
      return Array.of(list -> tensor.get(StaticHelper.reorder(list, sigma)), //
          StaticHelper.inverse(Dimensions.of(tensor), _sigma));
    throw TensorRuntimeException.of(_sigma); // sigma does not encode a permutation
  }
}
