// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Accumulate;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

/* package */ enum FoldListDemo {
  ;
  public static void main(String[] args) {
    Distribution distribution = UniformDistribution.unit();
    for (int count = 0; count < 20; ++count) {
      Tensor tensor = RandomVariate.of(distribution, 10000000);
      {
        Timing timing = Timing.started();
        FoldListTry.of(Tensor::add, tensor);
        System.out.println("new " + timing.nanoSeconds());
      }
      {
        Timing timing = Timing.started();
        Accumulate.of(tensor);
        System.out.println("old " + timing.nanoSeconds());
      }
    }
  }
}
