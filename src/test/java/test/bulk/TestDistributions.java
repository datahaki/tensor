// code by jph
package test.bulk;

import java.util.stream.Stream;

import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.c.BirnbaumSaundersDistribution;
import ch.alpine.tensor.pdf.c.CauchyDistribution;
import ch.alpine.tensor.pdf.c.ExponentialDistribution;
import ch.alpine.tensor.pdf.c.LogNormalDistribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;

public enum TestDistributions {
  ;
  public static Stream<Distribution> distributions() {
    return Stream.of( //
        BirnbaumSaundersDistribution.standard(), //
        CauchyDistribution.standard(), //
        ExponentialDistribution.standard(), //
        LogNormalDistribution.standard(), //
        NormalDistribution.standard() //
    );
  }

  public static Stream<Distribution> distributions2() {
    return Stream.of( //
        BirnbaumSaundersDistribution.standard(), //
        ExponentialDistribution.standard(), //
        NormalDistribution.standard() //
    );
  }
}
