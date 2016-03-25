package com.hellomyboy.filename;

import com.hellomyboy.FileNameStrategy;

/**
 * 输出文件命名策略
 * 
 * @author hellomyboy (ht0411@qq.com)
 * @date 2016/3/24
 */
public class OuputFileNameStrategy implements FileNameStrategy {
    private String fileDir;
    private String prefix;
    private String suffix;
    
    /**
     * @param fileDir
     * @param fileNamePrefix
     * @param fileNameSuffix
     */
    public OuputFileNameStrategy(String fileDir, String fileNamePrefix, String fileNameSuffix) {
        this.fileDir = fileDir;
        this.prefix = fileNamePrefix;
        this.suffix = fileNameSuffix;
    }
    
    @Override
    public String getFileName(String base) {
        return prefix + base + suffix;
    }

    @Override
    public String getFullName(String base) {
        return fileDir + System.getProperty("file.separator") + getFileName(base);
    }
    
}
