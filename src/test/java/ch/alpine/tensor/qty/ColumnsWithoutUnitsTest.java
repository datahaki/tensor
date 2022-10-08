// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.io.ResourceData;

class ColumnsWithoutUnitsTest {
  @Test
  void testUniform() {
    Tensor tensor = ResourceData.of("/ch/alpine/tensor/io/dateobject.csv");
    ColumnsWithoutUnits columnsWithoutUnit = ColumnsWithoutUnits.of(tensor);
    assertEquals(columnsWithoutUnit.units(), List.of(Unit.ONE, Unit.ONE, Unit.of("m")));
    assertEquals(columnsWithoutUnit.result().get(2), Tensors.fromString("{2022-03-31T08:30:59, 2398749, 2233.2}"));
    assertEquals(Dimensions.of(tensor), Dimensions.of(columnsWithoutUnit.result()));
  }

  @Test
  void testSi() {
    Tensor tensor = ResourceData.of("/ch/alpine/tensor/io/qty/matrix_mixedunits.csv");
    assertThrows(Exception.class, () -> ColumnsWithoutUnits.of(tensor));
    ColumnsWithoutUnits columnsWithoutUnit = ColumnsWithoutUnits.of(tensor, UnitSystem.SI());
    assertEquals(columnsWithoutUnit.units(), List.of(Unit.ONE, Unit.ONE, Unit.of("m"), Unit.of("s")));
    assertEquals(columnsWithoutUnit.result().get(2), Tensors.fromString("{2022-03-31T08:30:59, 2398749, 2.2332, 39600}"));
    assertEquals(Dimensions.of(tensor), Dimensions.of(columnsWithoutUnit.result()));
  }
}
