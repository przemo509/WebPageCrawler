package pl.ps.properties;

import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import pl.ps.utils.Assert;
import pl.ps.utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class PageProperties {
    private String configName;
    private boolean active;

    private String listPage;
    private Integer pagesToCheck;
    private Integer scoreThreshold;

    private FindPattern itemElements;
    private FindPattern itemUrl;
    private FindPattern itemId;
    private FindPattern itemScore;
    private MultiFindPattern excludeNodes;

    private Integer delayDays;
    private FindPattern itemDate;
    private final SimpleDateFormat dateFormat;

    public PageProperties(File propertiesFile) {
        Properties properties = getPropertiesFile(propertiesFile);

        String propertiesFileName = propertiesFile.getName();
        configName = propertiesFileName.substring(0, propertiesFileName.lastIndexOf("."));
        active = getBooleanProperty(properties, Constants.PROPERTY_ACTIVE);

        listPage = getStringProperty(properties, Constants.PROPERTY_LIST_PAGE);
        pagesToCheck = getIntegerProperty(properties, Constants.PROPERTY_PAGES_TO_CHECK);
        scoreThreshold = getIntegerProperty(properties, Constants.PROPERTY_SCORE_THRESHOLD);

        String multiFindPatternSplitter = getStringProperty(properties, Constants.PROPERTY_MULTI_FIND_PATTERN_SPLITTER);
        String findPatternSplitter = getStringProperty(properties, Constants.PROPERTY_FIND_PATTERN_SPLITTER);
        String findPatternPartSplitter = getStringProperty(properties, Constants.PROPERTY_FIND_PATTERN_PART_SPLITTER);
        itemElements = new FindPattern(getStringProperty(properties, Constants.PROPERTY_ITEM_ELEMENTS), findPatternSplitter, findPatternPartSplitter);
        itemUrl = new FindPattern(getStringProperty(properties, Constants.PROPERTY_ITEM_URL), findPatternSplitter, findPatternPartSplitter);
        itemId = new FindPattern(getStringProperty(properties, Constants.PROPERTY_ITEM_ID), findPatternSplitter, findPatternPartSplitter);
        itemScore = new FindPattern(getStringProperty(properties, Constants.PROPERTY_ITEM_SCORE), findPatternSplitter, findPatternPartSplitter);
        excludeNodes = new MultiFindPattern(getStringProperty(properties, Constants.PROPERTY_EXCLUDE_NODES), multiFindPatternSplitter, findPatternSplitter, findPatternPartSplitter);

        delayDays = getIntegerProperty(properties, Constants.PROPERTY_DELAY_DAYS);
        itemDate = new FindPattern(getStringProperty(properties, Constants.PROPERTY_ITEM_DATE), findPatternSplitter, findPatternPartSplitter);
        dateFormat = new SimpleDateFormat(getStringProperty(properties, Constants.PROPERTY_DATE_FORMAT));
    }

    private Properties getPropertiesFile(File propertiesFile) {
        Assert.fileExists(propertiesFile);

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            AssertionError ae = new AssertionError("Wystąpił niespodziewany problem z plikiem! [" + propertiesFile.getAbsolutePath() + "]");
            ae.initCause(e);
            throw ae;
        }
        return properties;
    }

    private String getStringProperty(Properties properties, String propertyName) {
        String property = properties.getProperty(propertyName);
        Assert.notNull(property, "Brak właściwości [" + propertyName + "] w pliku.");
        return property;
    }

    private boolean getBooleanProperty(Properties properties, String propertyName) {
        String property = getStringProperty(properties, propertyName);
        if(Constants.PROPERTY_BOOLEAN_TRUE.equals(property)) {
            return true;
        } else if(Constants.PROPERTY_BOOLEAN_FALSE.equals(property)) {
            return false;
        } else {
            throw new AssertionError("Właściwość [" + propertyName + "] ma wartość [" + property + "] a spodziewano się " +
                    "jednej z: [" + Constants.PROPERTY_BOOLEAN_FALSE + ", " + Constants.PROPERTY_BOOLEAN_TRUE + "]");
        }
    }

    private Integer getIntegerProperty(Properties properties, String propertyName) {
        String property = getStringProperty(properties, propertyName);
        Assert.isNumeric(property);
        return Integer.valueOf(property);
    }

    public Integer getPagesToCheck() {
        return pagesToCheck;
    }

    public String getListPage() {
        return listPage;
    }

    private MatchingResult apply(FindPattern pattern, Node node) {
        return pattern.apply(node, false);
    }

    public Elements applyItemElementsPattern(Node document) {
        MatchingResult result = apply(itemElements, document);
        Assert.notNull(result.getElements(), "Nie znaleziono ani jednego elementu.");
        return result.getElements();
    }

    public boolean isNodeExcluded(Node node) {
        return excludeNodes.isNodeMatched(node);
    }

    public String applyUrlPattern(Node node) {
        MatchingResult result = apply(itemUrl, node);
        Assert.notNull(result.getNode(), "Nie znaleziono elementu url.");
        Assert.stringNotEmpty(result.getNode().absUrl("href"));
        return result.getNode().absUrl("href");
    }

    public Integer applyItemIdPattern(Node node) {
        MatchingResult result = apply(itemId, node);
        Assert.isNumeric(result.getString());
        return Integer.parseInt(result.getString());
    }

    public Integer applyScorePattern(Node node) {
        MatchingResult result = apply(itemScore, node);
        Assert.isNumeric(result.getString());
        return Integer.parseInt(result.getString());
    }

    public Date applyDatePattern(Node node) throws ParseException {
        MatchingResult result = apply(itemDate, node);
        Assert.stringNotEmpty(result.getString());
        return new Date(dateFormat.parse(result.getString()).getTime());
    }

    public String getConfigName() {
        return configName;
    }

    public Integer getScoreThreshold() {
        return scoreThreshold;
    }

    public Integer getDelayDays() {
        return delayDays;
    }

    public boolean getActive() {
        return active;
    }
}
