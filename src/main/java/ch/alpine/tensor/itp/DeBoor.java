// code by jph
// adapted from https://en.wikipedia.org/wiki/De_Boor%27s_algorithm
package ch.alpine.tensor.itp;

import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.ext.Integers;

/** DeBoor denotes the function that is defined by control points over a sequence of knots.
 * 
 * @param binaryAverage
 * @param degree
 * @param knots vector of length degree * 2
 * @param control points of length degree + 1 */
public class DeBoor implements ScalarTensorFunction {
  /** @param binaryAverage non-null
   * @param knots vector of length degree * 2
   * @param control points of length degree + 1
   * @return
   * @throws Exception if given knots is not a vector, or degree cannot be established */
  public static DeBoor of(BinaryAverage binaryAverage, Tensor knots, Tensor control) {
    int length = knots.length();
    int degree = length / 2;
    Integers.requireEquals(control.length(), degree + 1);
    if (Integers.isEven(length))
      return new DeBoor(Objects.requireNonNull(binaryAverage), degree, VectorQ.require(knots), control);
    throw new Throw(knots, control);
  }

  private final BinaryAverage binaryAverage;
  private final int degree;
  private final Tensor knots;
  private final Tensor control;

  public DeBoor(BinaryAverage binaryAverage, int degree, Tensor knots, Tensor control) {
    this.binaryAverage = binaryAverage;
    this.degree = degree;
    this.knots = knots;
    this.control = control;
  }

  public int degree() {
    return degree;
  }

  public Tensor knots() {
    return knots;
  }

  public Tensor control() {
    return control;
  }

  @Override
  public Tensor apply(Scalar x) {
    Tensor[] d = control.stream().toArray(Tensor[]::new); // d is modified over the course of the algorithm
    for (int r = 1; r < degree + 1; ++r)
      for (int j = degree; j >= r; --j) {
        Scalar kj1 = knots.Get(j - 1); // knots max index = degree - 1
        Scalar den = knots.Get(j + degree - r).subtract(kj1); // knots max index = degree + degree - 1
        d[j] = Scalars.isZero(den) //
            ? d[j - 1]
            : binaryAverage.split(d[j - 1], d[j], x.subtract(kj1).divide(den)); // control max index = degree - 1
      }
    return d[degree]; // control max index = degree
  }
}
