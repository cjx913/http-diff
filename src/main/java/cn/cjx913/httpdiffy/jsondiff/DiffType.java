package cn.cjx913.httpdiffy.jsondiff;

import java.math.BigDecimal;

public enum DiffType {
    NONE("一致"),
    NEW("新增"), DELETE("删除"),
    VALUE("值不一致"), TYPE("类型不一致"), SIZE("长度不一致"), SCALE("精度不一致"), POSITION("位置不一致"),
    ;
    private String type;

    DiffType(String type) {
        this.type = type;
    }
}
