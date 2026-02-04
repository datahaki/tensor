// code by jph
package ch.alpine.tensor.spa;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.CategoricalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Times;

class TensorToSparseArrayTest {
  @Test
  void testSparseBinary() {
    Tensor a = Tensors.fromString("{{1,0,3,0,0},{0,0,0,0,0},{0,2,0,0,4}}");
    Tensor b = Tensors.fromString("{{3,0,0,7,0},{0,0,0,0,0},{0,4,0,3,0}}");
    Tensor sa = TensorToSparseArray.of(a);
    Tensor sb = TensorToSparseArray.of(b);
    {
      Tensor r_add = sa.add(sb);
      assertEquals(a.add(b), r_add);
      assertEquals(a.add(sb), r_add);
      assertEquals(sa.add(b), r_add);
      assertInstanceOf(SparseArray.class, r_add);
    }
    {
      Tensor r_sub = sa.subtract(sb);
      assertEquals(a.subtract(b), r_sub);
      assertEquals(a.subtract(sb), r_sub);
      assertEquals(sa.subtract(b), r_sub);
      assertInstanceOf(SparseArray.class, r_sub);
    }
    {
      Tensor r_pml = Times.of(sa, sb);
      assertEquals(Times.of(a, b), r_pml);
      assertEquals(Times.of(a, sb), r_pml);
      assertEquals(Times.of(sa, b), r_pml);
    }
  }

  @Test
  void testSparseWedge() {
    Tensor a = Tensors.fromString("{{1,0,3,0,0},{0,0,0,0,0},{0,2,0,0,4},{0,0,0,0,0},{0,0,0,0,0}}");
    Tensor s = TensorToSparseArray.of(a);
    Tensor tw_s = TensorWedge.of(s);
    assertInstanceOf(SparseArray.class, tw_s);
    assertEquals(TensorWedge.of(a), tw_s);
    assertEquals(Transpose.of(a), Transpose.of(s));
  }

  @Test
  void testSparseTranspose() {
    Tensor a = Tensors.fromString("{{1,0,3,0,0},{0,0,0,0,0},{0,2,0,0,4},{0,0,0,0,0}}");
    Tensor s = TensorToSparseArray.of(a);
    assertEquals(Transpose.of(a), Transpose.of(s));
  }

  @Test
  void testGenerateFail() {
    SparseArray.of(RealScalar.ZERO, 2, 3);
    assertEquals(SparseArray.of(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(SparseArray.of(GaussScalar.of(0, 7)), GaussScalar.of(0, 7));
    assertThrows(IllegalArgumentException.class, () -> SparseArray.of(RealScalar.ZERO, 2, -3));
    assertThrows(Throw.class, () -> SparseArray.of(RealScalar.ONE, 2, 3));
  }

  @Test
  void testPMulFullSparse() {
    Tensor tensor = Times.of(HilbertMatrix.of(3), LeviCivitaTensor.of(3));
    assertFalse(tensor instanceof SparseArray);
    assertTrue(tensor.toString().contains("SparseArray"));
  }

  @Test
  void testPMulSparseFull() {
    Tensor tensor = Times.of(IdentityMatrix.sparse(3), HilbertMatrix.of(3));
    assertFalse(tensor instanceof SparseArray);
    assertFalse(tensor.toString().contains("SparseArray"));
  }

  private final Distribution distribution = CategoricalDistribution.fromUnscaledPDF(Tensors.vector(10, 1, 0, 1, 1));
  private final RandomGenerator random = new Random(3);

  private Tensor _random(int... size) {
    return RandomVariate.of(distribution, random, size);
  }

  private static void _check(Tensor fa, Tensor fb) {
    assertFalse(fa instanceof SparseArray);
    assertFalse(fb instanceof SparseArray);
    Tensor sa = TensorToSparseArray.of(fa);
    Tensor sb = TensorToSparseArray.of(fb);
    assertInstanceOf(SparseArray.class, sa);
    assertInstanceOf(SparseArray.class, sb);
    Tensor fa_fb = fa.dot(fb);
    Tensor fa_sb = fa.dot(sb);
    Tensor sa_fb = sa.dot(fb);
    Tensor sa_sb = sa.dot(sb);
    assertTrue(sa_sb instanceof SparseArray || sa_sb instanceof Scalar);
    if (sa_sb instanceof SparseArray) {
      SparseArray sparse = (SparseArray) sa_sb;
      Nnz.of(sparse);
    }
    assertEquals(fa_fb, fa_sb);
    assertEquals(fa_sb, sa_fb);
    assertEquals(sa_fb, sa_sb);
  }

  @Test
  void testDot() {
    _check(_random(7), _random(7));
    _check(_random(8), _random(8, 2));
    _check(_random(5, 6), _random(6));
    _check(_random(2, 3, 4), _random(4, 5));
  }

  @Test
  void testDotZeroX() {
    _check(Array.zeros(7), _random(7));
    _check(Array.zeros(8), _random(8, 2));
    _check(Array.zeros(5, 6), _random(6));
    _check(Array.zeros(2, 3, 4), _random(4, 5));
  }

  @Test
  void testDotXZero() {
    _check(_random(7), Array.zeros(7));
    _check(_random(8), Array.zeros(8, 2));
    _check(_random(5, 6), Array.zeros(6));
    _check(_random(2, 3, 4), Array.zeros(4, 5));
  }

  @Test
  void testFallbackFail() {
    Tensor tensor = SparseArray.of(RealScalar.ZERO, 3);
    assertThrows(ArithmeticException.class, () -> tensor.divide(Quantity.of(0, "")));
    assertThrows(ArithmeticException.class, () -> tensor.divide(Quantity.of(0, "s*m")));
    assertThrows(Throw.class, () -> tensor.multiply(DoubleScalar.POSITIVE_INFINITY));
  }

  @Test
  void testArraysAsListSerialization() {
    assertDoesNotThrow(() -> Serialization.copy(Arrays.asList(3, 4, 5, 6)));
    assertThrows(Exception.class, () -> Serialization.copy(Arrays.asList(3, 4, 5, 6).subList(1, 3)));
  }

  @Test
  void testArrayListSerialization() {
    List<Integer> list = new ArrayList<>();
    list.add(3);
    list.add(4);
    list.add(5);
    list.add(6);
    assertDoesNotThrow(() -> Serialization.copy(list));
    assertThrows(Exception.class, () -> Serialization.copy(list.subList(1, 3)));
  }
}
