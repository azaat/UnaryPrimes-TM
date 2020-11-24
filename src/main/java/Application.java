import formallang.*;
import utils.TuringMachineUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Application {
    public static void main(String[] args) {
       /* Path turingMachinePath = Paths.get("src", "main", "resources", "turing_machine.txt");
        try {
            TuringMachine tm = TuringMachineUtils.loadTuringMachine(turingMachinePath);

            UnrestrictedGrammar grammar = TmToUnrestrictedGrammar.convert(tm);
            System.out.println(grammar.getProductions().size());
            WordsGenerator.generate(grammar, 5, tm.getFinalStates());*/
         Path turingMachinePath = Paths.get("src", "main", "resources", "turing_machine.txt");
        try {
            TuringMachine tm = TuringMachineUtils.loadTuringMachine(turingMachinePath);

            UnrestrictedGrammar grammar = TmToUnrestrictedGrammar.convert(tm);
            //GrammarUtils.storeGrammar(grammar, "prime_lba.txt");
            System.out.println(grammar.getProductions().size() + " productions");
            System.out.println(WordUtils.contains(grammar, 3, tm.getFinalStates()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
