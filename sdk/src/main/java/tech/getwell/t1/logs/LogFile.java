package tech.getwell.t1.logs;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import tech.getwell.t1.beans.RawDataLog;
import tech.getwell.t1.beans.RawSmo2Data;
import tech.getwell.t1.beans.Smo2DataLog;

/**
 * @author Wave
 * @date 2019/8/12
 */
public class LogFile {

    public static final String DIR_NAME = "trainings";
    final String TABLE_FLAG = "\t";
    final String NEWLINE_FLAG = "\r\n";

    // 临时文件区 suffix
    final String TEMP_SUFFIX = ".temp";
    // 完整数据区
    final String DATA_SUFFIX = ".txt";

    Context context;
    // 用户ID
    String userId = "10101";
    // 运动类型
    int exerciseNo;

    // 数据文件
    File to2File;
    // 原始数据文件
    File originalFile;
    // 参数文件
    //File paramsFile;

    RandomAccessFile to2RandomFile;

    RandomAccessFile originalRandomFile;


    public LogFile(Context context, String userId, int exerciseNo) throws IOException{
        this.context = context;
        this.userId = userId;
        this.exerciseNo = exerciseNo;
        newFile();
    }

    String getCurrentTime(){
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return new SimpleDateFormat("yyyyMMddHHmmss").format(c.getTime());
    }

    File newFile(String pathname) throws IOException{
        File file = new File(pathname);
        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        return file;
    }

    private void newFile() throws IOException {
        String currentTimes = getCurrentTime();
        String rootName = new SimpleDateFormat("MM-dd HH时mm分ss秒").format(System.currentTimeMillis());


        String fileName = rootName +"/"+this.userId+"_"+currentTimes+"_1hz_content.txt";
        String originalName = rootName +"/"+this.userId+"_"+currentTimes+"_1hz_original.txt";
        String paramsName = rootName +"/"+this.userId+"-"+currentTimes+"_"+exerciseNo+"_params.txt";

        String paramsPath = context.getCacheDir().getPath() +"/"+ DIR_NAME +"/"+paramsName;
        String to2Path = context.getCacheDir().getPath() +"/"+ DIR_NAME +"/"+fileName;
        String originalPath = context.getCacheDir().getPath() +"/"+ DIR_NAME +"/"+originalName;

        //paramsFile = newFile(paramsPath);
        to2File = newFile(to2Path);
        originalFile = newFile(originalPath);

        to2RandomFile = new RandomAccessFile(to2File, "rw");
        originalRandomFile = new RandomAccessFile(originalFile,"rw");
    }

    /**
     * 获取当前时间，格式 HH:mm:ss.SSS
     */
    SimpleDateFormat format;

    String getCurrentTimeByTime() {
        // 用于上传给服务器的格式
        if(format == null)format = new SimpleDateFormat("HH:mm:ss.SSS");
        String strDate = format.format(new Date());
        return strDate;
    }

    StringBuilder getOriginalData(RawDataLog rawDataLog){
        return getOriginalData(rawDataLog.frame,rawDataLog.ratio,rawDataLog.datas,rawDataLog.temp1,rawDataLog.temp2);
    }

    StringBuilder getOriginalData(int frame, double ratio, int[] datas, int temp1, int temp2){
        StringBuilder originalSb = new StringBuilder();
        originalSb.setLength(0);
        originalSb.append(getCurrentTimeByTime());
        originalSb.append(TABLE_FLAG);
        for (int data : datas) {
            originalSb.append(String.valueOf(data));
            originalSb.append(TABLE_FLAG);
        }
        originalSb.append(temp1);
        originalSb.append(TABLE_FLAG);
        originalSb.append(temp2);
        originalSb.append(TABLE_FLAG);
        originalSb.append("00");
        originalSb.append(TABLE_FLAG);
        originalSb.append(String.valueOf(frame));
        originalSb.append(TABLE_FLAG);
        originalSb.append(String.valueOf(ratio));
        originalSb.append(NEWLINE_FLAG);
        return originalSb;
    }

    int to2Count = 1;

    StringBuilder getTo2Data(Smo2DataLog smo2DataLog){
        return getTo2Data(smo2DataLog.smo2_filter,smo2DataLog.hr,smo2DataLog.conduction,smo2DataLog.temp1,smo2DataLog.temp2,smo2DataLog.latitude,smo2DataLog.longitude,smo2DataLog.speed);
    }

    StringBuilder getTo2Data(int smo2_filter, int hr, double conduction, int temp1, int temp2, double latitude, double longitude, double speed){
        StringBuilder sportSb = new StringBuilder();
        sportSb.setLength(0);
        sportSb.append(String.valueOf(to2Count++));
        sportSb.append(TABLE_FLAG);
        sportSb.append(String.valueOf(smo2_filter));
        sportSb.append(TABLE_FLAG);
        sportSb.append(String.valueOf(hr));
        sportSb.append(TABLE_FLAG);
        sportSb.append(String.valueOf(conduction));
        sportSb.append(TABLE_FLAG);
        sportSb.append(String.valueOf(temp1));
        sportSb.append(TABLE_FLAG);
        sportSb.append(String.valueOf(temp2));
        sportSb.append(TABLE_FLAG);
        sportSb.append(String.valueOf(longitude));
        sportSb.append(TABLE_FLAG);
        sportSb.append(String.valueOf(latitude));
        sportSb.append(TABLE_FLAG);
        sportSb.append(String.valueOf(speed));
        sportSb.append(NEWLINE_FLAG);
        return sportSb;
    }

    /**
     * 添加记录内容
     */
    public void addRawData(RawSmo2Data rawSmo2Data) throws IOException{
        writeDataLine(originalRandomFile,getOriginalData(rawSmo2Data.toRawDataLog()).toString());
        writeDataLine(to2RandomFile,getTo2Data(rawSmo2Data.toSmo2DataLog()).toString());
    }

    /**
     * 添加一行数据
     * @param accessFile
     * @param data
     * @throws IOException
     */
    private void writeDataLine(RandomAccessFile accessFile,String data) throws IOException{
        if(accessFile == null )return;
        // 文件长度，字节数
        long fileLength = accessFile.length();
        // 将写文件指针移到文件尾。
        accessFile.seek(fileLength);
        accessFile.writeBytes(data);
    }

    public void close() throws IOException{
        close(to2RandomFile);
        close(originalRandomFile);
    }

    void close(RandomAccessFile accessFile) throws IOException{
        if(accessFile != null)accessFile.close();
    }
}
