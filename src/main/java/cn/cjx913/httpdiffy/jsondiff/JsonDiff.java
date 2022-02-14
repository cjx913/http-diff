package cn.cjx913.httpdiffy.jsondiff;

public abstract class JsonDiff {
    /**
     * 一致
     */
    public final static Integer NONE = 0B00000000;
    /**
     * 不一致：新增
     */
    public final static Integer NEW = 0B00000001;
    /**
     * 不一致：删除
     */
    public final static Integer DELETE = 0B00000010;
    /**
     * 不一致：类型
     */
    public final static Integer TYPE = 0B00000100;
    /**
     * 不一致：值
     */
    public final static Integer VALUE = 0B00010000;
    /**
     * 不一致：长度
     */
    public final static Integer SIZE = 0B00100000;
    /**
     * 不一致：精度
     */
    public final static Integer SCALE = 0B01000000;
    /**
     * 不一致：位置
     */
    public final static Integer POSITION = 0B10000000;


}
