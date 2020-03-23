// code by jph
package ch.ethz.idsc.tensor.opt;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class InterpolatingPolynomialTest extends TestCase {
  public void testScaleInvariant() {
    Tensor suppor = Tensors.vector(2, 2.3, 4);
    Tensor values = Tensors.vector(6, -7, 20);
    ScalarUnaryOperator gn1 = InterpolatingPolynomial.of(suppor).scalarUnaryOperator(values);
    ScalarUnaryOperator gn2 = InterpolatingPolynomial.of(suppor.multiply(RealScalar.of(3)).map(RealScalar.ONE::subtract)).scalarUnaryOperator(values);
    Distribution distribution = UniformDistribution.of(2, 4);
    Tensor domain = RandomVariate.of(distribution, 20);
    Tolerance.CHOP.requireClose( //
        domain.map(gn1), //
        domain.multiply(RealScalar.of(3)).map(RealScalar.ONE::subtract).map(gn2));
  }

  public void testScaleInvariantMatrix() {
    Tensor suppor = Tensors.vector(2, 2.3, 4);
    Tensor values = Tensors.fromString("{{2,-3}, {-7, 5}, {5, 9}}");
    ScalarTensorFunction gn1 = InterpolatingPolynomial.of(suppor).scalarTensorFunction(values);
    ScalarTensorFunction gn2 = InterpolatingPolynomial.of(suppor.multiply(RealScalar.of(3)).map(RealScalar.ONE::subtract)).scalarTensorFunction(values);
    Distribution distribution = UniformDistribution.of(2, 4);
    Tensor domain = RandomVariate.of(distribution, 20);
    Tolerance.CHOP.requireClose( //
        domain.map(gn1), //
        domain.multiply(RealScalar.of(3)).map(RealScalar.ONE::subtract).map(gn2));
  }

  public void testLengthFail() throws ClassNotFoundException, IOException {
    InterpolatingPolynomial interpolatingPolynomial = //
        Serialization.copy(InterpolatingPolynomial.of(LinearBinaryAverage.INSTANCE, Tensors.vector(1, 2, 3)));
    try {
      interpolatingPolynomial.scalarUnaryOperator(Tensors.vector(1, 2));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      interpolatingPolynomial.scalarUnaryOperator(HilbertMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testKnotsNonVectorFail() {
    try {
      InterpolatingPolynomial.of(LinearBinaryAverage.INSTANCE, IdentityMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
