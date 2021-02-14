// code by jph
package ch.ethz.idsc.tensor.nrm;

import java.io.IOException;
import java.util.Random;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.lie.LeviCivitaTensor;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SchattenNormTest extends TestCase {
  public void testFrobenius() throws ClassNotFoundException, IOException {
    TensorScalarFunction tensorScalarFunction = Serialization.copy(SchattenNorm.of(2));
    Distribution distribution = UniformDistribution.of(-2, 2);
    Tensor design = RandomVariate.of(distribution, 5, 3);
    Scalar scalar = FrobeniusNorm.of(design);
    Tolerance.CHOP.requireClose(tensorScalarFunction.apply(design), scalar);
    Tolerance.CHOP.requireClose(tensorScalarFunction.apply(Transpose.of(design)), scalar);
    assertEquals(tensorScalarFunction.toString(), "SchattenNorm[2]");
  }

  public void testExact() throws ClassNotFoundException, IOException {
    Random random = new Random(1);
    TensorScalarFunction tensorScalarFunction = Serialization.copy(SchattenNorm.of(1.2));
    Distribution distribution = DiscreteUniformDistribution.of(0, 2);
    Tensor matrix = RandomVariate.of(distribution, random, 4, 3);
    Scalar scalar = tensorScalarFunction.apply(matrix);
    Sign.requirePositive(scalar);
  }

  public void testZero() {
    Tensor matrix = Array.zeros(3, 2);
    for (Tensor p : Subdivide.of(1, 3, 6)) {
      TensorScalarFunction tensorScalarFunction = SchattenNorm.of((Scalar) p);
      assertEquals(tensorScalarFunction.apply(matrix), RealScalar.ZERO);
    }
  }

  public void testPOutsideRangeFail() {
    AssertFail.of(() -> SchattenNorm.of(0.999));
  }

  public void testFormatFail() {
    TensorScalarFunction tensorScalarFunction = SchattenNorm.of(RationalScalar.of(3, 2));
    AssertFail.of(() -> tensorScalarFunction.apply(LeviCivitaTensor.of(3)));
    AssertFail.of(() -> tensorScalarFunction.apply(Tensors.vector(1, 2, 3)));
    AssertFail.of(() -> tensorScalarFunction.apply(Tensors.empty()));
    AssertFail.of(() -> tensorScalarFunction.apply(Pi.HALF));
  }

  public void testNullFail() {
    AssertFail.of(() -> SchattenNorm.of((Number) null));
    AssertFail.of(() -> SchattenNorm.of((Scalar) null));
  }
}
