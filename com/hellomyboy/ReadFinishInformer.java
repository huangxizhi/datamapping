package com.hellomyboy;

import java.util.Observable;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

/**
 * 文件读取完毕后通知观察者
 * 
 * @author hellomyboy (ht0411@qq.com)
 * @date 2016/3/24
 *
 */
public class ReadFinishInformer extends Observable implements Runnable {
    private static Logger log = Logger.getLogger(ReadFinishInformer.class);
    private CountDownLatch cdLatch ;    //生产者个数
    
    public ReadFinishInformer(int producterSum) {
        cdLatch = new CountDownLatch(producterSum);
        log.info("informer num:" + cdLatch.getCount());
    }
    
    public void finishOne() {
        cdLatch.countDown();
        log.info("informer left num:" + cdLatch.getCount());
    }

    @Override
    public void run() {
        try {
            cdLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        log.info("开始发布通知...");
        this.setChanged();
        this.notifyObservers();
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("informer finish...");
    }
    
}
