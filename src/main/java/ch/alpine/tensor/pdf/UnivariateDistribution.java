// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.pdf.c.AbstractContinuousDistribution;

/** common base class for {@link AbstractContinuousDistribution} and {@link DiscreteDistribution}
 * continuous distribution defined over non-discrete subset of the set of real numbers
 * 
 * CauchyDistribution
 * DagumDistribution
 * EqualizingDistribution
 * ExponentialDistribution
 * FrechetDistribution
 * GompertzMakehamDistribution
 * GumbelDistribution
 * HistogramDistribution
 * LaplaceDistribution
 * LogisticDistribution
 * LogNormalDistribution
 * NormalDistribution
 * ParetoDistribution
 * RayleighDistribution
 * TrapezoidalDistribution
 * 
 * @see DiscreteDistribution */
public interface UnivariateDistribution extends Distribution, //
    PDF, CDF, InverseCDF, RandomVariateInterface, MeanInterface, VarianceInterface {
  // ---
}
