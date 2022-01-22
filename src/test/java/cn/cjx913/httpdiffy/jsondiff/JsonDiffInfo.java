package cn.cjx913.httpdiffy.jsondiff;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class JsonDiffInfo {
    private void test(Map<String, Object> map, @NonNull String key) {
        Set<String> keySet = map.keySet();
        Object o = map.get(key);
        if (o instanceof JSONArray) {
            String k = "/".equals(key) ? key : key + "/";
            List<String> arrayKeys = keySet.stream()
                    .filter(s -> !(s == null || s.equals(key)
                            || s.indexOf(k) != 0 || s.indexOf("/", k.length()) > -1))
                    .collect(Collectors.toList());
            long hashCode = 0L;
            for (String arrayKey : arrayKeys) {
                Object o1 = map.get(arrayKey);
                if (o1 instanceof JSONArray) {
                    test(map, arrayKey);
                    o1 = map.get(arrayKey);
                }else if(o1 instanceof JSONObject){
                    JSONObject jsonObject = (JSONObject) o1;
                    jsonObject.forEach((n,v)->{
                        if(v instanceof JSONArray){
                            test(map, n);
                        }
                    });
                }
                hashCode = 31 * hashCode + (arrayKey != null ? arrayKey.hashCode() : 0L);
                hashCode = 31 * hashCode + (o1 != null ? o1.hashCode() : 0L);
            }
            map.put(key, hashCode);
        }
    }

    @Test
    public void test() {
        String s0 = "{\"name\": \"candidate\", \"path\": \"/test\", \"method\": \"GET\", \"formData\": {}, \"httpStatus\": \"OK\", \"queryParams\": {\"username\": [\"cjx913\"]}, \"requestBody\": null, \"responseBody\": {\"arr\": [\"a\", \"b\", \"c\", -1837761043, true, 0.34540833543984595], \"now\": \"2451-10-27T06:16:52.906\", \"body\": {}, \"data\": {\"aa\": \"ss\", \"nextFloat\": 0.59810996, \"nextDouble\": 0.6717755100700573, \"nextBoolean\": false}, \"username\": \"cjx913\"}, \"requestHeaders\": {\"Host\": [\"localhost:8080\"], \"Accept\": [\"*/*\"], \"Connection\": [\"keep-alive\"], \"User-Agent\": [\"PostmanRuntime/7.28.4\"], \"Content-Type\": [\"application/json\"], \"Postman-Token\": [\"62f56606-6d0a-43e4-8777-a449c5324cdf\"], \"Accept-Encoding\": [\"gzip, deflate, br\"]}, \"responseHeaders\": {\"Content-Type\": [\"application/json\"], \"content-length\": [\"214\"]}}";
        Object parse0 = JSON.parse(s0);
        System.out.println(parse0);

        String s1 = "{ \"path\": \"/test\",\"name\": \"candidate\", \"method\": \"GET\", \"formData\": {}, \"httpStatus\": \"OK\", \"queryParams\": {\"username\": [\"cjx913\"]}, \"requestBody\": null, \"responseBody\": {\"arr\": [\"a\", \"c\", \"b\", -1837761043, true, 0.34540833543984595], \"now\": \"2451-10-27T06:16:52.906\", \"body\": {}, \"data\": {\"aa\": \"ss\", \"nextFloat\": 0.59810996, \"nextDouble\": 0.6717755100700573, \"nextBoolean\": false}, \"username\": \"cjx913\"}, \"requestHeaders\": {\"Host\": [\"localhost:8080\"], \"Accept\": [\"*/*\"], \"Connection\": [\"keep-alive\"], \"User-Agent\": [\"PostmanRuntime/7.28.4\"], \"Content-Type\": [\"application/json\"], \"Postman-Token\": [\"62f56606-6d0a-43e4-8777-a449c5324cdf\"], \"Accept-Encoding\": [\"gzip, deflate, br\"]}, \"responseHeaders\": {\"Content-Type\": [\"application/json\"], \"content-length\": [\"214\"]}}";
        Object parse1 = JSON.parse(s1);
        System.out.println(parse1);

        System.out.println(parse0.equals(parse1));

        String s2 = "[{\"name\": \"master1\", \"path\": \"/test\", \"method\": \"GET\", \"formData\": {}, \"httpStatus\": \"OK\", \"queryParams\": {\"username\": [\"cjx913\"]}, \"requestBody\": null, \"responseBody\": {\"arr\": [\"a\", \"b\", \"c\", -860715306, false, 0.9152532885681344], \"now\": \"-1360-04-28T19:36:52.924\", \"body\": {}, \"data\": {\"aa\": \"ss\", \"nextFloat\": 0.9978352, \"nextDouble\": 0.9655744565806972, \"nextBoolean\": true}, \"username\": \"cjx913\"}, \"requestHeaders\": {\"Host\": [\"localhost:8080\"], \"Accept\": [\"*/*\"], \"Connection\": [\"keep-alive\"], \"User-Agent\": [\"PostmanRuntime/7.28.4\"], \"Content-Type\": [\"application/json\"], \"Postman-Token\": [\"62f56606-6d0a-43e4-8777-a449c5324cdf\"], \"Accept-Encoding\": [\"gzip, deflate, br\"]}, \"responseHeaders\": {\"Content-Type\": [\"application/json\"], \"content-length\": [\"212\"]}}, {\"name\": \"master2\", \"path\": \"/test\", \"method\": \"GET\", \"formData\": {\"$ref\": \"$[0].formData\"}, \"httpStatus\": \"OK\", \"queryParams\": {\"$ref\": \"$[0].queryParams\"}, \"requestBody\": null, \"responseBody\": {\"arr\": [\"a\", \"b\", \"c\", 576640713, true, 0.04390136321216953], \"now\": \"5229-07-01T04:50:52.94\", \"body\": {}, \"data\": {\"aa\": \"ss\", \"nextFloat\": 0.097783625, \"nextDouble\": 0.4374053423644533, \"nextBoolean\": true}, \"username\": \"cjx913\"}, \"requestHeaders\": {\"$ref\": \"$[0].requestHeaders\"}, \"responseHeaders\": {\"Content-Type\": [\"application/json\"], \"content-length\": [\"211\"]}}, {\"name\": \"master3\", \"path\": \"/test\", \"method\": \"GET\", \"formData\": {\"$ref\": \"$[0].formData\"}, \"httpStatus\": \"OK\", \"queryParams\": {\"$ref\": \"$[0].queryParams\"}, \"requestBody\": null, \"responseBody\": {\"arr\": [\"a\", \"b\", \"c\", -959925739, true, 0.16441020093827863], \"now\": \"3042-06-08T21:20:52.95\", \"body\": {}, \"data\": {\"aa\": \"ss\", \"nextFloat\": 0.4776551, \"nextDouble\": 0.20574510278720184, \"nextBoolean\": false}, \"username\": \"cjx913\"}, \"requestHeaders\": {\"$ref\": \"$[0].requestHeaders\"}, \"responseHeaders\": {\"Content-Type\": [\"application/json\"], \"content-length\": [\"212\"]}}]";
        Object parse2 = JSON.parse(s2);
        System.out.println(parse2);

        Map<String, Object> paths = JSONPath.paths(parse2);
        TreeMap<String, Object> treeMap = new TreeMap<>((o1, o2) -> o1.compareTo(o2));
        treeMap.putAll(paths);

        test(treeMap, "/");


        String s3 = "\"你好！\"";
        Object parse3 = JSON.parse(s3);
        System.out.println(parse3);

        String s4 = "1";
        Object parse4 = JSON.parse(s4, Feature.UseBigDecimal);
        System.out.println(parse4);

        String s5 = "1.11";
        Object parse5 = JSON.parse(s5, Feature.UseBigDecimal);
        System.out.println(parse5);

        String s6 = "false";
        Object parse6 = JSON.parse(s6);
        System.out.println(parse6);
    }
}
