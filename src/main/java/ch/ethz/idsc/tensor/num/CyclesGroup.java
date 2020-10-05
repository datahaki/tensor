// code by jph
package ch.ethz.idsc.tensor.num;

/* package */ enum CyclesGroup implements GroupInterface<Cycles> {
  INSTANCE;

  @Override // from BinaryPower
  public Cycles identity() {
    return Cycles.identity();
  }

  @Override // from BinaryPower
  public Cycles invert(Cycles cycles) {
    return cycles.inverse();
  }

  @Override // from BinaryPower
  public Cycles combine(Cycles cycles1, Cycles cycles2) {
    return cycles1.combine(cycles2);
  }
}
