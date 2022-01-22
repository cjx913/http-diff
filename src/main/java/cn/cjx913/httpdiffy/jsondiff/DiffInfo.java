package cn.cjx913.httpdiffy.jsondiff;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@ToString
public class DiffInfo implements Serializable {
    @Getter
    private Object data;
    /**
     * 基准值
     */
    @Getter
    private Object baseData;
    @Getter
    private DiffType diffType;

    public DiffInfo(Object data, Object baseData) {
        if (data instanceof Character) {
            data = new String(new char[]{(char) data});
        }
        if (baseData instanceof Character) {
            baseData = new String(new char[]{(char) baseData});
        }
        this.data = data;
        this.baseData = baseData;
        compare();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiffInfo diffInfo = (DiffInfo) o;

        if (data != null ? !data.equals(diffInfo.data) : diffInfo.data != null) return false;
        if (baseData != null ? !baseData.equals(diffInfo.baseData) : diffInfo.baseData != null) return false;
        return diffType == diffInfo.diffType;
    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (baseData != null ? baseData.hashCode() : 0);
        result = 31 * result + (diffType != null ? diffType.hashCode() : 0);
        return result;
    }

    private void compare() {
        boolean dataIsNull = this.data == null;
        boolean baseDataIsNull = this.baseData == null;
        if (dataIsNull && baseDataIsNull) {
            diffType = DiffType.NONE;
            return;
        }
        if (!dataIsNull && baseDataIsNull) {
            diffType = DiffType.NEW;
            return;
        }
        if (dataIsNull && !baseDataIsNull) {
            diffType = DiffType.DELETE;
            return;
        }
        if ((!dataIsNull && baseDataIsNull) || (dataIsNull && !baseDataIsNull)) {
            diffType = DiffType.VALUE;
            return;
        }
        if ((this.data == this.baseData) || this.data.equals(this.baseData)) {
            diffType = DiffType.NONE;
            return;
        }

        boolean instanceofCharSequence = this.data instanceof CharSequence && this.baseData instanceof CharSequence;
        boolean instanceofNumber = this.data instanceof Number && this.baseData instanceof Number;
        boolean instanceofBoolean = this.data instanceof Boolean && this.baseData instanceof Boolean;
        boolean instanceofCollection = this.data instanceof Collection && this.baseData instanceof Collection;
        boolean instanceofMap = this.data instanceof Map && this.baseData instanceof Map;
        boolean instanceofArray = this.data.getClass().isArray() && this.baseData.getClass().isArray();
        if (!(instanceofCharSequence || instanceofNumber || instanceofBoolean || instanceofCollection || instanceofMap || instanceofArray)) {
            diffType = DiffType.TYPE;
            return;
        }

        if (instanceofCharSequence) {
            CharSequence dataCharSequence = (CharSequence) this.data;
            CharSequence baseDataCharSequence = (CharSequence) this.baseData;
            if (dataCharSequence.length() != baseDataCharSequence.length()) {
                diffType = DiffType.SIZE;
                return;
            } else if (dataCharSequence.toString().equals(baseDataCharSequence.toString())) {
                diffType = DiffType.NONE;
                return;
            } else {
                diffType = DiffType.VALUE;
                return;
            }
        }

        if (instanceofNumber) {
            BigDecimal dataBigDecimal = new BigDecimal(this.data.toString());
            BigDecimal baseDataBigDecimal = new BigDecimal(this.baseData.toString());
            int dataScale = dataBigDecimal.scale();
            int baseDataScale = baseDataBigDecimal.scale();
            int scale = dataBigDecimal.scale();
            if (dataScale > baseDataScale) {
                scale = baseDataScale;
            }
            dataBigDecimal = dataBigDecimal.setScale(scale, RoundingMode.HALF_DOWN);
            baseDataBigDecimal = baseDataBigDecimal.setScale(scale, RoundingMode.HALF_DOWN);

            if (dataBigDecimal.equals(baseDataBigDecimal)) {
                if (dataScale != baseDataScale) {
                    diffType = DiffType.SCALE;
                    return;
                } else {
                    diffType = DiffType.NONE;
                    return;
                }
            }
            diffType = DiffType.VALUE;
            return;
        }

        if (instanceofBoolean) {
            if (Boolean.compare((boolean) data, (boolean) baseData) == 0) {
                diffType = DiffType.NONE;
                return;
            } else {
                diffType = DiffType.VALUE;
                return;
            }
        }

        if (instanceofCollection) {
            Collection data = (Collection) this.data;
            Collection baseData = (Collection) this.baseData;
            int dataSize = data.size();
            int baseDataSize = data.size();
            if (dataSize != baseDataSize) {
                diffType = DiffType.SIZE;
                return;
            }


            MultiValueMap<Object, Integer> valueIndexMap = new LinkedMultiValueMap<>(dataSize);
            Iterator iterator = data.iterator();
            int index = 0;
            while (iterator.hasNext()) {
                Object next = iterator.next();
                valueIndexMap.add(next, index);
                ++index;
            }
            iterator = baseData.iterator();
            index = 0;
            while (iterator.hasNext()) {
                Object next = iterator.next();
                valueIndexMap.add(next, index);
                ++index;
            }


            Collection<List<Integer>> indexs = valueIndexMap.values();
            boolean match = indexs.stream().anyMatch(integers -> integers.size() != 2);
            if (match) {
                //把一个值的对象取出
                Set<Object> collect = valueIndexMap.entrySet().stream()
                        .filter(objectListEntry -> objectListEntry.getValue().size() != 2)
                        .map(objectListEntry -> objectListEntry.getKey())
                        .collect(Collectors.toSet());

                diffType = DiffType.VALUE;
                return;
            }
            match = indexs.stream().anyMatch(integers -> !integers.get(0).equals(integers.get(1)));
            if (match) {
                diffType = DiffType.POSITION;
                return;
            }
        }

        if (instanceofArray) {
            Object[] data = (Object[]) this.data;
            Object[] baseData = (Object[]) this.baseData;
            if (data.length != baseData.length) {
                diffType = DiffType.SIZE;
                return;
            }
        }

        diffType = DiffType.NONE;
        return;
    }

    private final static Comparator COMPARATOR = (o1, o2) -> o1.hashCode() > o2.hashCode() ? 1 : o1.hashCode() < o2.hashCode() ? -1 : 0;
}
