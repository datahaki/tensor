// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;

class TableBuilderTest {
  @Test
  void testEmpty() {
    TableBuilder tableBuilder = new TableBuilder();
    assertEquals(tableBuilder.getRowCount(), 0);
    assertEquals(tableBuilder.getTable(), Tensors.empty());
  }

  @Test
  void testAppendRow() {
    TableBuilder tableBuilder = new TableBuilder();
    tableBuilder.appendRow(RealScalar.ONE, Tensors.vector(2, 3, 4), RealScalar.of(5));
    assertEquals(tableBuilder.getRowCount(), 1);
    tableBuilder.appendRow(RealScalar.of(-2), RealScalar.of(6), Tensors.vector(0, 7));
    assertEquals(tableBuilder.getRowCount(), 2);
    tableBuilder.appendRow();
    assertEquals(tableBuilder.getRowCount(), 3);
    tableBuilder.appendRow(RealScalar.of(100));
    assertEquals(tableBuilder.getRowCount(), 4);
    tableBuilder.appendRow(Tensors.vector(1, 2, 3));
    assertEquals(tableBuilder.getTable().stream().count(), 5);
    Tensor table = tableBuilder.getTable();
    assertEquals(table, Tensors.fromString("{{1, 2, 3, 4, 5}, {-2, 6, 0, 7}, {}, {100}, {1, 2, 3}}"));
  }

  @Test
  void testModify() {
    TableBuilder tableBuilder = new TableBuilder();
    tableBuilder.appendRow(Tensors.vector(1, 2, 3, 4));
    Tensor table = tableBuilder.getTable();
    assertThrows(UnsupportedOperationException.class, () -> table.set(RealScalar.of(99), 0, 2));
    assertEquals(table.get(0), Range.of(1, 5));
  }

  @Test
  void testUnmodifiableWrapper() {
    TableBuilder tableBuilder = new TableBuilder();
    Tensor tensor = tableBuilder.getTable();
    assertEquals(tensor.length(), 0);
    tableBuilder.appendRow();
    assertEquals(tensor.length(), 1);
    tableBuilder.appendRow();
    assertEquals(tensor.length(), 2);
    assertEquals(tableBuilder.getRowCount(), 2);
  }

  @Test
  void testFail() {
    TableBuilder tableBuilder = new TableBuilder();
    assertEquals(tableBuilder.getRowCount(), 0);
    assertThrows(NullPointerException.class, () -> tableBuilder.appendRow((Tensor[]) null));
    assertEquals(tableBuilder.getRowCount(), 0);
  }
}
