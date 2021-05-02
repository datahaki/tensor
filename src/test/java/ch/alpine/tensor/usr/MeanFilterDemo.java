// code by jph
package ch.alpine.tensor.usr;

import java.io.File;
import java.util.stream.IntStream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.MeanFilter;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.Import;

/* package */ enum MeanFilterDemo {
  ;
  public static void main(String[] args) throws Exception {
    String name = "bbc737a0";
    int depth = 10;
    // ---
    File file = HomeDirectory.file(name + ".jpg");
    Tensor image = Import.of(file);
    IntStream.range(0, 3).parallel().forEach(index -> //
    image.set(MeanFilter.of(image.get(Tensor.ALL, Tensor.ALL, index), depth), //
        Tensor.ALL, Tensor.ALL, index));
    // ---
    Export.of( //
        HomeDirectory.file(String.format("%s_mean_%02d.png", name, depth)), //
        Join.of(1, Import.of(file), image));
  }
}
