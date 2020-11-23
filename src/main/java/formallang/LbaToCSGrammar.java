package formallang;

import utils.GrammarUtils;
import utils.TuringMachineUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static formallang.UnrestrictedGrammar.GrammarSymbol;
import static formallang.UnrestrictedGrammar.Production;
import static formallang.TuringMachine.State;
import static formallang.TuringMachine.TransitionContext;
import static formallang.TuringMachine.Transition;
import static formallang.TuringMachine.Transition.Direction;

public class LbaToCSGrammar {
    private static final String L = "c";
    private static final String R = "$";
    private static final Set<String> ALPHABET = Set.of("0", "1");

    public static UnrestrictedGrammar convert(TuringMachine turingMachine) {
        Set<Production> productions = new LinkedHashSet<>();
        Set<GrammarSymbol> alphabetSymbols = ALPHABET.stream()
                .map(s -> new GrammarSymbol(s, true))
                .collect(Collectors.toSet());

        GrammarSymbol a1 = new GrammarSymbol("A1", false);
        GrammarSymbol a2 = new GrammarSymbol("A2", false);

        State init = turingMachine.getInitialState();

        for (var aSym : alphabetSymbols) {
            String a = aSym.getValue();
            // 4.1
            productions.add(new Production(
                    List.of(a1), List.of(createSymbol(init.getValue(), L, a, a), a2)
            ));
            // 4.2
            productions.add(new Production(
                    List.of(a2), List.of(createSymbol(a, a), a2)
            ));
            // 4.3
            productions.add(new Production(
                    List.of(a2), List.of(createSymbol(a, a, R))
            ));
        }

        Map<TransitionContext, Transition> func = turingMachine.getTransitionFunc();

        for (var state : turingMachine.getStates()) {
            if (turingMachine.getFinalStates().contains(state)) {
                continue;
            }

            for (Map.Entry<TransitionContext, Transition> entry : func.entrySet()) {
                TransitionContext ctx = entry.getKey();
                Transition trans = entry.getValue();

                State q = ctx.getState();
                State p = trans.getContextTo().getState();

                if (!q.equals(state)) {
                    continue;
                }

                for (var x : getPossTapeSymbols(turingMachine)) {
                    for (var z : getPossTapeSymbols(turingMachine)) {
                        for (var aSym : alphabetSymbols) {
                            for (var bSym : alphabetSymbols) {
                                String a = aSym.getValue();
                                String b = bSym.getValue();
                                String y = trans.getContextTo().getTapeSym();

                                if (
                                        ctx.getTapeSym().equals(L)
                                                && y.equals(L)
                                                && trans.getDirection().equals(Direction.RIGHT)
                                ) {
                                    // 5.1
                                    productions.add(new Production(
                                            List.of(createSymbol(q.getValue(), L, x, a)),
                                            List.of(createSymbol(L, p.getValue(), x, a))
                                    ));
                                } else if (
                                        ctx.getTapeSym().equals(x)
                                                && trans.getDirection().equals(Direction.LEFT)
                                ) {
                                    // 5.2
                                    productions.add(new Production(
                                            List.of(createSymbol(L, q.getValue(), x, a)),
                                            List.of(createSymbol(p.getValue(), L, y, a))
                                    ));
                                    // 6.2
                                    productions.add(new Production(
                                            List.of(createSymbol(z, b), createSymbol(q.getValue(), x, a)),
                                            List.of(createSymbol(p.getValue(), z, b), createSymbol(y, a))
                                    ));
                                    // 6.4
                                    productions.add(new Production(
                                            List.of(createSymbol(L, z, b), createSymbol(q.getValue(), x, a)),
                                            List.of(createSymbol(L, p.getValue(), z, b), createSymbol(y, a))
                                    ));
                                    // 7.3
                                    productions.add(new Production(
                                            List.of(createSymbol(z, b), createSymbol(q.getValue(), x, a, R)),
                                            List.of(createSymbol(p.getValue(), z, b), createSymbol(y, a, R))
                                    ));
                                    // this production should be in case |w|=2
                                    productions.add(new Production(
                                            List.of(createSymbol(L, z, b), createSymbol(q.getValue(), x, a, R)),
                                            List.of(createSymbol(L, p.getValue(), z, b), createSymbol(y, a, R))
                                    ));
                                } else if (
                                        ctx.getTapeSym().equals(x)
                                                && trans.getDirection().equals(Direction.RIGHT)
                                ) {
                                    // 5.3
                                    productions.add(new Production(
                                            List.of(createSymbol(L, q.getValue(), x, a), createSymbol(z, b)),
                                            List.of(createSymbol(L, y, a), createSymbol(p.getValue(), z, b))
                                    ));
                                    // this production should be in case |w|=2
                                    productions.add(new Production(
                                            List.of(createSymbol(L, q.getValue(), x, a), createSymbol(z, b, R)),
                                            List.of(createSymbol(L, y, a), createSymbol(p.getValue(), z, b, R))
                                    ));
                                    // 6.1
                                    productions.add(new Production(
                                            List.of(createSymbol(q.getValue(), x, a), createSymbol(z, b)),
                                            List.of(createSymbol(y, a), createSymbol(p.getValue(), z, b))
                                    ));
                                    // 6.3
                                    productions.add(new Production(
                                            List.of(createSymbol(q.getValue(), x, a), createSymbol(z, b, R)),
                                            List.of(createSymbol(y, a), createSymbol(p.getValue(), z, b, R))
                                    ));
                                    // 7.1
                                    productions.add(new Production(
                                            List.of(createSymbol(q.getValue(), x, a, R)),
                                            List.of(createSymbol(y, a, p.getValue(), R))
                                    ));
                                } else if (
                                        ctx.getTapeSym().equals(R)
                                                && y.equals(R)
                                                && trans.getDirection().equals(Direction.LEFT)
                                ) {
                                    // 7.2
                                    productions.add(new Production(
                                            List.of(createSymbol(x, a, q.getValue(), R)),
                                            List.of(createSymbol(p.getValue(), x, a, R))
                                    ));
                                }
                            }
                        }
                    }
                }
            }
        }

        for (var q : turingMachine.getFinalStates()) {
            for (var x : getPossTapeSymbols(turingMachine)) {
                for (var aSym : alphabetSymbols) {
                    String a = aSym.getValue();
                    // 8.1
                    productions.add(new Production(
                            List.of(createSymbol(q.getValue(), L, x, a)),
                            List.of(aSym)
                    ));
                    // 8.2
                    productions.add(new Production(
                            List.of(createSymbol(L, q.getValue(), x, a)),
                            List.of(aSym)
                    ));
                    // 8.3
                    productions.add(new Production(
                            List.of(createSymbol(q.getValue(), x, a)),
                            List.of(aSym)
                    ));
                    // 8.4
                    productions.add(new Production(
                            List.of(createSymbol(q.getValue(), x, a, R)),
                            List.of(aSym)
                    ));
                    // 8.5
                    productions.add(new Production(
                            List.of(createSymbol(x, a, q.getValue(), R)),
                            List.of(aSym)
                    ));
                }
            }
        }

        for (var x : getPossTapeSymbols(turingMachine)) {
            for (var aSym : alphabetSymbols) {
                for (var bSym : alphabetSymbols) {
                    String a = aSym.getValue();
                    String b = bSym.getValue();

                    // 9.1
                    productions.add(new Production(
                            List.of(aSym, createSymbol(x, b)),
                            List.of(aSym, bSym)
                    ));
                    // 9.2
                    productions.add(new Production(
                            List.of(aSym, createSymbol(x, b)),
                            List.of(aSym, bSym)
                    ));
                    // 9.3
                    productions.add(new Production(
                            List.of(createSymbol(x, a), bSym),
                            List.of(aSym, bSym)
                    ));
                    // 9.4
                    productions.add(new Production(
                            List.of(createSymbol(L, x, a), bSym),
                            List.of(aSym, bSym)
                    ));
                }
            }
        }

        return new UnrestrictedGrammar(productions, a1);
    }

    private static Set<String> getPossTapeSymbols(TuringMachine turingMachine) {
        Set<String> possTapeSyms = new HashSet<>();

        for (Map.Entry<TransitionContext, Transition> entry : turingMachine.getTransitionFunc().entrySet()) {
            TransitionContext ctx = entry.getKey();
            Transition trans = entry.getValue();

            String ctxSym = ctx.getTapeSym();
            String transSym = trans.getContextTo().getTapeSym();

            if (!ctxSym.equals(L) && !ctxSym.equals(R)) {
                possTapeSyms.add(ctxSym);
            }
            if (!transSym.equals(L) && !transSym.equals(R)) {
                possTapeSyms.add(transSym);
            }
        }

        return possTapeSyms;
    }

    private static GrammarSymbol createSymbol(String a, String b, String c, String d) {
        return new GrammarSymbol("[" + a + "," + b + "," + c + "," + d + "]", false);
    }

    private static GrammarSymbol createSymbol(String a, String b, String c) {
        return new GrammarSymbol("[" + a + "," + b + "," + c + "]", false);
    }

    private static GrammarSymbol createSymbol(String a, String b) {
        return new GrammarSymbol("[" + a + "," + b + "]", false);
    }

    public static void main(String[] args) {
        Path turingMachinePath = Paths.get("src", "main", "resources", "anbn_lba.txt");
        try {
            TuringMachine tm = TuringMachineUtils.loadTuringMachine(turingMachinePath);
            UnrestrictedGrammar grammar = LbaToCSGrammar.convert(tm);
            GrammarUtils.storeGrammar(grammar, "src/main/resources/anbn_lba_grammar.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
