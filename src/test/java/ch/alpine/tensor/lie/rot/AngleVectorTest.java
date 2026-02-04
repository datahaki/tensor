// code by jph
package ch.alpine.tensor.lie.rot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.tri.ArcTan;

class AngleVectorTest {
  @RepeatedTest(10)
  void testNumeric(RepetitionInfo repetitionInfo) {
    int count = repetitionInfo.getCurrentRepetition();
    Scalar scalar = N.DOUBLE.apply(RationalScalar.of(count, 12));
    Tensor tensor = AngleVector.turns(scalar);
    Tolerance.CHOP.requireClose(tensor, AngleVector.of(scalar.multiply(Pi.TWO)));
  }

  @RepeatedTest(10)
  void testNorm(RepetitionInfo repetitionInfo) {
    Distribution distribution = UniformDistribution.of(Pi.VALUE.negate(), Pi.VALUE);
    Scalar angle = RandomVariate.of(distribution).negate(); // prevent angle == -pi
    Tensor vector = AngleVector.of(angle);
    Chop._14.requireClose(Vector2Norm.of(vector), RealScalar.ONE);
    Scalar check = ArcTan.of(vector.Get(0), vector.Get(1));
    Chop._14.requireClose(angle, check);
  }

  @Test
  void testMatrix() {
    Scalar angle = RealScalar.ONE;
    Tensor vector = AngleVector.of(angle);
    Tensor matrix = RotationMatrix.of(angle);
    assertEquals(vector, matrix.get(Tensor.ALL, 0));
  }

  @Test
  void testRotation() {
    ExactTensorQ.require(AngleVector.turns(RationalScalar.of(-2, 2)));
    assertEquals(AngleVector.turns(RationalScalar.of(-2, 2)), Tensors.vector(+1, 0));
    assertEquals(AngleVector.turns(RationalScalar.of(0, 2)), Tensors.vector(+1, 0));
    assertEquals(AngleVector.turns(RationalScalar.of(1, 2)), Tensors.vector(-1, 0));
    assertFalse(ExactTensorQ.of(AngleVector.turns(RealScalar.of(-2.0))));
  }

  @RepeatedTest(10)
  void testRotationOfEquivalence(RepetitionInfo repetitionInfo) {
    Distribution distribution = NormalDistribution.standard();
    Scalar fraction = RandomVariate.of(distribution);
    Tolerance.CHOP.requireClose(AngleVector.turns(fraction), AngleVector.of(fraction.multiply(Pi.TWO)));
  }

  @Test
  void testModify() {
    Tensor o1 = AngleVector.turns(RealScalar.ZERO);
    assertEquals(o1, UnitVector.of(2, 0));
    o1.set(RealScalar.of(3), 0);
    Tensor o2 = AngleVector.turns(RealScalar.ZERO);
    assertEquals(o2, UnitVector.of(2, 0));
  }

  @Test
  void testNullFail() {
    assertThrows(Exception.class, () -> AngleVector.of(null));
  }
}
