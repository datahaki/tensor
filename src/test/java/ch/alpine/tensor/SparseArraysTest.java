// code by jph
package ch.alpine.tensor;

import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.lie.TensorWedge;
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
}
