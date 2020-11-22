import formallang.TuringMachine;
import utils.TuringMachineUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Application {
    public static void main(String[] args) {
        Path turingMachinePath = Paths.get("src", "main", "resources", "anbn_turing_machine.txt");
        try {
            TuringMachine tm = TuringMachineUtils.loadTuringMachine(turingMachinePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
