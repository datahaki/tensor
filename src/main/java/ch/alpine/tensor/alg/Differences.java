// code by jph
package ch.alpine.tensor.alg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.alpine.tensor.ScalarQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;

/** simplified version of Mathematica::Differences
 * 
 * <p>The implementation is consistent for the special cases
 * <pre>
 * Differences[ {} ] == {}
 * Differences[ {single} ] == {}
 * </pre>
 * 
 * <p>The implementation does not operate on scalar input
 * <pre>
 * Mathematica::Differences[ 1 ] not defined
 * Tensor-Lib.::Differences[ 1 ] throws an Exception
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Differences.html">Differences</a> */
public enum Differences {
  ;
  /** <pre>
   * Differences[{a, b, c, d, e}] == {b - a, c - b, d - c, e - d}
   * </pre>
   * 
   * @param tensor of rank at least 1
   * @return the successive differences of elements in tensor
   * @throws Exception if given tensor is a scalar */
  public static Tensor of(Tensor tensor) {
    int length = tensor.length();
    if (length <= 1) {
      ScalarQ.thenThrow(tensor);
      return Tensors.empty();
    }
    List<Tensor> list = new ArrayList<>(length - 1);
    Iterator<Tensor> iterator = tensor.iterator();
    Tensor prev = iterator.next();
    while (iterator.hasNext()) {
      Tensor next = iterator.next();
      list.add(next.subtract(prev));
      prev = next;
    }
    return Unprotect.using(list);
  }
}
