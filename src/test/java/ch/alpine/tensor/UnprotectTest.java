// code by jph
package ch.alpine.tensor;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.io.StringScalarQ;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.qty.UnitQ;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class UnprotectTest extends TestCase {
  public void testUsingNullFail() {
    AssertFail.of(() -> Unprotect.using(null));
  }

  public void testUsingEmpty() {
    assertEquals(Unprotect.using(new LinkedList<>()), Tensors.empty());
    assertEquals(Unprotect.using(new LinkedList<>()), Tensors.unmodifiableEmpty());
    assertEquals(Unprotect.using(Arrays.asList()), Tensors.empty());
    assertEquals(Unprotect.using(Arrays.asList()), Tensors.unmodifiableEmpty());
  }

  public void testUsingListScalar() {
    List<Tensor> list = Arrays.asList(RealScalar.of(4), RealScalar.of(5));
    Tensor tensor = Unprotect.using(list);
    assertEquals(tensor, Tensors.vector(4, 5));
  }

  public void testUsingCopyOnWrite() {
    List<Tensor> list = new CopyOnWriteArrayList<>();
    Tensor tensor = Unprotect.using(list);
    tensor.append(RealScalar.of(2));
    tensor.append(RealScalar.of(6));
    assertEquals(tensor, Tensors.vector(2, 6));
  }

  public void testUsingNCopies() {
    Tensor tensor = Unprotect.using(Collections.nCopies(5, RealScalar.of(2)));
    assertEquals(tensor, Tensors.vector(2, 2, 2, 2, 2));
    AssertFail.of(() -> tensor.append(RealScalar.ONE));
  }

  public void testEmptyLinkedListUnmodifiable() {
    AssertFail.of(() -> Unprotect.using(new LinkedList<>()).unmodifiable().append(RealScalar.ZERO));
  }

  public void testByref() {
    Tensor beg = Tensors.vector(1, 2, 3);
    Tensor byref = Unprotect.byRef(beg, beg, beg);
    byref.set(RealScalar.ZERO, 0, 0);
    assertEquals(beg, Tensors.vector(0, 2, 3));
    assertEquals(byref, Tensors.fromString("{{0, 2, 3}, {0, 2, 3}, {0, 2, 3}}"));
  }

  public void testByrefFail() {
    Tensor beg = Tensors.vector(1, 2, 3);
    Tensor byref = Unprotect.byRef(beg, null, beg);
    byref.get(0);
    byref.get(2);
    AssertFail.of(() -> byref.get(1)); // invokes copy() on the entry
    AssertFail.of(() -> byref.extract(0, 3)); // invokes copy() on the entries
  }

  public void testDimension1() {
    assertTrue(Unprotect.dimension1(Tensors.vector(1, 2, 3)) == Scalar.LENGTH);
    assertTrue(Unprotect.dimension1(HilbertMatrix.of(2, 4)) == 4);
    assertTrue(Unprotect.dimension1(Array.zeros(2, 3, 4)) == 3);
  }

  public void testDimension1Hint() {
    Tensor tensor = Tensors.fromString("{{0, 2, 3}, {0, 2, 3, 5}, {{}}}");
    assertEquals(Unprotect.dimension1Hint(tensor), 3);
    AssertFail.of(() -> Unprotect.dimension1(tensor));
  }

  public void testDimension1Vector() {
    Tensor vector = Tensors.vector(1, 2, 3);
    assertEquals(Unprotect.dimension1(vector), Unprotect.dimension1Hint(vector));
  }

  public void testDimension1Empty() {
    int dim1 = Unprotect.dimension1(Tensors.empty());
    assertEquals(dim1, Scalar.LENGTH);
    assertEquals(dim1, Unprotect.dimension1Hint(Tensors.empty()));
  }

  public void testWithoutUnit() {
    assertEquals(Unprotect.withoutUnit(Pi.VALUE), Pi.VALUE);
    assertEquals(Unprotect.withoutUnit(Quantity.of(3, "h*km")), RealScalar.of(3));
    assertEquals(Unprotect.withoutUnit(Quantity.of(ComplexScalar.I, "h*km")), ComplexScalar.I);
    assertEquals(Unprotect.withoutUnit(StringScalar.of("abd123")), StringScalar.of("abd123"));
    assertEquals(Unprotect.withoutUnit(Quantity.of(3, "s")), RealScalar.of(3));
    assertEquals(Unprotect.withoutUnit(RealScalar.of(5)), RealScalar.of(5));
    assertEquals(Unprotect.withoutUnit(GaussScalar.of(3, 11)), GaussScalar.of(3, 11));
    AssertFail.of(() -> Unprotect.withoutUnit(null));
  }

  public void testIsUnitUnique() {
    assertTrue(Unprotect.isUnitUnique(Tensors.fromString("{{1,2,3}}")));
    assertTrue(Unprotect.isUnitUnique(Tensors.fromString("{{1[m],2[m],3[m]}}")));
    assertFalse(Unprotect.isUnitUnique(Tensors.fromString("{{1[m],2,3[m]}}")));
    assertFalse(Unprotect.isUnitUnique(Tensors.fromString("{{1[m],2[kg],3[m]}}")));
  }

  public void testGetUnitUnique() {
    assertEquals(Unprotect.getUnitUnique(Range.of(1, 10)), Unit.of(""));
    assertEquals(Unprotect.getUnitUnique(Tensors.fromString("{{1[m],2[m],3[m]}}")), Unit.of("m"));
    assertEquals(Unprotect.getUnitUnique(DiagonalMatrix.of(3, Quantity.of(1, "s^2*m^-1"))), Unit.of("s^2*m^-1"));
  }

  public void testOne() {
    Tensor tensor = Tensors.vector(2, 3, 4);
    Unit unit = Unprotect.getUnitUnique(tensor);
    assertTrue(UnitQ.isOne(unit));
  }

  public void testUnprotectZeroProject() {
    assertEquals(Unprotect.zeroDropUnit(Quantity.of(0, "m")), RealScalar.ZERO);
    assertEquals(Unprotect.zeroDropUnit(Quantity.of(0, "")), RealScalar.ZERO);
    assertEquals(Unprotect.zeroDropUnit(Quantity.of(1, "A")), Quantity.of(1, "A"));
    AssertFail.of(() -> Unprotect.zeroDropUnit(null));
  }

  public void testGetUnitUniqueFail1() {
    AssertFail.of(() -> Unprotect.getUnitUnique(Tensors.fromString("{{1[m],2[s],3[m]}}")));
  }

  public void testGetUnitUniqueFail2() {
    Tensor tensor = Tensors.fromString("{1[m], 2[s]}");
    assertFalse(StringScalarQ.any(tensor));
    AssertFail.of(() -> Unprotect.getUnitUnique(tensor));
  }

  public void testDimension1Fail() {
    Tensor unstruct = Tensors.fromString("{{-1, 0, 1, 2}, {3, 4, 5}}");
    assertEquals(unstruct.length(), 2);
    AssertFail.of(() -> Unprotect.dimension1(unstruct));
  }

  public void testDimension1HintFail() {
    AssertFail.of(() -> Unprotect.dimension1(RealScalar.ONE));
    AssertFail.of(() -> Unprotect.dimension1Hint(RealScalar.ONE));
  }
}
