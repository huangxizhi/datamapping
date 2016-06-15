/**
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.hellomyboy.bean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.hellomyboy.FileNameStrategy;

/**
 * 全局共享数据
 * 
 * @author hellomyboy (ht0411@qq.com)
 * @date 2016/3/24
 */
public class ShareData {
    //每个输入文件入该队列
    public static BlockingQueue<File> inFileQueue = new LinkedBlockingQueue<File>();
    //每个写文件线程对应一个接受数据队列
    public static BlockingQueue<ReadDataItem>[] writeRcvQArr;
    //写每个文件对应一个BufferedWriter
    public static ConcurrentHashMap<String, BufferedWriter> bwMap;
    //文件名策略
    public static FileNameStrategy outFileStrategy;
    //读文件线程数
    public static int readerSum;
    //写文件线程数
    public static int writerSum;
    //最终输出文件数
    public static int outFileSum;
    
    //数据初始化
    public static void init(String inputFileDir, int rSum, int wSum, int oSum, FileNameStrategy strategy) throws IOException {
        //输入文件入队列
        File dir = new File(inputFileDir);
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(pathname.isFile()) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        for (File f : files) {
            inFileQueue.add(f);
        }
        
        readerSum = rSum;
        writerSum = wSum;
        writeRcvQArr = new BlockingQueue[wSum];
        for(int i = 0 ; i < wSum; i++) {
            writeRcvQArr[i] = new LinkedBlockingQueue();
        }
        outFileSum = oSum;
        outFileStrategy = strategy;
        
        bwMap = new ConcurrentHashMap<String, BufferedWriter>();
        for (int i = 0; i<outFileSum; i++) {
            //注意：后续写线程也必须根据index(0~outFileSum)来确定映射的文件名
            String ofileName = outFileStrategy.getFileName(i + "");
            String ofullName = outFileStrategy.getFullName(i + "");
            BufferedWriter bw = new BufferedWriter(new FileWriter(ofullName, true));
            bwMap.put(ofileName, bw);
        }
    }
}
