package cn.cjx913.httpdiffy.jsondiff;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class DiffInfoTest {
    @Test
    public void test() {

        System.out.println(new DiffInfo(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3)).compare());
        System.out.println(new DiffInfo(Arrays.asList(1, 2, 3), Arrays.asList(1, 3, 2)).compare());
        System.out.println(new DiffInfo(Arrays.asList(1, 2, "3"), Arrays.asList(1, 2, 3)).compare());
        System.out.println(new DiffInfo(Arrays.asList(1, 2, 3), Arrays.asList(1, 2)).compare());
        System.out.println(new DiffInfo(Arrays.asList(1, 2), Arrays.asList(1, 3, 2)).compare());
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("a", "a");
        map1.put("b", 1);
        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("a", "a");
        map2.put("b", 1);
        map2.put("c", 1.0);
        map2.put("d", false);
        System.out.println("map:" + new DiffInfo(map1, map2).compare());
        System.out.println(new DiffInfo(Arrays.asList(map1, map2), Arrays.asList(map1, map2)).compare());
        System.out.println(new DiffInfo(Arrays.asList(map1, map2), Arrays.asList(map2, map1)).compare());
        System.out.println(new DiffInfo(Arrays.asList(map1, map2), Arrays.asList(map1)).compare());
        HashMap<String, Object> map3 = new HashMap<>();
        map3.put("a", "a");
        map3.put("b", 1);
        map3.put("c", 1.0);
        map3.put("d", false);
        map3.put("e", Arrays.asList("a", "b", "c"));
        map3.put("f", map2);
        HashMap<String, Object> map4 = new HashMap<>();
        map4.put("a", "a");
        map4.put("b", 1);
        map4.put("c", 1.0);
        map4.put("d", false);
//        map4.put("e", Arrays.asList("a", "b", "c"));
        map4.put("f", map1);
        System.out.println("map:" + new DiffInfo(map3, map4).compare());
        System.out.println(new DiffInfo(Arrays.asList(map3, map4), Arrays.asList(map3, map4)).compare());//NONE
        HashMap<String, Object> map5 = new HashMap<>();
        map5.put("a", "a");
        map5.put("b", 1);
        map5.put("c", 1.0);
        map5.put("d", false);
        map5.put("e", Arrays.asList("a", "c", "b"));
        map5.put("f", map1);
        System.out.println(new DiffInfo(Arrays.asList(map3, map4), Arrays.asList(map3, map5)).compare());//POSITION

        System.out.println(new DiffInfo(1, 1).compare());
        System.out.println(new DiffInfo(233, 233).compare());
        System.out.println(new DiffInfo(233, 233L).compare());
        System.out.println(new DiffInfo(233D, 233L).compare());
        System.out.println(new DiffInfo(233.00D, 233F).compare());
        System.out.println(new DiffInfo(233.00D, 233.00F).compare());
        System.out.println(new DiffInfo(233.00D, 233.01F).compare());
        System.out.println(new DiffInfo(new BigDecimal("233.01"), new BigDecimal("233.01")).compare());
        System.out.println(new DiffInfo(new BigDecimal("233.01"), new BigDecimal("233.02")).compare());
        System.out.println(new DiffInfo(new BigDecimal("233.022"), new BigDecimal("233.02")).compare());
        System.out.println(new DiffInfo(new BigDecimal("233.012"), new BigDecimal("233.02")).compare());

        System.out.println(new DiffInfo("s", 's').compare());
        System.out.println(new DiffInfo("s", new String("s")).compare());
        System.out.println(new DiffInfo("s", new String("ss")).compare());
        System.out.println(new DiffInfo("s", new StringBuffer("s")).compare());
        System.out.println(new DiffInfo("s", new StringBuilder("s")).compare());
        System.out.println(new DiffInfo("ss", new StringBuilder("s")).compare());
        System.out.println(new DiffInfo(new StringBuilder("s"), new StringBuilder("s")).compare());
        System.out.println(new DiffInfo(new StringBuilder("ss"), new StringBuilder("s")).compare());

        System.out.println(new DiffInfo(true, false).compare());
        System.out.println(new DiffInfo(true, true).compare());
        System.out.println(new DiffInfo(true, Boolean.TRUE).compare());
        System.out.println(new DiffInfo(false, Boolean.FALSE).compare());
        System.out.println(new DiffInfo(Boolean.TRUE, Boolean.FALSE).compare());


    }
}