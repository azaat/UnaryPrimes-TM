package utils;

import formallang.UnrestrictedGrammar;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static formallang.UnrestrictedGrammar.GrammarSymbol;
import static formallang.UnrestrictedGrammar.Production;

public class GrammarUtils {
    public static Path storeGrammar(UnrestrictedGrammar grammar, String path) {
        Path grammarPath = Paths.get(path);

        try (BufferedWriter writer = Files.newBufferedWriter(grammarPath, StandardCharsets.UTF_8)) {
            writer.write(String.format("Start symbol: %s\n", grammar.getStartSymbol().getValue()));

            StringBuilder terminalsBuilder = new StringBuilder();
            grammar.getTerminals().forEach(s -> terminalsBuilder.append(s.getValue()).append(" "));
            writer.write(String.format("Terminals: %s\n", terminalsBuilder.toString()));
            writer.newLine();

            StringBuilder allProdBuilder = new StringBuilder();
            grammar.getProductions().forEach(production -> {
                final StringBuilder prodBuilder = new StringBuilder();
                production.getHead().forEach(
                        s -> prodBuilder
                        .append(s.getValue())
                        .append(" ")
                );
                prodBuilder.append(" -> ");
                production.getBody().forEach(s -> prodBuilder
                        .append(s.getValue())
                        .append(" ")
                );

                allProdBuilder.append(prodBuilder).append("\n");
            });

            writer.write(allProdBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return grammarPath;
    }

    public static void main(String[] args) {
        GrammarSymbol startSymb = new GrammarSymbol("S", false);
        GrammarSymbol a = new GrammarSymbol("a", true);
        GrammarSymbol b = new GrammarSymbol("b", true);
        GrammarSymbol eps = new GrammarSymbol("eps", true);

        Production prod1 = new Production(List.of(startSymb), List.of(startSymb, a, startSymb, b));
        Production prod2 = new Production(List.of(startSymb), List.of(eps));

        UnrestrictedGrammar grammar = new UnrestrictedGrammar(
                Set.of(a, b, eps),
                Set.of(startSymb),
                Set.of(prod1, prod2),
                startSymb
        );

        GrammarUtils.storeGrammar(grammar, "src/main/resources/test_grammar.txt");
    }
}
