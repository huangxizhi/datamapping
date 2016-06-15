package com.hellomyboy;

/**
 * 文件命名策略
 * 
 * @author hellomyboy (ht0411@qq.com)
 * @date 2016/3/24
 */
public interface FileNameStrategy {

    public String getFileName(String base);
    
    public String getFullName(String base);
}
