package cn.cjx913.httpdiffy.jsondiff;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonPathTest {
    @Test
    public  void test() {

        Map<String,Object> map1 = new LinkedHashMap<>();
        map1.put("aa","aa");
        map1.put("aaa",null);
        map1.put(null,"aaa");
        Object o = JSON.toJSON(map1);

        String s0 = "{\"name\": \"candidate\", \"path\": \"/test\", \"method\": \"GET\", \"formData\": {}, \"httpStatus\": \"OK\", \"queryParams\": {\"username\": [\"cjx913\"]}, \"requestBody\": null, \"responseBody\": {\"arr\": [\"a\", \"b\", \"c\", -1837761043, true, 0.34540833543984595], \"now\": \"2451-10-27T06:16:52.906\", \"body\": {}, \"data\": {\"aa\": \"ss\", \"nextFloat\": 0.59810996, \"nextDouble\": 0.6717755100700573, \"nextBoolean\": false}, \"username\": \"cjx913\"}, \"requestHeaders\": {\"Host\": [\"localhost:8080\"], \"Accept\": [\"*/*\"], \"Connection\": [\"keep-alive\"], \"User-Agent\": [\"PostmanRuntime/7.28.4\"], \"Content-Type\": [\"application/json\"], \"Postman-Token\": [\"62f56606-6d0a-43e4-8777-a449c5324cdf\"], \"Accept-Encoding\": [\"gzip, deflate, br\"]}, \"responseHeaders\": {\"Content-Type\": [\"application/json\"], \"content-length\": [\"214\"]}}";
        JSONObject jsonObject = JSON.parseObject(s0);

        Map<String, Integer> hashCode = JsonDiffUtil.hashCode(jsonObject);

        Map<String, Object> paths = JsonPath.paths(jsonObject, "$");
        System.out.println(paths);

        Map<String, Integer> compare = JsonDiffUtil.compare(jsonObject, jsonObject);

        String s2 = "[{\"name\": \"master1\", \"path\": \"/test\", \"method\": \"GET\", \"formData\": {}, \"httpStatus\": \"OK\", \"queryParams\": {\"username\": [\"cjx913\"]}, \"requestBody\": null, \"responseBody\": {\"arr\": [\"a\", \"b\", \"c\", -860715306, false, 0.9152532885681344], \"now\": \"-1360-04-28T19:36:52.924\", \"body\": {}, \"data\": {\"aa\": \"ss\", \"nextFloat\": 0.9978352, \"nextDouble\": 0.9655744565806972, \"nextBoolean\": true}, \"username\": \"cjx913\"}, \"requestHeaders\": {\"Host\": [\"localhost:8080\"], \"Accept\": [\"*/*\"], \"Connection\": [\"keep-alive\"], \"User-Agent\": [\"PostmanRuntime/7.28.4\"], \"Content-Type\": [\"application/json\"], \"Postman-Token\": [\"62f56606-6d0a-43e4-8777-a449c5324cdf\"], \"Accept-Encoding\": [\"gzip, deflate, br\"]}, \"responseHeaders\": {\"Content-Type\": [\"application/json\"], \"content-length\": [\"212\"]}}, {\"name\": \"master2\", \"path\": \"/test\", \"method\": \"GET\", \"formData\": {\"$ref\": \"$[0].formData\"}, \"httpStatus\": \"OK\", \"queryParams\": {\"$ref\": \"$[0].queryParams\"}, \"requestBody\": null, \"responseBody\": {\"arr\": [\"a\", \"b\", \"c\", 576640713, true, 0.04390136321216953], \"now\": \"5229-07-01T04:50:52.94\", \"body\": {}, \"data\": {\"aa\": \"ss\", \"nextFloat\": 0.097783625, \"nextDouble\": 0.4374053423644533, \"nextBoolean\": true}, \"username\": \"cjx913\"}, \"requestHeaders\": {\"$ref\": \"$[0].requestHeaders\"}, \"responseHeaders\": {\"Content-Type\": [\"application/json\"], \"content-length\": [\"211\"]}}, {\"name\": \"master3\", \"path\": \"/test\", \"method\": \"GET\", \"formData\": {\"$ref\": \"$[0].formData\"}, \"httpStatus\": \"OK\", \"queryParams\": {\"$ref\": \"$[0].queryParams\"}, \"requestBody\": null, \"responseBody\": {\"arr\": [\"a\", \"b\", \"c\", -959925739, true, 0.16441020093827863], \"now\": \"3042-06-08T21:20:52.95\", \"body\": {}, \"data\": {\"aa\": \"ss\", \"nextFloat\": 0.4776551, \"nextDouble\": 0.20574510278720184, \"nextBoolean\": false}, \"username\": \"cjx913\"}, \"requestHeaders\": {\"$ref\": \"$[0].requestHeaders\"}, \"responseHeaders\": {\"Content-Type\": [\"application/json\"], \"content-length\": [\"212\"]}}]";
        JSONArray jsonArray = JSON.parseArray(s2);
        Map<String, Object> map = JsonPath.paths(jsonArray, "$");
        System.out.println(map);

        DiffInfo diffInfo = new DiffInfo("222", "33");
        DiffInfo diffInfo2 = new DiffInfo("22233", "3322");
        Map<String, Object> paths1 = JsonPath.paths(diffInfo);
        System.out.println(paths1);

        Map<String, Object> paths2 = JsonPath.paths(Arrays.asList(Arrays.asList(diffInfo, diffInfo2), Arrays.asList(diffInfo2, diffInfo)));
        System.out.println(paths2);

        Map<String, Object> paths3 = JsonPath.paths(false);
        System.out.println(paths3);
    }
}