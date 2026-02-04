// code by jph
package ch.alpine.tensor.itp;

import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.ext.Integers;

/** <p>For parameters in the unit interval [0, 1] the function gives values "in between" the
 * control points.
 * 
 * <p>BezierFunction can be used for extrapolation when using parameters outside the unit
 * interval.
 * 
 * <p>Reference:
 * <a href="https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm">De Casteljau's algorithm</a>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BezierFunction.html">BezierFunction</a>
 * 
 * @see BernsteinBasis */
// TODO TENSOR it's dubious that sequence is public to the outside...
public record BezierFunction(BinaryAverage binaryAverage, Tensor sequence) implements ScalarTensorFunction {
  /** De Casteljau's algorithm for the evaluation of Bezier curves, i.e. the splitting operation
   * is between two elements (not a weighted average between many). The binary split is provided
   * via the interface {@link BinaryAverage}.
   *
   * @param binaryAverage
   * @param sequence non-empty tensor
   * @return function over the interval [0, 1] (but allows for evaluation outside that interval)
   * @throws Exception if given sequence is empty or a scalar */
  public BezierFunction {
    Integers.requirePositive(sequence.length());
    Objects.requireNonNull(binaryAverage);
  }

  @Override // from ScalarTensorFunction
  public Tensor apply(Scalar scalar) {
    Tensor[] points = sequence.stream().toArray(Tensor[]::new);
    for (int i = points.length; 1 <= i; --i) {
      int count = -1;
      Tensor p = points[0];
      for (int index = 1; index < i; ++index)
        points[++count] = binaryAverage.split(p, p = points[index], scalar);
    }
    return points[0];
  }
}
