import formallang.*;
import utils.GrammarUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import picocli.CommandLine;
import picocli.CommandLine.Option;

public class Application {
    @Option(names = "-grammar", description = "specify grammar path")
    String grammarType;

    @Option(names = "-contains", required = true, description = "specify input number of symbols for grammar")
    int number;

    @Option(names = "-derivation", description = "use full derivation")
    boolean derivationRequested = false;

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "display a help message")
    private boolean helpRequested = false;

    public static void main(String[] args) throws Exception {
        Application unary = new Application();
        new CommandLine(unary).parseArgs(args);

        Path grammarPath;

        if (unary.grammarType.equals("t0")) {
            grammarPath = Paths.get("src", "main", "resources", "grammars/tm_grammar.txt");
        } else if (unary.grammarType.equals("t1") ) {
            grammarPath = Paths.get("src", "main", "resources", "grammars/lba_grammar.txt");
        } else {
            throw new IllegalArgumentException();
        }

        UnrestrictedGrammar grammar = GrammarUtils.loadGrammar(grammarPath);
        Optional<List<UnrestrictedGrammar.Production>> result = WordUtils.contains(
                grammar, unary.number, Set.of(new TuringMachine.State("prime")), unary.derivationRequested
        );
        if (result.isPresent()) {
            System.out.println("Contains " + unary.number);
        } else {
            System.out.println("Doesn't contain " + unary.number);
        }

        if (unary.derivationRequested && result.isPresent()) {
            for (var prod : result.get()) {
                System.out.println(prod);
            }
        }
    }
}
