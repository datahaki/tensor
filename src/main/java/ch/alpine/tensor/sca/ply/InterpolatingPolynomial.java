// original code by Behzad Torkian
// adapted to binary averages by jph
package ch.alpine.tensor.sca.ply;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.itp.BinaryAverage;
import ch.alpine.tensor.itp.LinearBinaryAverage;

/** evaluates polynomial at given point without solving for the coefficients
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/InterpolatingPolynomial.html">InterpolatingPolynomial</a> */
public class InterpolatingPolynomial implements Serializable {
  /** @param binaryAverage
   * @param knots vector not necessarily ordered
   * @return
   * @throws Exception if knots is not a vector */
  public static InterpolatingPolynomial of(BinaryAverage binaryAverage, Tensor knots) {
    return new InterpolatingPolynomial(Objects.requireNonNull(binaryAverage), knots);
  }

  /** uses linear interpolation as binary average
   * 
   * @param knots vector not necessarily ordered
   * @return
   * @throws Exception if knots is not a vector */
  public static InterpolatingPolynomial of(Tensor knots) {
    return new InterpolatingPolynomial(LinearBinaryAverage.INSTANCE, knots);
  }

  // ---
  private final BinaryAverage binaryAverage;
  private final Scalar[] knots;

  private InterpolatingPolynomial(BinaryAverage binaryAverage, Tensor knots) {
    this.binaryAverage = binaryAverage;
    this.knots = ScalarArray.ofVector(knots);
  }

  /** @param tensor of values of polynomial evaluated at knots
   * @return
   * @throws Exception if length of tensor is different from number of knots */
  public ScalarTensorFunction scalarTensorFunction(Tensor tensor) {
    Integers.requireEquals(knots.length, tensor.length());
    return new Neville(tensor);
  }

  /** @param vector of values of polynomial evaluated at knots
   * @return
   * @throws Exception if given vector is not a tensor of rank 1
   * @throws Exception if length of vector is different from number of knots */
  public ScalarUnaryOperator scalarUnaryOperator(Tensor vector) {
    Neville neville = new Neville(VectorQ.requireLength(vector, knots.length));
    return scalar -> (Scalar) neville.apply(scalar);
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
