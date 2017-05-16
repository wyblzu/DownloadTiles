package com.wyb.tdt;

import com.wyb.util.Code;
import com.wyb.util.Coordinate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

/**
 * 合并瓦片 带坐标
 * Created by ii on 2017/5/15.
 */
public class CombineTiles {
    //瓦片路径
    public static String tilesPath;
    //瓦片大小
    public final static int tileSize = 256;
    //输出图片格式
    private final static String imageType = "PNG";

    /**
     * @param path 路径
     * @return 行或者列编号
     * @throws IOException 流异常
     */
    private static List calculateColRowCode(String path) throws IOException {
        File file = new File(path);
        File fList[] = file.listFiles();
        List<String> rowCodes = new ArrayList();
        List<String> colCodes = new ArrayList();
        if (fList == null || fList.length == 0) {
            return null;
        }
        for (File f : fList) {
            if (f.isDirectory()) {
                rowCodes.add(f.getName());
            } else {
                colCodes.add(f.getName());
            }
        }
        return rowCodes.size() > 0 ? rowCodes : colCodes;
    }

    /**
     * 合并图片
     *
     * @param outPath 输出合成图片路径
     * @throws IOException 流异常
     */
    public static void combinePNG(String outPath) throws IOException {
        List rowCodes = calculateColRowCode(tilesPath);
        List colCodes = calculateColRowCode(tilesPath + "\\" + rowCodes.get(0));
        int rowCount = rowCodes.size();
        if (colCodes == null) return;
        int colCount = colCodes.size();
        int type;
        BufferedImage[][] bufferedImages = new BufferedImage[rowCount][colCount];
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                String newTilesPath = tilesPath + "\\" + rowCodes.get(i) + "\\" + colCodes.get(j);
                File file = new File(newTilesPath);
                bufferedImages[i][j] = ImageIO.read(file);
            }
        }
        type = bufferedImages[0][0].getType();
        BufferedImage outPicture = new BufferedImage(tileSize * rowCount, tileSize * colCount, type);
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                outPicture.createGraphics().drawImage(bufferedImages[i][j], 256 * i, 256 * j, null);
            }
        }
        File outFile = new File(outPath);
        ImageIO.write(outPicture, imageType, outFile);
        System.out.println("拼接完成！");
    }

    /**
     * 创建坐标文件
     *
     * @param outFile 输出路径
     * @param level   级别
     */
    public static void createWorldFile(String outFile, int level) {
        File file = new File(outFile);
        double xResolution = 360 / Math.pow(2, level) / tileSize;
        double yResolution = 180 / Math.pow(2, level - 1) / tileSize;
        try {
            List rowCodes = calculateColRowCode(tilesPath);
            Collections.sort(rowCodes);
            List colCodes = calculateColRowCode(tilesPath + "\\" + rowCodes.get(0));
            Collections.sort(colCodes);
            String colCode = colCodes.get(0).toString();
            int column = Integer.valueOf(colCode.substring(0, colCode.length() - colCode.lastIndexOf(".") - 1));
            int row = Integer.valueOf(rowCodes.get(0).toString());
            Code code = new Code();
            code.setRow(row);
            code.setColumn(column);
            if (colCodes == null) return;
            if (!file.exists()) file.createNewFile();
            FileWriter fileWriter = new FileWriter(file, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(xResolution);
            printWriter.println(0.0);
            printWriter.println(0.0);
            printWriter.println(-yResolution);
            Coordinate[] coordinates = com.wyb.tdt.TDTTiles.code2Coordinate(code, level);
            printWriter.println(coordinates[0].getLon());
            printWriter.println(coordinates[0].getLat());
            printWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }


}




