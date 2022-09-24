// code by jph
package ch.alpine.tensor.spa;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.LeviCivitaTensor;

class UnmodifiableSparseArrayTest {
  @Test
  void test() {
    SparseArray sparseArray = (SparseArray) LeviCivitaTensor.of(3).unmodifiable();
    assertSame(sparseArray, sparseArray.unmodifiable());
    assertInstanceOf(UnmodifiableSparseArray.class, sparseArray);
    assertThrows(Exception.class, () -> sparseArray.set(RealScalar.ONE, 0, 0, 0));
    assertThrows(Exception.class, () -> sparseArray.set(RealScalar.ONE::add, 0, 0, 0));
    assertTrue(sparseArray.stream().allMatch(UnmodifiableSparseArray.class::isInstance));
    for (Tensor x : sparseArray)
      assertInstanceOf(UnmodifiableSparseArray.class, x);
    assertInstanceOf(UnmodifiableSparseArray.class, sparseArray.block(List.of(0), List.of(1)));
  }
}
