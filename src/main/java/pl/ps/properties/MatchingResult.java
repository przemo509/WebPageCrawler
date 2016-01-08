package pl.ps.properties;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import pl.ps.utils.Assert;

public class MatchingResult {
    private Node matchedNode;
    private Elements matchedElements;
    private String matchedString;

    public MatchingResult(Node node) {
        Assert.notNull(node, "Brak elementu.");
        matchedNode = node;
        matchedElements = null;
        matchedString = "";
    }

    public MatchingResult(Elements elements) {
        Assert.notNull(elements, "Brak elementów.");
        Assert.isTrue(!elements.isEmpty(), "Brak elementu.");
        matchedNode = elements.first();
        matchedElements = elements;
        matchedString = "";
    }

    public MatchingResult(String string) {
        Assert.stringNotEmpty(string);
        matchedNode = null;
        matchedElements = null;
        matchedString = string;
    }

    public MatchingResult() {
        matchedNode = null;
        matchedElements = null;
        matchedString = "";
    }

    public Node getNode() {
        return matchedNode;
    }

    public Elements getElements() {
        return matchedElements;
    }

    public String getString() {
        return matchedString;
    }

    public boolean isAnythingFound() {
        return isNodeFound() || isElementsFound() || isStringFound();
    }

    private boolean isNodeFound() {
        return getNode() != null;
    }

    private boolean isElementsFound() {
        return getElements() != null;
    }

    private boolean isStringFound() {
        return StringUtils.isNotBlank(getString());
    }
}
