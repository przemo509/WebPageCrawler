package pl.ps.properties;

import org.jsoup.nodes.Node;

import java.util.ArrayList;
import java.util.List;

public class FindPattern {
    private final List<FindPatternPart> parts;

    public FindPattern(String patternsString, String findPatternSplitter, String findPatternPartSplitter) {
        String[] patternsStrings = patternsString.split(findPatternSplitter);
        parts = new ArrayList<FindPatternPart>(patternsStrings.length);
        for (String patternString : patternsStrings) {
            parts.add(new FindPatternPart(patternString, findPatternPartSplitter));
        }
    }

    public MatchingResult apply(Node node, boolean isOptional) {
        MatchingResult currentResult = new MatchingResult(node);
        for (FindPatternPart part : parts) {
            currentResult = part.apply(currentResult, isOptional);
        }
        return currentResult;
    }

}
