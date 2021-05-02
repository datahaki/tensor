// code by jph
package ch.alpine.tensor.usr;

import java.io.IOException;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;

/* package */ enum ExportGifDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor matrix = Tensors.matrix((i, j) -> Tensors.vector(255 - i, j, 0, j < 128 ? 255 : i), 256, 256);
    Export.of(HomeDirectory.Pictures("redgreen.gif"), matrix);
    Export.of(HomeDirectory.Pictures("redgreen.png"), matrix);
  }
}
