// code by jph
package ch.alpine.tensor.mat.re;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.sca.N;

class DetTest {
  @Test
  void testEmpty() {
    assertThrows(TensorRuntimeException.class, () -> Det.of(Tensors.empty()));
  }

  @Test
  void testEmptyMatrix() {
    Tensor m = Tensors.matrix(new Number[][] { {} });
    // this is consistent with Mathematica
    // Mathematica throws an exception
    assertThrows(TensorRuntimeException.class, () -> Det.of(m));
  }

  @Test
  void testDet1() {
    Tensor m = Tensors.matrix(new Number[][] { //
        { +2, 3, 4 }, //
        { +0, 0, 1 }, //
        { -5, 3, 4 } });
    assertEquals(Det.of(m), RealScalar.of(-21));
  }

  @Test
  void testDet2() {
    Tensor m = Tensors.matrix(new Number[][] { //
        { -2, 3, 4 }, //
        { +0, 0, 1 }, //
        { -5, 3, 4 } });
    assertEquals(Det.of(m), RealScalar.of(-9));
  }

  @Test
  void testDet3() {
    Tensor m = Tensors.matrix(new Number[][] { //
        { -2, 3, +4 }, //
        { +0, 2, -1 }, //
        { -5, 3, +4 } });
    assertEquals(Det.of(m), RealScalar.of(33));
  }

  @Test
  void testId() {
    for (int n = 1; n < 10; ++n)
      assertEquals(Det.of(IdentityMatrix.of(n)), RealScalar.ONE);
  }

  @Test
  void testReversedId() {
    Tensor actual = Tensors.vector(0, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1);
    for (int n = 1; n < 10; ++n) {
      Tensor mat = Reverse.of(IdentityMatrix.of(n));
      Scalar det = Det.of(mat);
      assertEquals(det, actual.Get(n));
    }
  }

  @Test
  void testDet4() {
    Tensor m = Tensors.matrix(new Number[][] { //
        { -2, 3, +4, 0 }, //
        { +0, 2, -1, 2 }, //
        { -5, 3, +4, 1 }, //
        { +0, 2, -1, 0 } //
    });
    assertEquals(Det.of(m), RealScalar.of(-66));
    m.set(RealScalar.of(9), 3, 0);
    assertEquals(Det.of(m), RealScalar.of(33));
  }

  @Test
  void testNonSquare() {
    Tensor m = Tensors.matrix(new Number[][] { //
        { -2, 3, +4, 0 }, //
        { +0, 2, -1, 2 }, //
    });
    assertThrows(TensorRuntimeException.class, () -> Det.of(m));
  }

  @Test
  void testNonSquare2() {
    Tensor m = Tensors.matrix(new Number[][] { //
        { -2, 3, +4 }, //
        { +0, 2, -1 }, //
        { -5, 3, +4 }, //
        { +0, 2, -1 } //
    });
    assertThrows(TensorRuntimeException.class, () -> Det.of(m));
  }

  @Test
  void testComplex1() {
    Tensor re = Tensors.matrix(new Number[][] { //
        { 0, 0, 3 }, //
        { -2, 0, 0 }, //
        { -3, 0, 2 } //
    });
    Tensor im = Tensors.matrix(new Number[][] { //
        { 0, 0, -2 }, //
        { -1, 9, 0 }, //
        { 8, 0, 1 } //
    });
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply(re, im);
    assertEquals(Det.of(matrix), ComplexScalar.of(270, -63));
  }

  @Test
  void testComplex2() {
    Tensor re = Tensors.matrix(new Number[][] { //
        { 5, 0, 3 }, //
        { -2, 0, 0 }, //
        { -3, 0, 2 } //
    });
    Tensor im = Tensors.matrix(new Number[][] { //
        { -9, 0, -2 }, //
        { -1, 9, 0 }, //
        { 8, 0, 1 } //
    });
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply(re, im);
    assertEquals(Det.of(matrix), ComplexScalar.of(387, 108));
  }

  @Test
  void testComplex3() {
    Tensor re = Tensors.matrix(new Number[][] { //
        { 5, 0, 3 }, //
        { -2, 0, 0 }, //
        { -3, -4, 2 } //
    });
    Tensor im = Tensors.matrix(new Number[][] { //
        { -9, 0, -2 }, //
        { -1, 9, 0 }, //
        { 8, -2, 1 } //
    });
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply(re, im);
    assertEquals(Det.of(matrix), ComplexScalar.of(421, 120));
  }

  @ParameterizedTest
  @EnumSource(Pivots.class)
  void testSingular(Pivot pivot) {
    assertEquals(Det.of(Array.zeros(5, 5), pivot), RealScalar.ZERO);
    assertThrows(TensorRuntimeException.class, () -> Det.of(Array.zeros(2, 5), pivot));
    assertThrows(TensorRuntimeException.class, () -> Det.of(Array.zeros(5, 2), pivot));
  }

  @Test
  void testSingularFail() {
    assertThrows(TensorRuntimeException.class, () -> Det.of(Array.zeros(2, 5)));
    assertThrows(TensorRuntimeException.class, () -> Det.of(Array.zeros(5, 2)));
  }

  @Test
  void testNullFail() {
    assertThrows(TensorRuntimeException.class, () -> Det.of(Array.zeros(5, 2), null));
    assertThrows(TensorRuntimeException.class, () -> Det.of(Array.zeros(2, 5), null));
  }

  // https://ch.mathworks.com/help/matlab/ref/det.html
  @Test
  void testMatlabEx() {
    Tensor matrix = ResourceData.of("/ch/alpine/tensor/mat/re/det0-matlab.csv");
    Scalar det = Det.of(matrix);
    assertEquals(det, RealScalar.ZERO);
    // Matlab gives num == 1.0597e+05 !
    // Mathematica gives num == 44934.8 !
    Scalar num1 = Det.of(N.DOUBLE.of(matrix)); // indeed, our algo is no different:
    // num1 == 105968.67122221774
    num1.toString(); // to eliminate warning
    Scalar num2 = Det.of(N.DOUBLE.of(matrix), Pivots.FIRST_NON_ZERO); // indeed, our algo is no different:
    // num2 == 105968.67122221774
    num2.toString(); // to eliminate warning
  }

  @Test
  void testHilbert() {
    Scalar det = Det.of(HilbertMatrix.of(8));
    assertEquals(det, RationalScalar.of( //
        BigInteger.ONE, new BigInteger("365356847125734485878112256000000")));
  }

  @Test
  void testHilbert2() {
    Scalar det = Det.of(HilbertMatrix.of(8), Pivots.FIRST_NON_ZERO);
    assertEquals(det, Scalars.fromString("1/365356847125734485878112256000000"));
  }

  @Test
  void testGaussScalar() {
    int n = 7;
    int prime = 7879;
    Random random = new Random();
    Tensor matrix = Tensors.matrix((i, j) -> GaussScalar.of(random.nextInt(), prime), n, n);
    assertInstanceOf(GaussScalar.class, Det.of(matrix));
  }

  @Test
  void testUnitsSingle() {
    Tensor tensor = Tensors.fromString("{{1[m], 2}, {4, 5[m]}, {3, 5}}");
    assertThrows(TensorRuntimeException.class, () -> Det.of(tensor));
  }

  @Test
  void testUnitsMixed() {
    Tensor tensor = Tensors.fromString("{{1[m], 2}, {4, 5[s]}, {3, 5}}");
    assertThrows(TensorRuntimeException.class, () -> Det.of(tensor));
    // assertEquals(Det.of(tensor), Quantity.of(0, ""));
  }

  @Test
  void testQuantity1() {
    Scalar qs1 = Quantity.of(1, "m");
    Scalar qs2 = Quantity.of(2, "m");
    Scalar qs3 = Quantity.of(3, "rad");
    Scalar qs4 = Quantity.of(4, "rad");
    Tensor ve1 = Tensors.of(qs1.multiply(qs1), qs2.multiply(qs3));
    Tensor ve2 = Tensors.of(qs2.multiply(qs3), qs4.multiply(qs4));
    Tensor mat = Tensors.of(ve1, ve2);
    Scalar det = Det.of(mat);
    ExactScalarQ.require(det);
    assertEquals(det, Scalars.fromString("-20[m^2*rad^2]"));
  }

  @Test
  void testFailMatrixQ() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3}, {4, 5}}");
    assertThrows(TensorRuntimeException.class, () -> Det.of(tensor));
  }

  @Test
  void testFailNonArray() {
    Tensor matrix = HilbertMatrix.of(4);
    matrix.set(Tensors.vector(1, 2, 3), 1, 2);
    assertThrows(TensorRuntimeException.class, () -> Det.of(matrix));
  }

  @Test
  void testFailRank3() {
    assertThrows(TensorRuntimeException.class, () -> Det.of(LeviCivitaTensor.of(3)));
  }

  @Test
  void testFailScalar() {
    assertThrows(TensorRuntimeException.class, () -> Det.of(Pi.HALF));
  }

  @Test
  void testFailVector() {
    assertThrows(TensorRuntimeException.class, () -> Det.of(Tensors.vector(1, 2, 3)));
  }

  @Test
  void testFailRank3b() {
    assertThrows(TensorRuntimeException.class, () -> Det.of(Array.zeros(2, 2, 3)));
  }

  @Test
  void testFailNull() {
    assertThrows(NullPointerException.class, () -> Det.of(null));
    assertThrows(NullPointerException.class, () -> Det.of(HilbertMatrix.of(3), null));
  }
}
