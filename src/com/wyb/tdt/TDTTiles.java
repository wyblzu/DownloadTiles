package com.wyb.tdt;

import com.wyb.util.Code;
import com.wyb.util.Coordinate;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ii on 2017/5/16.
 */
public class TDTTiles {
    //瓦片大小
    public final static int tileSize = 256;

    /**
     * 经纬度转编码
     *
     * @param coordinate 经纬度坐标
     * @param level      级别
     * @return 编码
     */
    public static Code coordiante2Code(Coordinate coordinate, int level) {
        Code code = new Code();
        double resolution = 360 / (Math.pow(2, level) * tileSize);
        int row = (int) Math.floor(Math.abs(-180 - coordinate.getLon()) / (resolution * tileSize));
        int column = (int) Math.floor(Math.abs(90 - coordinate.getLat()) / (resolution * tileSize));
        code.setRow(row);
        code.setColumn(column);
        return code;
    }

    /**
     * 编号转坐标
     *
     * @param code  编号
     * @param level 级别
     * @return 坐标
     */
    public static Coordinate[] code2Coordinate(Code code, int level) {
        Coordinate leftTop = new Coordinate();
        Coordinate rightBottom = new Coordinate();
        leftTop.setLon(code2lon(code.getRow(), level));
        leftTop.setLat(code2lat(code.getColumn(), level));
        rightBottom.setLon(code2lon(code.getRow() + 1, level));
        rightBottom.setLat(code2lat(code.getColumn() + 1, level));
        return new Coordinate[]{leftTop, rightBottom};
    }

    /**
     * 行号转经度
     *
     * @param row   行号
     * @param level 级别
     * @return 经度
     */
    private static double code2lon(int row, int level) {
        return row / Math.pow(2, level) * 360 - 180;
    }

    /**
     * 列号转纬度
     *
     * @param column 列号
     * @param level  级别
     * @return 纬度
     */
    private static double code2lat(int column, int level) {
        return 90 - column / Math.pow(2, level - 1) * 180;
    }

    public static List<Code> mapCodes(Coordinate[] coordinates, int level){
        double tileSize = 360 / Math.pow(2, level);
        List<Code> codeList = new ArrayList<>();
        Coordinate coordinate = new Coordinate();
        double xOrigin = coordinates[0].getLon();
        double yOrigin = coordinates[0].getLat();
        int xCount = (int) Math.ceil(Math.abs(coordinates[0].getLon() - coordinates[1].getLon()) /tileSize);
        int yCount = (int) Math.ceil(Math.abs(coordinates[0].getLat() - coordinates[1].getLat()) /tileSize);
        for(int i = 1; i <= xCount; i ++ ){
            for(int j = 1; j <= yCount; j ++){
                coordinate.setLon( xOrigin + tileSize*(i - 1));
                coordinate.setLat( yOrigin + tileSize*(j - 1));
                Code code = coordiante2Code(coordinate, level);
                codeList.add(code);
            }
        }
        return codeList.size() > 0 ? codeList : null;
    }

}


