package formallang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static formallang.TmToUnrestrictedGrammar.*;
import static formallang.UnrestrictedGrammar.*;

public class WordsGenerator {
    public static void generate(UnrestrictedGrammar grammar, int n) {
        List<Production> productions = grammar.getProductions().stream().sorted(
                Comparator.comparingInt(p -> p.getBody().size())
        ).collect(Collectors.toList());

        final class Node {
            private Node(
                    List<GrammarSymbol> sentence,
                    int depth
            ) {
                this.sentence = sentence;
                this.depth = depth;
            }

            final int depth;
            final List<GrammarSymbol> sentence;
        }


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
                    if (sentence.size() >= n) return;
                    System.out.println("YAAAY! " + sentence + " depth: " + node.depth);
                }

                for (int pos = 0; pos < sentence.size() ; pos++) {
                    int limit = optMaxHead.orElse(sentence.size() - pos);
                    for (int partSize = 1; partSize <= Math.min(limit, sentence.size() - pos); partSize++) {
                        for (Production production : productions) {
                            List<GrammarSymbol> headNoEps = new ArrayList<>(production.getHead());
                            headNoEps.removeIf(
                                    sym -> sym.getValue().equals(EPS)
                            );
                            if (sentence.subList(pos, pos + partSize).equals(headNoEps)
                            ) {
                                List<GrammarSymbol> start = sentence.subList(0, pos);
                                List<GrammarSymbol> end = sentence.subList(pos + partSize, sentence.size());
                                List<GrammarSymbol> newSentence = new LinkedList<>(start);
                                List<GrammarSymbol> bodyNoEps = new ArrayList<>(production.getBody());
                                bodyNoEps.removeIf(
                                        sym -> sym.getValue().equals(EPS)
                                );
                                newSentence.addAll(bodyNoEps);
                                newSentence.addAll(end);
                                //if (node.depth < 1e3) {
                                queue.add(new Node(
                                        newSentence, node.depth + 1
                                ));
                                //}
                            }
                        }
                    }

                }
            }
        }
    }
}
