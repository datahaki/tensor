// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Rescale;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.sca.tri.Sin;

class BSplineInterpolationTest {
  @Test
  void testVector() {
    Tensor tensor = Tensors.vector(2, 6, 4, 9, 10, 3);
    for (int degree = 0; degree < 4; ++degree) {
      Interpolation interpolation = BSplineInterpolation.of(degree, tensor);
      for (int index = 0; index < tensor.length(); ++index) {
        Scalar scalar = interpolation.At(RealScalar.of(index));
        assertEquals(scalar, tensor.Get(index));
        ExactScalarQ.require(scalar);
      }
    }
  }

  @Test
  void testMatrix1() {
    Tensor tensor = HilbertMatrix.of(10, 5);
    for (int degree = 0; degree < 4; ++degree) {
      Interpolation interpolation = BSplineInterpolation.of(degree, tensor);
      for (int index = 0; index < tensor.length(); ++index) {
        Tensor result = interpolation.at(RealScalar.of(index));
        assertEquals(result, tensor.get(index));
        Tensor svalue = interpolation.get(Tensors.vector(1, 2));
        assertEquals(svalue, RationalScalar.of(1, 4));
        Tensor vvalue = interpolation.get(Tensors.vector(3));
        assertEquals(vvalue, tensor.get(3));
      }
      assertThrows(Throw.class, () -> interpolation.at(RealScalar.of(-0.1)));
      assertThrows(Throw.class, () -> interpolation.at(RealScalar.of(9.1)));
      interpolation.get(Tensors.vector(1));
      interpolation.get(Tensors.vector(1, 2));
    }
  }

  @Test
  void testMatrix2() {
    Tensor tensor = HilbertMatrix.of(5, 10);
    for (int degree = 0; degree < 4; ++degree) {
      Interpolation interpolation = BSplineInterpolation.of(degree, tensor);
      for (int index = 0; index < tensor.length(); ++index) {
        Tensor result = interpolation.at(RealScalar.of(index));
        assertEquals(result, tensor.get(index));
      }
    }
  }

  @Test
  void testAd() {
    Tensor tensor = LeviCivitaTensor.of(3);
    for (int degree = 0; degree < 4; ++degree) {
      Interpolation interpolation = BSplineInterpolation.of(degree, tensor);
      for (int index = 0; index < tensor.length(); ++index) {
        Tensor result = interpolation.at(RealScalar.of(index));
        assertEquals(result, tensor.get(index));
        Tensor svalue = interpolation.get(Tensors.vector(1, 2));
        assertEquals(svalue, UnitVector.of(3, 0));
        ExactTensorQ.require(svalue);
      }
    }
  }

  @Test
  void testSolve() {
    Tensor interp = Tensors.vector(1, 0, 3, 2);
    for (int degree = 0; degree < 4; ++degree) {
      Tensor tensor = BSplineInterpolation.solve(degree, interp);
      ExactTensorQ.require(tensor);
      ScalarTensorFunction bSplineFunction = BSplineFunctionString.of(degree, tensor);
      for (int index = 0; index < interp.length(); ++index)
        assertEquals(bSplineFunction.apply(RealScalar.of(index)), interp.get(index));
    }
  }

  @Test
  void testLinear() {
    for (int n = 1; n < 6; ++n) {
      Tensor tensor = BSplineInterpolation.matrix(1, n);
      assertEquals(tensor, IdentityMatrix.of(n));
      ExactTensorQ.require(tensor);
    }
  }

  @Test
  void testQuadratic() {
    for (int n = 1; n <= 6; ++n) {
      Tensor vector = ConstantArray.of(RealScalar.ONE, n);
      Tensor tensor = BSplineInterpolation.matrix(2, n);
      assertEquals(Dimensions.of(tensor), Arrays.asList(n, n));
      assertEquals(tensor.dot(vector), vector);
      ExactTensorQ.require(tensor);
    }
  }

  @Test
  void testCubic() {
    for (int n = 1; n <= 6; ++n) {
      Tensor vector = ConstantArray.of(RealScalar.ONE, n);
      Tensor tensor = BSplineInterpolation.matrix(3, n);
      assertEquals(Dimensions.of(tensor), Arrays.asList(n, n));
      assertEquals(tensor.dot(vector), vector);
      ExactTensorQ.require(tensor);
    }
  }

  @Test
  void testListDensityPlot() {
    Tensor d = Subdivide.of(0, Math.PI, 5);
    Tensor matrix = Tensors.matrix((i, j) -> Sin.FUNCTION.apply(d.Get(j).multiply(d.Get(j)).add(d.Get(i))), 6, 6);
    for (int degree = 0; degree < 5; ++degree) {
      Interpolation interpolation = BSplineInterpolation.of(degree, matrix);
      Tensor x = Subdivide.of(0, 5, 10);
      Tensor y = Reverse.of(x);
      Tensor eval = Tensors.matrix((i, j) -> interpolation.get( //
          Tensors.of(y.Get(i), x.Get(j))), x.length(), x.length());
      Rescale.of(eval).map(ColorDataGradients.SOUTH_WEST);
    }
  }
}
