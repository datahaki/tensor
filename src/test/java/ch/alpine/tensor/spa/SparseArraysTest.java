// code by jph
package ch.alpine.tensor.spa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.EmpiricalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SparseArraysTest extends TestCase {
  public void testSparseBinary() {
    Tensor a = Tensors.fromString("{{1,0,3,0,0},{0,0,0,0,0},{0,2,0,0,4}}");
    Tensor b = Tensors.fromString("{{3,0,0,7,0},{0,0,0,0,0},{0,4,0,3,0}}");
    Tensor sa = SparseArrays.of(a, RealScalar.ZERO);
    Tensor sb = SparseArrays.of(b, RealScalar.ZERO);
    {
      Tensor r_add = sa.add(sb);
      assertEquals(a.add(b), r_add);
      assertEquals(a.add(sb), r_add);
      assertEquals(sa.add(b), r_add);
      assertTrue(r_add instanceof SparseArray);
    }
    {
      Tensor r_sub = sa.subtract(sb);
      assertEquals(a.subtract(b), r_sub);
      assertEquals(a.subtract(sb), r_sub);
      assertEquals(sa.subtract(b), r_sub);
      assertTrue(r_sub instanceof SparseArray);
    }
    {
      Tensor r_pml = sa.pmul(sb);
      assertEquals(a.pmul(b), r_pml);
      assertEquals(a.pmul(sb), r_pml);
      assertEquals(sa.pmul(b), r_pml);
      assertTrue(r_pml instanceof SparseArray);
    }
  }

  public void testSparseWedge() {
    Tensor a = Tensors.fromString("{{1,0,3,0,0},{0,0,0,0,0},{0,2,0,0,4},{0,0,0,0,0},{0,0,0,0,0}}");
    Tensor s = SparseArrays.of(a, RealScalar.ZERO);
    assertEquals(TensorWedge.of(a), TensorWedge.of(s));
    assertEquals(Transpose.of(a), Transpose.of(s));
  }

  public void testSparseTranspose() {
    Tensor a = Tensors.fromString("{{1,0,3,0,0},{0,0,0,0,0},{0,2,0,0,4},{0,0,0,0,0}}");
    Tensor s = SparseArrays.of(a, RealScalar.ZERO);
    assertEquals(Transpose.of(a), Transpose.of(s));
  }

  public void testGenerateFail() {
    SparseArray.of(RealScalar.ZERO, 2, 3);
    AssertFail.of(() -> SparseArray.of(RealScalar.ZERO));
    AssertFail.of(() -> SparseArray.of(RealScalar.ZERO, 2, -3));
    AssertFail.of(() -> SparseArray.of(RealScalar.ONE, 2, 3));
  }

  private final Distribution distribution = EmpiricalDistribution.fromUnscaledPDF(Tensors.vector(10, 1, 0, 1, 1));
  private Random random = new Random(3);

  private Tensor _random(int... size) {
    return RandomVariate.of(distribution, random, size);
  }

  private static void _check(Tensor fa, Tensor fb) {
    assertFalse(fa instanceof SparseArray);
    assertFalse(fb instanceof SparseArray);
    Tensor sa = SparseArrays.of(fa, RealScalar.ZERO);
    Tensor sb = SparseArrays.of(fb, RealScalar.ZERO);
    assertTrue(sa instanceof SparseArray);
    assertTrue(sb instanceof SparseArray);
    Tensor fa_fb = fa.dot(fb);
    Tensor fa_sb = fa.dot(sb);
    Tensor sa_fb = sa.dot(fb);
    Tensor sa_sb = sa.dot(sb);
    assertTrue(sa_sb instanceof SparseArray || sa_sb instanceof Scalar);
    assertEquals(fa_fb, fa_sb);
    assertEquals(fa_sb, sa_fb);
    assertEquals(sa_fb, sa_sb);
  }

  public void testDot() {
    _check(_random(7), _random(7));
    _check(_random(8), _random(8, 2));
    _check(_random(5, 6), _random(6));
    _check(_random(2, 3, 4), _random(4, 5));
  }

  public void testDotZeroX() {
    _check(Array.zeros(7), _random(7));
    _check(Array.zeros(8), _random(8, 2));
    _check(Array.zeros(5, 6), _random(6));
    _check(Array.zeros(2, 3, 4), _random(4, 5));
  }

  public void testDotXZero() {
    _check(_random(7), Array.zeros(7));
    _check(_random(8), Array.zeros(8, 2));
    _check(_random(5, 6), Array.zeros(6));
    _check(_random(2, 3, 4), Array.zeros(4, 5));
  }

  public void testFallbackFail() {
    Tensor tensor = SparseArray.of(3);
    AssertFail.of(() -> tensor.multiply(Quantity.of(7, "s*m")));
    AssertFail.of(() -> tensor.divide(Quantity.of(7, "s*m")));
  }

  public void testArraysAsListSerialization() throws ClassNotFoundException, IOException {
    Serialization.copy(Arrays.asList(3, 4, 5, 6));
    try {
      Serialization.copy(Arrays.asList(3, 4, 5, 6).subList(1, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testArrayListSerialization() throws ClassNotFoundException, IOException {
    List<Integer> list = new ArrayList<>();
    list.add(3);
    list.add(4);
    list.add(5);
    list.add(6);
    Serialization.copy(list);
    try {
      Serialization.copy(list.subList(1, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
