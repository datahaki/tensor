// code by jph
package ch.ethz.idsc.tensor.fft;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.win.BlackmanHarrisWindow;
import ch.ethz.idsc.tensor.sca.win.HannWindow;
import ch.ethz.idsc.tensor.sca.win.NuttallWindow;
import ch.ethz.idsc.tensor.sca.win.TukeyWindow;
import ch.ethz.idsc.tensor.sca.win.WindowFunctions;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SpectrogramArrayTest extends TestCase {
  public void testDimension() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tensorUnaryOperator = Serialization.copy(SpectrogramArray.of(8, 8));
    Tensor tensor = tensorUnaryOperator.apply(Range.of(0, 128));
    assertEquals(Dimensions.of(tensor), Arrays.asList(16, 8));
    assertTrue(tensorUnaryOperator.toString().startsWith("SpectrogramArray["));
  }

  public void testMathematicaDefault() {
    Tensor tensor = Tensor.of(IntStream.range(0, 500) //
        .mapToDouble(i -> Math.cos(i * 0.25 + (i / 20.0) * (i / 20.0))) //
        .mapToObj(RealScalar::of));
    for (WindowFunctions windowFunctions : WindowFunctions.values()) {
      int windowLength = Unprotect.dimension1(SpectrogramArray.of(tensor, windowFunctions.get()));
      assertEquals(windowLength, 32);
    }
  }

  public void testQuantity() {
    Tensor tensor = Tensor.of(IntStream.range(0, 2000) //
        .mapToDouble(i -> Math.cos(i * 0.25 + (i / 20.0) * (i / 20.0))) //
        .mapToObj(d -> Quantity.of(d, "m")));
    Tensor array = SpectrogramArray.of(tensor);
    Tensor array2 = array.map(QuantityMagnitude.SI().in("km"));
    int windowLength = Unprotect.dimension1(array2);
    assertEquals(windowLength, 64);
  }

  public void testStaticOps() {
    SpectrogramArray.of(Quantity.of(1, "s"), Quantity.of(100, "s^-1"), HannWindow.FUNCTION);
    SpectrogramArray.of(Quantity.of(1, "s"), Quantity.of(100, "s^-1"), 10, TukeyWindow.FUNCTION);
  }

  public void testStaticOpsFail() {
    AssertFail.of(() -> SpectrogramArray.of(Quantity.of(0, "s"), Quantity.of(100, "s^-1"), NuttallWindow.FUNCTION));
    AssertFail.of(() -> SpectrogramArray.of(Quantity.of(1, "s"), Quantity.of(0.100, "s^-1"), BlackmanHarrisWindow.FUNCTION));
  }

  public void testPreallocate() {
    for (int windowLength = 1; windowLength < 8; ++windowLength)
      for (int offset = 1; offset <= windowLength; ++offset) {
        TensorUnaryOperator tensorUnaryOperator = SpectrogramArray.of(windowLength, offset);
        for (int length = 10; length < 20; ++length) {
          Tensor signal = Range.of(0, length);
          tensorUnaryOperator.apply(signal);
        }
      }
  }

  public void testHighestOneBit() {
    int highestOneBit = Integer.highestOneBit(64 + 3);
    assertEquals(highestOneBit, 64);
  }

  public void testIterate() {
    List<Integer> list = IntStream.iterate(0, i -> i + 10).limit(10).boxed().collect(Collectors.toList());
    assertEquals(list, Arrays.asList(0, 10, 20, 30, 40, 50, 60, 70, 80, 90));
  }

  public void testFailWindowLength() {
    AssertFail.of(() -> SpectrogramArray.of(0, 8));
  }

  public void testFailWindowLengthOffset() {
    AssertFail.of(() -> SpectrogramArray.of(4, 8));
  }

  public void testFailOffset() {
    AssertFail.of(() -> SpectrogramArray.of(4, 0));
  }

  public void testDimensionsFail() {
    TensorUnaryOperator tensorUnaryOperator = SpectrogramArray.of(32, 8);
    AssertFail.of(() -> tensorUnaryOperator.apply(RealScalar.ONE));
    AssertFail.of(() -> tensorUnaryOperator.apply(HilbertMatrix.of(32)));
  }

  public void testScalarFail() {
    AssertFail.of(() -> SpectrogramArray.of(RealScalar.ONE));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> SpectrogramArray.of(HilbertMatrix.of(32)));
  }
}
