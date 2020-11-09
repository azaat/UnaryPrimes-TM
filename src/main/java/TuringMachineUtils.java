import formallang.TuringMachine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TuringMachineUtils {
    private static final String INIT_PREFIX = "init: ";
    private static final String FINAL_PREFIX = "accept: ";
    private static final String SEPARATOR = ",";

    public static TuringMachine loadTuringMachine(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = null;
            Set<TuringMachine.State> states = new HashSet<>();
            Set<TuringMachine.State> initialStates = new HashSet<>();
            Set<TuringMachine.State> finalStates = new HashSet<>();
            Map<TuringMachine.TransitionContext, TuringMachine.Transition> transitionFunc = new HashMap<>();

            boolean isPreviousEmptyLine = false;
            TuringMachine.TransitionContext lastContext = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith(INIT_PREFIX)) {
                    String[] initialStatesVals = line.replace(INIT_PREFIX, "").split(SEPARATOR);
                    for (String initialStatesVal : initialStatesVals) {
                        initialStates.add(
                            new TuringMachine.State(initialStatesVal)
                        );
                    }
                } else if (line.startsWith(FINAL_PREFIX)) {
                    String[] finalStatesVals = line.replace(INIT_PREFIX, "").split(SEPARATOR);
                    for (String finalStateVal : finalStatesVals) {
                        finalStates.add(
                                new TuringMachine.State(finalStateVal)
                        );
                    }
                } else if (line.startsWith(" ") || line.length() == 0) {
                    isPreviousEmptyLine = true;
                } else if (!line.startsWith("//")) {
                    String[] values = line.split(SEPARATOR);
                    if (isPreviousEmptyLine) {
                        // Encountered line with transition context
                        TuringMachine.State state = new TuringMachine.State(
                                values[0]
                        );
                        states.add(state);
                        lastContext = new TuringMachine.TransitionContext(
                                state,
                                values[1]
                        );

                    } else {
                        // Encountered line with transition
                        TuringMachine.Transition.Direction direction;
                        switch (values[2]) {
                            case ">":
                                direction = TuringMachine.Transition.Direction.RIGHT;
                                break;
                            case "<":
                                direction = TuringMachine.Transition.Direction.LEFT;
                                break;
                            case "-":
                                direction = TuringMachine.Transition.Direction.STAY;
                                break;
                            default:
                                throw new IllegalArgumentException("Provided incorrect direction");
                        }
                        TuringMachine.State state = new TuringMachine.State(
                                values[0]
                        );
                        states.add(state);
                        TuringMachine.Transition transition = new TuringMachine.Transition(
                            new TuringMachine.TransitionContext(
                                state,
                                values[1]
                            ),
                            direction
                        );
                        if (lastContext != null) {
                            transitionFunc.put(lastContext, transition);
                        }
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
