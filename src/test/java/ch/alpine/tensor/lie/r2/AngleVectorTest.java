// code by jph
package ch.alpine.tensor.lie.r2;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.sca.ArcTan;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class AngleVectorTest extends TestCase {
  public void testNumeric() {
    for (int count = 0; count < 12; ++count) {
      Scalar scalar = N.DOUBLE.of(RationalScalar.of(count, 12));
      Tensor tensor = AngleVector.turns(scalar);
      Tolerance.CHOP.requireClose(tensor, AngleVector.of(scalar.multiply(Pi.TWO)));
    }
  }

  public void testNorm() {
    Distribution distribution = UniformDistribution.of(Pi.VALUE.negate(), Pi.VALUE);
    for (int index = 0; index < 10; ++index) {
      Scalar angle = RandomVariate.of(distribution).negate(); // prevent angle == -pi
      Tensor vector = AngleVector.of(angle);
      Chop._14.requireClose(Vector2Norm.of(vector), RealScalar.ONE);
      Scalar check = ArcTan.of(vector.Get(0), vector.Get(1));
      Chop._14.requireClose(angle, check);
    }
  }

  public void testMatrix() {
    Scalar angle = RealScalar.ONE;
    Tensor vector = AngleVector.of(angle);
    Tensor matrix = RotationMatrix.of(angle);
    assertEquals(vector, matrix.get(Tensor.ALL, 0));
  }

  public void testRotation() {
    ExactTensorQ.require(AngleVector.turns(RationalScalar.of(-2, 2)));
    assertEquals(AngleVector.turns(RationalScalar.of(-2, 2)), Tensors.vector(+1, 0));
    assertEquals(AngleVector.turns(RationalScalar.of(0, 2)), Tensors.vector(+1, 0));
    assertEquals(AngleVector.turns(RationalScalar.of(1, 2)), Tensors.vector(-1, 0));
    assertFalse(ExactTensorQ.of(AngleVector.turns(RealScalar.of(-2.0))));
  }

  public void testRotationOfEquivalence() {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 10; ++count) {
      Scalar fraction = RandomVariate.of(distribution);
      Tolerance.CHOP.requireClose(AngleVector.turns(fraction), AngleVector.of(fraction.multiply(Pi.TWO)));
    }
  }

  public void testModify() {
    Tensor o1 = AngleVector.turns(RealScalar.ZERO);
    assertEquals(o1, UnitVector.of(2, 0));
    o1.set(RealScalar.of(3), 0);
    Tensor o2 = AngleVector.turns(RealScalar.ZERO);
    assertEquals(o2, UnitVector.of(2, 0));
  }

  public void testNullFail() {
    AssertFail.of(() -> AngleVector.of(null));
  }
}
