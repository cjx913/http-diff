package cn.cjx913.httpdiffy.jsondiff;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.*;

public abstract class JsonDiffUtil {

    public static Map<String, Integer> hashCode(Object data) {
        Map<String, Integer> map = new LinkedHashMap<>();
        hashCode(data, "$", map);
        return map;
    }

    private static void hashCode(Object data, String path, @NonNull Map<String, Integer> map) {
        if (data == null) map.put(path, 0);
        else if (data instanceof CharSequence
                || data instanceof Boolean
                || data instanceof Number) map.put(path, Objects.hashCode(data));
        else {
            Object json = data;
            if (json instanceof JSON == false)
                json = JSON.parse(JSON.toJSONString(json, SerializerFeature.WriteMapNullValue));
            if (json instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) json;
                Set<String> keys = jsonObject.keySet();
                int hashCode = 0;
                for (String key : keys) {
                    String childPath = path + "[\"" + key + "\"]";
                    hashCode(jsonObject.get(key), childPath, map);
                    Integer hc = map.getOrDefault(childPath, 0);
                    hashCode += key == null ? 0 : Objects.hashCode(key) ^ hc;
                }
                map.put(path, hashCode);
            } else if (json instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) json;
                int hashCode = 0;
                for (int i = 0, size = jsonArray.size(); i < size; i++) {
                    String childPath = path + "[" + i + "]";
                    hashCode(jsonArray.get(i), childPath, map);
                    Integer hc = map.getOrDefault(childPath, 0);
                    hashCode += Objects.hashCode(i) ^ hc;
                }
                map.put(path, hashCode);
            } else {
                map.put(path, Objects.hashCode(json));
            }

        }
    }

    public static Map<String, Integer> compare(Object data, Object baseData) {
        Map<String, Integer> map = new LinkedHashMap<>();
        compare(data, baseData, "$", map);
        return map;
    }

    private static Integer compare(Object data, Object baseData, String path, Map<String, Integer> map) {

        if (baseData == null && data == null) {
            map.put(path, JsonDiff.NONE);
            return JsonDiff.NONE;
        }
        if (baseData == null && data != null) {
            map.put(path, JsonDiff.NEW);
            return JsonDiff.NEW;
        }
        if (baseData != null && data == null) {
            map.put(path, JsonDiff.DELETE);
            return JsonDiff.DELETE;
        }

        if (data instanceof CharSequence) {
            Integer diffType = JsonDiff.NONE;
            if (baseData instanceof CharSequence) {
                if (!data.equals(baseData)) {
                    diffType = JsonDiff.VALUE;
                    if (((CharSequence) data).length() != ((CharSequence) baseData).length()) {
                        diffType = diffType ^ JsonDiff.SIZE;
                    }
                }
            } else {
                diffType = JsonDiff.TYPE;
            }
            map.put(path, diffType);
            return diffType;
        }
        if (data instanceof Boolean) {
            Integer diffType = JsonDiff.NONE;
            if (baseData instanceof Boolean) {
                if (!data.equals(baseData)) {
                    map.put(path, JsonDiff.VALUE);
                }
            } else {
                diffType = JsonDiff.TYPE;
            }
            map.put(path, diffType);
            return diffType;
        }
        if (data instanceof Number) {
            Integer diffType = JsonDiff.NONE;
            if (baseData instanceof Number) {
                BigDecimal dataBigDecimal = new BigDecimal(data.toString());
                BigDecimal baseDataBigDecimal = new BigDecimal(baseData.toString());

                int dataScale = dataBigDecimal.scale();
                int baseDataScale = baseDataBigDecimal.scale();

                if (dataScale != baseDataScale) {
                    diffType = JsonDiff.VALUE ^ JsonDiff.SCALE;
                } else {
                    if (!dataBigDecimal.equals(baseDataBigDecimal)) {
                        diffType = JsonDiff.VALUE;
                    }
                }
            } else {
                diffType = JsonDiff.TYPE;
            }
            map.put(path, diffType);
            return diffType;
        }

        if (data instanceof JSON == false)
            data = JSON.parse(JSON.toJSONString(data, SerializerFeature.WriteMapNullValue));
        if (baseData instanceof JSON == false)
            baseData = JSON.parse(JSON.toJSONString(baseData, SerializerFeature.WriteMapNullValue));

        if (data instanceof JSONObject) {
            Integer diffType = JsonDiff.NONE;
            if (baseData instanceof JSONObject == false) {
                diffType = JsonDiff.TYPE;
            } else {
                JSONObject dataJsonObject = (JSONObject) data;
                JSONObject baseDataJsonObject = (JSONObject) baseData;
                int dataSize = dataJsonObject.size();
                int baseDataSize = baseDataJsonObject.size();
                if (dataSize != baseDataSize) {
                    diffType = JsonDiff.VALUE ^ JsonDiff.SIZE;
                } else {
                    Set<String> baseDataKeySet = baseDataJsonObject.keySet();
                    Set<String> dataKeySet = dataJsonObject.keySet();

                    Set<String> sameKeys = new LinkedHashSet<>();
                    for (String key : dataKeySet) {
                        if (baseDataKeySet.contains(key)) {
                            sameKeys.add(key);
                        } else {
                            map.put(path + "[\"" + key + "\"]", JsonDiff.NEW);
                            diffType = diffType ^ JsonDiff.NEW;
                        }
                    }
                    for (String key : baseDataKeySet) {
                        if (sameKeys.contains(key)) continue;

                        map.put(path + "[\"" + key + "\"]", JsonDiff.DELETE);
                        diffType = diffType ^ JsonDiff.DELETE;
                    }

                    for (String sameKey : sameKeys) {
                        Integer jsonDiff = compare(dataJsonObject.get(sameKey), baseDataJsonObject.get(sameKey),
                                path + "[\"" + sameKey + "\"]", map);
                        diffType = diffType ^ jsonDiff;
                    }
                    map.put(path, diffType);
                }
            }
            map.put(path, diffType);
            return diffType;
        }

        if (data instanceof JSONArray) {
            if (baseData instanceof JSONArray) {
                JSONArray dataJsonArray = (JSONArray) data;
                JSONArray baseDataJsonArray = (JSONArray) baseData;
                if (dataJsonArray.size() != baseDataJsonArray.size()) {
                    map.put(path, JsonDiff.SIZE);
                }
            } else {
                map.put(path, JsonDiff.TYPE);
            }
        }

        return JsonDiff.NONE;
    }
}
