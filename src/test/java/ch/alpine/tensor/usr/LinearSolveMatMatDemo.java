// code by jph
package ch.alpine.tensor.usr;

import java.io.IOException;

import ch.alpine.tensor.Parallelize;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.io.Put;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;

/* package */ enum LinearSolveMatMatDemo {
  ;
  public static void main(String[] args) throws IOException {
    Distribution distribution;
    distribution = NormalDistribution.of(1, 4);
    // distribution = PoissonDistribution.of(RealScalar.ONE);
    {
      int n = 100;
      Tensor a = RandomVariate.of(distribution, n, n);
      Tensor b = RandomVariate.of(distribution, n, n);
      Inverse.of(a);
      a.dot(b);
      Parallelize.dot(a, b);
    }
    Tensor timings = Tensors.empty();
    for (int dim = 0; dim < 40; ++dim) {
      System.out.println(dim);
      Timing timing = Timing.stopped();
      int trials = 50;
      for (int count = 0; count < trials; ++count) {
        Tensor a = RandomVariate.of(distribution, dim, dim);
        Tensor b = RandomVariate.of(distribution, dim, dim);
        timing.start();
        LinearSolve.of(a, b);
        timing.stop();
      }
      timings.append(Tensors.vector(timing.nanoSeconds() / trials));
    }
    Put.of(HomeDirectory.file("timing_solvematmat_ser2.txt"), Transpose.of(timings));
  }
}
