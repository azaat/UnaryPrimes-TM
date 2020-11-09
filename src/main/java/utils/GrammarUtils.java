package utils;

import formallang.UnrestrictedGrammar;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GrammarUtils {
    public static Path storeGrammar(UnrestrictedGrammar grammar, String path) {
        Path grammarPath = Paths.get(path);
        try {
            Files.createFile(grammarPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        try (BufferedWriter writer = Files.newBufferedWriter(grammarPath, StandardCharsets.UTF_8)) {
            writer.write(String.format("Start symbol: %s\n", grammar.getStartSymbol().getValue()));

            StringBuilder terminalsBuilder = new StringBuilder();
            grammar.getTerminals().forEach(s -> terminalsBuilder.append(s.getValue()).append(" "));
            writer.write(String.format("Terminals: %s\n", terminalsBuilder.toString()));
            writer.newLine();

            StringBuilder allProdBuilder = new StringBuilder();
            grammar.getProductions().forEach(production -> {
                final StringBuilder prodBuilder = new StringBuilder();
                production.getHead().forEach(s -> prodBuilder.append(s.getValue()));
                prodBuilder.append(" -> ");
                production.getBody().forEach(s -> prodBuilder.append(s.getValue()));

                allProdBuilder.append(prodBuilder).append("\n");
            });

            writer.write(allProdBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return grammarPath;
    }
}
