// code by jph
package ch.alpine.tensor.alg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ScalarQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.sca.Conjugate;

/** Transpose is consistent with Mathematica::Transpose
 * 
 * <p>Transpose does <b>not</b> conjugate the elements.
 * For that purpose, use {@link Conjugate}, or {@link ConjugateTranspose}.
 * 
 * <p>related to MATLAB::permute
 * 
 * <p>Careful:
 * Transpose[tensor] is the special case of Transpose[tensor, new int[] {1, 0}].
 * Transpose[tensor] is not the special case of Transpose[tensor, new int[] {}].
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
   * @param tensor with array structure up to sigma.length
   * @param sigma is a permutation with sigma.length less or equals to rank of tensor
   * @return
   * @throws Exception if sigma is not a permutation */
  public static Tensor of(Tensor tensor, int... sigma) {
    if (ScalarQ.of(tensor) && sigma.length == 0)
      return tensor;
    Dimensions dimensions = new Dimensions(tensor);
    if (dimensions.isArray()) {
      Size size = Size.of(dimensions.list());
      if (size.rank() == sigma.length) {
        Scalar[] data = tensor.flatten(sigma.length - 1).map(Scalar.class::cast).toArray(Scalar[]::new);
        return ArrayReshape.of(size.stream(sigma).mapToObj(i -> data[i]), size.permute(sigma));
      }
    }
    return nonArray(tensor, sigma);
  }

  /** generalization of {@link #of(Tensor, Integer...)} as function
   * only requires that tensor has array structure up to sigma.length
   * 
   * @param tensor with array structure up to sigma.length
   * @param sigma is a permutation
   * @return
   * @throws Exception if sigma is not a permutation */
  @PackageTestAccess
  static Tensor nonArray(Tensor tensor, int... sigma) {
    return Array.of(list -> tensor.get(reorder(list, sigma)), inverse(Dimensions.of(tensor), sigma));
  }

  /** @param list
   * @param sigma
   * @return */
  @PackageTestAccess
  static <T> List<T> reorder(List<T> list, int[] sigma) {
    return Arrays.stream(sigma).mapToObj(list::get).collect(Collectors.toList());
  }

  /** @param list
   * @param sigma permutation
   * @return */
  @PackageTestAccess
  static <T> List<T> inverse(List<T> list, int[] sigma) {
    return reorder(list, inverse(sigma)); //
  }

  /** Hint: function is a special case of permute with size==Range
   * 
   * <pre>
   * inverse(inverse(x)) == x
   * </pre>
   * 
   * @param sigma permutation
   * @return inverse({0,1,2,...}, sigma) */
  @PackageTestAccess
  static int[] inverse(int[] sigma) {
    Integers.requirePermutation(sigma);
    int[] dims = new int[sigma.length];
    for (int index = 0; index < sigma.length; ++index)
      dims[sigma[index]] = index;
    return dims;
  }
}
