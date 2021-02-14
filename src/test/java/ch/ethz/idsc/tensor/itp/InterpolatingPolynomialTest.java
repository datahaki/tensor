// code by jph
package ch.ethz.idsc.tensor.itp;

import java.io.IOException;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.lie.Permutations;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.QuantityTensor;
import ch.ethz.idsc.tensor.usr.AssertFail;
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
