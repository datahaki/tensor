// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

// cubic basis functions over unit interval [0, 1]
// {(1 - t)^3, 4 - 6 t^2 + 3 t^3, 1 + 3 t + 3 t^2 - 3 t^3, t^3}/6
class BSplineFunctionStringTest {
  @Test
  void testConstant() {
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(0, Tensors.vector(2, 1, 5, 0, -2));
    assertEquals(bSplineFunction.apply(RealScalar.of(0)), RealScalar.of(2));
    assertEquals(bSplineFunction.apply(RealScalar.of(0.3333)), RealScalar.of(2));
    assertEquals(bSplineFunction.apply(RealScalar.of(0.5)), RealScalar.of(1));
    assertEquals(bSplineFunction.apply(RealScalar.of(1.25)), RealScalar.of(1));
    assertEquals(bSplineFunction.apply(RealScalar.of(1.49)), RealScalar.of(1));
    assertEquals(bSplineFunction.apply(RealScalar.of(1.75)), RealScalar.of(5));
    assertEquals(bSplineFunction.apply(RealScalar.of(2)), RealScalar.of(5));
    assertEquals(bSplineFunction.apply(RealScalar.of(2.50)), RealScalar.of(0));
    assertEquals(bSplineFunction.apply(RealScalar.of(3)), RealScalar.of(0));
    assertEquals(bSplineFunction.apply(RealScalar.of(3.5)), RealScalar.of(-2));
    assertEquals(bSplineFunction.apply(RealScalar.of(4)), RealScalar.of(-2));
  }

  @Test
  void testLinear() {
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(1, Tensors.vector(2, 1, 5, 0, -2));
    assertEquals(bSplineFunction.apply(RealScalar.of(0)), RealScalar.of(2));
    assertEquals(bSplineFunction.apply(RationalScalar.of(1, 2)), RationalScalar.of(3, 2));
    assertEquals(bSplineFunction.apply(RealScalar.of(1)), RealScalar.of(1));
    assertEquals(bSplineFunction.apply(RealScalar.of(1.25)), RealScalar.of(2));
    assertEquals(bSplineFunction.apply(RealScalar.of(1.50)), RealScalar.of(3));
    assertEquals(bSplineFunction.apply(RealScalar.of(1.75)), RealScalar.of(4));
    assertEquals(bSplineFunction.apply(RealScalar.of(2)), RealScalar.of(5));
    assertEquals(bSplineFunction.apply(RealScalar.of(2.50)), RealScalar.of(2.5));
    assertEquals(bSplineFunction.apply(RealScalar.of(3)), RealScalar.of(0));
    assertEquals(bSplineFunction.apply(RealScalar.of(3.5)), RealScalar.of(-1));
    assertEquals(bSplineFunction.apply(RealScalar.of(4)), RealScalar.of(-2));
  }

  @Test
  void testLinearCurve() {
    Tensor control = Tensors.fromString("{{2, 3}, {1, 0}, {5, 7}, {0, 0}, {-2, 3}}");
    assertTrue(MatrixQ.of(control));
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(1, control);
    assertEquals(bSplineFunction.apply(RealScalar.of(1.5)), Tensors.vector(3, 3.5));
    assertEquals(bSplineFunction.apply(RealScalar.of(4)), Tensors.vector(-2, 3));
  }

  @Test
  void testQuadratic() {
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(2, Tensors.vector(2, 1, 5, 0, -2));
    assertEquals(bSplineFunction.apply(RealScalar.of(0)), RealScalar.of(2));
    assertEquals(bSplineFunction.apply(RealScalar.of(1)), RationalScalar.of(5, 3));
    assertEquals(bSplineFunction.apply(RealScalar.of(2)), RationalScalar.of(31, 8));
    assertEquals(bSplineFunction.apply(RealScalar.of(4)), RealScalar.of(-2));
  }

  @Test
  void testQuadraticSymmetry() {
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(2, Tensors.vector(0, 1, 2, 3));
    Tensor r1 = bSplineFunction.apply(RealScalar.of(0.5));
    Tensor r2 = bSplineFunction.apply(RealScalar.of(1.5));
    Tensor r3 = bSplineFunction.apply(RealScalar.of(2.5)); // does not evaluate correctly
    Tolerance.CHOP.requireClose(r1, RealScalar.of(1 / 3.0));
    assertEquals(r2, RealScalar.of(1.5));
    Tolerance.CHOP.requireClose(r3, RealScalar.of(8 / 3.0));
  }

  @Test
  void testCubic() {
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(3, Tensors.vector(2, 1, 5, 0, -2));
    assertEquals(bSplineFunction.apply(RealScalar.of(0)), RealScalar.of(2));
    assertEquals(bSplineFunction.apply(RationalScalar.HALF), RationalScalar.of(173, 96));
    assertEquals(bSplineFunction.apply(RealScalar.of(1)), RationalScalar.of(23, 12));
    assertEquals(bSplineFunction.apply(RealScalar.of(4)), RealScalar.of(-2));
  }

  @Test
  void testCubicLinear() {
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(3, Tensors.vector(2, 1, 0, -1, -2));
    assertEquals(bSplineFunction.apply(RealScalar.of(0)), RealScalar.of(2));
    assertEquals(bSplineFunction.apply(RealScalar.of(1)), RationalScalar.of(13, 12));
    assertEquals(bSplineFunction.apply(RealScalar.of(2)), RealScalar.of(0));
    Tolerance.CHOP.requireClose(bSplineFunction.apply(RealScalar.of(3.999999999999)), RealScalar.of(-2));
    assertEquals(bSplineFunction.apply(RealScalar.of(4)), RealScalar.of(-2));
  }

  @Test
  void testSymmetric() {
    Tensor control = Tensors.vector(1, 5, 3, -1, 0);
    int n = control.length() - 1;
    for (int degree = 0; degree <= 5; ++degree) {
      ScalarTensorFunction bsf = BSplineFunctionString.of(degree, control);
      ScalarTensorFunction bsr = BSplineFunctionString.of(degree, Reverse.of(control));
      Tensor res1f = Subdivide.of(0, n, 10).map(bsf);
      Tensor res1r = Subdivide.of(n, 0, 10).map(bsr);
      assertEquals(res1f, res1r);
    }
  }

  @Test
  void testQuantity() {
    Tensor control = Tensors.fromString("{2[m], 7[m], 3[m]}");
    Clip clip = Clips.interval(2, 7);
    int n = control.length() - 1;
    for (int degree = 0; degree <= 5; ++degree) {
      ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(degree, control);
      Tensor tensor = Subdivide.of(0, n, 10).map(bSplineFunction);
      VectorQ.require(tensor);
      Tensor nounit = tensor.map(QuantityMagnitude.SI().in("m"));
      ExactTensorQ.require(nounit);
      nounit.map(clip::requireInside);
    }
  }

  @Test
  void testSingleton() {
    for (int degree = 0; degree < 4; ++degree) {
      ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(degree, Tensors.vector(99));
      assertEquals(bSplineFunction.apply(RealScalar.ZERO), RealScalar.of(99));
      assertEquals(bSplineFunction.apply(RealScalar.of(0.0)), RealScalar.of(99));
    }
  }

  @Test
  void testIndex() {
    for (int degree = 0; degree <= 5; ++degree)
      for (int length = 2; length <= 6; ++length) {
        ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(degree, IdentityMatrix.of(length));
        for (Tensor _x : Subdivide.of(0, length - 1, 13)) {
          Tensor tensor = bSplineFunction.apply((Scalar) _x);
          assertTrue(tensor.stream().map(Scalar.class::cast).allMatch(Clips.unit()::isInside));
          assertEquals(Total.of(tensor), RealScalar.ONE);
          ExactTensorQ.require(tensor);
        }
      }
  }

  @Test
  void testSerializable() throws Exception {
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(3, Tensors.vector(2, 1, 0, -1, -2));
    Tensor value0 = bSplineFunction.apply(RealScalar.of(2.3));
    ScalarTensorFunction copy = Serialization.copy(bSplineFunction);
    Tensor value1 = copy.apply(RealScalar.of(2.3));
    assertEquals(value0, value1);
  }

  @Test
  void testSymmetry() {
    Distribution distribution = DiscreteUniformDistribution.of(-4, 7);
    int n = 20;
    Tensor domain = Subdivide.of(0, n - 1, 31);
    for (int degree = 1; degree < 8; ++degree) {
      Tensor control = RandomVariate.of(distribution, n, 3);
      ScalarTensorFunction mapForward = BSplineFunctionString.of(degree, control);
      Tensor forward = domain.map(mapForward);
      ScalarTensorFunction mapReverse = BSplineFunctionString.of(degree, Reverse.of(control));
      Tensor reverse = Reverse.of(domain.map(mapReverse));
      assertEquals(forward, reverse);
      ExactTensorQ.require(forward);
      ExactTensorQ.require(reverse);
    }
  }

  @Test
  void testBasisWeights1a() {
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(1, UnitVector.of(3, 1));
    Tensor limitMask = Range.of(1, 2).map(bSplineFunction);
    ExactTensorQ.require(limitMask);
    assertEquals(limitMask, Tensors.fromString("{1}"));
  }

  @Test
  void testBasisWeights2() {
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(2, UnitVector.of(5, 2));
    Tensor limitMask = Range.of(1, 4).map(bSplineFunction);
    ExactTensorQ.require(limitMask);
    assertEquals(limitMask, Tensors.fromString("{1/8, 3/4, 1/8}"));
  }

  @Test
  void testBasisWeights3a() {
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(3, UnitVector.of(7, 3));
    Tensor limitMask = Range.of(2, 5).map(bSplineFunction);
    ExactTensorQ.require(limitMask);
    assertEquals(limitMask, Tensors.fromString("{1/6, 2/3, 1/6}"));
  }

  @Test
  void testBasisWeights3b() {
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(3, UnitVector.of(5, 2));
    Tensor limitMask = Range.of(1, 4).map(bSplineFunction);
    ExactTensorQ.require(limitMask);
    assertEquals(limitMask, Tensors.fromString("{1/6, 2/3, 1/6}"));
  }

  @Test
  void testBasisWeights4() {
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(4, UnitVector.of(9, 4));
    Tensor limitMask = Range.of(2, 7).map(bSplineFunction);
    assertEquals(Total.of(limitMask), RealScalar.ONE);
    ExactTensorQ.require(limitMask);
    assertEquals(limitMask, Tensors.fromString("{1/384, 19/96, 115/192, 19/96, 1/384}"));
  }

  @Test
  void testBasisWeights5a() {
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(5, UnitVector.of(11, 5));
    Tensor limitMask = Range.of(3, 8).map(bSplineFunction);
    assertEquals(Total.of(limitMask), RealScalar.ONE);
    ExactTensorQ.require(limitMask);
    assertEquals(limitMask, Tensors.fromString("{1/120, 13/60, 11/20, 13/60, 1/120}"));
  }

  @Test
  void testBasisWeights5b() {
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(5, UnitVector.of(9, 4));
    Tensor limitMask = Range.of(2, 7).map(bSplineFunction);
    assertEquals(Total.of(limitMask), RealScalar.ONE);
    ExactTensorQ.require(limitMask);
    assertEquals(limitMask, Tensors.fromString("{1/120, 13/60, 11/20, 13/60, 1/120}"));
  }

  @Test
  void testBasisWeights5c() {
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(5, UnitVector.of(7, 3));
    Tensor limitMask = Range.of(1, 6).map(bSplineFunction);
    assertEquals(Total.of(limitMask), RealScalar.ONE);
    ExactTensorQ.require(limitMask);
    assertEquals(limitMask, Tensors.fromString("{1/120, 13/60, 11/20, 13/60, 1/120}"));
  }

  @Test
  void testEmptyFail() {
    assertThrows(IllegalArgumentException.class, () -> BSplineFunctionString.of(-2, Tensors.empty()));
    assertThrows(IllegalArgumentException.class, () -> BSplineFunctionString.of(-1, Tensors.empty()));
    assertThrows(Throw.class, () -> BSplineFunctionString.of(-0, Tensors.empty()));
    assertThrows(Throw.class, () -> BSplineFunctionString.of(+1, Tensors.empty()));
    assertThrows(Throw.class, () -> BSplineFunctionString.of(+2, Tensors.empty()));
  }

  @Test
  void testNegativeFail() {
    assertThrows(IllegalArgumentException.class, () -> BSplineFunctionString.of(-1, Tensors.vector(1, 2, 3, 4)));
  }

  @Test
  void testOutsideFail() {
    ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(3, Tensors.vector(2, 1, 0, -1, -2));
    bSplineFunction.apply(RealScalar.of(4));
    assertThrows(Throw.class, () -> bSplineFunction.apply(RealScalar.of(-0.1)));
    assertThrows(Throw.class, () -> bSplineFunction.apply(RealScalar.of(5.1)));
    assertThrows(Throw.class, () -> bSplineFunction.apply(RealScalar.of(4.1)));
  }
}
