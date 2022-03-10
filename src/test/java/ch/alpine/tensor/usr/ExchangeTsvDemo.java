package ch.alpine.tensor.usr;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.SimpleUnitSystem;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.qty.UnitSystem;
import ch.alpine.tensor.qty.UnitSystems;

/** https://ec.europa.eu/eurostat/estat-navtree-portlet-prod/BulkDownloadListing?file=data/ert_bil_eur_d.tsv.gz */
public enum ExchangeTsvDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor tensor = Import.of(HomeDirectory.Downloads("ert_bil_eur_d.tsv.gz"));
    List<Integer> list = Dimensions.of(tensor);
    list.set(0, list.get(0) - 1);
    list.set(1, 2);
    Tensor block = tensor.block(Arrays.asList(1, 2), list);
    // System.out.println(Pretty.of(block));
    Map<String, Scalar> map = new HashMap<>();
    Unit unit = Unit.of("EUR");
    for (Tensor row : block) {
      String key = row.Get(0).toString();
      String string = row.Get(1).toString();
      if (!string.equals(":")) {
        Scalar scalar = Quantity.of(Scalars.fromString(string).reciprocal(), unit);
        map.put(key, scalar);
        // System.out.println(key + "=" + scalar);
      }
    }
    UnitSystem unitSystem = SimpleUnitSystem.from(map);
    UnitSystem rotate = UnitSystems.rotate(unitSystem, unit.toString(), "CHF");
    rotate.map().entrySet().stream().forEach(System.out::println);
  }
}
