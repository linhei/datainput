package com.linhei.datainput.entity;

/**
 * @author linhei
 */
public class FileSetting {


    private String fullFileName;
    private String fileName;
    private String filePath;
    private String key;

    public String getFullFileName() {
        return fullFileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getKey() {
        return key;
    }

    public FileSetting() {
    }

    public FileSetting(String fullFileName, String fileName, String filePath, String key) {
        this.fullFileName = fullFileName;
        this.fileName = fileName;
        this.filePath = filePath;
        this.key = key;
    }

    @Override
    public String toString() {
        return "FileSetting{" +
                "fullFileName='" + fullFileName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
