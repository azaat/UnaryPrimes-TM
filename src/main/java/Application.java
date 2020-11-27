import models.*;
import picocli.CommandLine;
import utils.GrammarUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import utils.WordUtils;

import static models.UnrestrictedGrammar.*;
import static utils.WordUtils.*;

@Command(name = "Unary primes checker", version = "1.0", mixinStandardHelpOptions = true)
public class Application implements Runnable {
    @Option(names = "--grammar", required = true, description = "Specify grammar type: \"t0\" or \"t1\"")
    String type;

    @Option(names = "--contains", required = true, description = "Specify number to check if it is prime")
    int number;

    @Option(names = "--derivation", description = "Show full derivation")
    boolean derivationRequested = false;

    @Override
    public void run() {
        Path grammarPath;
        if (type.equals("t0")) {
            grammarPath = Paths.get("src", "main", "resources", "grammars", "tm_grammar.txt");
        } else if (type.equals("t1") ) {
            grammarPath = Paths.get("src", "main", "resources", "grammars", "lba_grammar.txt");
        } else {
            throw new IllegalArgumentException("Grammar type should be \"t0\" or \"t1\"");
        }

        UnrestrictedGrammar grammar;
        try {
            grammar = GrammarUtils.loadGrammar(grammarPath);
        } catch (Exception e) {
            throw new RuntimeException("Can't load specified grammar");
        }

        Optional<List<WordUtils.DerivationUnit>> result = WordUtils.contains(
                grammar, number, Set.of(new TuringMachine.State("prime")), derivationRequested
        );
        if (result.isPresent()) {
            System.out.println(number + " is prime");
        } else {
            System.out.println(number + " is not prime");
        }

        if (derivationRequested && result.isPresent()) {
            GrammarUtils.renameVariables(grammar);
            Map<String, String> newNames = grammar.getRenamings();

            System.out.println("Start symbol: " + newNames.get(grammar.getStartSymbol().getValue()));
            for (var unit : result.get()) {
                DerivationUnit newUnit = getRenamedDerivationUnit(unit, newNames);
                System.out.println("Applied " + newUnit.getProduction() + ", got " + newUnit.getSentence());
            }
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    private DerivationUnit getRenamedDerivationUnit(DerivationUnit unit, Map<String, String> newNames) {
        Production p = new Production(
                unit.getProduction().getHead().stream()
                        .map(s -> {
                            if (s.isTerminal()) {
                                return s;
                            } else {
                                return new GrammarSymbol(newNames.get(s.getValue()), false);
                            }
                        })
                        .collect(Collectors.toList()),
                unit.getProduction().getBody().stream()
                        .map(s -> {
                            if (s.isTerminal()) {
                                return s;
                            } else {
                                return new GrammarSymbol(newNames.get(s.getValue()), false);
                            }
                        })
                        .collect(Collectors.toList()),
                unit.getProduction().getType()
        );

        List<GrammarSymbol> sentence = unit.getSentence().stream()
                .map(s -> {
                    if (s.isTerminal()) {
                        return s;
                    } else {
                        return new GrammarSymbol(newNames.get(s.getValue()), false);
                    }
                })
                .collect(Collectors.toList());

        return new DerivationUnit(p, sentence);
    }
}
