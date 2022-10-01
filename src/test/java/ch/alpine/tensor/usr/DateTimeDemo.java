// code by jph
package ch.alpine.tensor.usr;

import java.time.Month;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;

/* package */ enum DateTimeDemo {
  ;
  public static void main(String[] args) {
    Scalar mean = DateTime.of(2022, Month.FEBRUARY, 28, 12, 00);
    Scalar sigma = Quantity.of(30, "h");
    Distribution distribution = NormalDistribution.of(mean, sigma);
    Scalar guess = RandomVariate.of(distribution);
    System.out.println(mean.add(sigma));
    System.out.println(guess);
  }
}
