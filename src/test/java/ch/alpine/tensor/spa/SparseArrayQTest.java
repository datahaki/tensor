// code by jph
package ch.alpine.tensor.spa;

import java.util.Arrays;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SparseArrayQTest extends TestCase {
  public void testSimple() {
    SparseArrayQ.require(LeviCivitaTensor.of(2));
    assertFalse(SparseArrayQ.of(Tensors.empty()));
  }

  public void testAddOne() {
    Tensor tensor = LeviCivitaTensor.of(2);
    Tensor result = tensor.map(RealScalar.ONE::add);
    assertEquals(result, Tensors.fromString("{{1, 2}, {0, 1}}"));
  }

  public void testVector() {
    Tensor tensor = LeviCivitaTensor.of(3);
    Tensor result = tensor.map(s -> Tensors.of(s, s));
    assertEquals(Dimensions.of(result), Arrays.asList(3, 3, 3, 2));
    SparseArrayQ.require(result);
    assertTrue(result.toString().contains("{0, 1, 2, 0}"));
  }

  public void testVector2() {
    Tensor tensor = LeviCivitaTensor.of(3);
    Tensor result = tensor.map(s -> Tensors.of(s, s.one(), s, RationalScalar.HALF));
    assertEquals(Dimensions.of(result), Arrays.asList(3, 3, 3, 4));
    assertTrue(SparseArrayQ.of(result));
  }

  public void testUnit() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}").map(s -> Quantity.of(s, "m"));
    SparseArray sparse = (SparseArray) TestHelper.of(tensor);
    assertEquals(sparse.fallback(), Quantity.of(0, "m"));
  }

  public void testMapUnit() {
    Tensor tensor = LeviCivitaTensor.of(3);
    Tensor result = tensor.map(s -> Tensors.of(Quantity.of(s, "m"), Quantity.of(s, "m")));
    SparseArray sparse = (SparseArray) result;
    assertEquals(sparse.fallback(), Quantity.of(0, "m"));
  }

  public void testDot() {
    Tensor t1 = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}").map(s -> Quantity.of(s, "m"));
    Tensor t2 = Tensors.fromString("{0,1,3,0,0}").map(s -> Quantity.of(s, "s^-1"));
    assertEquals(t1.dot(t2), Tensors.fromString("{9[m*s^-1], 30[m*s^-1], 29[m*s^-1]}"));
  }

  public void testId() {
    int n = 3;
    Tensor t1 = IdentityMatrix.sparse(n).map(s -> Quantity.of(s, "m"));
    Tensor t2 = IdentityMatrix.sparse(n).map(s -> Quantity.of(s, "kg"));
    Tensor t3 = t1.dot(t2);
    Tensor t4 = IdentityMatrix.sparse(n).map(s -> Quantity.of(s, "kg*m"));
    assertEquals(t3, t4);
  }
}
