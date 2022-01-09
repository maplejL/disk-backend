package com.cslg.disk.example;

import com.alibaba.druid.sql.visitor.functions.Char;
import com.cslg.disk.config.SwaggerConfig;
import net.bytebuddy.utility.RandomString;
import sun.misc.BASE64Decoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import sun.misc.BASE64Encoder;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@SpringBootApplication
@EnableSwagger2
@EnableJpaAuditing
@Slf4j
@Import(SwaggerConfig.class)
public class ExampleApplication extends SpringBootServletInitializer {
    public String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static void main(String[] args) {

//        ExampleApplication exampleApplication = new ExampleApplication();
//        int[] nums1 = {-1,0,1,2,-1,-4};
//        System.out.println(exampleApplication.threeSum(nums1));
        SpringApplication.run(ExampleApplication.class, args);
    }


    public String valiate(String s) {
        String regix1="[a-z,A-Z]";
        if (!s.substring(0,1).matches(regix1)) {
            return "Wrong";
        }
        if (!s.substring(1,s.length()).matches("[0-9]")) {
            return "Wrong";
        }
        if (!s.matches("^[a-z0-9A-Z]+")) {
            return "Wrong";
        }
        return "Accept";
    }

    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);

        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < nums.length-2; i++) {
            if (i>0 && nums[i]==nums[i-1]) {
                continue;
            }
            for (int j = i+1; j < nums.length-1; j++) {
                if (j>i+1 && nums[j]==nums[j-1]) {
                    continue;
                }
                for (int k = j+1; k < nums.length; k++) {
                    if (k>j+1 && nums[k]==nums[k-1]) {
                        continue;
                    }
                    if (nums[i] + nums[j] + nums[k] == 0) {
                        List<Integer> item = new ArrayList<>();
                        item.add(nums[i]);
                        item.add(nums[j]);
                        item.add(nums[k]);
                        if (!result.contains(item)) {
                            result.add(item);
                        }
                    }
                }
            }
        }


//        List<Integer> left = Arrays.stream(nums).boxed().filter(e -> e<=0).collect(Collectors.toList());
//        List<Integer> right = Arrays.stream(nums).boxed().filter(e -> e>0).collect(Collectors.toList());
//        List<List<Integer>> result = new ArrayList<>();
//        if (left.get(left.size()-1) == 0) {
//            left.remove(left.size() -1);
//            left.stream().forEach(a -> {
//                List<Integer> collect = right.stream().filter(b -> b + a == 0).collect(Collectors.toList());
//                if (collect.size() > 0) {
//                    collect.add(0);
//                    collect.add(a);
//                    result.add(collect);
//                }
//            });
//            result.stream().findAny();
//            log.info("1");
//        } else {
//            right.stream().forEach(a -> {
//                List<Integer> collect = left.stream().filter(b -> Math.abs(b) < a).collect(Collectors.toList());
//                if (collect.size() >= 2) {
//                    for (int i = 0; i < collect.size()-1; i++) {
//                        for (int j = i+1; j < collect.size(); j++) {
//                            if (collect.get(i) + collect.get(j) + a == 0) {
//                                List<Integer> resultItem = new ArrayList<>();
//                                resultItem.add(collect.get(i));
//                                resultItem.add(collect.get(j));
//                                result.add(resultItem);
//                            }
//                        }
//                    }
//                }
//            });
//        }
        return null;
    }

}
