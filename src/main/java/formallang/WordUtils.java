package formallang;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static formallang.TmToUnrestrictedGrammar.BLANK;
import static formallang.TmToUnrestrictedGrammar.EPS;
import static formallang.UnrestrictedGrammar.GrammarSymbol;
import static formallang.UnrestrictedGrammar.Production;

public class WordUtils {
    public static final GrammarSymbol epsBlankSym = new GrammarSymbol("[" + EPS + "|" + BLANK + "]", false);

    public static boolean isHieroglyph(GrammarSymbol sym) {
        return sym.getValue().contains("[") && !sym.getValue().equals(epsBlankSym.getValue());
    }

    public static Optional<List<Production>> contains(UnrestrictedGrammar grammar, int n, Set<TuringMachine.State> finalStates) {
        return contains(grammar, n, finalStates, false);
    }

    public static Optional<List<Production>> contains(UnrestrictedGrammar grammar, int n, Set<TuringMachine.State> finalStates, boolean needDerivation) {
        List<Production> productions = grammar.getProductions().stream().sorted(
                Comparator.comparingInt(p -> p.getBody().size())
        ).collect(Collectors.toList());

        final class Node {
            final int depth;
            final List<GrammarSymbol> sentence;
            final List<Production> derivation;

            Node(
                    List<GrammarSymbol> sentence,
                    int depth,
                    List<Production> derivation
            ) {
                this.sentence = sentence;
                this.depth = depth;
                this.derivation = derivation;
            }
        }

        // Getting max head length
        Optional<Integer> optMaxHead = productions.stream().map(
                p -> p.getHead().size()
        ).max(Comparator.naturalOrder());

        LinkedList<Node> queue = new LinkedList<>();
        Set<List<GrammarSymbol>> visited = new HashSet<>();
        queue.add(new Node(List.of(grammar.getStartSymbol()), 0, new LinkedList<>()));

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            List<GrammarSymbol> sentence = node.sentence;
            boolean foundFinal = false;

            if (!needDerivation) {
                for (TuringMachine.State finalState : finalStates) {
                    Optional<Integer> optWordSize = getWordSizeIfHasFinal(sentence, productions, finalState);
                    if (optWordSize.isPresent()) {
                        int wordSize = optWordSize.get();

                        if (wordSize == n) {
                            System.out.println("Found match " + sentence + " len: " + sentence.size());
                            return Optional.of(node.derivation);
                        } else if (wordSize > n) {
                            System.out.println("Did not find any match");
                            return Optional.empty();
                        }

                        foundFinal = true;
                    }
                }
            }

            if (!visited.contains(sentence)) {
                visited.add(sentence);

                if (foundFinal) {
                    System.out.println("Sent with final state " + sentence + " len: " + sentence.size());
                    // If we encountered sentence with final state we don't open it up further
                    continue;
                }

                if (sentence.stream().filter(
                        WordUtils::isHieroglyph
                ).count() > n) {
                    // Encountered more than n hieroglyphs, should not open up further
                    //System.out.println("Sent with more than n hieroglyphs, skipping..");
                    continue;
                }

                if (sentence.stream().allMatch(
                        GrammarSymbol::isTerminal
                )) {
                    // All terminals, generated word
                    if (sentence.size() > n) return Optional.empty();
                    else if (sentence.size() == n) return Optional.of(node.derivation);
                    System.out.println("Generated " + sentence + " depth: " + node.depth);
                }


                for (int pos = 0; pos < sentence.size(); pos++) {
                    int limit = optMaxHead.orElse(sentence.size() - pos);
                    for (int partSize = 1; partSize <= Math.min(limit, sentence.size() - pos); partSize++) {
                        // Checking all substrings from pos with partSize length as possible "children"
                        // limiting substring size with max head length as optimization
                        for (Production production : productions) {
                            // Remove eps from head (since eps = empty sym)
                            List<GrammarSymbol> headNoEps = new ArrayList<>(production.getHead());
                            headNoEps.removeIf(
                                    sym -> sym.getValue().equals(EPS)
                            );
                            if (sentence.subList(pos, pos + partSize).equals(headNoEps)
                            ) {
                                List<GrammarSymbol> start = sentence.subList(0, pos);
                                List<GrammarSymbol> end = sentence.subList(pos + partSize, sentence.size());
                                // Remove eps from body (since eps = empty sym)
                                List<GrammarSymbol> bodyNoEps = new ArrayList<>(production.getBody());
                                bodyNoEps.removeIf(
                                        sym -> sym.getValue().equals(EPS)
                                );
                                // Construct new sentence from chosen substring with current production
                                // [pref] [new body] [suff]
                                List<GrammarSymbol> newSentence = new LinkedList<>(start);
                                newSentence.addAll(bodyNoEps);
                                newSentence.addAll(end);

                                queue.add(new Node(
                                        newSentence, node.depth + 1, Stream.concat(node.derivation.stream(), Stream.of(production))
                                            .collect(Collectors.toList())
                                ));
                            }
                        }
                    }
                }
            }
        }

        // Exited loop without returning, so we did not find accepting sentence
        return Optional.empty();
    }

    private static Optional<Integer> getWordSizeIfHasFinal(List<GrammarSymbol> sentence, List<Production> productions, TuringMachine.State finalState) {
        // Hacky optimization to accept word when encountered final state
        // without opening all the variables with BFS
        if (sentence.contains(new GrammarSymbol(finalState.getValue(), false))) {

            // Apply eps productions
            for (Production production : productions) {
                List<GrammarSymbol> head = production.getHead();
                if (
                        head.size() == 1 && sentence.contains(head.get(0))
                                && production.getBody().size() == 1 && production.getBody().get(0).getValue().equals(EPS)
                ) {
                    sentence.removeIf(
                            grammarSymbol -> grammarSymbol.equals(head.get(0))
                    );
                }
            }

            // Remove symbols generating into blanks
            sentence.removeIf(
                    grammarSymbol -> grammarSymbol.equals(epsBlankSym)
            );

            return Optional.of(sentence.size());
        } else if (sentence.stream().anyMatch(s -> s.getValue().contains(finalState.getValue()))) {
            return Optional.of(sentence.size());
        } else {
            return Optional.empty();
        }
    }
}
