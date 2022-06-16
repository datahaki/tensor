// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.sca.Chop;

class RootsTest {
  private static final int LIMIT = 20;

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

  @Test
  void testUnitVector() {
    for (int length = 1; length < 10; ++length) {
      Tensor coeffs = UnitVector.of(length, length - 1);
      assertEquals(Roots.of(coeffs), Array.zeros(length - 1));
    }
  }

  @Test
  void testUnitVectorPlus() {
    for (int length = 1; length < 10; ++length) {
      Tensor coeffs = UnitVector.of(length + 3, length - 1);
      assertEquals(Roots.of(coeffs), Array.zeros(length - 1));
    }
  }

  @Test
  void testUniform5() {
    Distribution distribution = UniformDistribution.of(-5, 5);
    for (int length = 1; length <= 4; ++length)
      for (int index = 0; index < LIMIT; ++index) {
        Tensor coeffs = RandomVariate.of(distribution, length);
        if (Scalars.nonZero(Last.of(coeffs))) {
          Tensor roots = Roots.of(coeffs);
          VectorQ.requireLength(roots, length - 1);
          Tensor check = roots.map(Polynomial.of(coeffs));
          if (!Chop._03.allZero(check)) {
            System.err.println("uni5 " + coeffs);
            System.err.println(check);
            fail();
          }
        } else
          System.out.println("skip " + coeffs);
      }
  }

  @Test
  void testUniform10() {
    Distribution distribution = UniformDistribution.of(-10, 10);
    for (int length = 1; length <= 4; ++length)
      for (int index = 0; index < LIMIT; ++index) {
        Tensor coeffs = RandomVariate.of(distribution, length);
        if (Scalars.nonZero(Last.of(coeffs))) {
          Tensor roots = Roots.of(coeffs);
          VectorQ.requireLength(roots, length - 1);
          Tensor check = roots.map(Polynomial.of(coeffs));
          if (!Chop._03.allZero(check)) {
            System.err.println("uniT " + coeffs);
            System.err.println(check);
            fail();
          }
        } else
          System.out.println("skip " + coeffs);
      }
  }

  @Test
  void testNormal() {
    Distribution distribution = NormalDistribution.of(0, 0.3);
    for (int length = 1; length <= 4; ++length)
      for (int index = 0; index < LIMIT; ++index) {
        Tensor coeffs = RandomVariate.of(distribution, length);
        Tensor roots = Roots.of(coeffs);
        Tensor check = roots.map(Polynomial.of(coeffs));
        Chop._04.requireAllZero(check);
      }
  }

  @Test
  void testRandomReal() {
    Distribution distribution = NormalDistribution.standard();
    for (int length = 1; length <= 4; ++length)
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

  @Test
  void testRandomRealQuantity() {
    Distribution distribution = NormalDistribution.standard();
    for (int length = 1; length <= 4; ++length)
      for (int index = 0; index < LIMIT; ++index) {
        Tensor coeffs = Array.of(list -> Quantity.of(RandomVariate.of(distribution), "m^-" + list.get(0)), length);
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

  @Test
  void testRandomComplex() {
    Distribution distribution = NormalDistribution.standard();
    for (int length = 1; length <= 4; ++length)
      for (int index = 0; index < LIMIT; ++index) {
        Tensor coeffs = Entrywise.with(ComplexScalar::of).apply( //
            RandomVariate.of(distribution, length), //
            RandomVariate.of(distribution, length));
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

  @Test
  void testRandomComplexQuantity() {
    Distribution distribution = NormalDistribution.standard();
    for (int length = 1; length <= 4; ++length)
      for (int index = 0; index < LIMIT; ++index) {
        Tensor coeffs = Array.of(list -> Quantity.of(ComplexScalar.of( //
            RandomVariate.of(distribution), RandomVariate.of(distribution)), "m^-" + list.get(0)), length);
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

  @Test
  void testRealUniqueRoots() {
    Distribution distribution = NormalDistribution.standard();
    for (int length = 1; length <= 3; ++length)
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

  @Test
  void testRealTripleRoot() {
    Distribution distribution = NormalDistribution.standard();
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

  @Test
  void testComplexTripleRoot() {
    Distribution distribution = NormalDistribution.standard();
    for (int length = 1; length <= 3; ++length)
      for (int index = 0; index < LIMIT; ++index) {
        Tensor zeros = ConstantArray.of(ComplexScalar.of( //
            RandomVariate.of(distribution), //
            RandomVariate.of(distribution)), length);
        Tensor roots = Roots.of(CoefficientList.of(zeros));
        for (int count = 0; count < length; ++count) {
          boolean anyZero = roots.map(zeros.Get(count).negate()::add).stream() //
              .anyMatch(Chop._01::allZero);
          if (!anyZero) {
            System.err.println(zeros);
            System.err.println(roots);
            fail();
          }
        }
      }
  }

  @Test
  void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> Roots.of(RealScalar.ONE));
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
    assertThrows(TensorRuntimeException.class, () -> Roots.of(Tensors.vector(1, 2, 3, 4, 5, 6)));
  }
}
