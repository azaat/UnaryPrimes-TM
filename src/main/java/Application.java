import formallang.TuringMachine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Application {
    public static void main(String[] args) {
        Path turingMachinePath = Paths.get("src", "main", "resources", "turing_machine.txt");
        try {
            TuringMachine tm = TuringMachineUtils.loadTuringMachine(turingMachinePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
