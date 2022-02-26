// code by jph
package ch.alpine.tensor.mat.sv;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.LogNormalDistribution;

/* package */ enum QRSvdDemo {
  ;
  public static void main(String[] args) {
    Timing t1 = Timing.stopped();
    Timing t2 = Timing.stopped();
    for (int c = 0; c < 10; ++c) {
      Tensor matrix = RandomVariate.of(LogNormalDistribution.standard(), 200, 40);
      t1.start();
      SingularValueDecomposition.of(matrix);
      t1.stop();
      t2.start();
      QRSvd.of(matrix);
      t2.stop();
    }
    System.out.println(t1.seconds());
    System.out.println(t2.seconds());
  }
}
