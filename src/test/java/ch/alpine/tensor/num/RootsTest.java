// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
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
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class RootsTest extends TestCase {
  private static final int LIMIT = 20;

  public void testConstantUniform() {
    Tensor roots = Roots.of(Tensors.vector(2));
    assertTrue(Tensors.isEmpty(roots));
  }

  public void testZeros() {
    Tensor roots = Roots.of(Tensors.vector(0, 0, 1, 0));
    assertEquals(roots, Array.zeros(2));
  }

  public void testUnitVector() {
    for (int length = 1; length < 10; ++length) {
      Tensor coeffs = UnitVector.of(length, length - 1);
      assertEquals(Roots.of(coeffs), Array.zeros(length - 1));
    }
  }

  public void testUnitVectorPlus() {
    for (int length = 1; length < 10; ++length) {
      Tensor coeffs = UnitVector.of(length + 3, length - 1);
      assertEquals(Roots.of(coeffs), Array.zeros(length - 1));
    }
  }

  public void testUniform5() {
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

  public void testUniform10() {
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

  public void testNormal() {
    Distribution distribution = NormalDistribution.of(0, 0.3);
    for (int length = 1; length <= 4; ++length)
      for (int index = 0; index < LIMIT; ++index) {
        Tensor coeffs = RandomVariate.of(distribution, length);
        Tensor roots = Roots.of(coeffs);
        Tensor check = roots.map(Polynomial.of(coeffs));
        Chop._04.requireAllZero(check);
      }
  }

  public void testRandomReal() {
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

  public void testRandomRealQuantity() {
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

  public void testRandomComplex() {
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

  public void testRandomComplexQuantity() {
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

  public void testRealUniqueRoots() {
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

  public void testRealTripleRoot() {
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

  public void testRealTripleRoot1() {
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

  public void testSpecific() {
    Tensor coeffs = Tensors.fromString("{-1.7577173839803[m^3], 4.36938808469565[m^2], -3.620519887265771[m], 1.0}");
    Tensor roots = Roots.of(coeffs);
    Tolerance.CHOP.requireClose(roots, ConstantArray.of(Scalars.fromString("1.2068399624219235[m]"), 3));
  }

  public void testComplexTripleRoot() {
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

  public void testScalarFail() {
    AssertFail.of(() -> Roots.of(RealScalar.ONE));
  }

  public void testEmptyFail() {
    AssertFail.of(() -> Roots.of(Tensors.empty()));
  }

  public void testOnes() {
    Tensor coeffs = Tensors.vector(0);
    AssertFail.of(() -> Roots.of(coeffs));
  }

  public void testConstantZeroFail() {
    AssertFail.of(() -> Roots.of(Tensors.vector(0)));
  }

  public void testZerosFail() {
    for (int n = 0; n < 10; ++n) {
      int fn = n;
      AssertFail.of(() -> Roots.of(Array.zeros(fn)));
    }
  }

  public void testMatrixFail() {
    AssertFail.of(() -> Roots.of(HilbertMatrix.of(2, 3)));
  }

  public void testNotImplemented() {
    AssertFail.of(() -> Roots.of(Tensors.vector(1, 2, 3, 4, 5, 6)));
  }
}
