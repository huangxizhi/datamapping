package com.hellomyboy.bean;

/**
 * 读文件线程处理一条记录信息
 * 
 * @author hellomyboy (ht0411@qq.com)
 * @date 2016/3/24
 */
public class ReadDataItem {
    public String data;             //具体数据
    public String dataBucketIdx;    //数据最终所属的文件bucket
    public int processThreadIdx;    //待处理线程的index
    
    /**
     * @param data
     * @param dataBucketIdx
     * @param dataBucketFile
     * @param processThreadIdx
     */
    public ReadDataItem(String data, String dataBucketIdx, int processThreadIdx) {
        super();
        this.data = data;
        this.dataBucketIdx = dataBucketIdx;
        this.processThreadIdx = processThreadIdx;
    }
   
}
