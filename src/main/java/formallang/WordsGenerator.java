package formallang;

import java.util.*;
import java.util.stream.Collectors;

import static formallang.TmToUnrestrictedGrammar.EPS;
import static formallang.UnrestrictedGrammar.GrammarSymbol;
import static formallang.UnrestrictedGrammar.Production;

public class WordsGenerator {
    public static void generate(UnrestrictedGrammar grammar, int n) {
        List<Production> productions = grammar.getProductions().stream().sorted(
                Comparator.comparingInt(p -> p.getBody().size())
        ).collect(Collectors.toList());

        final class Node {
            final int depth;
            final List<GrammarSymbol> sentence;
            private Node(
                    List<GrammarSymbol> sentence,
                    int depth
            ) {
                this.sentence = sentence;
                this.depth = depth;
            }
        }

        // Getting max head length
        Optional<Integer> optMaxHead = productions.stream().map(
                p -> p.getHead().size()
        ).max(Comparator.naturalOrder());

        LinkedList<Node> queue = new LinkedList<>();
        Set<List<GrammarSymbol>> visited = new HashSet<>();
        queue.add(new Node(List.of(grammar.getStartSymbol()), 0));

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            List<GrammarSymbol> sentence = node.sentence;
            if (!visited.contains(sentence)) {
                visited.add(sentence);
                if (sentence.stream().allMatch(
                        GrammarSymbol::isTerminal
                )) {
                    // All terminals, generated word
                    if (sentence.size() >= n) return;
                    System.out.println("YAAAY! " + sentence + " depth: " + node.depth);
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
                                // [pref] [new sentence] [suff]
                                List<GrammarSymbol> newSentence = new LinkedList<>(start);
                                newSentence.addAll(bodyNoEps);
                                newSentence.addAll(end);
                                queue.add(new Node(
                                        newSentence, node.depth + 1
                                ));
                            }
                        }
                    }
                }
            }
        }
    }
}
