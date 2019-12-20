package club.qixqi.uiqq.util;



public class FileLinkUtil {


    /**
     * 获取格式化的文件大小表示形式
     * @param fileSize
     * @return
     */
    public static String getFormatSize(long fileSize){
        long temp = -1;      // 获取小数点后的数
        int label = 0;  //  'B'
        int counter = 3;
        while(fileSize >= 1000){
            temp = fileSize % 1000;
            fileSize /= 1000;
            label ++;
        }
        String formatSize = Long.toString(fileSize);
        if(temp != -1){
            temp /= 10;
            if(temp != 0){      // 有小数
                formatSize += ".";
                if(temp % 10 == 0){     // 一位小数
                    formatSize += temp/10;
                }else{                  // 两位小数
                    formatSize += temp;
                }
            }
        }
        switch (label){
            case 0:
                formatSize += "B";
                break;
            case 1:
                formatSize += "KB";
                break;
            case 2:
                formatSize += "MB";
                break;
            case 3:
                formatSize += "GB";
                break;
            case 4:
                formatSize += "TB";
                break;
            default:
                formatSize += "超出范围";
                break;
        }
        return formatSize;
    }

}
