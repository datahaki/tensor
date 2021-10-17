// code by jph
package ch.alpine.tensor.alg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.spa.SparseArray;

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
    return tensor instanceof SparseArray //
        ? of(tensor, 1, 0)
        : Tensors.vector(i -> tensor.get(Tensor.ALL, i), length);
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
    if (sigma.length == 0)
      return tensor.copy();
    Dimensions dimensions = new Dimensions(tensor);
    List<Integer> dimensions_list = dimensions.list();
    if (tensor instanceof SparseArray) {
      Integers.requireLessEquals(sigma.length, dimensions_list.size());
      int[] sigma_ = IntStream.range(0, dimensions_list.size()).map(i -> i < sigma.length ? sigma[i] : i).toArray();
      return ((SparseArray) tensor) //
          .visit(new SparseEntryTranspose(sigma_, SparseArray.of(Size.of(dimensions_list).permute(sigma_))));
    }
    if (dimensions.isArray() && //
        dimensions_list.size() == sigma.length) {
      Size size = Size.of(dimensions_list);
      Scalar[] data = tensor.flatten(sigma.length - 1).map(Scalar.class::cast).toArray(Scalar[]::new);
      return ArrayReshape.of(size.stream(sigma).mapToObj(i -> data[i]), size.permute(sigma));
    }
    return Array.of( //
        list -> tensor.get(Arrays.stream(sigma).mapToObj(list::get).collect(Collectors.toList())), // extraction
        Size.of(dimensions_list.subList(0, sigma.length)).permute(sigma)); // dimensions
  }
}
