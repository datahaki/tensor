// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.Tensor;

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
    return ADJACENT_REDUCE.apply(tensor);
  }

  private static final AdjacentReduce ADJACENT_REDUCE = new AdjacentReduce() {
    @Override // from AdjacentReduce
    protected Tensor reduce(Tensor prev, Tensor next) {
      return next.subtract(prev);
    }
  };
}
