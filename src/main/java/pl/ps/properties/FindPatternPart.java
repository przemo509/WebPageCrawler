package pl.ps.properties;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import pl.ps.utils.Assert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindPatternPart {
    private FindPatternPartTypeEnum patternType;
    private String patternValue;

    public FindPatternPart(String patternString, String findPatternSplitter) {
        String[] patternParts = patternString.split(findPatternSplitter);
        Assert.isTrue(patternParts.length == 2, "Nie udało się podzielić [" + patternString + "] na typ i wartość za pomocą [" + findPatternSplitter + "]");
        patternType = FindPatternPartTypeEnum.getByCode(patternParts[0]);
        patternValue = patternParts[1];
    }

    public MatchingResult apply(MatchingResult previousResult, boolean isOptional) {
        Node node = previousResult.getNode();
        String string = previousResult.getString();
        switch (patternType) {
            case PARENT_NODE:
            case PREVOIUS_NODE:
            case NEXT_NODE:
                Assert.notNull(node, "Parent, prev i next wymagaja trybu Node a nie String");
                Assert.isNumeric(patternValue);
                int distance = Integer.parseInt(patternValue);
                for (int i = 0; i < distance; i++) {
                    switch (patternType) {
                        case PARENT_NODE:
                            node = node.parentNode();
                            break;
                        case PREVOIUS_NODE:
                            node = node.previousSibling();
                            break;
                        case NEXT_NODE:
                            node = node.nextSibling();
                            break;
                    }
                    if(node == null) {
                        Assert.isTrue(isOptional, "Nie znaleziono węzła [" + patternValue + " " + i + "]");
                        return emptyResult();
                    }
                }
                return new MatchingResult(node);
            case CSS:
                Elements foundElements = ((Element) node).select(patternValue);
                if(!foundElements.isEmpty()) {
                    return new MatchingResult(foundElements);
                }
                Assert.isTrue(isOptional, "Nie znaleziono wzorca [" + patternValue + "] we wewnątrz [" + node + "]");
                return emptyResult();
            case HTML_TAG_ATTRIBUTE:
                Assert.notNull(node, "Attr wymaga trybu Node a nie String");
                String attrValue = node.attr(patternValue);
                if(StringUtils.isNotBlank(attrValue)) {
                    return new MatchingResult(attrValue);
                }
                Assert.isTrue(isOptional, "Nie znaleziono atrybutu [" + patternValue + "] wewnątrz [" + node + "]");
                return emptyResult();
            case REGULAR_EXPRESSION:
                Pattern pattern = Pattern.compile(patternValue);
                String findIn = node != null ? node.toString() : string;
                Matcher matcher = pattern.matcher(findIn);
                if(matcher.matches()) {
                    return new MatchingResult(matcher.group(1));
                }
                Assert.isTrue(isOptional, "Nie udało się dopasować wzorca [" + patternValue + "] do tekstu [" + findIn + "]");
                return emptyResult();
            default:
                throw new AssertionError("Nieobsługiwany typ wzorca [" + patternType + "]");
        }
    }

    private MatchingResult emptyResult() {
        return new MatchingResult();
    }
}
