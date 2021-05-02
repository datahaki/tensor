// code by jph
package ch.alpine.tensor.fft;

import java.util.Arrays;
import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.win.BlackmanWindow;
import ch.alpine.tensor.sca.win.DirichletWindow;
import ch.alpine.tensor.sca.win.GaussianWindow;
import ch.alpine.tensor.sca.win.HammingWindow;
import ch.alpine.tensor.sca.win.ParzenWindow;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SpectrogramTest extends TestCase {
  public void testDefault() {
    Tensor vector = Tensor.of(IntStream.range(0, 2000) //
        .mapToDouble(i -> Math.cos(i * 0.25 + (i / 20.0) * (i / 20.0))) //
        .mapToObj(RealScalar::of));
    Tensor image = Spectrogram.of(vector, DirichletWindow.FUNCTION, ColorDataGradients.VISIBLESPECTRUM);
    ImageFormat.of(image);
    assertEquals(Dimensions.of(image), Arrays.asList(32, 93, 4));
    assertEquals(Dimensions.of(Spectrogram.array(vector, DirichletWindow.FUNCTION)), Arrays.asList(32, 93));
    Tensor tensor = SpectrogramArray.of(vector).map(Abs.FUNCTION);
    assertEquals(Dimensions.of(tensor), Arrays.asList(93, 64));
  }

  public void testQuantity() {
    Tensor signal = Tensors.vector(1, 2, 1, 4, 3, 2, 3, 4, 3, 4);
    Tensor vector = signal.map(s -> Quantity.of(s, "m"));
    Tensor array1 = Spectrogram.of(signal, GaussianWindow.FUNCTION, ColorDataGradients.VISIBLESPECTRUM);
    Tensor array2 = Spectrogram.of(vector, GaussianWindow.FUNCTION, ColorDataGradients.VISIBLESPECTRUM);
    assertEquals(array1, array2);
  }

  public void testNullFail() {
    AssertFail.of(() -> Spectrogram.array(null, HammingWindow.FUNCTION));
  }

  public void testScalarFail() {
    AssertFail.of(() -> Spectrogram.of(RealScalar.ONE, ParzenWindow.FUNCTION, ColorDataGradients.VISIBLESPECTRUM));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> Spectrogram.of(HilbertMatrix.of(32), BlackmanWindow.FUNCTION, ColorDataGradients.VISIBLESPECTRUM));
  }
}
