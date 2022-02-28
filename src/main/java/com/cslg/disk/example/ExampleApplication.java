package com.cslg.disk.example;

import com.google.common.collect.Lists;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.SpringApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@SpringBootApplication
@EnableSwagger2
@EnableJpaAuditing
@Slf4j
public class ExampleApplication extends SpringBootServletInitializer {

    public static void main(String[] args){
        SpringApplication.run(ExampleApplication.class, args);
    }
    public void writeIntoUS() {
        String excelPath = "C:\\Users\\user\\Desktop\\langs.xlsx";
        File excel = new File(excelPath);
        Properties properties = new Properties();
        if (excel.exists()) {
            String[] split = excel.getName().split("\\.");
            try {
                Workbook wb = new XSSFWorkbook(excel);
                Sheet sheet = wb.getSheetAt(0);
                int firstRowIndex = sheet.getFirstRowNum()+1;   //第一行是列名，所以不读
                int lastRowIndex = sheet.getLastRowNum();
                List<List<Cell>> lists = new ArrayList<>();
                for(int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行
                    System.out.println("rIndex: " + rIndex);
                    Row row = sheet.getRow(rIndex);
                    List<Cell> list = Lists.newArrayList();
                    if (row != null) {
                        int firstCellIndex = row.getFirstCellNum();
                        int lastCellIndex = row.getLastCellNum();
                        for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {
                            list.add(row.getCell(cIndex));//遍历列
//                            Cell cell = row.getCell(cIndex);
//                            if (cell != null) {
//                                System.out.println(cell.toString());
//                            }
                        }
                    }
                    lists.add(list);
                }
                FileOutputStream fileOutputStream = new FileOutputStream("E:\\毕设\\disk-code-backend\\src\\main\\resources\\static\\en-US.properties");
                lists.stream().forEach(e -> {
                    properties.setProperty(e.get(0).getStringCellValue(), e.get(4).getStringCellValue());
                });
                properties.store(fileOutputStream,null);
                log.info("导入成功");
                fileOutputStream.flush();
                fileOutputStream.close();
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeIntoCN() {
        String excelPath = "C:\\Users\\user\\Desktop\\langs.xlsx";
        File excel = new File(excelPath);
        Properties properties = new Properties();
        if (excel.exists()) {
            String[] split = excel.getName().split("\\.");
            try {
                Workbook wb = new XSSFWorkbook(excel);
                Sheet sheet = wb.getSheetAt(0);
                int firstRowIndex = sheet.getFirstRowNum()+1;   //第一行是列名，所以不读
                int lastRowIndex = sheet.getLastRowNum();
                List<List<Cell>> lists = new ArrayList<>();
                for(int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行
                    System.out.println("rIndex: " + rIndex);
                    Row row = sheet.getRow(rIndex);
                    List<Cell> list = Lists.newArrayList();
                    if (row != null) {
                        int firstCellIndex = row.getFirstCellNum();
                        int lastCellIndex = row.getLastCellNum();
                        for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {
                            list.add(row.getCell(cIndex));//遍历列
//                            Cell cell = row.getCell(cIndex);
//                            if (cell != null) {
//                                System.out.println(cell.toString());
//                            }
                        }
                    }
                    lists.add(list);
                }
                FileOutputStream fileOutputStream = new FileOutputStream("E:\\毕设\\disk-code-backend\\src\\main\\resources\\static\\zh-CN.properties");
                lists.stream().forEach(e -> {
                    properties.setProperty(e.get(0).getStringCellValue(), e.get(3).getStringCellValue());
                });
                properties.store(fileOutputStream,null);
                log.info("导入成功");
                fileOutputStream.flush();
                fileOutputStream.close();
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            }
        }
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
