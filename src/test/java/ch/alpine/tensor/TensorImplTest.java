// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.num.Pi;

class TensorImplTest {
  @Test
  public void testUnmodifiable() {
    Tensor eye = IdentityMatrix.of(4).unmodifiable();
    assertThrows(UnsupportedOperationException.class, () -> eye.flatten(0).forEach(e -> e.set(RealScalar.of(4), 2)));
  }

  @Test
  public void testIteratorSize() {
    int count = 0;
    for (Tensor scalar : Tensors.vector(4, 2, 6, 3, 8).unmodifiable()) {
      scalar.add(scalar);
      ++count;
    }
    assertEquals(count, 5);
  }

  @Test
  public void testCopy() {
    Tensor eye = IdentityMatrix.of(4).unmodifiable().copy();
    eye.flatten(0).forEach(e -> e.set(RealScalar.of(4), 2));
    assertEquals(eye.get(Tensor.ALL, 2), Tensors.vector(4, 4, 4, 4));
  }

  @Test
  public void testIteratorUnmod() {
    Tensor eye = IdentityMatrix.of(4).unmodifiable();
    for (Tensor unit : eye)
      assertThrows(UnsupportedOperationException.class, () -> unit.set(RealScalar.of(4), 2));
    assertEquals(eye.get(Tensor.ALL, 2), Tensors.vector(0, 0, 1, 0));
  }

  @Test
  public void testIteratorUnmod2() {
    Tensor eye = IdentityMatrix.of(4).unmodifiable();
    Tensor rep = Tensors.empty();
    for (Tensor unit : eye)
      rep.append(unit);
    assertEquals(eye, rep);
  }

  @Test
  public void testIteratorUnmod3() {
    Tensor eye = IdentityMatrix.of(4).unmodifiable();
    for (Tensor unit : eye)
      assertThrows(UnsupportedOperationException.class, () -> unit.append(RealScalar.ZERO));
  }

  @Test
  public void testIteratorRemove() {
    Tensor tensor = IdentityMatrix.of(4);
    for (Iterator<Tensor> iterator = tensor.iterator(); iterator.hasNext();) {
      iterator.next();
      iterator.remove();
    }
    assertEquals(tensor, Tensors.empty());
  }

  @Test
  public void testHashCode() {
    List<Tensor> list = new ArrayList<>();
    list.add(RealScalar.ONE);
    list.add(Tensors.vector(2, 3, 4));
    list.add(Pi.VALUE);
    list.add(HilbertMatrix.of(2, 3));
    int hashCode1 = list.hashCode();
    int hashCode2 = Unprotect.using(list).hashCode();
    int hashCode3 = Tensor.of(list.stream()).hashCode();
    assertEquals(hashCode1, hashCode2);
    assertEquals(hashCode2, hashCode3);
  }

  @Test
  public void testIteratorCopy() {
    Tensor eye = IdentityMatrix.of(4).unmodifiable().copy();
    for (Tensor unit : eye)
      unit.set(RealScalar.of(4), 2);
    assertEquals(eye.get(Tensor.ALL, 0), UnitVector.of(4, 0));
    assertEquals(eye.get(Tensor.ALL, 1), UnitVector.of(4, 1));
    assertEquals(eye.get(Tensor.ALL, 2), Tensors.vector(4, 4, 4, 4));
    assertEquals(eye.get(Tensor.ALL, 3), UnitVector.of(4, 3));
  }

  @Test
  public void testExtract() {
    Tensor eye = IdentityMatrix.of(4).unmodifiable();
    eye.extract(2, 4).set(RealScalar.of(4), 1);
  }

  @Test
  public void testExtract2() {
    Tensor vector = Tensors.vector(1, 2, 3);
    Tensor slevel = vector.extract(1, 3);
    slevel.set(RealScalar.ONE::add, 0);
    assertEquals(vector, Tensors.vector(1, 2, 3));
  }

  @Test
  public void testArrayList() {
    List<Tensor> list = Arrays.asList(RealScalar.of(2), RealScalar.of(3)).stream().map(Tensor.class::cast).collect(Collectors.toList());
    assertInstanceOf(ArrayList.class, list); // used in TensorParser
  }

  @Test
  public void testSetFail() {
    Tensor matrix = HilbertMatrix.of(3, 3);
    assertThrows(IllegalArgumentException.class, () -> matrix.set(Array.zeros(2), Tensor.ALL, 1));
  }

  @Test
  public void testNonPublic() {
    assertEquals(TensorImpl.class.getModifiers(), 0);
  }
}
