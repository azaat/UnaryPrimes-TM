package formallang;

import utils.GrammarUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static formallang.TuringMachine.*;
import static formallang.UnrestrictedGrammar.*;

public class TmToUnrestrictedGrammar {
    public static final Set<String> SIGMA = Set.of("0", "1");
    public static final String EPS = "eps";
    public static final String BLANC = "_";
    public static final Set<String> SIGMA_EPS = Stream.concat(
            SIGMA.stream(), Stream.of(EPS)).collect(Collectors.toSet());
    public static final Set<String> GAMMA = Stream.concat(
            SIGMA.stream(), List.of("A", "B", BLANC).stream()).collect(Collectors.toSet());

    public static UnrestrictedGrammar convert(TuringMachine tm) {
        // Construct terminals
        Set<GrammarSymbol> terminals = SIGMA.stream().map(
                s -> new GrammarSymbol(s, true)
        ).collect(Collectors.toSet());

        // Construct variables
        Set<State> states = tm.getStates();
        Set<GrammarSymbol> variables = tm.getStates().stream().map(
                s -> new GrammarSymbol(s.getValue(), false)
        ).collect(Collectors.toSet());

        GrammarSymbol s = new GrammarSymbol("S", false);
        GrammarSymbol s1 = new GrammarSymbol("S1", false);
        GrammarSymbol s2 = new GrammarSymbol("S2", false);
        GrammarSymbol s3 = new GrammarSymbol("S3", false);
        variables.addAll(Arrays.asList(s1, s2, s3, s));

        for (String xSym : SIGMA) {
            for (String ySym : GAMMA) {
                variables.add(
                        new GrammarSymbol(
                                "[" + xSym + "|" + ySym + "]", false
                        )
                );
            }
        }

        GrammarSymbol q0 = new GrammarSymbol(tm.getInitialState().getValue(), false);

        Set<Production> productions = new HashSet<>(Arrays.asList(
                new Production(List.of(s), List.of(s1, q0, s2)),
                new Production(List.of(s2), List.of(s3)),
                new Production(List.of(s1), List.of(s1, new GrammarSymbol("[" + EPS + "|" + BLANC + "]", false))),
                new Production(List.of(s3), List.of(new GrammarSymbol("[" + EPS + "|" + BLANC + "]", false), s3)),
                new Production(List.of(s1), List.of(new GrammarSymbol(EPS, false))),
                new Production(List.of(s3), List.of(new GrammarSymbol(EPS, false)))
        ));

        for (String sym : SIGMA) {
            productions.add(
                    new Production(
                            List.of(s2),
                            List.of(
                                    new GrammarSymbol("[" + sym + "|" + sym + "]", false),
                                    s2
                            )
                    )
            );
        }

        // Emulation of the transition function
        for (String aVal : SIGMA_EPS) {
            for (TransitionContext transitionContext : tm.getTransitionFunc().keySet()) {
                Transition tr = tm.getTransitionFunc().get(transitionContext);

                GrammarSymbol stateFromSym = new GrammarSymbol(transitionContext.getState().getValue(), false);
                GrammarSymbol stateToSym = new GrammarSymbol(tr.getContextTo().getState().getValue(), false);
                GrammarSymbol dSym = new GrammarSymbol(tr.getContextTo().getTapeSym(), false);
                GrammarSymbol contextSym = new GrammarSymbol("[" + aVal + "|" + transitionContext.getTapeSym() + "]", false);
                GrammarSymbol contextToSym = new GrammarSymbol("[" + aVal + "|" + dSym.getValue() + "]", false);

                List<GrammarSymbol> head;
                List<GrammarSymbol> body;
                switch (tr.getDirection()) {
                    case LEFT:
                        for (String leftSym : GAMMA) {
                            for (String bVal : SIGMA_EPS) {
                                GrammarSymbol contextLeftSym = new GrammarSymbol("[" + bVal + "|" + leftSym + "]", false);

                                head = List.of(contextLeftSym, stateFromSym, contextSym);
                                body = List.of(stateToSym, contextLeftSym, contextToSym);
                                productions.add(new Production(head, body));
                            }
                        }
                        break;
                    case STAY:
                        head = List.of(stateFromSym, contextSym);
                        body = List.of(stateToSym, contextToSym);
                        productions.add(new Production(head, body));
                        break;
                    case RIGHT:
                        head = List.of(stateFromSym, contextSym);
                        body = List.of(contextToSym, stateToSym);
                        productions.add(new Production(head, body));
                        break;
                }
            }
        }

        List<GrammarSymbol> head;
        List<GrammarSymbol> body;
        // Final states
        for (State finalState : tm.getFinalStates()) {
            GrammarSymbol qSym = new GrammarSymbol(finalState.getValue(), false);

            for (String cVal : GAMMA) {
                for (String aVal : SIGMA_EPS) {
                    GrammarSymbol aSym = new GrammarSymbol(aVal, true);
                    GrammarSymbol contextSym = new GrammarSymbol("[" + aVal + "|" + cVal + "]", false);
                    head = List.of(contextSym, qSym);
                    body = List.of(qSym, aSym, qSym);
                    productions.add(new Production(head, body));

                    head = List.of(qSym, contextSym);
                    productions.add(new Production(head, body));
                }
            }

            head = List.of(qSym);
            body = List.of(new GrammarSymbol(EPS, true));
            productions.add(new Production(head, body));
        }
        return new UnrestrictedGrammar(terminals, variables, productions, s);
    }
}
