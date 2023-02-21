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

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.chq.DeterminateScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.sca.SawtoothWave;
import ch.alpine.tensor.sca.win.BlackmanHarrisWindow;
import ch.alpine.tensor.sca.win.HammingWindow;
import ch.alpine.tensor.sca.win.HannWindow;
import ch.alpine.tensor.sca.win.NuttallWindow;
import ch.alpine.tensor.sca.win.TukeyWindow;
import ch.alpine.tensor.sca.win.WindowFunctions;

class XtrogramArrayTest {
  private static Tensor signal() {
    return Tensor.of(IntStream.range(0, 10000) //
        .mapToObj(i -> RationalScalar.of(i, 100).add(RationalScalar.of(i * i, 1000_000))) //
        .map(SawtoothWave.INSTANCE));
  }

  @Test
  void testMathematica() {
    Tensor tensor = XtrogramArray.CEPSTROGRAM_Real.of(signal());
    boolean status = tensor.flatten(-1) //
        .map(Scalar.class::cast) //
        .allMatch(DeterminateScalarQ::of);
    assertTrue(status);
  }

  @Test
  void testMathematicaUnits() {
    Tensor vector = signal().extract(0, 100).map(s -> Quantity.of(s, "s"));
    Tensor tensor = XtrogramArray.SPECTROGRAM.of(10, 3).apply(vector);
    boolean status = tensor.flatten(-1) //
        .map(Scalar.class::cast) //
        .allMatch(DeterminateScalarQ::of);
    assertTrue(status);
  }

  @Test
  void testOperator() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tuo = XtrogramArray.CEPSTROGRAM_Real.of(1345, 300);
    Serialization.copy(tuo);
    tuo.apply(signal());
    // assertTrue(tuo.toString().startsWith("CepstrogramRealArray["));
  }

  @Test
  void testDimension() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tensorUnaryOperator = Serialization.copy(XtrogramArray.SPECTROGRAM.of(8, 8));
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
      int windowLength = Unprotect.dimension1(XtrogramArray.SPECTROGRAM.of(tensor, windowFunctions.get()));
      assertEquals(windowLength, 32);
    }
  }

  @Test
  void testQuantity() {
    Tensor tensor = Tensor.of(IntStream.range(0, 500) //
        .mapToDouble(i -> Math.cos(i * 0.25 + (i / 20.0) * (i / 20.0))) //
        .mapToObj(d -> Quantity.of(d, "m")));
    Tensor array = XtrogramArray.SPECTROGRAM.of(tensor);
    ScalarUnaryOperator suo = QuantityMagnitude.SI().in("km");
    Tensor array2 = array.map(suo);
    int windowLength = Unprotect.dimension1(array2);
    assertEquals(windowLength, 32);
    Tensor matrix = XtrogramArray.SPECTROGRAM.half_abs(tensor, HannWindow.FUNCTION);
    MatrixQ.require(matrix);
    matrix.map(suo);
  }

  @Test
  void testStaticOps() {
    XtrogramArray.SPECTROGRAM.of(Quantity.of(1, "s"), Quantity.of(100, "s^-1"), HannWindow.FUNCTION);
    XtrogramArray.SPECTROGRAM.of(Quantity.of(1, "s"), Quantity.of(100, "s^-1"), 10, TukeyWindow.FUNCTION);
  }

  @Test
  void testStaticOpsFail() {
    assertThrows(IllegalArgumentException.class, () -> XtrogramArray.SPECTROGRAM.of(Quantity.of(0, "s"), Quantity.of(100, "s^-1"), NuttallWindow.FUNCTION));
    assertThrows(IllegalArgumentException.class,
        () -> XtrogramArray.SPECTROGRAM.of(Quantity.of(1, "s"), Quantity.of(0.100, "s^-1"), BlackmanHarrisWindow.FUNCTION));
  }

  @RepeatedTest(7)
  void testPreallocate(RepetitionInfo repetitionInfo) {
    int windowLength = repetitionInfo.getCurrentRepetition();
    for (int offset = 1; offset <= windowLength; ++offset) {
      TensorUnaryOperator tensorUnaryOperator = XtrogramArray.SPECTROGRAM.of(windowLength, offset);
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
  void testFailWindowLength() {
    assertThrows(IllegalArgumentException.class, () -> XtrogramArray.SPECTROGRAM.of(0, 8));
  }

  @Test
  void testFailWindowLengthOffset() {
    assertThrows(IllegalArgumentException.class, () -> XtrogramArray.SPECTROGRAM.of(4, 8));
  }

  @Test
  void testFailOffset() {
    assertThrows(IllegalArgumentException.class, () -> XtrogramArray.SPECTROGRAM.of(4, 0));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> XtrogramArray.SPECTROGRAM.half_abs(null, HammingWindow.FUNCTION));
  }

  @Test
  void testDimensionsFail() {
    TensorUnaryOperator tensorUnaryOperator = XtrogramArray.SPECTROGRAM.of(32, 8);
    assertThrows(Throw.class, () -> tensorUnaryOperator.apply(RealScalar.ONE));
    assertThrows(ClassCastException.class, () -> tensorUnaryOperator.apply(HilbertMatrix.of(32)));
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> XtrogramArray.SPECTROGRAM.of(RealScalar.ONE));
  }

  @Test
  void testMatrixFail() {
    assertThrows(ClassCastException.class, () -> XtrogramArray.SPECTROGRAM.of(HilbertMatrix.of(32)));
  }
}
