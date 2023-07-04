// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.d.GeometricDistribution;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class LinearInterpolationTest {
  @Test
  void testEmpty() {
    Interpolation interpolation = LinearInterpolation.of(Tensors.empty());
    assertEquals(interpolation.get(Tensors.empty()), Tensors.empty());
  }

  @Test
  void testEmpty1() {
    Tensor tensor = Tensors.vector(10, 20, 30, 40);
    Interpolation interpolation = LinearInterpolation.of(tensor);
    Tensor res = interpolation.get(Tensors.empty());
    assertEquals(res, tensor);
  }

  @Test
  void testEmpty2() {
    Tensor tensor = Tensors.vector(10, 20, 30, 40);
    Tensor ori = tensor.copy();
    Interpolation interpolation = LinearInterpolation.of(tensor);
    Tensor res = interpolation.get(Tensors.empty());
    res.set(RealScalar.ONE::add, Tensor.ALL);
    assertEquals(tensor, ori);
    assertNotEquals(tensor, res);
    assertEquals(interpolation.get(Tensors.empty()), ori);
  }

  @Test
  void testVectorGet() {
    Interpolation interpolation = LinearInterpolation.of(Tensors.vector(10, 20, 30, 40));
    assertEquals(interpolation.get(Tensors.vector(0)), RealScalar.of(10));
    assertEquals(interpolation.get(Tensors.vector(2)), RealScalar.of(30));
    assertEquals(interpolation.get(Tensors.vector(2.5)), RealScalar.of(35));
    assertEquals(interpolation.get(Tensors.vector(3)), RealScalar.of(40));
  }

  @Test
  void testVectorAt() {
    Interpolation interpolation = LinearInterpolation.of(Tensors.vector(10, 20, 30, 40));
    assertEquals(interpolation.At(RealScalar.of(0)), RealScalar.of(10));
    assertEquals(interpolation.At(RealScalar.of(2)), RealScalar.of(30));
    assertEquals(interpolation.At(RealScalar.of(2.5)), RealScalar.of(35));
    assertEquals(interpolation.At(RealScalar.of(3)), RealScalar.of(40));
  }

  @Test
  void testMatrix1() {
    Tensor tensor = Tensors.matrix(new Number[][] { { 5, 5, 5 }, { 1, 10, 100 } });
    Interpolation interpolation = LinearInterpolation.of(tensor);
    {
      Tensor res = interpolation.get(Tensors.vector(1, 3).multiply(RationalScalar.of(1, 2)));
      assertEquals(res, RealScalar.of(30)); // 5+5+10+100==120 -> 120 / 4 == 30
    }
    {
      Tensor res = interpolation.get(Tensors.of(RationalScalar.of(1, 2)));
      Tensor from = Tensors.fromString("{3, 15/2, 105/2}");
      assertEquals(res, from);
    }
  }

  @Test
  void testMatrix2() {
    Tensor tensor = Tensors.matrix(new Number[][] { { 5, 5, 5 }, { 1, 10, 100 } });
    Interpolation interpolation = LinearInterpolation.of(tensor);
    assertEquals(interpolation.get(UnitVector.of(1, 0)), Tensors.vector(1, 10, 100));
    assertEquals(interpolation.get(Tensors.vector(1, 2)), RealScalar.of(100));
    assertEquals(interpolation.get(Tensors.vector(0)), Tensors.vector(5, 5, 5));
    assertEquals(interpolation.get(UnitVector.of(2, 0)), RealScalar.of(1));
    assertEquals(interpolation.get(Array.zeros(2)), RealScalar.of(5));
  }

  @Test
  void testRank3() {
    Tensor arr = Array.of(Tensors::vector, 2, 3);
    Interpolation interpolation = LinearInterpolation.of(arr);
    Scalar result = interpolation.Get(Tensors.vector(0.3, 1.8, 0.3));
    assertFalse(ExactScalarQ.of(result));
    assertEquals(result, RationalScalar.of(3, 4));
  }

  @Test
  void testQuantity() {
    Tensor vector = Tensors.of(Quantity.of(1, "m"), Quantity.of(4, "m"), Quantity.of(2, "m"));
    Interpolation interpolation = LinearInterpolation.of(vector);
    Scalar r = Quantity.of((1 + 4) * 0.5, "m");
    Scalar s = interpolation.Get(Tensors.vector(0.5));
    assertEquals(s, r);
  }

  @Test
  void testQuantity2() {
    Tensor v1 = Tensors.of(Quantity.of(1, "m"), Quantity.of(4, "m"), Quantity.of(2, "m"));
    Tensor v2 = Tensors.of(Quantity.of(9, "s"), Quantity.of(6, "s"), Quantity.of(-3, "s"));
    Tensor matrix = Transpose.of(Tensors.of(v1, v2));
    Interpolation interpolation = LinearInterpolation.of(matrix);
    Scalar r1 = Quantity.of((1 + 4) * 0.5, "m");
    Scalar r2 = Quantity.of((9 + 6) * 0.5, "s");
    Tensor vec = interpolation.get(Tensors.of(RationalScalar.HALF));
    assertEquals(vec, Tensors.of(r1, r2));
    ExactTensorQ.require(vec);
    Tensor at = interpolation.at(RationalScalar.HALF);
    assertEquals(at, Tensors.of(r1, r2));
    ExactTensorQ.require(at);
  }

  @Test
  void testExact() {
    Distribution distribution = GeometricDistribution.of(RationalScalar.of(1, 3));
    Tensor matrix = RandomVariate.of(distribution, 3, 5);
    Interpolation interpolation = LinearInterpolation.of(matrix);
    Scalar index = RationalScalar.of(45, 31);
    Tensor res1 = interpolation.at(index);
    Tensor res2 = interpolation.get(Tensors.of(index));
    VectorQ.requireLength(res1, 5);
    ExactTensorQ.require(res1);
    ExactTensorQ.require(res2);
    assertEquals(res1, res2);
  }

  @Test
  void testUseCase() {
    Tensor tensor = Range.of(1, 11);
    Interpolation interpolation = LinearInterpolation.of(tensor);
    Distribution distribution = DiscreteUniformDistribution.of(0, (tensor.length() - 1) * 3 + 1);
    for (int count = 0; count < 10; ++count) {
      Scalar index = RandomVariate.of(distribution).divide(RealScalar.of(3));
      Scalar scalar = interpolation.At(index);
      ExactScalarQ.require(scalar);
      assertEquals(RealScalar.ONE.add(index), scalar);
      assertEquals(scalar, interpolation.get(Tensors.of(index)));
      assertEquals(scalar, interpolation.at(index));
    }
  }

  @Test
  void test0D() {
    Interpolation interpolation = LinearInterpolation.of(Tensors.empty());
    assertThrows(Exception.class, () -> interpolation.get(RealScalar.ZERO));
  }

  @Test
  void test1D() {
    Interpolation interpolation = LinearInterpolation.of(Tensors.vector(10, 20, 30, 40));
    TestHelper.checkMatch(interpolation);
    TestHelper.checkMatchExact(interpolation);
    TestHelper.getScalarFail(interpolation);
  }

  @Test
  void test2D() {
    Distribution distribution = UniformDistribution.unit();
    Interpolation interpolation = LinearInterpolation.of(RandomVariate.of(distribution, 3, 5));
    TestHelper.checkMatch(interpolation);
    TestHelper.checkMatchExact(interpolation);
    TestHelper.getScalarFail(interpolation);
  }

  @Test
  void testClip() throws ClassNotFoundException, IOException {
    ScalarUnaryOperator interpolation = Serialization.copy(LinearInterpolation.of(Clips.interval(10, 14)));
    assertEquals(ExactScalarQ.require(interpolation.apply(RealScalar.ZERO)), RealScalar.of(10));
    assertEquals(ExactScalarQ.require(interpolation.apply(RationalScalar.of(1, 4))), RealScalar.of(11));
    assertEquals(ExactScalarQ.require(interpolation.apply(RealScalar.ONE)), RealScalar.of(14));
    assertThrows(Exception.class, () -> interpolation.apply(RealScalar.of(-0.1)));
    assertThrows(Exception.class, () -> interpolation.apply(RealScalar.of(1.1)));
  }

  @Test
  void testClipWidthZero() {
    ScalarUnaryOperator interpolation = LinearInterpolation.of(Clips.interval(3.0, 3.0));
    assertEquals(interpolation.apply(RationalScalar.of(3, 10)), RealScalar.of(3.0));
  }

  @Test
  void testLinterpWidthZero() {
    Interpolation interpolation = LinearInterpolation.of(Tensors.vector(3.0, 3.0));
    assertEquals(interpolation.at(RationalScalar.of(3, 10)), RealScalar.of(3.0));
  }

  @Test
  void testCsv() {
    Tensor tensor = Import.of("/ch/alpine/tensor/io/dateobject.csv");
    assertInstanceOf(DateTime.class, tensor.Get(0, 0));
    Tensor result = Subdivide.of(0, 2, 20).map(LinearInterpolation.of(tensor)::at);
    assertEquals(result.get(0).toString(), tensor.get(0).toString());
    assertEquals(Last.of(result).toString(), Last.of(tensor).toString());
  }

  @Test
  void testFailNull() {
    assertThrows(NullPointerException.class, () -> LinearInterpolation.of((Tensor) null));
    assertThrows(NullPointerException.class, () -> LinearInterpolation.of((Clip) null));
  }

  @Test
  void testFailScalar() {
    Interpolation interpolation = LinearInterpolation.of(RealScalar.ONE);
    assertEquals(interpolation.get(Tensors.empty()), RealScalar.ONE);
    assertThrows(Throw.class, () -> interpolation.get(Tensors.vector(0)));
  }
}
