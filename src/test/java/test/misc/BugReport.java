// code by jph
package test.misc;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.fft.FourierDST;
import ch.alpine.tensor.io.Pretty;
import ch.alpine.tensor.sca.Round;

enum BugReport {
  ;
  static void main() {
    Tensor matrix = FourierDST._2.matrix(4);
    IO.println(Pretty.of(matrix.maps(Round._3)));
  }
}
