package com.linhei.datainput.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileOperate {

    /**
     * @param filePath 需要查询的文件目录
     * @return 所有的文件名列表，但去除后4个字符(.txt，.png等)
     */
    public static List<String> getFileName(String filePath) {

        // 获取文件列表

        String[] list = FileUtilsS.StringToArray(gatFileNameList(filePath)[0], ", ");
//        System.out.println(list[0]);
        List<String> fileName = new ArrayList<>();
        // 去除文件后缀，只取文件名
        for (String s : list) {
//            System.out.println(list.get(i).substring(0, list.get(i).length() - 4));
            // 通过lastIndexOf获取最后一次出现.的位置以只获取文件名
            try {
                fileName.add(s.substring(0, s.lastIndexOf(".")));
//                System.out.println(s.substring(0, s.lastIndexOf(".")));
            } catch (StringIndexOutOfBoundsException e) {
                // 当文件没有`.`时 直接将整个文件添加进字符串列表
                e.printStackTrace();
                fileName.add(s);
            } catch (Exception e) {
                return null;
            }
        }

        return fileName;
    }


    /**
     * @param filePath 文件路径
     * @return 获取到的完整文件名的字符串
     */
    public static String[] getFullFileName(String filePath) {

        // 获取文件列表
        String[] str = gatFileNameList(filePath);

//        List<String> list = Arrays.asList(str[0].split(", "));

        // 通过工具类以`, `分隔字符串

        return FileUtilsS.StringToArray(str[0], ", ");

    }

    /**
     * @param filePath 文件路径
     * @return 以fileNameList为key的值并去除[]
     */
    public static String[] gatFileNameList(String filePath) {

        // 获取带有文件夹内所有信息的HashMap
        HashMap<String, Object> map = FileUtilsS.getFilesName(filePath, null);

//        遍历hashMap的内容
//        for (Object value : map.values()) {
//            System.out.println(value);
//        }
//        System.out.println(map);
//        System.out.println(map.get("fileNameList"));
//        System.out.println(map);
        // 返回以fileNameList为key的值并去除[]并以字符串数组的方式接收
        return StringUtils.stripAll(new String[]{map.get("fileNameList").toString()}, "[]");
    }

    /**
     * @param filePath 文件路径
     * @return 删除信息
     */
    public static String deleteFile(String filePath, String fileName) {

        System.out.println(filePath + fileName);
        File file = new File(filePath + fileName);
        return file.delete() ? "文件删除成功:" + fileName : "文件删除失败:" + fileName;

    }
}
