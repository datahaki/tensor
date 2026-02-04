// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;

class UnprotectTest {
  @Test
  void testUsingNullFail() {
    assertThrows(NullPointerException.class, () -> Unprotect.using(null));
  }

  @Test
  void testUsingEmpty() {
    assertEquals(Unprotect.using(new LinkedList<>()), Tensors.empty());
    assertEquals(Unprotect.using(new LinkedList<>()), Tensors.unmodifiableEmpty());
    assertEquals(Unprotect.using(List.of()), Tensors.empty());
    assertEquals(Unprotect.using(List.of()), Tensors.unmodifiableEmpty());
  }

  @Test
  void testUsingListScalar() {
    List<Tensor> list = Arrays.asList(RealScalar.of(4), RealScalar.of(5));
    Tensor tensor = Unprotect.using(list);
    assertEquals(tensor, Tensors.vector(4, 5));
  }

  @Test
  void testUsingCopyOnWrite() {
    List<Tensor> list = new CopyOnWriteArrayList<>();
    Tensor tensor = Unprotect.using(list);
    tensor.append(RealScalar.of(2));
    tensor.append(RealScalar.of(6));
    assertEquals(tensor, Tensors.vector(2, 6));
  }

  @Test
  void testUsingNCopies() {
    Tensor tensor = Unprotect.using(Collections.nCopies(5, RealScalar.of(2)));
    assertEquals(tensor, Tensors.vector(2, 2, 2, 2, 2));
    assertThrows(UnsupportedOperationException.class, () -> tensor.append(RealScalar.ONE));
  }

  @Test
  void testEmptyLinkedListUnmodifiable() {
    assertThrows(UnsupportedOperationException.class, () -> Unprotect.using(new LinkedList<>()).unmodifiable().append(RealScalar.ZERO));
  }

  @Test
  void testByref() {
    Tensor beg = Tensors.vector(1, 2, 3);
    Tensor byref = Unprotect.byRef(beg, beg, beg);
    byref.set(RealScalar.ZERO, 0, 0);
    assertEquals(beg, Tensors.vector(0, 2, 3));
    assertEquals(byref, Tensors.fromString("{{0, 2, 3}, {0, 2, 3}, {0, 2, 3}}"));
  }

  @Test
  void testByrefFail() {
    Tensor beg = Tensors.vector(1, 2, 3);
    Tensor byref = Unprotect.byRef(beg, null, beg);
    byref.get(0);
    byref.get(2);
    assertThrows(NullPointerException.class, () -> byref.get(1)); // invokes copy() on the entry
    assertThrows(NullPointerException.class, () -> byref.extract(0, 3)); // invokes copy() on the entries
  }

  @Test
  void testDimension1() {
    assertEquals(Unprotect.dimension1(Tensors.vector(1, 2, 3)), Scalar.LENGTH);
    assertEquals(Unprotect.dimension1(HilbertMatrix.of(2, 4)), 4);
    assertEquals(Unprotect.dimension1(Array.zeros(2, 3, 4)), 3);
  }

  @Test
  void testDimension1Hint() {
    Tensor tensor = Tensors.fromString("{{0, 2, 3}, {0, 2, 3, 5}, {{}}}");
    assertEquals(Unprotect.dimension1Hint(tensor), 3);
    assertThrows(Throw.class, () -> Unprotect.dimension1(tensor));
  }

  @Test
  void testDimension1Vector() {
    Tensor vector = Tensors.vector(1, 2, 3);
    assertEquals(Unprotect.dimension1(vector), Unprotect.dimension1Hint(vector));
  }

  @Test
  void testDimension1Empty() {
    int dim1 = Unprotect.dimension1(Tensors.empty());
    assertEquals(dim1, Scalar.LENGTH);
    assertEquals(dim1, Unprotect.dimension1Hint(Tensors.empty()));
  }

  @Test
  void testWithoutUnit() {
    assertEquals(Unprotect.withoutUnit(Pi.VALUE), Pi.VALUE);
    assertEquals(Unprotect.withoutUnit(Quantity.of(3, "h*km")), RealScalar.of(3));
    assertEquals(Unprotect.withoutUnit(Quantity.of(ComplexScalar.I, "h*km")), ComplexScalar.I);
    assertEquals(Unprotect.withoutUnit(StringScalar.of("abd123")), StringScalar.of("abd123"));
    assertEquals(Unprotect.withoutUnit(Quantity.of(3, "s")), RealScalar.of(3));
    assertEquals(Unprotect.withoutUnit(RealScalar.of(5)), RealScalar.of(5));
    assertEquals(Unprotect.withoutUnit(GaussScalar.of(3, 11)), GaussScalar.of(3, 11));
    assertThrows(NullPointerException.class, () -> Unprotect.withoutUnit(null));
  }

  @Test
  void testIsUnitUnique() {
    assertTrue(Unprotect.isUnitUnique(Tensors.fromString("{{1,2,3}}")));
    assertTrue(Unprotect.isUnitUnique(Tensors.fromString("{{1[m],2[m],3[m]}}")));
    assertFalse(Unprotect.isUnitUnique(Tensors.fromString("{{1[m],2,3[m]}}")));
    assertFalse(Unprotect.isUnitUnique(Tensors.fromString("{{1[m],2[kg],3[m]}}")));
  }

  @Test
  void testDimension1Fail() {
    Tensor unstruct = Tensors.fromString("{{-1, 0, 1, 2}, {3, 4, 5}}");
    assertEquals(unstruct.length(), 2);
    assertThrows(Throw.class, () -> Unprotect.dimension1(unstruct));
  }

  @Test
  void testQuantity() {
    Scalar p = Quantity.of(123, "");
    Scalar q = Quantity.of(123, "m");
    assertEquals(Unprotect.negateUnit(p), p);
    assertEquals(Unprotect.negateUnit(q), Quantity.of(123, "m^-1"));
    assertEquals(Unprotect.zero_negateUnit(p), p.zero());
    assertEquals(Unprotect.zero_negateUnit(q), Quantity.of(0, "m^-1"));
  }

  @Test
  void testDimension1HintFail() {
    assertThrows(Throw.class, () -> Unprotect.dimension1(RealScalar.ONE));
    assertThrows(Throw.class, () -> Unprotect.dimension1Hint(RealScalar.ONE));
  }

  @Test
  void testFile() {
    File file = Unprotect.file("/ch/alpine/tensor/io/basic.mathematica");
    assertTrue(file.isFile());
  }

  @Test
  void testDirectory() {
    File file = Unprotect.file("/ch/alpine/tensor/io");
    assertTrue(file.isDirectory());
  }

  @Test
  void testFileFail() {
    assertThrows(Exception.class, () -> Unprotect.file("/does/not/exist.txt"));
  }

  @Test
  void testFile2Fail() {
    assertThrows(Exception.class, () -> Unprotect.class.getResource("/does/not/exist.txt").getFile());
  }
}
