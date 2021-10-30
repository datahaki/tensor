// code by jph
package ch.alpine.tensor.usr;

import java.io.IOException;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.ImageResize;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.sca.Clips;

/* package */ enum ColorDataGradientDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor domain = Subdivide.increasing(Clips.positive(1.0), 255).map(Tensors::of);
    Tensor result = Tensors.empty();
    for (ColorDataGradients colorDataGradients : ColorDataGradients.values())
      result.append(ImageResize.nearest(Transpose.of(domain.map(colorDataGradients)), 8, 1));
    Tensor image = Flatten.of(result, 1);
    System.out.println(Dimensions.of(image));
    Export.of(StaticHelper.file(ColorDataGradients.class), image);
  }
}
