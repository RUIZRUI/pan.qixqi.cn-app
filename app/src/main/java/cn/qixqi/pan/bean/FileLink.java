package cn.qixqi.pan.bean;

import java.io.Serializable;

public class FileLink implements Serializable {
    private int linkId;         // 返回到父文件夹时用到
    private int userId;     // 多用户登录
    private int fileId;
    private String fileName;
    private String fileType;
    private long fileSize;
    private char isFolder;
    private String folderName;
    private String fileList;      // 监听子文件增加或删除
    private String folderList;      // 监听子文件夹增加、修改或删除
    private char isRoot;
    private int parent;
    private String createLinkTime;
    private String downloadFinishTime;          // 下载完成时间
    private String uploadFinishTime;            // 上传完成时间
    private char downloadStatus;        // 下载状态（i下载中/h下载完成/u未知）
    private char uploadStatus;          // 上传状态（i上传中/h上传完成/u未知)

    public FileLink(){

    }

    public FileLink(int linkId, int userId, int fileId, String fileName, String fileType, long fileSize, char isFolder, String folderName, String fileList, String folderList, char isRoot, int parent, String createLinkTime) {
        this.linkId = linkId;
        this.userId = userId;
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.isFolder = isFolder;
        this.folderName = folderName;
        this.fileList = fileList;
        this.folderList = folderList;
        this.isRoot = isRoot;
        this.parent = parent;
        this.createLinkTime = createLinkTime;
    }


    /**
     * 保存到本地，记录下载上传
     * @param linkId
     * @param userId
     * @param fileId
     * @param fileName
     * @param fileType
     * @param fileSize
     * @param isFolder
     * @param folderName
     * @param fileList
     * @param folderList
     * @param isRoot
     * @param parent
     * @param downloadFinishTime
     * @param uploadFinishTime
     * @param downloadStatus
     * @param uploadStatus
     */
    public FileLink(int linkId, int userId, int fileId, String fileName, String fileType, long fileSize, char isFolder, String folderName, String fileList, String folderList, char isRoot, int parent, String downloadFinishTime, String uploadFinishTime, char downloadStatus, char uploadStatus) {
        this.linkId = linkId;
        this.userId = userId;
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.isFolder = isFolder;
        this.folderName = folderName;
        this.fileList = fileList;
        this.folderList = folderList;
        this.isRoot = isRoot;
        this.parent = parent;
        this.downloadFinishTime = downloadFinishTime;
        this.uploadFinishTime = uploadFinishTime;
        this.downloadStatus = downloadStatus;
        this.uploadStatus = uploadStatus;
    }

    public int getLinkId() {
        return linkId;
    }

    public void setLinkId(int linkId) {
        this.linkId = linkId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public char getIsFolder() {
        return isFolder;
    }

    public void setIsFolder(char isFolder) {
        this.isFolder = isFolder;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFileList() {
        return fileList;
    }

    public void setFileList(String fileList) {
        this.fileList = fileList;
    }

    public String getFolderList() {
        return folderList;
    }

    public void setFolderList(String folderList) {
        this.folderList = folderList;
    }

    public char getIsRoot() {
        return isRoot;
    }

    public void setIsRoot(char isRoot) {
        this.isRoot = isRoot;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getCreateLinkTime() {
        return createLinkTime;
    }

    public void setCreateLinkTime(String createLinkTime) {
        this.createLinkTime = createLinkTime;
    }

    public String getDownloadFinishTime() {
        return downloadFinishTime;
    }

    public void setDownloadFinishTime(String downloadFinishTime) {
        this.downloadFinishTime = downloadFinishTime;
    }

    public String getUploadFinishTime() {
        return uploadFinishTime;
    }

    public void setUploadFinishTime(String uploadFinishTime) {
        this.uploadFinishTime = uploadFinishTime;
    }

    public char getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(char downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public char getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(char uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    @Override
    public String toString() {
        return "FileLink{" +
                "linkId=" + linkId +
                ", userId=" + userId +
                ", fileId=" + fileId +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", isFolder=" + isFolder +
                ", folderName='" + folderName + '\'' +
                ", fileList='" + fileList + '\'' +
                ", folderList='" + folderList + '\'' +
                ", isRoot=" + isRoot +
                ", parent=" + parent +
                ", createLinkTime='" + createLinkTime + '\'' +
                '}';
    }
}
