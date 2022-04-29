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
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;

public class BSplineInterpolationTest {
  @Test
  public void testVector() {
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
  public void testMatrix1() {
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
      assertThrows(TensorRuntimeException.class, () -> interpolation.at(RealScalar.of(-0.1)));
      assertThrows(TensorRuntimeException.class, () -> interpolation.at(RealScalar.of(9.1)));
      interpolation.get(Tensors.vector(1));
      interpolation.get(Tensors.vector(1, 2));
      assertThrows(TensorRuntimeException.class, () -> interpolation.get(Tensors.vector(1, 1.8)));
    }
  }

  @Test
  public void testMatrix2() {
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
  public void testAd() {
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
  public void testSolve() {
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
  public void testLinear() {
    for (int n = 1; n < 6; ++n) {
      Tensor tensor = BSplineInterpolation.matrix(1, n);
      assertEquals(tensor, IdentityMatrix.of(n));
      ExactTensorQ.require(tensor);
    }
  }

  @Test
  public void testQuadratic() {
    for (int n = 1; n <= 6; ++n) {
      Tensor vector = Array.of(l -> RealScalar.ONE, n);
      Tensor tensor = BSplineInterpolation.matrix(2, n);
      assertEquals(Dimensions.of(tensor), Arrays.asList(n, n));
      assertEquals(tensor.dot(vector), vector);
      ExactTensorQ.require(tensor);
    }
  }

  @Test
  public void testCubic() {
    for (int n = 1; n <= 6; ++n) {
      Tensor vector = Array.of(l -> RealScalar.ONE, n);
      Tensor tensor = BSplineInterpolation.matrix(3, n);
      assertEquals(Dimensions.of(tensor), Arrays.asList(n, n));
      assertEquals(tensor.dot(vector), vector);
      ExactTensorQ.require(tensor);
    }
  }
  // TODO
  // Table[Sin[j^2 + i], {i, 0, Pi, Pi/5}, {j, 0, Pi, Pi/5}];
  // see ListDensityPlot help
}
