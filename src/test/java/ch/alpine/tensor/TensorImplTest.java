// code by jph
package ch.alpine.tensor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class TensorImplTest extends TestCase {
  public void testUnmodifiable() {
    Tensor eye = IdentityMatrix.of(4).unmodifiable();
    AssertFail.of(() -> eye.flatten(0).forEach(e -> e.set(RealScalar.of(4), 2)));
  }

  public void testIteratorSize() {
    int count = 0;
    for (Tensor scalar : Tensors.vector(4, 2, 6, 3, 8).unmodifiable()) {
      scalar.add(scalar);
      ++count;
    }
    assertEquals(count, 5);
  }

  public void testCopy() {
    Tensor eye = IdentityMatrix.of(4).unmodifiable().copy();
    eye.flatten(0).forEach(e -> e.set(RealScalar.of(4), 2));
    assertEquals(eye.get(Tensor.ALL, 2), Tensors.vector(4, 4, 4, 4));
  }

  public void testIteratorUnmod() {
    Tensor eye = IdentityMatrix.of(4).unmodifiable();
    for (Tensor unit : eye)
      AssertFail.of(() -> unit.set(RealScalar.of(4), 2));
    assertEquals(eye.get(Tensor.ALL, 2), Tensors.vector(0, 0, 1, 0));
  }

  public void testIteratorUnmod2() {
    Tensor eye = IdentityMatrix.of(4).unmodifiable();
    Tensor rep = Tensors.empty();
    for (Tensor unit : eye)
      rep.append(unit);
    assertEquals(eye, rep);
  }

  public void testIteratorUnmod3() {
    Tensor eye = IdentityMatrix.of(4).unmodifiable();
    for (Tensor unit : eye)
      AssertFail.of(() -> unit.append(RealScalar.ZERO));
  }

  public void testIteratorRemove() {
    Tensor tensor = IdentityMatrix.of(4);
    for (Iterator<Tensor> iterator = tensor.iterator(); iterator.hasNext();) {
      iterator.next();
      iterator.remove();
    }
    assertEquals(tensor, Tensors.empty());
  }

  public void testIteratorCopy() {
    Tensor eye = IdentityMatrix.of(4).unmodifiable().copy();
    for (Tensor unit : eye)
      unit.set(RealScalar.of(4), 2);
    assertEquals(eye.get(Tensor.ALL, 0), UnitVector.of(4, 0));
    assertEquals(eye.get(Tensor.ALL, 1), UnitVector.of(4, 1));
    assertEquals(eye.get(Tensor.ALL, 2), Tensors.vector(4, 4, 4, 4));
    assertEquals(eye.get(Tensor.ALL, 3), UnitVector.of(4, 3));
  }

  public void testExtract() {
    Tensor eye = IdentityMatrix.of(4).unmodifiable();
    eye.extract(2, 4).set(RealScalar.of(4), 1);
  }

  public void testArrayList() {
    Tensor tensor = Tensor.of(Arrays.asList(RealScalar.of(2), RealScalar.of(3)).stream());
    TensorImpl tensorImpl = (TensorImpl) tensor;
    assertTrue(tensorImpl.list() instanceof ArrayList); // used in TensorParser
  }

  public void testSetFail() {
    Tensor matrix = HilbertMatrix.of(3, 3);
    AssertFail.of(() -> matrix.set(Array.zeros(2), Tensor.ALL, 1));
  }

  public void testNonPublic() {
    assertEquals(TensorImpl.class.getModifiers(), 0);
  }
}
