// code by jph
package ch.alpine.tensor.usr;

import java.io.IOException;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.fft.Spectrogram;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.ImageResize;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.num.Series;
import ch.alpine.tensor.sca.Cos;
import ch.alpine.tensor.sca.win.DirichletWindow;

/** Example from Mathematica::Spectrogram:
 * Table[Cos[ i/4 + (i/20)^2], {i, 2000}] */
/* package */ enum SpectrogramDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor data = Cos.of(Subdivide.of(0, 100, 2000).map(Series.of(Tensors.vector(0, 5, 1))));
    Tensor image = Spectrogram.of(data, DirichletWindow.FUNCTION, ColorDataGradients.VISIBLESPECTRUM);
    Export.of(StaticHelper.image(Spectrogram.class), ImageResize.nearest(image, 1));
  }
}
