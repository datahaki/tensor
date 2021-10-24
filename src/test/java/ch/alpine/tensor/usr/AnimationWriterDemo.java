// code by jph
package ch.alpine.tensor.usr;

import java.util.concurrent.TimeUnit;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.TensorArrayPlot;
import ch.alpine.tensor.io.AnimationWriter;
import ch.alpine.tensor.io.GifAnimationWriter;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;

/* package */ enum AnimationWriterDemo {
  ;
  public static void main(String[] args2) throws Exception {
    try (AnimationWriter animationWriter = //
        new GifAnimationWriter(HomeDirectory.Pictures("grayscale.gif"), 100, TimeUnit.MILLISECONDS)) {
      for (int count = 1; count <= 16; ++count) {
        Distribution distribution = DiscreteUniformDistribution.of(0, count * 16);
        animationWriter.write(RandomVariate.of(distribution, 128, 128));
      }
    }
    try (AnimationWriter animationWriter = //
        new GifAnimationWriter(HomeDirectory.Pictures("colornoise.gif"), 100, TimeUnit.MILLISECONDS)) {
      for (int count = 1; count <= 16; ++count) {
        Distribution distribution = DiscreteUniformDistribution.of(0, count * 16);
        Tensor image = RandomVariate.of(distribution, 128, 128, 4);
        image.set(s -> RealScalar.of(255), Tensor.ALL, Tensor.ALL, 3);
        animationWriter.write(image);
      }
    }
    try (AnimationWriter animationWriter = //
        new GifAnimationWriter(HomeDirectory.Pictures("palettenoise.gif"), 100, TimeUnit.MILLISECONDS)) {
      Distribution distribution = DiscreteUniformDistribution.of(0, 256);
      for (int count = 1; count <= 16; ++count)
        animationWriter.write(TensorArrayPlot.of(RandomVariate.of(distribution, 128, 128), ColorDataGradients.ALPINE));
    }
  }
}
