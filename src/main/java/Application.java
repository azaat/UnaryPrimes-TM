import formallang.*;
import utils.GrammarUtils;
import utils.TuringMachineUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class Application {
    public static void main(String[] args) {
        Path turingMachinePath = Paths.get("src", "main", "resources", "even_lba.txt");
        try {
            TuringMachine tm = TuringMachineUtils.loadTuringMachine(turingMachinePath);

            UnrestrictedGrammar grammar = LbaToCSGrammar.convert(tm);
            System.out.println(grammar.getProductions().size());
            WordsGenerator.generate(grammar, 5);
            //GrammarUtils.storeGrammar(grammar, "even.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
