// code by jph
package ch.alpine.tensor.usr;

import java.io.IOException;

import ch.alpine.tensor.Parallelize;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.io.Put;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;

/* package */ enum DotMatMatDemo {
  ;
  public static void main(String[] args) throws IOException {
    Distribution distribution = NormalDistribution.of(1, 4);
    {
      int n = 100;
      Tensor a = RandomVariate.of(distribution, n, n);
      Tensor b = RandomVariate.of(distribution, n, n);
      a.dot(b);
      Parallelize.dot(a, b);
    }
    Tensor timing = Tensors.empty();
    for (int dim = 0; dim < 40; ++dim) {
      System.out.println(dim);
      Timing s_ser = Timing.stopped();
      Timing s_par = Timing.stopped();
      int trials = 50;
      for (int count = 0; count < trials; ++count) {
        Tensor a = RandomVariate.of(distribution, dim, dim);
        Tensor b = RandomVariate.of(distribution, dim, dim);
        s_ser.start();
        Tensor cs = a.dot(b);
        s_ser.stop();
        s_par.start();
        Tensor cp = Parallelize.dot(a, b);
        s_par.stop();
        if (!Chop._12.isClose(cs, cp))
          throw new Throw(cs);
      }
      timing.append(Tensors.vector(s_ser.nanoSeconds() / trials, s_par.nanoSeconds() / trials));
    }
    Put.of(HomeDirectory.file("timing_matmat.txt"), Transpose.of(timing));
  }
}
