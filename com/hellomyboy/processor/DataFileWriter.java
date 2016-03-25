package com.hellomyboy.processor;

import java.io.BufferedWriter;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.hellomyboy.FileNameStrategy;
import com.hellomyboy.bean.ReadDataItem;

/**
 * 写数据到对应bucket的文件中
 * 
 * @author hellomyboy (ht0411@qq.com)
 * @date 2016/3/24
 * 
 */
public class DataFileWriter implements Runnable, Observer {
    private static Logger log = Logger.getLogger(DataFileWriter.class);
    private BlockingQueue<ReadDataItem> queue;
    private FileNameStrategy strategy ;
    
    //存储小文件名称和对应文件缓冲写进行映射
    //下面这个声明有问题：每个线程都会判断是否保存了对某个文件的writer，不存在就创建，最后每个线程都创建了对所有目标文件的bufferwriter对象，
    //可能导致数据写串了。应该将这个fileWriterMap对所有线程共享；
    private Map<String, BufferedWriter> fileWriterMap;  //  = new HashMap<String, BufferedWriter>();
    // end by huangtao
    private boolean allFileReadFinish = false;

    //~ constructer =====================================
    /**
     * @param queue
     * @param allFileReadFinish
     * @param fileWriterMap
     */
    public DataFileWriter(BlockingQueue<ReadDataItem> queue, FileNameStrategy strategy, Map<String, BufferedWriter> fileWriterMap) {
        this.queue = queue;
        this.strategy = strategy;
        this.fileWriterMap = fileWriterMap;
    }

    //~ instance function ================================
    @Override
    public void update(Observable o, Object arg) {
        //数据处理完成后，关闭所有文件
        allFileReadFinish = true;
    }

    @Override
    public void run() {
        ReadDataItem data = null;
        BufferedWriter bw = null;
        String fileName;
        while(true) {
            try {
                data = queue.poll();
                if(data != null) {
                    fileName = strategy.getFileName(data.dataBucketIdx);
                    bw = fileWriterMap.get(fileName);
                    /*if(bw == null) {
                        bw = new BufferedWriter(new FileWriter(strategy.getFullName(data.dataBucketIdx), true));
                        fileWriterMap.put(fileName, bw);
                    }*/
                    bw.write(data.data+"\n");
                } else {
                    //关闭所有的写文件流
                    if(allFileReadFinish && queue.size() == 0) {
                        for(Map.Entry<String, BufferedWriter> map : fileWriterMap.entrySet()) {
                            try {
                                map.getValue().close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
        
        log.info("写文件线程结束：" + Thread.currentThread().getId());
    }

}
