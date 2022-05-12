package com.linhei.datainput.main;

import com.linhei.datainput.thread.GetDataThread;
import com.linhei.datainput.utils.FileOperate;
import com.linhei.datainput.utils.FileUtilsS;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author linhei
 * @date 2022.5.12
 */
public class DataInput {


    public static void main(String[] args) {

        // 数据文件路径
        String filePath = "//opt//data//uidToBiliHex//";
//        String filePath = "D:\\bigdata\\uidToBiliHex\\";


        // 获取不带后缀的文件名用于创建表
        List<String> fileName = FileOperate.getFileName(filePath);
        // 获取带后缀文件名，用于获取文件内数据
        String[] fullFileName = FileOperate.getFullFileName(filePath);

        //
//        for (int i = 0; i < Objects.requireNonNull(fileName).size(); i++) {
//        }


        // 标识符i
        int i = 0;
        // 使用while循环判断是否继续获取文件夹下所有文件的所有数据
        int size = Objects.requireNonNull(fileName).size();
        ExecutorService pool = Executors.newFixedThreadPool(6);
//        ThreadPoolExecutor pool = new ThreadPoolExecutor(5, 6, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));


        while (size > i) {

            // 判断本次循环是否创建6次且线程是否已经足够
            for (int j = 0; j < 6 && i < size; j++) {
                // 创建线程
                Thread thread = new GetDataThread(fullFileName[i], fileName.get(i), filePath);
                // 为线程命名
                thread.setName(fileName.get(i));
                // 将线程加入线程池
                pool.execute(thread);

                i++;


            }

            // 单独创建线程效率低于线程池 故而淘汰线程
            // 创建第一个线程
//            Thread t1 = new GetDataThread(fullFileName[i], fileName.get(i), filePath);
//            t1.start();
            // 判断线程1是否执行完毕
//            try {
//                t1.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

//            GetDataThread.createClass(fullFileName[i], fileName.get(i), filePath);
//            i++;



    /*
            // 判断是否创建第二个线程
            if (i < size) {
                Thread t2 = new GetDataThread(fullFileName[i], fileName.get(i), filePath);
                t2.start();
                i++;

                // 判断线程1,2是否执行完毕
                try {
                    t1.join();
                    t2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {

            }



            // 判断是否创建第三个线程
            if (i < size) {
                Thread t3 = new GetDataThread(fullFileName[i], fileName.get(i), filePath);
                t3.start();
                i++;
            }

            // 判断是否创建第4个线程
            if (i < size) {
                Thread t3 = new GetDataThread(fullFileName[i], fileName.get(i), filePath);
                t3.start();
                i++;
            }*/

        }
        pool.shutdown();
    }

    /**
     * 读取key文件内的key信息
     *
     * @return key
     */
    public static String getKey() {
        // key文件路径
        String keyPath = "//opt//javaApps//key//key.txt";
        // 读取key文件内的key
        try {
            LineIterator it = FileUtils.lineIterator(new File(keyPath), "UTF-8");
            if (it.hasNext()) {
                return it.nextLine();
            }
            it.close();
        } catch (IOException e) {
            DataInput.log("getKey\t", e);
        }
        return null;
    }


    /**
     * 将错误信息记录到日志文件中
     *
     * @param e Exception
     */
    public static void log(String methodName, Exception e) {
        FileUtilsS.fileLinesWrite("//opt//javaApps//log//log.txt", methodName + "\tTime=" + new Date() + "\t" + e.toString(), true);
    }

}
