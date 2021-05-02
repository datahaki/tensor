// code by jph
package ch.alpine.tensor.nrm;

import java.io.IOException;
import java.util.Random;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.usr.AssertFail;
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
