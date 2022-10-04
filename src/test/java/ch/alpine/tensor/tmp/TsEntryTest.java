// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;

class TsEntryTest {
  @Test
  void test() {
    TsEntry tsEntry = new TsEntry(Pi.VALUE, Tensors.vector(1, 2, 3));
    tsEntry.hashCode();
    tsEntry.toString();
    assertEquals(tsEntry, new TsEntry(Pi.VALUE, Tensors.vector(1, 2, 3)));
    assertNotEquals(tsEntry, new TsEntry(Pi.VALUE, Tensors.vector(1, 3, 3)));
    assertNotEquals(tsEntry, new TsEntry(Pi.TWO, Tensors.vector(1, 2, 3)));
    assertNotEquals(tsEntry, Pi.VALUE);
  }
}
