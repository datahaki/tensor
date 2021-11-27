// code by jph
package ch.alpine.tensor.itp;

import java.io.IOException;
import java.util.stream.IntStream;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.lie.Permutations;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.VandermondeMatrix;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.QuantityTensor;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class InterpolatingPolynomialTest extends TestCase {
  private static final ScalarUnaryOperator MINUS_ONE = RealScalar.ONE.negate()::add;

  public void testScaleInvariant() {
    Tensor suppor = Tensors.vector(2, 2.3, 4);
    Tensor values = Tensors.vector(6, -7, 20);
    ScalarUnaryOperator suo1 = InterpolatingPolynomial.of(suppor).scalarUnaryOperator(values);
    ScalarUnaryOperator suo2 = InterpolatingPolynomial.of(suppor.multiply(RealScalar.of(3)).map(MINUS_ONE)).scalarUnaryOperator(values);
    Distribution distribution = UniformDistribution.of(2, 4);
    Tensor domain = RandomVariate.of(distribution, 20);
    Tolerance.CHOP.requireClose( //
        domain.map(suo1), //
        domain.multiply(RealScalar.of(3)).map(MINUS_ONE).map(suo2));
  }

  public void testPermutationInvariant() {
    Tensor suppor = Tensors.vector(2, 2.3, 4);
    Tensor values = Tensors.vector(6, -7, 20);
    ScalarUnaryOperator suo1 = InterpolatingPolynomial.of(suppor).scalarUnaryOperator(values);
    for (Tensor perm : Permutations.of(Range.of(0, 3))) {
      int[] index = Primitives.toIntArray(perm);
      ScalarUnaryOperator suo2 = InterpolatingPolynomial.of(Tensor.of(IntStream.of(index).mapToObj(suppor::get)))
          .scalarUnaryOperator(Tensor.of(IntStream.of(index).mapToObj(values::get)));
      Distribution distribution = UniformDistribution.of(2, 4);
      Tensor domain = RandomVariate.of(distribution, 20);
      Tolerance.CHOP.requireClose(domain.map(suo1), domain.map(suo2));
    }
  }

  public void testScaleInvariantMatrix() {
    Tensor suppor = Tensors.vector(2, 2.3, 4);
    Tensor values = Tensors.fromString("{{2,-3}, {-7, 5}, {5, 9}}");
    ScalarTensorFunction suo1 = InterpolatingPolynomial.of(suppor).scalarTensorFunction(values);
    ScalarTensorFunction suo2 = InterpolatingPolynomial.of(suppor.multiply(RealScalar.of(3)).map(MINUS_ONE)).scalarTensorFunction(values);
    Distribution distribution = UniformDistribution.of(2, 4);
    Tensor domain = RandomVariate.of(distribution, 20);
    Tolerance.CHOP.requireClose( //
        domain.map(suo1), //
        domain.multiply(RealScalar.of(3)).map(MINUS_ONE).map(suo2));
    AssertFail.of(() -> InterpolatingPolynomial.of(suppor).scalarTensorFunction(Tensors.vector(2, 3, 4, 5)));
  }

  public void testQuantity() {
    Tensor suppor = Tensors.fromString("{2[m], 2.3[m], 4[m]}");
    Tensor values = Tensors.fromString("{6[s], -7[s], 20[s]}");
    ScalarUnaryOperator suo1 = InterpolatingPolynomial.of(suppor).scalarUnaryOperator(values);
    Distribution distribution = UniformDistribution.of(2, 4);
    Tensor domain = QuantityTensor.of(RandomVariate.of(distribution, 20), "m");
    domain.map(suo1).map(QuantityMagnitude.singleton("s"));
  }

  public void testGaussScalar() {
    int prime = 7211;
    Tensor suppor = Tensors.of(GaussScalar.of(53, prime), GaussScalar.of(519, prime), GaussScalar.of(6322, prime));
    Tensor values = Tensors.of(GaussScalar.of(2233, prime), GaussScalar.of(719, prime), GaussScalar.of(32, prime));
    ScalarUnaryOperator suo1 = InterpolatingPolynomial.of(suppor).scalarUnaryOperator(values);
    for (int index = 0; index < suppor.length(); ++index)
      assertEquals(suo1.apply(suppor.Get(index)), values.Get(index));
    assertEquals(suo1.apply(GaussScalar.of(54, prime)), GaussScalar.of(4527, prime));
  }

  private static Tensor polynomial_coeffs(Tensor xdata, Tensor ydata, int degree) {
    return LinearSolve.of(VandermondeMatrix.of(xdata), ydata);
  }

  public void testDegreesUnits() {
    Tensor xdata = Tensors.vector(10, 11, 14, 20).map(s -> Quantity.of(s, "K"));
    Tensor ydata = Tensors.vector(5, -2, 1, 9).map(s -> Quantity.of(s, "bar"));
    for (int degree = 0; degree <= 3; ++degree) {
      Tensor x = xdata.extract(0, degree + 1);
      Tensor y = ydata.extract(0, degree + 1);
      Tensor coeffs = polynomial_coeffs(x, y, degree);
      ExactTensorQ.require(coeffs);
      ScalarUnaryOperator scalarUnaryOperator = InterpolatingPolynomial.of(x).scalarUnaryOperator(y);
      assertEquals(x.map(scalarUnaryOperator), y);
    }
  }

  public void testDegreesUnitsNumeric() {
    Tensor xdata = Tensors.vector(10, 11, 14, 20).map(s -> Quantity.of(s, "K")).map(N.DOUBLE);
    Tensor ydata = Tensors.vector(5, -2, 1, 9).map(s -> Quantity.of(s, "bar")).map(N.DOUBLE);
    for (int degree = 0; degree <= 3; ++degree) {
      Tensor x = xdata.extract(0, degree + 1);
      Tensor y = ydata.extract(0, degree + 1);
      ScalarUnaryOperator scalarUnaryOperator = InterpolatingPolynomial.of(x).scalarUnaryOperator(y);
      Tolerance.CHOP.requireClose(x.map(scalarUnaryOperator), y);
    }
  }

  public void testScalarLengthFail() throws ClassNotFoundException, IOException {
    InterpolatingPolynomial interpolatingPolynomial = //
        Serialization.copy(InterpolatingPolynomial.of(LinearBinaryAverage.INSTANCE, Tensors.vector(1, 2, 3)));
    AssertFail.of(() -> interpolatingPolynomial.scalarUnaryOperator(Tensors.vector(1, 2)));
    AssertFail.of(() -> interpolatingPolynomial.scalarUnaryOperator(HilbertMatrix.of(3)));
  }

  public void testTensorLengthFail() throws ClassNotFoundException, IOException {
    InterpolatingPolynomial interpolatingPolynomial = //
        Serialization.copy(InterpolatingPolynomial.of(LinearBinaryAverage.INSTANCE, Tensors.vector(1, 2, 3)));
    AssertFail.of(() -> interpolatingPolynomial.scalarTensorFunction(Tensors.vector(1, 2)));
    AssertFail.of(() -> interpolatingPolynomial.scalarTensorFunction(HilbertMatrix.of(2, 3)));
  }

  public void testKnotsNonVectorFail() {
    AssertFail.of(() -> InterpolatingPolynomial.of(LinearBinaryAverage.INSTANCE, IdentityMatrix.of(3)));
  }
}
