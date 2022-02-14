package cn.cjx913.httpdiffy.content;

import cn.cjx913.httpdiffy.jsondiff.JsonPath;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class PrimaryHttpDiffContentTest {

    private int path(Iterator<String> iterator, Map<String, Object> path, Map<String, Integer> map) {
        int hashCode = 0;
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = path.get(key);
            if (value instanceof JSON) {
                hashCode += path(iterator, path, map);
                map.put(key, hashCode);
            }
            else {
                int code = Objects.hashCode(key) ^ Objects.hashCode(value);
                map.put(key,code);
                hashCode += code;
            }
        }
        return hashCode;
    }

    private Map<String, Integer> path(Map<String, Object> path) {
        Set<String> keySet = path.keySet();

        Iterator<String> iterator = keySet.iterator();

        if (!iterator.hasNext()) return new LinkedHashMap<>(0);

        Map<String, Integer> map = new LinkedHashMap<>();

        path(iterator, path, map);

        return map;
    }

    @Test
    public void test() {
        String primary = "{\"name\": \"secondary\", \"path\": \"/test/arr\", \"method\": \"POST\", \"formData\": {}, \"httpStatus\": \"OK\", \"queryParams\": {\"username\": [\"cjx913\"]}, \"requestBody\": [{\"ss\": true, \"222\": 0, \"aaa\": \"asf\", \"aaaf\": 0.22}, {\"ss\": true, \"222\": 0, \"aaa\": \"asf\", \"aaaf\": 0.221}], \"responseBody\": [[{\"ss\": true, \"222\": 0, \"aaa\": \"asf\", \"aaaf\": 0.22}, {\"ss\": true, \"222\": 0, \"aaa\": \"asf\", \"aaaf\": 0.221}], \"test\", \"2022-01-22T19:22:53.575\", \"cjx913\", 1, true, -1034778346, 2303015366001475437, 0.7066225306085453, false, \"c1b73f9b-3397-4186-8445-c4e8a34bb88d\", [\"a\", \"b\", \"c\", 1537942650, true, 0.17889547776545722, \"47af3c5f-1978-427f-b305-42feed5cf4ad\"]], \"requestHeaders\": {\"Host\": [\"localhost:8080\"], \"Accept\": [\"*/*\"], \"Connection\": [\"keep-alive\"], \"User-Agent\": [\"PostmanRuntime/7.28.4\"], \"Content-Type\": [\"application/json\"], \"Postman-Token\": [\"15de3387-91c3-462b-96b3-06740adf4687\"], \"content-length\": [\"138\"], \"Accept-Encoding\": [\"gzip, deflate, br\"]}, \"responseHeaders\": {\"Content-Type\": [\"application/json\"], \"content-length\": [\"326\"]}}";
        String secondary = "{\"name\": \"primary\", \"path\": \"/test/arr\", \"method\": \"POST\", \"formData\": {}, \"httpStatus\": \"OK\", \"queryParams\": {\"username\": [\"cjx913\"]}, \"requestBody\": [{\"ss\": true, \"222\": 0, \"aaa\": \"asf\", \"aaaf\": 0.22}, {\"ss\": true, \"222\": 0, \"aaa\": \"asf\", \"aaaf\": 0.221}], \"responseBody\": [[{\"ss\": true, \"222\": 0, \"aaa\": \"asf\", \"aaaf\": 0.22}, {\"ss\": true, \"222\": 0, \"aaa\": \"asf\", \"aaaf\": 0.221}], \"test\", \"2022-01-22T19:22:53.572\", \"cjx913\", 1, true, -134847921, 7282223323530678692, 0.7497458142881993, false, \"52e24f1f-231e-4784-bbd6-3ce76c58e133\", [\"a\", \"b\", \"c\", -973921745, true, 0.10901610752048818, \"c2d62260-c2e8-4c37-8be3-8e727195fc87\"]], \"requestHeaders\": {\"Host\": [\"localhost:8080\"], \"Accept\": [\"*/*\"], \"Connection\": [\"keep-alive\"], \"User-Agent\": [\"PostmanRuntime/7.28.4\"], \"Content-Type\": [\"application/json\"], \"Postman-Token\": [\"15de3387-91c3-462b-96b3-06740adf4687\"], \"content-length\": [\"138\"], \"Accept-Encoding\": [\"gzip, deflate, br\"]}, \"responseHeaders\": {\"Content-Type\": [\"application/json\"], \"content-length\": [\"325\"]}}";
        JSONObject primaryJsonObject = JSON.parseObject(primary);
        JSONObject secondaryJsonObject = JSON.parseObject(secondary);
        Map<String, Object> primaryPaths = JsonPath.paths(primaryJsonObject);
        Map<String, Object> secondaryPaths = JsonPath.paths(secondaryJsonObject);
        System.out.println(primaryPaths);
        System.out.println(secondaryPaths);

        path(primaryPaths);

        String s = "$['responseBody'][0][1]['ss']";
        Pattern pattern = Pattern.compile("^\\$(?:\\[(.*?)\\])*");
        Matcher matcher = pattern.matcher(s);
        if (matcher.matches()) {
            int count = matcher.groupCount();
            System.out.println(count);
            System.out.println(matcher.group(1));
        }
    }


}