package com.linhei.datainput.thread;

import com.linhei.datainput.entity.FileSetting;
import com.linhei.datainput.main.DataInput;
import com.linhei.datainput.utils.FileOperate;
import com.linhei.datainput.utils.FileUtilsS;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * @author linhei
 * @date 2022.5.12
 */
public class GetDataThread extends Thread {


    FileSetting fileSetting;

    /**
     * @param fullFileName 文件名
     * @param fileName     文件名
     * @param filePath     文件路径
     */
    public GetDataThread(String fullFileName, String fileName, String filePath) {
        // 获取key
        String key = DataInput.getKey();
        fileSetting = new FileSetting(fullFileName, fileName, filePath, key);
    }


    @Override
    public void run() {

        // 打印线程名和读取的文件
        System.out.println(Thread.currentThread().getName() + "正在执行:" + "fileName=" + fileSetting.getFileName());
        // 调用方法
        try {
            createClass(fileSetting.getFullFileName(), fileSetting.getFileName(), fileSetting.getFilePath());
        } catch (IOException e) {
            DataInput.log("createClass", e);
        }

    }


    /**
     * 数据导入线程的具体实现方法
     *
     * @param fullFileName 完整文件名
     * @param tableName    文件名
     * @param filePath     文件路径
     * @throws IOException IO异常
     */
    void createClass(String fullFileName, String tableName, String filePath) throws IOException {

        // 执行创建方法
//        input("createTable", tableName, fileSetting.getKey(), null);

        // 判断数据是否匹配 防止将数据导入错误的表中
        if (fullFileName.contains(tableName)) {

            // 使用Commons IO读取文件
            LineIterator it = FileUtils.lineIterator(new File(filePath + fullFileName), "UTF-8");
            while (it.hasNext()) {
                // 以,分隔读取的数据
                String[] split = it.nextLine().split(",");
                // 调用方法实现数据导入
                input("insert", "user_" + tableName, fileSetting.getKey(), String.format("id=%s&hex=%s", split[0], split[1]));
            }
            // 关闭流
            it.close();
            // 删除数据源文件 并将结果写入log文件
            FileUtilsS.fileLinesWrite("//opt//javaApps//log//inputLog.txt", FileOperate.deleteFile(filePath, fullFileName), true);
        }

        /*
        // 此方法对内存需求极大 故而改用Apache.commons.io.FileUtils和行迭代器
        // 将文件中的数据添加到List中
        List<String> list = FileUtilsS.readFileContent(filePath + fullFileName);
        if (fullFileName.contains(tableName)) {

            Iterator<String> iterable = list.iterator();
            while (iterable.hasNext()) {
                String[] split = iterable.next().split(",");
                input("insert", "user_" + tableName, key, String.format("id=%s&hex=%s", split[0], split[1]));
                iterable.remove();
            }

            // 遍历数据
            for (String s : list) {
                String[] str = s.split(",");
                // 执行导入方法
                input("insert", "user_" + tableName, key, String.format("id=%s&hex=%s", str[0], str[1]));
                str.clone();

            }

        }
        list.clear();
*/
    }

    /**
     * 导入方法
     *
     * @param method    导入方法名
     * @param tableName 表名
     * @param key       密钥
     * @param user      导入的具体内容
     */
    public void input(String method, String tableName, String key, String user) {
        BufferedReader bufIn = null;

        try {
            // 本地url
            URL url = new URL("http://127.0.0.1:2550/user/" + method + "?" + "tableName=" + tableName + "&key=" + key + "&" + user);

            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            //设置请求属性，有部分网站不加这句话会抛出IOException: Server returned HTTP response code: 403 for URL异常
            //如：b站
            urlConn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36");

            if (method.contains("insert")) {
                urlConn.setRequestMethod("POST");
            }

            bufIn = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

        } catch (IOException e) {
            // 判断是否为 HTTP500
            if (e.getMessage().contains("Server returned HTTP response code: 500")) {
                // 若出现IO异常则直接返回
                return;
            }

            e.printStackTrace();
            DataInput.log("input\tURLConnection:", e);
        }


        String line = "";
        StringBuilder textStr = new StringBuilder();
        while (line != null) {
            //将2行内容追加到textStr字符串中 由于是自己写的api故而只读取2行
            int i = 0;
            while (i <= 1) {

                try {
                    assert bufIn != null;
                    line = bufIn.readLine();

                    if (line != null) {
                        textStr.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    DataInput.log("input\t防止空指针异常", e);
                }
                i++;
            }
        }
        String test = textStr.toString();
        // 若出现未设置key或key错误则重新调用方法
        if ("密钥为空".equals(test) || "密钥错误，请确认后重试".equals(test)) {
            System.out.println("key==null or key != KEY" + Thread.currentThread().getName());
            // 调用方法更新key
            key = DataInput.getKey();
            this.fileSetting = new FileSetting(this.fileSetting.getFullFileName(), this.fileSetting.getFileName(), this.fileSetting.getFilePath(), key);
            // 再次调用方法重试
            input(method, tableName, key, user);
        } else if ("导入失败".equals(test) || "".equals(test)) {
            System.out.println("调用方法失败 请重试");
        }


    }


}
