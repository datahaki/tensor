// original code by Behzad Torkian
// adapted to binary averages by jph
package ch.ethz.idsc.tensor.opt;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/InterpolatingPolynomial.html">InterpolatingPolynomial</a> */
public class InterpolatingPolynomial implements Serializable {
  /** @param binaryAverage
   * @param knots vector
   * @return */
  public static InterpolatingPolynomial of(BinaryAverage binaryAverage, Tensor knots) {
    return new InterpolatingPolynomial(Objects.requireNonNull(binaryAverage), knots);
  }

  /** uses linear interpolation as binary average
   * 
   * @param knots vector
   * @return */
  public static InterpolatingPolynomial of(Tensor knots) {
    return new InterpolatingPolynomial(LinearBinaryAverage.INSTANCE, knots);
  }

  /***************************************************/
  private final BinaryAverage binaryAverage;
  private final Scalar[] knots;

  private InterpolatingPolynomial(BinaryAverage binaryAverage, Tensor knots) {
    this.binaryAverage = binaryAverage;
    this.knots = knots.stream().map(Scalar.class::cast).toArray(Scalar[]::new);
  }

  /** @param tensor
   * @return */
  public ScalarTensorFunction scalarTensorFunction(Tensor tensor) {
    if (knots.length == tensor.length())
      return new Neville(tensor);
    throw TensorRuntimeException.of(tensor);
  }

  /** @param vector
   * @return
   * @throws Exception if given vector is not a tensor of rank 1 */
  public ScalarUnaryOperator scalarUnaryOperator(Tensor vector) {
    return new TensorScalarFunctionCast(new Neville(VectorQ.requireLength(vector, knots.length)));
  }

  /** Neville's algorithm for polynomial interpolation by Eric Harold Neville
   * 
   * https://en.wikipedia.org/wiki/Neville%27s_algorithm */
  private class Neville implements ScalarTensorFunction {
    private final Tensor tensor;

    public Neville(Tensor tensor) {
      this.tensor = tensor;
    }

    @Override // from ScalarTensorFunction
    public Tensor apply(Scalar scalar) {
      int length = knots.length;
      Tensor[] d = tensor.stream().toArray(Tensor[]::new);
      for (int j = 1; j < length; ++j)
        for (int i = length - 1; j <= i; --i) {
          Scalar ratio = knots[i].subtract(scalar).divide(knots[i].subtract(knots[i - j]));
          d[i] = binaryAverage.split(d[i], d[i - 1], ratio);
        }
      return d[length - 1];
    }
  }
}
