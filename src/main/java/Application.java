import formallang.*;
import utils.GrammarUtils;
import utils.TuringMachineUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
//            if (WordUtils.contains(grammar, 8, tm.getFinalStates()).isPresent()) {
//                System.out.println(true);
//            };


            Set<Production> usedProds = new HashSet<>();
            for (var i = 2; i < 8; i++) {
                Optional<List<Production>> result = WordUtils.contains(grammar, i, tm.getFinalStates(), false);
                result.ifPresent(
                        derivation -> derivation.forEach(
                                production -> {
                                    if (production.getType().equals(Production.Type.TM_EMULATING)) {
                                        usedProds.add(production);
                                    }
                                }
                        )
                );
            }

            grammar.getProductions().removeIf(
                    production ->
                            production.getType().equals(Production.Type.TM_EMULATING)
                                    && !usedProds.contains(production)
            );

            GrammarUtils.storeGrammar(grammar, "lba_grammar.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
