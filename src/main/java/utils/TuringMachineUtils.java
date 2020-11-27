package utils;

import models.TuringMachine;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static models.TuringMachine.*;

public class TuringMachineUtils {
    private static final String INIT_PREFIX = "init: ";
    private static final String FINAL_PREFIX = "accept: ";
    private static final String SEPARATOR = ",";

    public static TuringMachine loadTuringMachine(Path path)
            throws IOException, IllegalStateException {
        BufferedReader reader = Files.newBufferedReader(path);
        String line;
        Set<State> states = new HashSet<>();
        State initialState = null;
        Set<State> finalStates = new HashSet<>();
        Map<TransitionContext, Transition> transitionFunc = new HashMap<>();

        boolean isPreviousEmptyLine = false;
        TransitionContext lastContext = null;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith(INIT_PREFIX)) {
                String initialStateVal = line.replace(INIT_PREFIX, "");
                initialState = new State(initialStateVal);
            } else if (line.startsWith(FINAL_PREFIX)) {
                String[] finalStatesVals = line.replace(FINAL_PREFIX, "").split(SEPARATOR);
                for (String finalStateVal : finalStatesVals) {
                    finalStates.add(
                            new State(finalStateVal)
                    );
                }
            } else if (line.startsWith(" ") || line.length() == 0) {
                isPreviousEmptyLine = true;
            } else if (!line.startsWith("//")) {
                String[] values = line.split(SEPARATOR);
                if (isPreviousEmptyLine) {
                    // Encountered line with transition context
                    State state = new State(
                            values[0]
                    );
                    states.add(state);
                    lastContext = new TransitionContext(
                            state,
                            values[1]
                    );

                } else {
                    // Encountered line with transition
                    Transition.Direction direction;
                    switch (values[2]) {
                        case ">":
                            direction = Transition.Direction.RIGHT;
                            break;
                        case "<":
                            direction = Transition.Direction.LEFT;
                            break;
                        case "-":
                            direction = Transition.Direction.STAY;
                            break;
                        default:
                            throw new IllegalArgumentException("Provided incorrect direction");
                    }
                    State state = new State(
                            values[0]
                    );
                    states.add(state);
                    Transition transition = new Transition(
                        new TransitionContext(
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
        if (initialState != null) {
            return new TuringMachine(states, initialState, finalStates, transitionFunc);
        } else {
            throw new IllegalStateException();
        }
    }
}
