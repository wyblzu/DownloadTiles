package com.wyb.util;

import com.wyb.tdt.TDTTiles;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.wyb.util.DownloadTiles.writePictureStream;

/**
 * Created by wyb on 2017/5/16.
 */
public class DownloadTiles {
    /**
     * 多线程下载瓦片
     *
     * @param minZoom   最小级别
     * @param maxZoom   最大级别
     * @param urlString 瓦片地址
     * @param path      瓦片存放路径
     * @param codeList  编号集合
     */
    public static void downLoadTile(int minZoom, int maxZoom, String urlString, String
            path, List<Code> codeList) {
        //开启4个线程下载
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int level = minZoom; level <= maxZoom; level++) {
            for (Code code : codeList) {
                String url = String.format(urlString, code.getRow(), code.getColumn(), level);
                Runner runner = new Runner(code, url, path, level);
                executorService.execute(runner);
            }
        }
        executorService.shutdown();
        System.out.println("瓦片下载完成！");
    }

    /**
     * 下载单个瓦片
     *
     * @param code      单个瓦片编号
     * @param urlString 瓦片地址
     * @param path      存放路径
     * @param level     级别
     */
    public static void writePictureStream(Code code, String urlString, String path, int level) {
        int x = code.getRow();
        int y = code.getColumn();
        String tileFullPath = path + "/" + level + "/" + x + "/" + y + ".png";
        File file = new File(tileFullPath);
        //如果瓦片在本地存在，则直接返回，不再下载
        if (file.exists()) return;
        try {
            URL url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(5 * 2000);
            InputStream inputStream = urlConnection.getInputStream();
            byte[] bytes = new byte[1024];
            //按照层、行、列存储
            String outputPath = path + "/" + level + "/" + x;
            file = new File(outputPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            OutputStream os = new FileOutputStream(file.getPath() + "/" + y + ".png");
            int length;
            while ((length = inputStream.read(bytes)) != -1) {
                os.write(bytes, 0, length);
            }
            os.close();
            inputStream.close();
        } catch (IOException e) {
            if (e instanceof MalformedURLException) {
                System.out.println("请检查下载网址是否正确或可用！");
            }
            e.printStackTrace();
        }
    }
}

/**
 * 线程目标对象
 */
class Runner implements Runnable {
    private Code code;
    private String urlString;
    private String path;
    private int level;

    //构造函数
    public Runner(Code code, String urlString, String path, int level) {
        this.code = code;
        this.urlString = urlString;
        this.path = path;
        this.level = level;
    }

    @Override
    public void run() {
        writePictureStream(code, urlString, path, level);
    }
}
