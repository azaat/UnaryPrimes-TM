package formallang;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static formallang.TuringMachine.*;
import static formallang.UnrestrictedGrammar.*;

public class TmToUnrestrictedGrammar {
    //public static final Set<String> SIGMA = Set.of("0", "1");
    public static final Set<String> SIGMA = Set.of("1");
    public static final String EPS = "eps";
    public static final String BLANK = "_";
    public static final Set<String> SIGMA_EPS = Stream.concat(
            SIGMA.stream(), Stream.of(EPS)).collect(Collectors.toSet());

    public static UnrestrictedGrammar convert(TuringMachine tm) {
        final Set<String> GAMMA;
        GAMMA = getPossTapeSymbols(tm);

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

        // Generating productions
        Set<Production> productions = new HashSet<>(Arrays.asList(
                new Production(List.of(s), List.of(s1, q0, s2), Production.Type.TAPE_GENERATING),
                new Production(List.of(s2), List.of(s3), Production.Type.TAPE_GENERATING),
                new Production(List.of(s1), List.of(s1, new GrammarSymbol("[" + EPS + "|" + BLANK + "]", false)), Production.Type.TAPE_GENERATING),
                new Production(List.of(s3), List.of(new GrammarSymbol("[" + EPS + "|" + BLANK + "]", false), s3), Production.Type.TAPE_GENERATING),
                new Production(List.of(s1), List.of(new GrammarSymbol(EPS, false)), Production.Type.TAPE_GENERATING),
                new Production(List.of(s3), List.of(new GrammarSymbol(EPS, false)), Production.Type.TAPE_GENERATING)
        ));

        for (String sym : SIGMA) {
            productions.add(
                    new Production(
                            List.of(s2),
                            List.of(
                                    new GrammarSymbol("[" + sym + "|" + sym + "]", false),
                                    s2
                            ),
                            Production.Type.TAPE_GENERATING
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
                                productions.add(new Production(head, body, Production.Type.TM_EMULATING));
                            }
                        }
                        break;
                    case STAY:
                        head = List.of(stateFromSym, contextSym);
                        body = List.of(stateToSym, contextToSym);
                        productions.add(new Production(head, body, Production.Type.TM_EMULATING));
                        break;
                    case RIGHT:
                        head = List.of(stateFromSym, contextSym);
                        body = List.of(contextToSym, stateToSym);
                        productions.add(new Production(head, body, Production.Type.TM_EMULATING));
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
                    productions.add(new Production(head, body, Production.Type.WORD_RESTORING));

                    head = List.of(qSym, contextSym);
                    productions.add(new Production(head, body, Production.Type.WORD_RESTORING));
                }
            }

            head = List.of(qSym);
            body = List.of(new GrammarSymbol(EPS, true));
            productions.add(new Production(head, body, Production.Type.WORD_RESTORING));
        }
        return new UnrestrictedGrammar(terminals, variables, productions, s);
    }

    private static Set<String> getPossTapeSymbols(TuringMachine turingMachine) {
        Set<String> possTapeSyms = new HashSet<>();

        for (Map.Entry<TransitionContext, Transition> entry : turingMachine.getTransitionFunc().entrySet()) {
            TransitionContext ctx = entry.getKey();
            Transition trans = entry.getValue();

            String ctxSym = ctx.getTapeSym();
            String transSym = trans.getContextTo().getTapeSym();
            possTapeSyms.add(transSym);
            possTapeSyms.add(ctxSym);
        }

        return possTapeSyms;
    }
}
