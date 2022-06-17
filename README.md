![alpine_877](https://user-images.githubusercontent.com/4012178/116814864-1b1a1580-ab5b-11eb-97e6-1441af4ececa.png)

# ch.alpine.tensor

Library for tensor computations in Java.

The tensor library was developed with the following objectives in mind
* support for exact precision using integer fractions
* support for calculation with physical units
* suitable for use in safety-critical real-time systems
* API and string expressions inspired by `Mathematica`

Diverse projects rely on the tensor library:

<table>
<tr>
<td>

![usecase_amodeus](https://user-images.githubusercontent.com/4012178/35968174-668b6e54-0cc3-11e8-9c1b-a3e011fa0600.png)

Mobility-on-Demand

<td>

![usecase_swisstrolley](https://user-images.githubusercontent.com/4012178/35968228-88547e90-0cc3-11e8-978d-4f822515156f.png)

SwissTrolley plus

<td>

![usecase_motionplan](https://user-images.githubusercontent.com/4012178/35968244-96577dee-0cc3-11e8-80a1-b38691e863af.png)

Motion Planning

<td>

![usecase_gokart](https://user-images.githubusercontent.com/4012178/35968269-a92a3b46-0cc3-11e8-8d5e-1276762cdc36.png)

Autonomous Gokart

</tr>
</table>

## Features

* multi-dimensional arrays: scalars, vectors, matrices, n-linear forms, Lie-algebra ad-tensor, ...
* unstructured, nested tensors, for instance `{{1+2*I[A], -3/4}, {{5.678}, 9[kg*s^-1], 2[m^3]}}`
* scalars are real-, or complex numbers, from finite fields, or quantities with physical units
* values are encoded as exact integer fractions, in double precision, and as `java.math.BigDecimal`
* probability distributions for random variate generation: Binomial-, Poisson-, Exponential-distribution, etc.
* linear solvers `GaussianElimination`, `CholeskyDecomposition`, `QRDecomposition`, `SingularValueDecomposition`
* matrix functions `MatrixExp`, `MatrixLog`, `MatrixSqrt`, etc.
* tensor functions `TensorProduct`, `TensorWedge`, `Trace`, `HodgeDual`, etc.
* parametric functions `LinearInterpolation`, `BSplineFunction`, etc.
* window functions: Gaussian, Hamming, Hann, Blackman, etc.
* spectral analysis: `Fourier`, `SpectrogramArray`, etc.
* import from and export to `Mathematica`, `CSV`, and image files

## Gallery

<table>
<tr>
<td>

![gammademo](https://user-images.githubusercontent.com/4012178/28755698-bdb96546-7560-11e7-88d5-2d143e155e75.png)

Gamma function

<td>

![inversetrigdemo2](https://user-images.githubusercontent.com/4012178/28755697-bdb72d58-7560-11e7-8a70-3ef9d82ff48c.png)

Trigonometry

<td>

![mandelbulbdemo](https://user-images.githubusercontent.com/4012178/28755696-bd98789a-7560-11e7-8ebc-001c37f0a4fd.png)

Nylander's formula

<td>

![newtondemo](https://user-images.githubusercontent.com/4012178/35206180-22bed070-ff3b-11e7-8def-407345e3693e.png)

Newton's method

</tr>
</table>

## Examples

### Exact Precision

Solving systems of linear equations

```java
Tensor matrix = Tensors.matrixInt(new int[][] { { 2, -3, 2 }, { 4, 9, -3 }, { -1, 3, 2 } });
System.out.println(Pretty.of(Inverse.of(matrix)));

[
 [   9/37    4/37   -3/37 ]
 [ -5/111    2/37  14/111 ]
 [   7/37   -1/37   10/37 ]
]
```

Linear programming

```java
Tensor x = LinearProgramming.maxLessEquals( //
    Tensors.vector(1, 1), // rewards
    Tensors.fromString("{{4, -1}, {2, 1}, {-5, 2}}"), // matrix
    Tensors.vector(8, 7, 2)); // rhs
System.out.println(x);

{4/3, 13/3}
```

Pseudoinverse, Moore-Penrose inverse

```java
Tensor matrix = Tensors.fromString("{{-1 + I, 0}, {-I, 2}, {2 - I, 2 * I}}");
System.out.println(Pretty.of(PseudoInverse.of(matrix)));

[
 [   -1/3-I/3     1/6-I/6     1/6+I/6 ]
 [    1/6-I/3   5/12+I/12  -1/12-I/12 ]
]
```


Nullspace

```java
Tensor matrix = Tensors.fromString("{{-1/3, 0, I}}");
System.out.println(Pretty.of(NullSpace.of(matrix)));

[
 [    1     0  -I/3 ]
 [    0     1     0 ]
]
```

Statistics

```java
Distribution distribution = HypergeometricDistribution.of(10, 50, 100);
System.out.println(RandomVariate.of(distribution, 20));
PDF pdf = PDF.of(distribution);
System.out.println("P(X=3)=" + pdf.at(RealScalar.of(3)));

{6, 5, 1, 4, 3, 4, 7, 5, 7, 4, 6, 3, 5, 4, 5, 4, 6, 2, 6, 7}
P(X=3)=84000/742729
```

### Physical Quantities

The tensor library implements `Quantity`, i.e. numbers with physical units.
Several algorithms are verified to work with scalars of type `Quantity`.

```java
Tensor matrix = Tensors.fromString( //
  "{{60[m^2], 30[m*rad], 20[kg*m]}, {30[m*rad], 20[rad^2], 15[kg*rad]}, {20[kg*m], 15[kg*rad], 12[kg^2]}}");
CholeskyDecomposition cd = CholeskyDecomposition.of(matrix);
System.out.println(cd.diagonal());
System.out.println(Pretty.of(cd.getL()));
System.out.println(cd.det().divide(Quantity.of(20, "m^2*rad")));

{60[m^2], 5[rad^2], 1/3[kg^2]}
[
 [             1              0              0 ]
 [ 1/2[m^-1*rad]              1              0 ]
 [  1/3[kg*m^-1]   1[kg*rad^-1]              1 ]
]
5[kg^2*rad]
```

The units of a quantity are chosen by the application layer.
For instance, `Quantity.of(3, "Apples")` is valid syntax.

The tensor library contains the resource `/unit/si.properties` that encodes the SI unit system in the familiar strings such as `m`, `kg`, `s`, but the use of this convention is optional.
The example below makes use of these provided definitions

```java
Scalar mass = Quantity.of(300, "g"); // in gram
Scalar a = Quantity.of(981, "cm*s^-2"); // in centi-meters per seconds square
Scalar force = mass.multiply(a);
System.out.println(force);
Scalar force_N = UnitConvert.SI().to(Unit.of("N")).apply(force);
System.out.println(force_N);

294300[cm*g*s^-2]
2943/1000[N]
```

The arithmetic for the scalar type `Quantity` was developed in collaboration with the project `SwissTrolley+`.

### Miscellaneous

Tensors of rank 3

```java
Tensor ad = LeviCivitaTensor.of(3).negate();
Tensor x = Tensors.vector(7, 2, -4);
Tensor y = Tensors.vector(-3, 5, 2);
System.out.println(ad);
System.out.println(ad.dot(x).dot(y)); // coincides with cross product of x and y

{{{0, 0, 0}, {0, 0, -1}, {0, 1, 0}}, {{0, 0, 1}, {0, 0, 0}, {-1, 0, 0}}, {{0, -1, 0}, {1, 0, 0}, {0, 0, 0}}}
{24, -2, 41}
```

Functions for complex numbers

```java
System.out.println(Sqrt.of(RationalScalar.of(-9, 16)));

3/4*I
```

Several functions support evaluation to higher than machine precision for type `DecimalScalar`.

```java
System.out.println(Exp.of(DecimalScalar.of(10)));
System.out.println(Sqrt.of(DecimalScalar.of(2)));

220255.6579480671651695790064528423`34
1.414213562373095048801688724209698`34
```

The number after the prime indicates the precision of the decimal.
The string representation is compatible with `Mathematica`.

Indices for the `set` and `get` functions start from zero like in C/Java:

```java
Tensor matrix = Array.zeros(3, 4);
matrix.set(Tensors.vector(9, 8, 4, 5), 2);
matrix.set(Tensors.vector(6, 7, 8), Tensor.ALL, 1);
System.out.println(Pretty.of(matrix));
System.out.println(matrix.get(Tensor.ALL, 3)); // extraction of the 4th column

[
 [ 0  6  0  0 ]
 [ 0  7  0  0 ]
 [ 9  8  4  5 ]
]
{0, 0, 5}
```

### Optimization

Distance-based queries for point sets in Euclidean space

<table>
<tr>
<td>

![image](https://user-images.githubusercontent.com/4012178/138601155-8324ff52-6de5-41ee-bd5e-726653404b6b.png)

k-nearest neighbors

<td>

![image](https://user-images.githubusercontent.com/4012178/138601071-1ebde233-fea9-43a9-9c15-6d38438fc28e.png)

radius search

</tr>
</table>

### Visualization

Predefined color gradients

![colordatagradients](https://user-images.githubusercontent.com/4012178/157915484-62554aac-60d5-4e98-b6ab-1b14c0942b7f.png)

Predefined color lists

![colordatalists](https://user-images.githubusercontent.com/4012178/42363765-095692fe-80f9-11e8-8376-3e1364937536.png)

## Integration

From time to time, a version is deployed and made available for maven integration. Specify `repository` and `dependency` of the library `tensor` in the `pom.xml` file of your maven project:

```xml
<dependencies>
  <!-- other dependencies -->
  <dependency>
    <groupId>ch.alpine</groupId>
    <artifactId>tensor</artifactId>
    <version>1.0.6</version>
  </dependency>
</dependencies>

<repositories>
  <!-- other repositories -->
  <repository>
    <id>tensor-mvn-repo</id>
    <url>https://raw.github.com/datahaki/tensor/mvn-repo/</url>
    <snapshots>
      <enabled>true</enabled>
      <updatePolicy>always</updatePolicy>
    </snapshots>
  </repository>
</repositories>
<<<<<<< HEAD

<dependencies>
  <!-- other dependencies -->
  <dependency>
    <groupId>ch.alpine</groupId>
    <artifactId>tensor</artifactId>
    <version>1.0.6-jdk-11</version>
  </dependency>
</dependencies>
=======
>>>>>>> master
```

For Java 17, for `version` use `1.0.6` .

For Java 11, for `version` use `1.0.6-jdk-11`.

The source code is attached to every release.

The branch `master` always contains the latest features for Java 17, and does not correspond to the most recent deployed version generally.
