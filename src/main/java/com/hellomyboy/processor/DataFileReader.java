package com.hellomyboy.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.hellomyboy.ReadFinishInformer;
import com.hellomyboy.bean.ReadDataItem;
import com.hellomyboy.bean.ShareData;

/**
 * 读取并处理输入文件
 * 
 * @author hellomyboy (ht0411@qq.com)
 * @date 2016/3/24
 * 
 */
public class DataFileReader implements Runnable {
    private static Logger log = Logger.getLogger(DataFileReader.class);
    private ReadFinishInformer informer ;
    
    public DataFileReader(ReadFinishInformer informer) throws FileNotFoundException {
        this.informer = informer;
    }
    
    //~ instance function ============================
    @Override
    public void run() {
        String line;
        int estimateData, dataBucket, threadIdx;
        
        while(true) {
            try {
                File file = ShareData.inFileQueue.poll();
                if (file == null) {
                    informer.finishOne();
                    break;
                } else {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    while((line = br.readLine())!=null) {
                        line = line.trim();
                        if(StringUtils.isNotBlank(line)) {
                            estimateData = calDataBucket(DigestUtils.md5Hex(line));
                            dataBucket = estimateData % ShareData.outFileSum;
                            threadIdx = estimateData % ShareData.writerSum;
                            ReadDataItem item = new ReadDataItem(line, String.valueOf(dataBucket), threadIdx);
                            ShareData.writeRcvQArr[threadIdx].put(item);
                        }
                    }
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
        
        log.info("Reader线程[" + Thread.currentThread().getName()+"]处理完成...");
    }
    
    public static  int calDataBucket(String md5) throws UnsupportedEncodingException {
        byte[] b = md5.getBytes();
        return b[0] + b[1] * 10 + b[2] * 100 + b[3] * 1000;
    }
    
    /*public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        //String line = "bos:/lebo18/data2/music/11820938/11820938.mp3";
        String line = "bos:/lebo18/data2/music/1026030/1026030.mp3";
        int s = DataFileReader.calDataBucket(DigestUtils.md5Hex(line));
        System.out.println(s%50);  //61000
    }*/

}
