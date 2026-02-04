// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.io.StringScalarQ;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;

class TensorsTest {
  @Test
  void testEmpty() {
    Tensor tensor = Tensors.empty();
    assertEquals(tensor, Tensors.empty());
    assertEquals(tensor, Tensors.vector());
    assertEquals(tensor, Tensors.of());
    assertEquals(tensor, Tensors.reserve(10));
  }

  @Test
  void testReserveFail() {
    Tensors.reserve(0);
    assertThrows(IllegalArgumentException.class, () -> Tensors.reserve(-1));
  }

  @Test
  void testNorm() {
    Tensor vector = Tensors.vectorLong(2, 3, 4, 5);
    ExactTensorQ.require(vector);
    Scalar scalar = (Scalar) vector.dot(vector);
    assertEquals(scalar, RationalScalar.of(4 + 9 + 16 + 25, 1));
    ExactScalarQ.require(scalar);
  }

  @Test
  void testNorm2() {
    Tensor a = Tensors.of(RationalScalar.of(2, 3), RationalScalar.of(4, 5));
    Scalar s = (Scalar) a.dot(a);
    assertEquals(s, RationalScalar.of(244, 225));
  }

  @Test
  void testNorm5() {
    int n = 6;
    int m = 12;
    RandomGenerator random = ThreadLocalRandom.current();
    Tensor A = Tensors.matrix((_, _) -> //
    RationalScalar.of( //
        random.nextInt(100) - 50, //
        random.nextInt(100) + 1), n, m);
    Tensor c = Tensors.vector(_ -> RationalScalar.of(1, 1), n);
    assertEquals(Total.of(A), c.dot(A));
  }

  @Test
  void testInteger() {
    Tensor p = Tensors.vector(Arrays.asList(3, 4, -5, 6));
    Tensor q = Tensors.vector(3, 4, -5, 6);
    Tensor r = Tensors.vector(3.0, 4.0, -5.0, 6.0);
    assertInstanceOf(RationalScalar.class, p.Get(0));
    assertInstanceOf(RationalScalar.class, p.Get(1));
    assertInstanceOf(RationalScalar.class, p.Get(2));
    assertInstanceOf(RationalScalar.class, p.Get(3));
    assertInstanceOf(RationalScalar.class, q.Get(0));
    assertInstanceOf(RationalScalar.class, q.Get(1));
    assertInstanceOf(RationalScalar.class, q.Get(2));
    assertInstanceOf(RationalScalar.class, q.Get(3));
    assertEquals(p, q);
    assertEquals(p, r);
  }

  @Test
  void testDoubleArray() {
    double[] asd = new double[] { 3.2, -0.3, 1.0 };
    Tensors.vectorDouble(asd);
  }

  @Test
  void testDouble() {
    Tensor p = Tensors.vector(Arrays.asList(3.3, 4., 5.3, 6.));
    Tensor q = Tensors.vector(3.3, 4., 5.3, 6.);
    assertEquals(p, q);
  }

  @Test
  void testNumber() {
    Tensor p = Tensors.vector(Arrays.asList(3, 4, 5.3, 6));
    Tensor q = Tensors.vector(3, 4, 5.3, 6);
    assertInstanceOf(RationalScalar.class, p.Get(0));
    assertEquals(p, q);
  }

  @Test
  void testIntArrays() {
    int[][] data = new int[][] { { 1, -2, 3 }, { 4, 9 } };
    Tensor actual = Tensors.matrixInt(data);
    Tensor expected = Tensors.fromString("{{1, -2, 3}, {4, 9}}");
    assertEquals(expected, actual);
  }

  @Test
  void testLongArrays() {
    long[][] data = new long[][] { { 1, -2, 3 }, { 4, 9 }, { 0, 0, 0, 0, 0 }, {} };
    Tensor actual = Tensors.matrixLong(data);
    Tensor expected = Tensors.fromString("{{1, -2, 3}, {4, 9}, {0, 0, 0, 0, 0}, {}}");
    assertEquals(expected, actual);
  }

  @Test
  void testVectorFloat() {
    float[] fvalues = { 3.1f, 4.3f, -1.89f };
    double[] dvalues = { 3.1, 4.3, -1.89 };
    Chop._06.requireClose( //
        Tensors.vectorFloat(fvalues), //
        Tensors.vectorDouble(dvalues));
  }

  @Test
  void testMatrixFloat() {
    float[][] values = { { 3.1f, 4.3f, -1.89f }, { -3.6f, 9.3f } };
    Tensor tensor = Tensors.matrixFloat(values);
    assertEquals(tensor.length(), 2);
    assertEquals(tensor.get(0).length(), 3);
    assertEquals(tensor.get(1).length(), 2);
  }

  @Test
  void testDoubleArrays() {
    double[][] data = new double[][] { { 1, -2, 3 }, { 4, 9 }, { 0, 0, 0, 0, 0 }, {} };
    Tensor actual = Tensors.matrixDouble(data);
    Tensor expected = Tensors.fromString("{{1, -2, 3}, {4, 9}, {0, 0, 0, 0, 0}, {}}");
    assertEquals(expected, actual);
  }

  @Test
  void testNumberArrays() {
    Number[][] data = new Number[][] { { 1, -2, 3 }, { 4, 9 }, { 0, 0, 0, 0, 0 }, {} };
    Tensor actual = Tensors.matrix(data);
    Tensor expected = Tensors.fromString("{{1, -2, 3}, {4, 9}, {0, 0, 0, 0, 0}, {}}");
    assertEquals(expected, actual);
  }

  @Test
  void testScalarArrays() {
    Scalar[][] data = new Scalar[][] { { RealScalar.ZERO, RealScalar.ONE }, {}, { ComplexScalar.of(2, 3) } };
    Tensor actual = Tensors.matrix(data);
    Tensor expected = Tensors.fromString("{{0, 1}, {}, {2+3*I}}");
    assertEquals(expected, actual);
  }

  @Test
  void testNCopies() {
    Tensor ncopies = Tensor.of(Collections.nCopies(6, RealScalar.of(3)).stream().map(Tensor.class::cast));
    ncopies.set(RealScalar.ZERO, 2);
    assertEquals(ncopies, Tensors.vector(3, 3, 0, 3, 3, 3));
  }

  @Test
  void testOfReferences() {
    Tensor vector = Tensors.vector(1, 2, 3);
    Tensor matrix = Tensors.of(vector);
    vector.set(RealScalar.of(4), 0);
    assertEquals(vector, Tensors.vector(4, 2, 3));
    assertEquals(matrix.get(0), Tensors.vector(1, 2, 3));
  }

  @Test
  void testOfComparison() {
    Tensor row = Tensors.vector(1, 2, 3);
    Tensor tensor = Tensors.of(RealScalar.of(1), row);
    tensor.set(RealScalar.of(4), 1, 1);
    assertEquals(tensor, Tensors.fromString("{1, {1, 4, 3}}"));
    assertEquals(row, Range.of(1, 4));
    Tensor vector = Tensors.of(RealScalar.of(1), Quantity.of(2, "V"));
    assertTrue(VectorQ.ofLength(vector, 2));
  }

  @Test
  void testOfTensors() {
    Function<Tensor[], Tensor> ftensors = Tensors::of;
    assertEquals(ftensors.apply(new Tensor[] {}), Tensors.empty());
    assertEquals(ftensors.apply(new Scalar[] {}), Tensors.empty());
  }

  @Test
  void testUnmodifiable() {
    assertTrue(Tensors.isUnmodifiable(Pi.VALUE));
  }

  @Test
  void testOfScalars() {
    Function<Scalar[], Tensor> fscalars = Tensors::of;
    assertEquals(fscalars.apply(new Scalar[] {}), Tensors.empty());
  }

  @Test
  void testStringDateTime() {
    String string = "{   2022-10-08T07:33   ,  {   2022-10-08T07:33   } , {  2022-10-08T07:33 , 2022-10-08T07:33 }  }";
    Tensor tensor = Tensors.fromString(string);
    assertFalse(StringScalarQ.any(tensor));
  }
}
