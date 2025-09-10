package com.serliunx.stc4j.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 键值对测试
 *
 * @author <a href="mailto:root@serliunx.com">SerLiunx</a>
 * @since 2025/8/30
 */
public class PairTest {

    @Test
    public void testPair() {
        Map<String, String> vm = new HashMap<>();
        vm.put("key1", "value1");
        vm.put("key2", "value2");

        List<Pair<String, String>> extract = Pair.extract(vm);

        extract.forEach(System.out::println);

        Pair<String, String> pair = extract.get(0);
        Pair<String, String> pair2 = Pair.of("key1", "value1");

        System.out.println(pair.equals(pair2));
    }
}
