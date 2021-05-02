// code by jph
package ch.alpine.tensor.num;

/* package */ enum CyclesGroup implements GroupInterface<Cycles> {
  INSTANCE;

  @Override // from GroupInterface
  public Cycles neutral(Cycles cycles) {
    return Cycles.identity();
  }

  @Override // from GroupInterface
  public Cycles invert(Cycles cycles) {
    return cycles.inverse();
  }

  @Override // from GroupInterface
  public Cycles combine(Cycles cycles1, Cycles cycles2) {
    return cycles1.combine(cycles2);
  }

  @Override // from Object
  public String toString() {
    return getClass().getSimpleName();
  }
}
