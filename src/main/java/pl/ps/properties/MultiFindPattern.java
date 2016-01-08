package pl.ps.properties;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Node;

import java.util.ArrayList;
import java.util.List;

public class MultiFindPattern {
    private final List<FindPattern> findPatterns;

    public MultiFindPattern(String multiPatternsString, String multiPatternSplitter, String findPatternSplitter, String findPatternPartSplitter) {
        String[] multiPatternsStrings = StringUtils.isNotBlank(multiPatternsString) ? multiPatternsString.split(multiPatternSplitter) : new String[0];
        findPatterns = new ArrayList<FindPattern>(multiPatternsStrings.length);
        for (String patternsString : multiPatternsStrings) {
            findPatterns.add(new FindPattern(patternsString, findPatternSplitter, findPatternPartSplitter));
        }
    }

    public boolean isNodeMatched(Node node) {
        for (FindPattern findPattern : findPatterns) {
            MatchingResult result = findPattern.apply(node, true);
            if (result.isAnythingFound()) {
                return true;
            }
        }
        return false;
    }
}
