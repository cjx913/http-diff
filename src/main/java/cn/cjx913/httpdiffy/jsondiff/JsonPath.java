package cn.cjx913.httpdiffy.jsondiff;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

public abstract class JsonPath {

    public static Map<String, Object> paths(Object json) {
        return paths(json, "$");
    }

    public static Map<String, Object> paths(Object o, String parent) {
        if (o == null) return new LinkedHashMap<>(0);
        if (o instanceof CharSequence
                || o instanceof Number
                || o instanceof Boolean) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>(1);
            map.put(parent, o);
            return map;
        }
        if (o instanceof JSON) {
            return paths((JSON) o, parent);
        }
        String string = JSON.toJSONString(o);
        JSON josn = (JSON) JSON.parse(string);
        return paths(josn, parent);
    }

    public static Map<String, Object> paths(JSON json, String parent) {
        if (json == null) return new LinkedHashMap<>(0);

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        if (parent == null) parent = "$";
        map.put(parent, json);

        if (json instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) json;
            for (int i = 0, size = jsonArray.size(); i < size; i++) {
                String parentPath = parent + "[" + i + "]";
                Object v = jsonArray.get(i);
                map.put(parentPath, v);
                if (v instanceof JSON) {
                    Map<String, Object> childrenPaths = paths((JSON) v, parentPath);
                    map.putAll(childrenPaths);
                }
            }
        } else if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;
            Set<String> keys = jsonObject.keySet();
            for (String key : keys) {
                String parentPath = parent + "['" + key + "']";
                Object v = jsonObject.get(key);
                map.put(parentPath, v);
                if (v instanceof JSON) {
                    Map<String, Object> childrenPaths = paths((JSON) v, parentPath);
                    map.putAll(childrenPaths);
                }
            }
        }
        return map;
    }
}
