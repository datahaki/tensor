// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.sca.win.BlackmanHarrisWindow;
import ch.alpine.tensor.sca.win.HammingWindow;
import ch.alpine.tensor.sca.win.HannWindow;
import ch.alpine.tensor.sca.win.NuttallWindow;
import ch.alpine.tensor.sca.win.TukeyWindow;
import ch.alpine.tensor.sca.win.WindowFunctions;

class SpectrogramArrayTest {
  @Test
  public void testDimension() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tensorUnaryOperator = Serialization.copy(SpectrogramArray.of(8, 8));
    Tensor tensor = tensorUnaryOperator.apply(Range.of(0, 128));
    assertEquals(Dimensions.of(tensor), Arrays.asList(16, 8));
    assertTrue(tensorUnaryOperator.toString().startsWith("SpectrogramArray["));
  }

  @Test
  public void testMathematicaDefault() {
    Tensor tensor = Tensor.of(IntStream.range(0, 200) //
        .mapToDouble(i -> Math.cos(i * 0.25 + (i / 20.0) * (i / 20.0))) //
        .mapToObj(RealScalar::of));
    for (WindowFunctions windowFunctions : WindowFunctions.values()) {
      int windowLength = Unprotect.dimension1(SpectrogramArray.of(tensor, windowFunctions.get()));
      assertEquals(windowLength, 32);
    }
  }

  @Test
  public void testQuantity() {
    Tensor tensor = Tensor.of(IntStream.range(0, 500) //
        .mapToDouble(i -> Math.cos(i * 0.25 + (i / 20.0) * (i / 20.0))) //
        .mapToObj(d -> Quantity.of(d, "m")));
    Tensor array = SpectrogramArray.of(tensor);
    ScalarUnaryOperator suo = QuantityMagnitude.SI().in("km");
    Tensor array2 = array.map(suo);
    int windowLength = Unprotect.dimension1(array2);
    assertEquals(windowLength, 32);
    Tensor matrix = SpectrogramArray.half_abs(tensor, HannWindow.FUNCTION);
    MatrixQ.require(matrix);
    matrix.map(suo);
  }

  @Test
  public void testStaticOps() {
    SpectrogramArray.of(Quantity.of(1, "s"), Quantity.of(100, "s^-1"), HannWindow.FUNCTION);
    SpectrogramArray.of(Quantity.of(1, "s"), Quantity.of(100, "s^-1"), 10, TukeyWindow.FUNCTION);
  }

  @Test
  public void testStaticOpsFail() {
    assertThrows(IllegalArgumentException.class, () -> SpectrogramArray.of(Quantity.of(0, "s"), Quantity.of(100, "s^-1"), NuttallWindow.FUNCTION));
    assertThrows(IllegalArgumentException.class, () -> SpectrogramArray.of(Quantity.of(1, "s"), Quantity.of(0.100, "s^-1"), BlackmanHarrisWindow.FUNCTION));
  }

  @RepeatedTest(7)
  public void testPreallocate(RepetitionInfo repetitionInfo) {
    int windowLength = repetitionInfo.getCurrentRepetition();
    for (int offset = 1; offset <= windowLength; ++offset) {
      TensorUnaryOperator tensorUnaryOperator = SpectrogramArray.of(windowLength, offset);
      for (int length = 10; length < 20; ++length) {
        Tensor signal = Range.of(0, length);
        tensorUnaryOperator.apply(signal);
      }
    }
  }

  @Test
  public void testHighestOneBit() {
    int highestOneBit = Integer.highestOneBit(64 + 3);
    assertEquals(highestOneBit, 64);
  }

  @Test
  public void testIterate() {
    List<Integer> list = IntStream.iterate(0, i -> i + 10).limit(10).boxed().collect(Collectors.toList());
    assertEquals(list, Arrays.asList(0, 10, 20, 30, 40, 50, 60, 70, 80, 90));
  }

  @Test
  public void testFailWindowLength() {
    assertThrows(IllegalArgumentException.class, () -> SpectrogramArray.of(0, 8));
  }

  @Test
  public void testFailWindowLengthOffset() {
    assertThrows(IllegalArgumentException.class, () -> SpectrogramArray.of(4, 8));
  }

  @Test
  public void testFailOffset() {
    assertThrows(IllegalArgumentException.class, () -> SpectrogramArray.of(4, 0));
  }

  @Test
  public void testNullFail() {
    assertThrows(NullPointerException.class, () -> SpectrogramArray.half_abs(null, HammingWindow.FUNCTION));
  }

  @Test
  public void testDimensionsFail() {
    TensorUnaryOperator tensorUnaryOperator = SpectrogramArray.of(32, 8);
    assertThrows(TensorRuntimeException.class, () -> tensorUnaryOperator.apply(RealScalar.ONE));
    assertThrows(ClassCastException.class, () -> tensorUnaryOperator.apply(HilbertMatrix.of(32)));
  }

  @Test
  public void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> SpectrogramArray.of(RealScalar.ONE));
  }

  @Test
  public void testMatrixFail() {
    assertThrows(ClassCastException.class, () -> SpectrogramArray.of(HilbertMatrix.of(32)));
  }
}
