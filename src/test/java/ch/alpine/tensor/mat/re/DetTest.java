// code by jph
package ch.alpine.tensor.mat.re;

import java.math.BigInteger;
import java.util.Random;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class DetTest extends TestCase {
  public void testEmpty() {
    AssertFail.of(() -> Det.of(Tensors.empty()));
  }

  public void testEmptyMatrix() {
    Tensor m = Tensors.matrix(new Number[][] { {} });
    // this is consistent with Mathematica
    // Mathematica throws an exception
    AssertFail.of(() -> Det.of(m));
  }

  public void testDet1() {
    Tensor m = Tensors.matrix(new Number[][] { //
        { +2, 3, 4 }, //
        { +0, 0, 1 }, //
        { -5, 3, 4 } });
    assertEquals(Det.of(m), RealScalar.of(-21));
  }

  public void testDet2() {
    Tensor m = Tensors.matrix(new Number[][] { //
        { -2, 3, 4 }, //
        { +0, 0, 1 }, //
        { -5, 3, 4 } });
    assertEquals(Det.of(m), RealScalar.of(-9));
  }

  public void testDet3() {
    Tensor m = Tensors.matrix(new Number[][] { //
        { -2, 3, +4 }, //
        { +0, 2, -1 }, //
        { -5, 3, +4 } });
    assertEquals(Det.of(m), RealScalar.of(33));
  }

  public void testId() {
    for (int n = 1; n < 10; ++n)
      assertEquals(Det.of(IdentityMatrix.of(n)), RealScalar.ONE);
  }

  public void testReversedId() {
    Tensor actual = Tensors.vector(0, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1);
    for (int n = 1; n < 10; ++n) {
      Tensor mat = Reverse.of(IdentityMatrix.of(n));
      Scalar det = Det.of(mat);
      assertEquals(det, actual.Get(n));
    }
  }

  public void testDet4() {
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

  public void testNonSquare() {
    Tensor m = Tensors.matrix(new Number[][] { //
        { -2, 3, +4, 0 }, //
        { +0, 2, -1, 2 }, //
    });
    assertEquals(Det.of(m), RealScalar.of(0));
  }

  public void testNonSquare2() {
    Tensor m = Tensors.matrix(new Number[][] { //
        { -2, 3, +4 }, //
        { +0, 2, -1 }, //
        { -5, 3, +4 }, //
        { +0, 2, -1 } //
    });
    assertEquals(Det.of(m), RealScalar.of(0));
  }

  public void testComplex1() {
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

  public void testComplex2() {
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

  public void testComplex3() {
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

  public void testSingular() {
    for (Pivot pivot : Pivots.values()) {
      assertEquals(Det.of(Array.zeros(5, 5), pivot), RealScalar.ZERO);
      assertEquals(Det.of(Array.zeros(2, 5), pivot), RealScalar.ZERO);
      assertEquals(Det.of(Array.zeros(5, 2), pivot), RealScalar.ZERO);
    }
  }

  public void testNullFail() {
    AssertFail.of(() -> Det.of(Array.zeros(5, 2), null));
    AssertFail.of(() -> Det.of(Array.zeros(2, 5), null));
  }

  // https://ch.mathworks.com/help/matlab/ref/det.html
  public void testMatlabEx() {
    Tensor matrix = ResourceData.of("/mat/det0-matlab.csv");
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

  public void testHilbert() {
    Scalar det = Det.of(HilbertMatrix.of(8));
    assertEquals(det, RationalScalar.of( //
        BigInteger.ONE, new BigInteger("365356847125734485878112256000000")));
  }

  public void testHilbert2() {
    Scalar det = Det.of(HilbertMatrix.of(8), Pivots.FIRST_NON_ZERO);
    assertEquals(det, Scalars.fromString("1/365356847125734485878112256000000"));
  }

  public void testGaussScalar() {
    int n = 7;
    int prime = 7879;
    Random random = new Random();
    Tensor matrix = Tensors.matrix((i, j) -> GaussScalar.of(random.nextInt(), prime), n, n);
    assertTrue(Det.of(matrix) instanceof GaussScalar);
  }
}
