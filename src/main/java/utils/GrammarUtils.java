package utils;

import formallang.UnrestrictedGrammar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static formallang.UnrestrictedGrammar.GrammarSymbol;
import static formallang.UnrestrictedGrammar.Production;

public class GrammarUtils {
    public static UnrestrictedGrammar loadGrammar(Path path) throws Exception {
        BufferedReader reader = Files.newBufferedReader(path);
        Set<Production> productions = new LinkedHashSet<>();

        String line = reader.readLine().replace("Start symbol: ", "").strip();
        GrammarSymbol startSymbol = new GrammarSymbol(line, false);

        String[] terminals = reader.readLine().replace("Terminals: ", "").strip().split(" ");

        line = reader.readLine();
        while (line != null) {
            if (line.strip().equals("")) {
                line = reader.readLine();
                continue;
            }
            String[] splitLine = line.strip().split(" -> ");

            List<GrammarSymbol> head = new LinkedList<>();
            List<GrammarSymbol> body = new LinkedList<>();

            for (var headSym : splitLine[0].strip().split(" ")) {
                head.add(new GrammarSymbol(headSym, Set.of(terminals).contains(headSym)));
            }
            for (var bodySym : splitLine[1].strip().split(" ")) {
                body.add(new GrammarSymbol(bodySym, Set.of(terminals).contains(bodySym)));
            }

            productions.add(new Production(head, body));

            line = reader.readLine();
        }

        return new UnrestrictedGrammar(productions, startSymbol);
    }

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

    public static void main(String[] args) throws Exception {
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

        UnrestrictedGrammar g = GrammarUtils.loadGrammar(Paths.get("lba_grammar.txt"));

        System.out.println(g.getStartSymbol());
        System.out.println(g.getTerminals());
        System.out.println(g.getVariables());
        System.out.println(g.getProductions());
    }
}
