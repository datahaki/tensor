// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class PartitionTest extends TestCase {
  public void testPartition() {
    Tensor s = Partition.of(Tensors.vector(1, 2, 3), 3);
    Tensor r = Tensors.fromString("{{1, 2, 3}}");
    assertEquals(s, r);
  }

  public void testPartitionSizeLarge() {
    Tensor s = Partition.of(Tensors.vector(1, 2, 3), 10);
    assertEquals(s, Tensors.empty());
  }

  public void testPartitionTwo() {
    Tensor s = Partition.of(Tensors.vector(1, 2, 3, 4), 2);
    Tensor r = Tensors.fromString("{{1, 2}, {3, 4}}");
    assertEquals(s, r);
  }

  public void testPartitionOffset9() {
    Tensor s = Partition.of(Range.of(0, 9), 3, 2);
    Tensor r = Tensors.fromString("{{0, 1, 2}, {2, 3, 4}, {4, 5, 6}, {6, 7, 8}}");
    assertEquals(s, r);
  }

  public void testPartitionOffset10() {
    Tensor s = Partition.of(Range.of(0, 10), 3, 2);
    Tensor r = Tensors.fromString("{{0, 1, 2}, {2, 3, 4}, {4, 5, 6}, {6, 7, 8}}");
    assertEquals(s, r);
  }

  public void testPartitionOffset11() {
    Tensor s = Partition.of(Range.of(0, 11), 3, 2);
    Tensor r = Tensors.fromString("{{0, 1, 2}, {2, 3, 4}, {4, 5, 6}, {6, 7, 8}, {8, 9, 10}}");
    assertEquals(s, r);
  }

  public void testPartitionInsufficient() {
    Tensor tensor = Partition.of(Tensors.vector(1, 2, 3, 4), 3);
    assertEquals(tensor, Tensors.fromString("{{1, 2, 3}}"));
  }

  public void testScalarFail() {
    AssertFail.of(() -> Partition.of(RealScalar.ONE, 2));
  }

  public void testFailOffsetZero() {
    Tensor tensor = Tensors.vector(1, 2, 3, 4);
    AssertFail.of(() -> Partition.of(tensor, 2, 0));
  }

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

  public void testStreamFail() {
    AssertFail.of(() -> Partition.stream(Pi.VALUE, 3, 2));
    AssertFail.of(() -> Partition.stream(Pi.VALUE, 2, 3));
  }
}
