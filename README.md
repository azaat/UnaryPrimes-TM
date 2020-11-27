# Unary primes

Who need a grammar for **prime numbers in unary system**? Well, not a single soul on this earth.
Still interested in this repo? You can try to generate one yourself!

## Grammars

You can find final grammar files in [final_t0_grammar](./final_t0_grammar.txt) and
[final_t1_grammar](./final_t1_grammar.txt)

## Installation

1) Install [maven](https://maven.apache.org/) 
2) Run ```mvn package``` in the root directory of the project
3) It generates a java jar file ```./target/primesChecker.jar```, which you can run like this:
```java -jar ./target/primesChecker.jar [ARGUMENTS]```

## Usage

```
Usage: PrimesChecker [-hV] [--derivation] --contains=<number> --grammar=<type>
      --contains=<number>   Specify number to check if it is prime
      --derivation          Show full derivation
      --grammar=<type>      Specify grammar type: "t0" or "t1"
  -h, --help                Show this help message and exit.
  -V, --version             Print version information and exit.
```

## Examples

##### Input:
```
--contains=7 --grammar=t1
```
##### Output:
```
7 is prime
```

##### Input:
```
--contains=10 --grammar=t0
```
##### Output:
```
10 is not prime
```

##### Input:
```
--contains=5 --grammar=t0 --derivation
```
##### Output:
```
5 is prime
Start symbol: S0
Applied [S0] -> [S1, S2, S3], got [S1, S2, S3]
Applied [S1] -> [S5], got [S5, S2, S3]
Applied [S3] -> [S6, S3], got [S5, S2, S6, S3]
...
Applied [S27, S5] -> [S27, eps, S27], got [1, 1, 1, 1, 1, S27, S27]
Applied [S27] -> [eps], got [1, 1, 1, 1, 1, S27]
Applied [S27] -> [eps], got [1, 1, 1, 1, 1]
```
