package cn.cjx913.httpdiffy.jsondiff;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@ToString
public class DiffInfo implements Serializable {
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

    @Getter
    private String path;
    @Getter
    private String jsonType;
    @Getter
    private int diff;
    @Getter
    private Object data;
    /**
     * 基准值
     */
    @Getter
    private Object baseData;

    public DiffInfo(Object data, Object baseData) {
        this(data, baseData, "$");
    }

    public DiffInfo(Object data, Object baseData, String path) {
        if (data instanceof Character) {
            data = new String(new char[]{(char) data});
        }
        if (baseData instanceof Character) {
            baseData = new String(new char[]{(char) baseData});
        }
        if (!(data instanceof JSON || data instanceof CharSequence || data instanceof Number || data instanceof Boolean)) {
            data = JSON.toJSON(data);
        }
        if (!(baseData instanceof JSON || baseData instanceof CharSequence || baseData instanceof Number || baseData instanceof Boolean)) {
            baseData = JSON.toJSON(baseData);
        }

        this.data = data;
        this.baseData = baseData;
        this.path = path;
    }

    private final static Comparator COMPARATOR = (o1, o2) -> o1.hashCode() > o2.hashCode() ? 1 : o1.hashCode() < o2.hashCode() ? -1 : 0;

    private boolean isBaseJsonType(Object o) {
        return o == null || o instanceof CharSequence || o instanceof Number || o instanceof Boolean;
    }

    private String getJsonType(Object o) {
        if (o == null) return "null";
        else if (o instanceof CharSequence) return "string";
        else if (o instanceof Number) return "number";
        else if (o instanceof Boolean) return "boolean";
        else if (o instanceof Collection || o.getClass().isArray()) return "array";
        else return "object";
    }

    public Map<String, DiffInfo> compare() {
        Map<String, DiffInfo> map = new LinkedHashMap<>();
        compare(map);
        return map;
    }

    /**
     * @param map
     */
    public void compare(Map<String, DiffInfo> map) {
        boolean dataIsNull = this.data == null;
        boolean baseDataIsNull = this.baseData == null;
        if (dataIsNull && baseDataIsNull) {
            this.jsonType = "null";
            this.diff = NONE;
            map.put(this.path, this);
            return;
        }

        /**
         * 值不为null，基准值为null;
         * 把新增的值也列出来，标记为NEW
         */
        if (!dataIsNull && baseDataIsNull) {
            this.jsonType = getJsonType(this.data);
            this.diff = NEW;
            map.put(this.path, this);

            if (this.data instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) this.data;
                Set keys = jsonObject.keySet();
                for (Object key : keys) {
                    DiffInfo diffInfo = new DiffInfo(jsonObject.get(key), null, this.path + "[\"" + key + "\"]");
                    diffInfo.compare(map);
                }
            } else if (this.data instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) this.data;
                for (int i = 0, size = jsonArray.size(); i < size; i++) {
                    DiffInfo diffInfo = new DiffInfo(jsonArray.get(i), null, this.path + "[" + i + "]");
                    diffInfo.compare(map);
                }
            }

            return;
        }
        /**
         * 值为null，基准值不为null;
         * 把删除的值也列出来，标记为DELETE
         */
        if (dataIsNull && !baseDataIsNull) {
            this.jsonType = getJsonType(this.baseData);
            this.diff = DELETE;
            map.put(this.path, this);

            if (this.baseData instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) this.baseData;
                Set keys = jsonObject.keySet();
                for (Object key : keys) {
                    DiffInfo diffInfo = new DiffInfo(null, jsonObject.get(key), this.path + "[\"" + key + "\"]");
                    diffInfo.compare(map);
                }
            } else if (this.baseData instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) this.baseData;
                for (int i = 0, size = jsonArray.size(); i < size; i++) {
                    DiffInfo diffInfo = new DiffInfo(null, jsonArray.get(i), this.path + "[" + i + "]");
                    diffInfo.compare(map);
                }
            }

            return;
        }

//这对直接比较不需要
//        if ((this.data == this.baseData) || this.data.equals(this.baseData)) {
//            this.diff = NONE;
//            map.put(this.path, this);
//            return;
//        }

        /**
         * NONE:值一致
         * VALUE:长度一致但值一致
         * VALUE ^ SIZE:长度一致并且值不一致
         */
        if (this.data instanceof CharSequence && this.baseData instanceof CharSequence) {
            this.jsonType = "string";
            CharSequence dataCharSequence = (CharSequence) this.data;
            CharSequence baseDataCharSequence = (CharSequence) this.baseData;
            if (dataCharSequence.toString().equals(baseDataCharSequence.toString())) {
                this.diff = NONE;
            } else if (dataCharSequence.length() != baseDataCharSequence.length()) {
                this.diff = VALUE ^ SIZE;
            } else {
                this.diff = VALUE;
            }
            map.put(this.path, this);
            return;
        }

        /**
         * 都是数值类型
         * NONE:完全一致
         * VALUE ^ SCALE:值并且精度不一致
         * NONE ^ SCALE:忽略精度值一致
         * VALUE:值不一致
         */
        if (this.data instanceof Number && this.baseData instanceof Number) {
            this.jsonType = "number";
            BigDecimal dataBigDecimal = new BigDecimal(this.data.toString());
            BigDecimal baseDataBigDecimal = new BigDecimal(this.baseData.toString());
            if (dataBigDecimal.equals(baseDataBigDecimal)) {
                this.diff = NONE;
                map.put(this.path, this);
                return;
            } else {
                int dataScale = dataBigDecimal.scale();
                int baseDataScale = baseDataBigDecimal.scale();
                int scale = baseDataBigDecimal.scale();
                if (dataScale < baseDataScale) {
                    scale = dataScale;
                }
                dataBigDecimal = dataBigDecimal.setScale(scale, RoundingMode.HALF_DOWN);
                baseDataBigDecimal = baseDataBigDecimal.setScale(scale, RoundingMode.HALF_DOWN);

                if (dataBigDecimal.equals(baseDataBigDecimal)) {
                    this.diff = NONE ^ SCALE;
                } else {
                    if (dataScale != baseDataScale) {
                        this.diff = VALUE ^ SCALE;
                    } else {
                        this.diff = VALUE;
                    }
                }
                map.put(this.path, this);
                return;
            }
        }

        /**
         *
         */
        if (this.data instanceof Boolean && this.baseData instanceof Boolean) {
            this.jsonType = "boolean";
            if (Boolean.compare((boolean) data, (boolean) baseData) == 0) {
                this.diff = NONE;
            } else {
                this.diff = VALUE;
            }
            map.put(this.path, this);
            return;
        }

        if (this.data instanceof JSONObject && this.baseData instanceof JSONObject) {
            this.jsonType = "object";
            map.put(this.path, this);
            JSONObject data = (JSONObject) this.data;
            JSONObject baseData = (JSONObject) this.baseData;

            int dataSize = data.size();
            int baseDataSize = baseData.size();
            if (data.equals(baseData)) {
                this.diff = NONE;
            } else if (dataSize != baseDataSize) {
                this.diff = SIZE;
            }

            Set keys = new HashSet(dataSize);
            keys.addAll(data.keySet());
            keys.addAll(baseData.keySet());
            int dataHashCode = 0;
            int baseDataHashCode = 0;
            for (Object key : keys) {
                String childPath = this.path + "[\"" + key + "\"]";
                DiffInfo diffInfo = new DiffInfo(data.get(key), baseData.get(key), childPath);
                diffInfo.compare(map);
                dataHashCode += 31 * dataHashCode + (Objects.hashCode(childPath) ^ Objects.hashCode(diffInfo.getData()));
                baseDataHashCode += 31 * baseDataHashCode + (Objects.hashCode(childPath) ^ Objects.hashCode(diffInfo.getBaseData()));
            }
            //修改data和baseBase
            this.data = dataHashCode;
            this.baseData = baseDataHashCode;
            if (this.data != null && !this.data.equals(this.baseData)) {
                this.diff = this.diff ^ VALUE;
            }

            return;
        }

        /**
         *
         */
        if (this.data instanceof JSONArray && this.baseData instanceof JSONArray) {
            this.jsonType = "array";
            map.put(this.path, this);
            JSONArray data = (JSONArray) this.data;
            JSONArray baseData = (JSONArray) this.baseData;
            if (data.equals(baseData)) {
                this.diff = NONE;
            }
            int dataSize = data.size();
            int baseDataSize = baseData.size();
            if (dataSize != baseDataSize) {
                this.diff = this.diff ^ SIZE;
            }

            int dataHashCode = 0;
            int baseDataHashCode = 0;
            if (dataSize < baseDataSize) {
                int i = 0;
                while (i < dataSize) {
                    DiffInfo diffInfo = new DiffInfo(data.get(i), baseData.get(i), this.path + "[" + i + "]");
                    diffInfo.compare(map);
                    dataHashCode = dataHashCode + Objects.hashCode(diffInfo.getData());
                    baseDataHashCode = baseDataHashCode + Objects.hashCode(diffInfo.getBaseData());
                    i++;
                }
                while (i < baseDataSize) {
                    DiffInfo diffInfo = new DiffInfo(null, baseData.get(i), this.path + "[" + i + "]");
                    diffInfo.compare(map);
                    dataHashCode = dataHashCode + Objects.hashCode(diffInfo.getData());
                    baseDataHashCode = baseDataHashCode + Objects.hashCode(diffInfo.getBaseData());
                    i++;
                }
            } else if (dataSize > baseDataSize) {
                int i = 0;
                while (i < baseDataSize) {
                    DiffInfo diffInfo = new DiffInfo(data.get(i), baseData.get(i), this.path + "[" + i + "]");
                    diffInfo.compare(map);
                    dataHashCode = dataHashCode + Objects.hashCode(diffInfo.getData());
                    baseDataHashCode = baseDataHashCode + Objects.hashCode(diffInfo.getBaseData());
                    i++;
                }
                while (i < dataSize) {
                    DiffInfo diffInfo = new DiffInfo(data.get(i), null, this.path + "[" + i + "]");
                    diffInfo.compare(map);
                    dataHashCode = dataHashCode + Objects.hashCode(diffInfo.getData());
                    baseDataHashCode = baseDataHashCode + Objects.hashCode(diffInfo.getBaseData());
                    i++;
                }
            } else {
                int i = 0;
                while (i < baseDataSize) {
                    DiffInfo diffInfo = new DiffInfo(data.get(i), baseData.get(i), this.path + "[" + i + "]");
                    diffInfo.compare(map);
                    dataHashCode = dataHashCode + Objects.hashCode(diffInfo.getData());
                    baseDataHashCode = baseDataHashCode + Objects.hashCode(diffInfo.getBaseData());
                    i++;
                }
            }
            this.data = dataHashCode;
            this.baseData = baseDataHashCode;
            if (this.data != null && !this.data.equals(this.baseData)) {
                this.diff = this.diff ^ VALUE;
            }
            return;

//            JSONObject dataJSONObject = new JSONObject(dataSize);
//            JSONObject baseDataJSONObject = new JSONObject(baseDataSize);
//            MultiValueMap<Object, Integer> valueIndexMap = new LinkedMultiValueMap<>(baseDataSize);
//            for (int i = 0; i < dataSize; i++) {
//                Object o = data.get(i);
//                dataJSONObject.put(String.valueOf(i), o);
//                valueIndexMap.add(o, i);
//            }
//            for (int i = 0; i < baseDataSize; i++) {
//                Object o = baseData.get(i);
//                dataJSONObject.put(String.valueOf(i), o);
//                valueIndexMap.add(o, i);
//            }
//
//            Collection<List<Integer>> indexs = valueIndexMap.values();
//            boolean match = indexs.stream().anyMatch(integers -> integers.size() != 2);
//            if (match) {
//                //把一个值的对象取出
//                Set<Object> collect = valueIndexMap.entrySet().stream()
//                        .filter(objectListEntry -> objectListEntry.getValue().size() != 2)
//                        .map(objectListEntry -> objectListEntry.getKey())
//                        .collect(Collectors.toSet());
//                this.diff = this.diff ^ VALUE;
//                map.put(this.path, this);
//                return;
//            }
//            match = indexs.stream().anyMatch(integers -> !integers.get(0).equals(integers.get(1)));
//            if (match) {
//                this.diff = this.diff ^ POSITION;
//                map.put(this.path, this);
//                return;
//            }
        }


        //类型不一致
        this.jsonType = getJsonType(this.baseData);
        this.diff = TYPE;
        map.put(this.path, this);

        if (!isBaseJsonType(this.baseData)) {
            if (this.baseData instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) this.baseData;
                Set keys = jsonObject.keySet();
                for (Object key : keys) {
                    DiffInfo diffInfo = new DiffInfo(null, jsonObject.get(key), this.path + "[\"" + key + "\"]");
                    diffInfo.compare(map);
                }
            } else if (this.baseData instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) this.baseData;
                for (int i = 0, size = jsonArray.size(); i < size; i++) {
                    DiffInfo diffInfo = new DiffInfo(null, jsonArray.get(i), this.path + "[" + i + "]");
                    diffInfo.compare(map);
                }
            }
        }


        return;
    }


}
