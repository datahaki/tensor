package ch.alpine.tensor.usr;

import java.io.IOException;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Drop;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.Import;

/* package */ enum AlpineImageDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor tensor = Import.of(HomeDirectory.file("alpine_877.png"));
    System.out.println(Dimensions.of(tensor));
    int wid = 6;
    Tensor alpha = Drop.tail(Subdivide.of(255, 0, wid), 1);
    int offset = tensor.length() - wid;
    for (int index = 0; index < alpha.length(); ++index) {
      Scalar a = alpha.Get(index);
      tensor.set(s -> a, offset + index, Tensor.ALL, 3);
      System.out.println(offset + index + " " + a);
    }
    Export.of(HomeDirectory.file("alpine_877_alpha.png"), tensor);
  }
}
