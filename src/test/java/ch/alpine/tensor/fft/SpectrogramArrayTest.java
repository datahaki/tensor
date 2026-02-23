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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.sca.win.BlackmanHarrisWindow;
import ch.alpine.tensor.sca.win.HannWindow;
import ch.alpine.tensor.sca.win.NuttallWindow;
import ch.alpine.tensor.sca.win.TukeyWindow;
import ch.alpine.tensor.sca.win.WindowFunctions;

class SpectrogramArrayTest {
  @Test
  void testOperator() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tuo = SpectrogramArray.of(Fourier.INVERSE::transform, 1345, 300, null);
    Serialization.copy(tuo);
    tuo.apply(TestHelper.signal());
    assertTrue(tuo.toString().startsWith("SpectrogramArray["));
  }

  @Test
  void testDimension() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tensorUnaryOperator = Serialization.copy(SpectrogramArray.of(Fourier.FORWARD::transform, 8, 8, null));
    Tensor tensor = tensorUnaryOperator.apply(Range.of(0, 128));
    assertEquals(Dimensions.of(tensor), Arrays.asList(16, 8));
    // assertTrue(tensorUnaryOperator.toString().startsWith("SpectrogramArray["));
  }

  @Test
  void testMathematicaDefault() {
    Tensor tensor = Tensor.of(IntStream.range(0, 200) //
        .mapToDouble(i -> Math.cos(i * 0.25 + (i / 20.0) * (i / 20.0))) //
        .mapToObj(RealScalar::of));
    for (WindowFunctions windowFunctions : WindowFunctions.values()) {
      int windowLength = Unprotect.dimension1(SpectrogramArray.of(Fourier.FORWARD::transform, null, null, windowFunctions.get()).apply(tensor));
      assertEquals(windowLength, 32);
    }
  }

  @Test
  void testQuantity() {
    Tensor tensor = Tensor.of(IntStream.range(0, 500) //
        .mapToDouble(i -> Math.cos(i * 0.25 + (i / 20.0) * (i / 20.0))) //
        .mapToObj(d -> Quantity.of(d, "m")));
    Tensor array = SpectrogramArrays.FOURIER.operator().apply(tensor);
    ScalarUnaryOperator suo = QuantityMagnitude.SI().in("km");
    Tensor array2 = array.maps(suo);
    int windowLength = Unprotect.dimension1(array2);
    assertEquals(windowLength, 32);
    Tensor matrix = SpectrogramArrays.FOURIER.operator().half_abs(tensor);
    MatrixQ.require(matrix);
    matrix.maps(suo);
  }

  @Test
  void testStaticOps() {
    SpectrogramArrays.FOURIER.operator().of(Quantity.of(1, "s"), Quantity.of(100, "s^-1"), HannWindow.FUNCTION);
    SpectrogramArrays.FOURIER.operator().of(Quantity.of(1, "s"), Quantity.of(100, "s^-1"), 10, TukeyWindow.FUNCTION);
  }

  @Disabled
  @Test
  void testStaticOpsFail() {
    assertThrows(IllegalArgumentException.class,
        () -> SpectrogramArrays.FOURIER.operator().of(Quantity.of(0, "s"), Quantity.of(100, "s^-1"), NuttallWindow.FUNCTION));
    assertThrows(IllegalArgumentException.class,
        () -> SpectrogramArrays.FOURIER.operator().of(Quantity.of(1, "s"), Quantity.of(0.100, "s^-1"), BlackmanHarrisWindow.FUNCTION));
  }

  @RepeatedTest(7)
  void testPreallocate(RepetitionInfo repetitionInfo) {
    int windowLength = repetitionInfo.getCurrentRepetition();
    for (int offset = 1; offset <= windowLength; ++offset) {
      TensorUnaryOperator tensorUnaryOperator = SpectrogramArray.of(Fourier.FORWARD::transform, windowLength, offset, null);
      for (int length = 10; length < 20; ++length) {
        Tensor signal = Range.of(0, length);
        tensorUnaryOperator.apply(signal);
      }
    }
  }

  @Test
  void testHighestOneBit() {
    int highestOneBit = Integer.highestOneBit(64 + 3);
    assertEquals(highestOneBit, 64);
  }

  @Test
  void testIterate() {
    List<Integer> list = IntStream.iterate(0, i -> i + 10).limit(10).boxed().collect(Collectors.toList());
    assertEquals(list, Arrays.asList(0, 10, 20, 30, 40, 50, 60, 70, 80, 90));
  }

  @Test
  void testQuantity2() {
    Tensor signal = Tensors.vector(1, 2, 1, 4, 3, 2, 3, 4, 3, 4);
    Tensor vector = signal.maps(s -> Quantity.of(s, "m"));
    Tensor array1 = SpectrogramArrays.FOURIER.operator().apply(signal);
    Tensor array2 = SpectrogramArrays.FOURIER.operator().apply(vector);
    Tolerance.CHOP.requireClose(array1.maps(s -> Quantity.of(s, "m")), array2);
  }
  // @Test
  // void testFailWindowLength() {
  // assertThrows(IllegalArgumentException.class, () -> XtrogramArray.SPECTROGRAM.of(0, 8));
  // }
  //
  // @Test
  // void testFailWindowLengthOffset() {
  // assertThrows(IllegalArgumentException.class, () -> XtrogramArray.SPECTROGRAM.of(4, 8));
  // }
  //
  // @Test
  // void testFailOffset() {
  // assertThrows(IllegalArgumentException.class, () -> XtrogramArray.SPECTROGRAM.of(4, 0));
  // }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> SpectrogramArrays.FOURIER.operator().half_abs(null));
  }
  // @Test
  // void testDimensionsFail() {
  // TensorUnaryOperator tensorUnaryOperator = XtrogramArray.SPECTROGRAM.of(32, 8);
  // assertThrows(Throw.class, () -> tensorUnaryOperator.apply(RealScalar.ONE));
  // assertThrows(ClassCastException.class, () -> tensorUnaryOperator.apply(HilbertMatrix.of(32)));
  // }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> SpectrogramArrays.FOURIER.operator().apply(RealScalar.ONE));
  }

  @Test
  void testMatrixFail() {
    assertThrows(ClassCastException.class, () -> SpectrogramArrays.FOURIER.operator().apply(HilbertMatrix.of(32)));
  }
}
