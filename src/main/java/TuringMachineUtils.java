import formallang.TuringMachine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TuringMachineUtils {
    private static final String INIT_PREFIX = "init: ";
    private static final String FINAL_PREFIX = "accept: ";

    public static TuringMachine loadTuringMachine(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = null;
            Set<TuringMachine.State> states = new HashSet<>();
            Set<TuringMachine.State> initialStates = new HashSet<>();
            Set<TuringMachine.State> finalStates = new HashSet<>();
            Map<TuringMachine.TransitionContext, TuringMachine.Transition> transitionFunc = new HashMap<>();

            boolean isPreviousEmptyLine = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("//")) {
                    continue;
                } else if (line.startsWith(INIT_PREFIX)) {
                    initialStates.add(
                        new TuringMachine.State(line.replaceFirst(INIT_PREFIX, ""))
                    );
                } else if (line.startsWith(FINAL_PREFIX)) {
                    finalStates.add(
                        new TuringMachine.State(line.replaceFirst(FINAL_PREFIX, ""))
                    );
                } else if (line.startsWith(" ")) {
                    isPreviousEmptyLine = true;
                } else {
                    if (isPreviousEmptyLine) {
                        // TODO: transition context
                    } else {
                        // TODO: transition
                    }
                    isPreviousEmptyLine = false;
                }
            }

            return new TuringMachine(states, initialStates, finalStates, transitionFunc);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }
}
