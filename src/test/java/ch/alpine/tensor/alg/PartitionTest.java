// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;

class PartitionTest {
  @Test
  public void testPartition() {
    Tensor s = Partition.of(Tensors.vector(1, 2, 3), 3);
    Tensor r = Tensors.fromString("{{1, 2, 3}}");
    assertEquals(s, r);
  }

  @Test
  public void testPartitionSizeLarge() {
    Tensor s = Partition.of(Tensors.vector(1, 2, 3), 10);
    assertEquals(s, Tensors.empty());
  }

  @Test
  public void testPartitionTwo() {
    Tensor s = Partition.of(Tensors.vector(1, 2, 3, 4), 2);
    Tensor r = Tensors.fromString("{{1, 2}, {3, 4}}");
    assertEquals(s, r);
  }

  @Test
  public void testPartitionOffset9() {
    Tensor s = Partition.of(Range.of(0, 9), 3, 2);
    Tensor r = Tensors.fromString("{{0, 1, 2}, {2, 3, 4}, {4, 5, 6}, {6, 7, 8}}");
    assertEquals(s, r);
  }

  @Test
  public void testPartitionOffset10() {
    Tensor s = Partition.of(Range.of(0, 10), 3, 2);
    Tensor r = Tensors.fromString("{{0, 1, 2}, {2, 3, 4}, {4, 5, 6}, {6, 7, 8}}");
    assertEquals(s, r);
  }

  @Test
  public void testPartitionOffset11() {
    Tensor s = Partition.of(Range.of(0, 11), 3, 2);
    Tensor r = Tensors.fromString("{{0, 1, 2}, {2, 3, 4}, {4, 5, 6}, {6, 7, 8}, {8, 9, 10}}");
    assertEquals(s, r);
  }

  @Test
  public void testPartitionInsufficient() {
    Tensor tensor = Partition.of(Tensors.vector(1, 2, 3, 4), 3);
    assertEquals(tensor, Tensors.fromString("{{1, 2, 3}}"));
  }

  @Test
  public void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> Partition.of(RealScalar.ONE, 2));
  }

  @Test
  public void testFailOffsetZero() {
    Tensor tensor = Tensors.vector(1, 2, 3, 4);
    assertThrows(IllegalArgumentException.class, () -> Partition.of(tensor, 2, 0));
  }

  @Test
  public void testFailOffsetNegative() {
    Tensor tensor = Tensors.vector(1, 2, 3, 4);
    for (int offset = -20; offset <= 0; ++offset)
      try {
        Tensor partition = Partition.of(tensor, 2, offset);
        System.out.println(offset);
        System.out.println(partition);
        fail();
      } catch (Exception exception) {
        // ---
      }
  }

  @Test
  public void testStreamFail() {
    assertThrows(TensorRuntimeException.class, () -> Partition.stream(Pi.VALUE, 3, 2));
    assertThrows(TensorRuntimeException.class, () -> Partition.stream(Pi.VALUE, 2, 3));
  }
}
