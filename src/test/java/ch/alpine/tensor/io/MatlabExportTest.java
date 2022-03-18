// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Entrywise;

public class MatlabExportTest {
  @Test
  public void testScalar() {
    Stream<String> stream = MatlabExport.of(ComplexScalar.of(2, 3));
    List<String> list = stream.collect(Collectors.toList());
    assertTrue(list.contains("a=2+3*I;"));
    // list.forEach(System.out::println);
  }

  @Test
  public void testScalar2() {
    Scalar s = Scalars.fromString("-1/41+73333/12*I");
    Stream<String> stream = MatlabExport.of(s);
    List<String> list = stream.collect(Collectors.toList());
    assertTrue(list.contains("a=-1/41+73333/12*I;"));
  }

  @Test
  public void testVector() {
    Stream<String> stream = MatlabExport.of(Tensors.vector(1, 2, 3));
    List<String> list = stream.collect(Collectors.toList());
    assertTrue(list.contains("a=zeros([3, 1]);"));
    // list.forEach(System.out::println);
  }

  @Test
  public void testMatrix() {
    Tensor m = HilbertMatrix.of(3, 4);
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply(m, m);
    Stream<String> stream = MatlabExport.of(matrix);
    List<String> list = stream.collect(Collectors.toList());
    assertTrue(list.contains("a=zeros([3, 4]);"));
  }

  @Test
  public void testLieAlgebras() {
    Tensor m = LeviCivitaTensor.of(3);
    Stream<String> stream = MatlabExport.of(m);
    List<String> list = stream.collect(Collectors.toList());
    assertTrue(list.contains("a=zeros([3, 3, 3]);"));
    assertTrue(list.size() < 12);
    // list.forEach(System.out::println);
  }

  @Test
  public void testUnits() {
    ScalarUnaryOperator scalarUnaryOperator = //
        scalar -> scalar instanceof Quantity //
            ? ((Quantity) scalar).value()
            : scalar;
    Tensor m = Tensors.fromString("{{1[m], 2[s^-1], 0[r]}, {3, 4[N], 7[rad]}}");
    Stream<String> stream = MatlabExport.of(m, sc -> scalarUnaryOperator.apply(sc).toString());
    List<String> list = stream.collect(Collectors.toList());
    assertTrue(list.contains("a=zeros([2, 3]);"));
    // list.forEach(System.out::println);
    assertTrue(list.contains("a(1)=1;"));
    assertTrue(list.contains("a(2)=3;"));
    assertTrue(list.contains("a(3)=2;"));
    assertTrue(list.contains("a(4)=4;"));
    assertTrue(list.contains("a(6)=7;"));
    assertFalse(list.stream().anyMatch(s -> s.startsWith("a(5)=")));
  }

  @Test
  public void testFail() {
    Tensor tensor = Tensors.fromString("{{1, 2}, {3, 4, 5}}");
    assertThrows(TensorRuntimeException.class, () -> MatlabExport.of(tensor));
  }
}
