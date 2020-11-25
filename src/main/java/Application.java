import formallang.*;
import utils.TuringMachineUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static formallang.UnrestrictedGrammar.*;

public class Application {
    public static void main(String[] args) {
       /* Path turingMachinePath = Paths.get("src", "main", "resources", "turing_machine.txt");
        try {
            TuringMachine tm = TuringMachineUtils.loadTuringMachine(turingMachinePath);

            UnrestrictedGrammar grammar = TmToUnrestrictedGrammar.convert(tm);
            System.out.println(grammar.getProductions().size());
            WordsGenerator.generate(grammar, 5, tm.getFinalStates());*/
         Path turingMachinePath = Paths.get("src", "main", "resources", "lba.txt");
        try {
            TuringMachine tm = TuringMachineUtils.loadTuringMachine(turingMachinePath);

            UnrestrictedGrammar grammar = LbaToCSGrammar.convert(tm);
            System.out.println(grammar.getProductions().size() + " productions");
            System.out.println(WordUtils.contains(grammar, 4, tm.getFinalStates()));

            Optional<List<List<GrammarSymbol>>> result = WordUtils.contains(grammar, 3, tm.getFinalStates());
            result.ifPresent(
                    derivation -> {
                        for (var sentence : derivation) {
                            System.out.println(sentence);
                        }
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
