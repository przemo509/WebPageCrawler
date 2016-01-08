package pl.ps.properties;

public enum FindPatternPartTypeEnum {
    PARENT_NODE("parent"),
    PREVOIUS_NODE("prev"),
    NEXT_NODE("next"),
    CSS("css"),
    HTML_TAG_ATTRIBUTE("attr"),
    REGULAR_EXPRESSION("regexp");

    private String typeCode;

    private FindPatternPartTypeEnum(String typeCode) {
        this.typeCode = typeCode;
    }

    public static FindPatternPartTypeEnum getByCode(String typeCode) {
        if (typeCode.equals(PARENT_NODE.typeCode)) {
            return PARENT_NODE;
        } if (typeCode.equals(PREVOIUS_NODE.typeCode)) {
            return PREVOIUS_NODE;
        } if (typeCode.equals(NEXT_NODE.typeCode)) {
            return NEXT_NODE;
        } if (typeCode.equals(CSS.typeCode)) {
            return CSS;
        } if (typeCode.equals(HTML_TAG_ATTRIBUTE.typeCode)) {
            return HTML_TAG_ATTRIBUTE;
        } if (typeCode.equals(REGULAR_EXPRESSION.typeCode)) {
            return REGULAR_EXPRESSION;
        } else {
            throw new AssertionError("FindPatternPartTypeEnum: nieobsługiwany kod typu: [" + typeCode + "]");
        }
    }
}
