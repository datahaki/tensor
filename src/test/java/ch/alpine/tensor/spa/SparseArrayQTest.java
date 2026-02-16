// code by jph
package ch.alpine.tensor.spa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.qty.Quantity;

class SparseArrayQTest {
  @Test
  void testSimple() {
    SparseArrayQ.require(LeviCivitaTensor.of(2));
    assertFalse(SparseArrayQ.of(Tensors.empty()));
  }

  @Test
  void testAddOne() {
    Tensor tensor = LeviCivitaTensor.of(2);
    Tensor result = tensor.maps(RealScalar.ONE::add);
    assertEquals(result, Tensors.fromString("{{1, 2}, {0, 1}}"));
  }

  @Test
  void testAddOneNormal() {
    Tensor tensor = Normal.of(LeviCivitaTensor.of(2));
    Tensor result = tensor.maps(RealScalar.ONE::add);
    assertEquals(result, Tensors.fromString("{{1, 2}, {0, 1}}"));
  }

  @Test
  void testVector() {
    Tensor tensor = LeviCivitaTensor.of(3);
    Tensor result = tensor.maps(s -> Tensors.of(s, s));
    assertEquals(Dimensions.of(result), Arrays.asList(3, 3, 3, 2));
    // SparseArrayQ.require(result);
    // assertTrue(result.toString().contains("{0, 1, 2, 0}"));
  }

  @Test
  void testVector2() {
    Tensor tensor = LeviCivitaTensor.of(3);
    Tensor result = tensor.maps(s -> Tensors.of(s, s.one(), s, Rational.HALF));
    assertEquals(Dimensions.of(result), Arrays.asList(3, 3, 3, 4));
    // assertTrue(SparseArrayQ.of(result));
  }

  @Test
  void testUnit() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}").maps(s -> Quantity.of(s, "m"));
    SparseArray sparse = (SparseArray) TensorToSparseArray.of(tensor);
    assertEquals(sparse.fallback(), Quantity.of(0, "m"));
  }

  @Test
  void testMapUnit() {
    Tensor tensor = LeviCivitaTensor.of(3);
    Tensor result = tensor.maps(s -> Tensors.of(Quantity.of(s, "m"), Quantity.of(s, "m")));
    result.maps(Scalar::zero);
    // SparseArray sparse = (SparseArray) result;
    // assertEquals(sparse.fallback(), Quantity.of(0, "m"));
    // assertSame(SparseArrayQ.require(sparse), sparse);
  }

  @Test
  void testDot() {
    Tensor t1 = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}").maps(s -> Quantity.of(s, "m"));
    Tensor t2 = Tensors.fromString("{0,1,3,0,0}").maps(s -> Quantity.of(s, "s^-1"));
    assertEquals(t1.dot(t2), Tensors.fromString("{9[m*s^-1], 30[m*s^-1], 29[m*s^-1]}"));
  }

  @RepeatedTest(4)
  void testId(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor t1 = IdentityMatrix.sparse(n).maps(s -> Quantity.of(s, "m"));
    Tensor t2 = IdentityMatrix.sparse(n).maps(s -> Quantity.of(s, "kg"));
    Tensor t3 = t1.dot(t2);
    Tensor t4 = IdentityMatrix.sparse(n).maps(s -> Quantity.of(s, "kg*m"));
    assertEquals(t3, t4);
  }
}
