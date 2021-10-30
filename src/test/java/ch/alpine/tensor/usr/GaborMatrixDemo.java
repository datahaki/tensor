// code by jph
package ch.alpine.tensor.usr;

import java.io.IOException;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.Raster;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.mat.GaborMatrix;

/* package */ enum GaborMatrixDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor tensor = GaborMatrix.of(255, Tensors.vector(0.1, 0.2), RealScalar.of(0.3));
    Export.of(HomeDirectory.Pictures(GaborMatrix.class.getSimpleName() + ".png"), //
        Raster.of(tensor, ColorDataGradients.CLASSIC));
  }
}
