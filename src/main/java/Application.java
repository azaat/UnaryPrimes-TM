import formallang.TmToUnrestrictedGrammar;
import formallang.TuringMachine;
import formallang.UnrestrictedGrammar;
import formallang.WordsGenerator;
import utils.GrammarUtils;
import utils.TuringMachineUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class Application {
    public static void main(String[] args) {
        Path turingMachinePath = Paths.get("src", "main", "resources", "anbn_turing_machine.txt");
        try {
            TuringMachine tm = TuringMachineUtils.loadTuringMachine(turingMachinePath);

            UnrestrictedGrammar grammar = TmToUnrestrictedGrammar.convert(tm);
            WordsGenerator.generate(grammar, 4);
            //GrammarUtils.storeGrammar(grammar, "anbn.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
