// code by jph
package ch.alpine.tensor.sca.ply;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.util.Random;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Im;

class RootsTest {
  private final int LIMIT = 20;

  @ParameterizedTest
  @MethodSource(value = "test.TestDistributions#distributions2")
  void testComplexTripleRoot(Distribution distribution) {
    Random random = new Random(3);
    for (int length = 1; length <= 3; ++length)
      for (int index = 0; index < LIMIT; ++index) {
        Tensor zeros = ConstantArray.of(ComplexScalar.of( //
            RandomVariate.of(distribution, random), //
            RandomVariate.of(distribution, random)), length);
        Tensor coeffs = CoefficientList.of(zeros);
        Tensor roots = Roots.of(coeffs);
        Polynomial polynomial = Polynomial.of(coeffs);
        Tensor map = roots.map(polynomial);
        if (!Chop._05.allZero(map))
          for (int count = 0; count < length; ++count) {
            boolean anyZero = roots.map(zeros.Get(count).negate()::add).stream() //
                .anyMatch(Chop._01::allZero);
            if (!anyZero) {
              IO.println("coeffs: " + coeffs);
              IO.println(map);
              System.err.println(zeros);
              System.err.println(roots);
              fail();
            }
          }
      }
  }

  @Test
  void testConstantUniform() {
    Tensor roots = Roots.of(Tensors.vector(2));
    assertTrue(Tensors.isEmpty(roots));
  }

  @Test
  void testZeros() {
    Tensor roots = Roots.of(Tensors.vector(0, 0, 1, 0));
    assertEquals(roots, Array.zeros(2));
  }

  @RepeatedTest(9)
  void testUnitVector(RepetitionInfo repetitionInfo) {
    int length = repetitionInfo.getCurrentRepetition();
    Tensor coeffs = UnitVector.of(length, length - 1);
    assertEquals(Roots.of(coeffs), Array.zeros(length - 1));
  }

  @RepeatedTest(9)
  void testUnitVectorPlus(RepetitionInfo repetitionInfo) {
    int length = repetitionInfo.getCurrentRepetition();
    Tensor coeffs = UnitVector.of(length + 3, length - 1);
    assertEquals(Roots.of(coeffs), Array.zeros(length - 1));
  }

  @RepeatedTest(4)
  void testUniform5(RepetitionInfo repetitionInfo) {
    Distribution distribution = UniformDistribution.of(-5, 5);
    int length = repetitionInfo.getCurrentRepetition();
    for (int index = 0; index < LIMIT; ++index) {
      Tensor coeffs = RandomVariate.of(distribution, length);
      assumeFalse(Scalars.isZero(Last.of(coeffs)));
      Tensor roots = Roots.of(coeffs);
      VectorQ.requireLength(roots, length - 1);
      Tensor check = roots.map(Polynomial.of(coeffs));
      if (!Chop._03.allZero(check)) {
        System.err.println("uni5 " + coeffs);
        System.err.println(check);
        fail();
      }
    }
  }

  @RepeatedTest(4)
  void testUniform10(RepetitionInfo repetitionInfo) {
    Distribution distribution = UniformDistribution.of(-10, 10);
    int length = repetitionInfo.getCurrentRepetition();
    for (int index = 0; index < LIMIT; ++index) {
      Tensor coeffs = RandomVariate.of(distribution, length);
      assumeFalse(Scalars.isZero(Last.of(coeffs)));
      Tensor roots = Roots.of(coeffs);
      VectorQ.requireLength(roots, length - 1);
      Tensor check = roots.map(Polynomial.of(coeffs));
      if (!Chop._03.allZero(check)) {
        System.err.println("uniT " + coeffs);
        System.err.println(check);
        fail();
      }
    }
  }

  @RepeatedTest(4)
  void testNormal(RepetitionInfo repetitionInfo) {
    Distribution distribution = NormalDistribution.of(0, 0.3);
    int length = repetitionInfo.getCurrentRepetition();
    for (int index = 0; index < LIMIT; ++index) {
      Tensor coeffs = RandomVariate.of(distribution, length);
      Tensor roots = Roots.of(coeffs);
      Tensor check = roots.map(Polynomial.of(coeffs));
      Chop._04.requireAllZero(check);
    }
  }

  @RepeatedTest(4)
  void testRandomReal(RepetitionInfo repetitionInfo) {
    Distribution distribution = NormalDistribution.standard();
    int length = repetitionInfo.getCurrentRepetition();
    for (int index = 0; index < LIMIT; ++index) {
      Tensor coeffs = RandomVariate.of(distribution, length);
      Tensor roots = Roots.of(coeffs);
      ScalarUnaryOperator scalarUnaryOperator = Polynomial.of(coeffs);
      Tensor tensor = roots.map(scalarUnaryOperator);
      boolean allZero = Chop._04.allZero(tensor);
      if (!allZero) {
        System.err.println(coeffs);
        System.err.println(tensor);
      }
      assertTrue(allZero);
    }
  }

  @RepeatedTest(4)
  void testRandomRealQuantity(RepetitionInfo repetitionInfo) {
    Distribution distribution = NormalDistribution.standard();
    int length = repetitionInfo.getCurrentRepetition();
    for (int index = 0; index < LIMIT; ++index) {
      Tensor coeffs = Array.of(list -> Quantity.of(RandomVariate.of(distribution), "m^-" + list.getFirst()), length);
      Tensor roots = Roots.of(coeffs);
      ScalarUnaryOperator scalarUnaryOperator = Polynomial.of(coeffs);
      Tensor tensor = roots.map(scalarUnaryOperator);
      boolean allZero = Chop._04.allZero(tensor);
      if (!allZero) {
        System.err.println(coeffs);
        System.err.println(tensor);
      }
      assertTrue(allZero);
    }
  }

  @RepeatedTest(4)
  void testRandomComplex(RepetitionInfo repetitionInfo) {
    int length = repetitionInfo.getCurrentRepetition();
    for (int index = 0; index < LIMIT; ++index) {
      Tensor coeffs = RandomVariate.of(ComplexNormalDistribution.STANDARD, length);
      Tensor roots = Roots.of(coeffs);
      ScalarUnaryOperator scalarUnaryOperator = Polynomial.of(coeffs);
      Tensor tensor = roots.map(scalarUnaryOperator);
      boolean allZero = Chop._04.allZero(tensor);
      if (!allZero) {
        System.err.println(coeffs);
        System.err.println(tensor);
      }
      assertTrue(allZero);
    }
  }

  @RepeatedTest(4)
  void testRandomComplexQuantity(RepetitionInfo repetitionInfo) {
    Distribution distribution = NormalDistribution.standard();
    int length = repetitionInfo.getCurrentRepetition();
    for (int index = 0; index < LIMIT; ++index) {
      Tensor coeffs = Array.of(list -> Quantity.of(ComplexScalar.of( //
          RandomVariate.of(distribution), RandomVariate.of(distribution)), "m^-" + list.getFirst()), length);
      Tensor roots = Roots.of(coeffs);
      ScalarUnaryOperator scalarUnaryOperator = Polynomial.of(coeffs);
      Tensor tensor = roots.map(scalarUnaryOperator);
      boolean allZero = Chop._04.allZero(tensor);
      if (!allZero) {
        System.err.println(coeffs);
        System.err.println(tensor);
      }
      assertTrue(allZero);
    }
  }

  @RepeatedTest(3)
  void testRealUniqueRoots(RepetitionInfo repetitionInfo) {
    Distribution distribution = NormalDistribution.standard();
    int length = repetitionInfo.getCurrentRepetition();
    for (int index = 0; index < LIMIT; ++index) {
      Tensor roots = Sort.of(RandomVariate.of(distribution, length));
      Tensor solve = Roots.of(CoefficientList.of(roots));
      if (!Chop._03.isClose(roots, solve)) {
        System.err.println("real unique");
        System.err.println(roots);
        System.err.println(solve);
        fail();
      }
    }
  }

  @ParameterizedTest
  @MethodSource(value = "test.TestDistributions#distributions2")
  void testRealTripleRoot(Distribution distribution) {
    for (int length = 1; length <= 3; ++length)
      for (int index = 0; index < LIMIT; ++index) {
        Tensor zeros = ConstantArray.of(RandomVariate.of(distribution), length);
        Tensor coeff = CoefficientList.of(zeros);
        Tensor roots = Roots.of(coeff);
        if (!Chop._01.isClose(zeros, roots)) {
          System.err.println(zeros);
          fail();
        }
      }
  }

  @Test
  void testRealTripleRoot1() {
    Distribution distribution = NormalDistribution.of(Quantity.of(1, "m"), Quantity.of(0.5, "m"));
    for (int index = 0; index < LIMIT; ++index) {
      Tensor zeros = ConstantArray.of(RandomVariate.of(distribution), 3);
      Tensor coeff = CoefficientList.of(zeros);
      Tensor roots = Roots.of(coeff);
      if (!Chop._01.isClose(zeros, roots)) {
        System.err.println(zeros);
        fail();
      }
    }
  }

  @Test
  void testSpecific() {
    Tensor coeffs = Tensors.fromString("{-1.7577173839803[m^3], 4.36938808469565[m^2], -3.620519887265771[m], 1.0}");
    Tensor roots = Roots.of(coeffs);
    Tolerance.CHOP.requireClose(roots, ConstantArray.of(Scalars.fromString("1.2068399624219235[m]"), 3));
  }

  /** safety critical code used for the gokart steering system
   *
   * @param b linear coefficient
   * @param d cubic coefficient */
  /* package */ record InverseSteerCubic(Scalar b, Scalar d) implements ScalarUnaryOperator {
    @Override
    public Scalar apply(Scalar y) {
      return Roots.of(Tensors.of(y.negate(), b, RealScalar.ZERO, d)).Get(1);
    }
  }

  @Test
  void testSteer() {
    Scalar c = RealScalar.of(+0.8284521034333863);
    Scalar a = RealScalar.of(-0.33633373640449604);
    Tensor coeffs = Tensors.of(RealScalar.ZERO, c, RealScalar.ZERO, a);
    ScalarUnaryOperator cubic = Polynomial.of(coeffs);
    for (Tensor t : Subdivide.of(-0.75, 0.75, 1230)) {
      Scalar d = cubic.apply((Scalar) t);
      Tensor roots = Roots.of(Tensors.of(d.negate(), c, RealScalar.ZERO, a));
      assertEquals(ExactTensorQ.require(roots.map(Im.FUNCTION)), Array.zeros(3));
      Chop._13.requireClose(roots.Get(1), t);
    }
  }

  @Test
  void testCubicOp() {
    Scalar b = RealScalar.of(+0.8284521034333863);
    Scalar d = RealScalar.of(-0.33633373640449604);
    InverseSteerCubic inverseSteerCubic = new InverseSteerCubic(b, d);
    Tensor coeffs = Tensors.of(RealScalar.ZERO, b, RealScalar.ZERO, d);
    ScalarUnaryOperator cubic = Polynomial.of(coeffs);
    for (Tensor t : Subdivide.of(-0.75, 0.75, 1230)) {
      Scalar apply = cubic.apply((Scalar) t);
      Scalar root = inverseSteerCubic.apply(apply);
      Chop._13.requireClose(root, t);
    }
    assertEquals(inverseSteerCubic.apply(RealScalar.ZERO), RealScalar.ZERO);
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> Roots.of(RealScalar.ONE));
  }

  @Test
  void testEmptyFail() {
    assertThrows(IndexOutOfBoundsException.class, () -> Roots.of(Tensors.empty()));
  }

  @Test
  void testOnes() {
    Tensor coeffs = Tensors.vector(0);
    assertThrows(IndexOutOfBoundsException.class, () -> Roots.of(coeffs));
  }

  @Test
  void testConstantZeroFail() {
    assertThrows(IndexOutOfBoundsException.class, () -> Roots.of(Tensors.vector(0)));
  }

  @Test
  void testZerosFail() {
    for (int n = 0; n < 10; ++n) {
      int fn = n;
      assertThrows(IndexOutOfBoundsException.class, () -> Roots.of(Array.zeros(fn)));
    }
  }

  @Test
  void testMatrixFail() {
    assertThrows(ClassCastException.class, () -> Roots.of(HilbertMatrix.of(2, 3)));
  }

  @Test
  void testNotImplemented() {
    Polynomial polynomial = Polynomial.of(Tensors.vector(1, 2, 3, 4, 5, 6));
    Tensor tensor = polynomial.roots();
    Tolerance.CHOP.requireAllZero(tensor.map(polynomial));
  }
}
