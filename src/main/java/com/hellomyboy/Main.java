package com.hellomyboy;

import java.io.BufferedWriter;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.hellomyboy.bean.ShareData;
import com.hellomyboy.filename.OuputFileNameStrategy;
import com.hellomyboy.processor.DataFileReader;
import com.hellomyboy.processor.DataFileWriter;

/**
 * 主方法
 * @author hellomyboy (ht0411@qq.com)
 * @date 2016/3/25
 */
public class Main{
    private static Logger log = Logger.getLogger(Main.class);
    //~ instance variables  =============================================
    private String infileDir = "";      //输入文件夹地址
    private String outfiledir;          //输出目录
    private int outfileSum;             //输出文件总数
    private String outfilePrefix;       //输出文件名前缀
    private String outfileSuffix;       //输出文件名后缀
    private int readerSum;              //读文件线程总数
    private int writerSum;              //写文件线程总数
    

    public static void main(String[] args) throws Exception {
        Main m = new Main();
        m.parseParams(args);
        m.runLogic();
    }
    
    private void parseParams(String[] args) {
        for(int i = 0; i < args.length; i++) {
            if("-infileDir".equals(args[i])) {
                infileDir = args[++i];
            } else if("-outfileDir".equals(args[i])) {
                outfiledir = args[++i];
            } else if("-outfileSum".equals(args[i])) {
                outfileSum = NumberUtils.toInt(args[++i]);
            } else if("-outfilePrefix".equals(args[i])) {
                outfilePrefix = args[++i];
            } else if("-outfileSuffix".equals(args[i])) {
                outfileSuffix = args[++i];
            } else if("-readerSum".equals(args[i])) {
                readerSum = NumberUtils.toInt(args[++i]);
            } else if("-writerSum".equals(args[i])) {
                writerSum = NumberUtils.toInt(args[++i]);
            } else {
                log.warn("不认识参数："+args[i]);
            }
        }
    }
    
    public void runLogic() throws Exception {
        //初始化全局数据
        FileNameStrategy outFileStrategy = new OuputFileNameStrategy(outfiledir, outfilePrefix, outfileSuffix);
        ShareData.init(infileDir, readerSum, writerSum, outfileSum, outFileStrategy);
        
        //启动通知者线程
        ReadFinishInformer informer = new ReadFinishInformer(ShareData.readerSum);
        Thread tInformer = new Thread(informer);
        tInformer.start();
        
        // 创建消费者
        for(int i = 0; i < writerSum; i++) {
            DataFileWriter writer = new DataFileWriter(ShareData.writeRcvQArr[i], 
                    outFileStrategy, ShareData.bwMap);  //观察者:所有文件读取结束事件
            informer.addObserver(writer);
            new Thread(writer).start();
        }
        
        // 创建生产者
        for(int i = 0; i < ShareData.readerSum; i++) {
            new Thread(new DataFileReader(informer)).start();;
        }
        
        tInformer.join();
        for(Map.Entry<String, BufferedWriter> map : ShareData.bwMap.entrySet()) {
            map.getValue().close();
        }
        log.info("主线程结束...");
    }

}
