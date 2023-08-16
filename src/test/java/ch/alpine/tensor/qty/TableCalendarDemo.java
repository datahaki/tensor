// code by jph
package ch.alpine.tensor.qty;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.Pretty;

public enum TableCalendarDemo {
  ;
  public static void main(String[] args) throws IOException {
    DateTime beg = DateTime.of(LocalDate.of(2023, Month.SEPTEMBER, 1 + 2), LocalTime.MIDNIGHT);
    DateTime end = DateTime.of(LocalDate.of(2024, Month.MAY, 2 + 3), LocalTime.MIDNIGHT);
    System.out.println("from");
    System.out.println(beg);
    System.out.println("until");
    System.out.println(end);
    System.out.println("Su Mo Tu We Th Fr Sa");
    Tensor table = Tensors.empty();
    Tensor row = Tensors.empty();
    while (Scalars.lessThan(beg, end)) {
      row.append(beg);
      DayOfWeek dayOfWeek = beg.dayOfWeek();
      if (dayOfWeek.equals(DayOfWeek.SATURDAY)) {
        table.append(row);
        row = Tensors.empty();
      }
      beg = beg.plusDays(1);
    }
    if (row.length() != 0)
      table.append(row);
    System.out.println(Pretty.of(table.map(s -> RealScalar.of(((DateTime) s).dayOfMonth()))));
    Export.of(HomeDirectory.file("cal.csv"), table);
  }
}
