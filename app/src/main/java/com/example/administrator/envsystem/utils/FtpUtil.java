package com.example.administrator.envsystem.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * FTP工具类
 */

public class FtpUtil {

    public static boolean uploadFile(String url, int port, String username, String password, String path,
                                     String filename, InputStream input) throws IOException {
//        String TAG = "TAG";
        boolean success = false;
        //新建ftp对象
        FTPClient ftp = new FTPClient();
//        Log.i(TAG, "创建FTP对象成功");
        int reply;
        //设置超时
        ftp.setConnectTimeout(3000);
        ftp.connect(url);//连接FTP服务器
        ftp.login(username, password);
//        Log.i(TAG, "登录成功");
        //连接的状态码
        reply = ftp.getReplyCode();
        ftp.setDataTimeout(12000);
        //判断是否连接上FTP
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return success;
        }
        ftp.enterLocalPassiveMode();
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        File dic = new File(path);
        if (dic.isDirectory()) {
            ftp.changeWorkingDirectory(path);
        } else {
            //对传入的路径进行拆分，ftp里创建路径要一层一层的创建
            String[] argPath = path.split("/");
            String pathName = null;
            //一层一层开始创建路径
            for (int i = 0; i < argPath.length; i++) {
                pathName = argPath[i];
                ftp.makeDirectory(pathName);
                //创建一层就切换到该目录下
                ftp.changeWorkingDirectory(pathName);
            }
        }
//        Log.i(TAG, "FTP创建目录成功");
        //保存文件到目录下
        ftp.storeFile(new String(filename.getBytes("utf-8"), "iso-8859-1"), input);
//        Log.i(TAG, "FTP上传文件成功");
        //关闭流，退出FTP
        input.close();
        ftp.logout();
        success = true;
        //判断是否退出成功，不成功就在断开连接
        if (ftp.isConnected()) {
            try {
                ftp.disconnect();
            } catch (IOException ioe) {
                throw new RuntimeException("FTP disconnect fail!", ioe);
            }
        }
        return success;
    }

    public static int download(String url, int port, String username, String password, String path,
                               String filename, String remotePath, String localPath) throws IOException {

        int code = 0;
        //新建ftp对象
        FTPClient ftp = new FTPClient();
//        Log.i(TAG, "创建FTP对象成功");
        int reply;
        //设置超时
        ftp.setConnectTimeout(3000);
        ftp.connect(url);//连接FTP服务器
        ftp.login(username, password);
//        Log.i(TAG, "登录成功");
        //连接的状态码
        reply = ftp.getReplyCode();
        ftp.setDataTimeout(12000);
        //判断是否连接上FTP
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return -1;
        }
        ftp.changeWorkingDirectory(remotePath);
        FTPFile[] fs = ftp.listFiles();
        for (FTPFile ff : fs) {
            if (ff.getName().equals(filename)) {
                code = 1;
                File localFile = new File(localPath + "/" + ff.getName());
                OutputStream os = new FileOutputStream(localFile);
                ftp.retrieveFile(ff.getName(), os);
                os.close();
            }
        }
        ftp.logout();
        //判断是否退出成功，不成功就在断开连接
        if (ftp.isConnected()) {
            try {
                ftp.disconnect();
            } catch (IOException ioe) {
                throw new RuntimeException("FTP disconnect fail!", ioe);
            }
        }
        return code;
    }

    public static int downloadFile(String url, int port, String username, String password, List<String> files,
                                   String filename, String remotePath, String localPath) throws IOException {

        int code = 0;
        //新建ftp对象
        FTPClient ftp = new FTPClient();
//        Log.i(TAG, "创建FTP对象成功");
        int reply;
        //设置超时
        ftp.setConnectTimeout(3000);
        ftp.connect(url);//连接FTP服务器
        ftp.login(username, password);
//        Log.i(TAG, "登录成功");
        //连接的状态码
        reply = ftp.getReplyCode();
        ftp.setDataTimeout(12000);
        //判断是否连接上FTP
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return -1;
        }
        ftp.changeWorkingDirectory(remotePath);
        FTPFile[] fs = ftp.listFiles();
        //遍历数组
        for (FTPFile ff : fs) {
            for (int i = 0; i < files.size(); i++) {
                if (ff.getName().equals(files.get(i))) {
                    code = 1;
                    File localFile = new File(localPath + "/" + ff.getName());
                    OutputStream os = new FileOutputStream(localFile);
                    ftp.retrieveFile(ff.getName(), os);
                    os.close();
                }
            }
        }
        ftp.logout();
        //判断是否退出成功，不成功就在断开连接
        if (ftp.isConnected()) {
            try {
                ftp.disconnect();
            } catch (IOException ioe) {
                throw new RuntimeException("FTP disconnect fail!", ioe);
            }
        }
        return code;
    }

    public static boolean searchFile(String url, int port, String username, String password, String file1,
                                  String remotePath) throws IOException {

        boolean code = false;
        //新建ftp对象
        FTPClient ftp = new FTPClient();
//        Log.i(TAG, "创建FTP对象成功");
        int reply;
        //设置超时
        ftp.setConnectTimeout(3000);
        ftp.connect(url);//连接FTP服务器
        ftp.login(username, password);
//        Log.i(TAG, "登录成功");
        //连接的状态码
        reply = ftp.getReplyCode();
        ftp.setDataTimeout(12000);
        //判断是否连接上FTP
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return false;
        }
        ftp.changeWorkingDirectory(remotePath);
        FTPFile[] fs = ftp.listFiles();
        //遍历数组
        for (FTPFile ff : fs) {
            if (ff.getName().equals(file1)) {
                code=true;
            }
        }
        ftp.logout();
        //判断是否退出成功，不成功就在断开连接
        if (ftp.isConnected()) {
            try {
                ftp.disconnect();
            } catch (IOException ioe) {
                throw new RuntimeException("FTP disconnect fail!", ioe);
            }
        }
        return code;
    }
}
