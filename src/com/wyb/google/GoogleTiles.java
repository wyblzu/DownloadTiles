package com.wyb.google;

import com.wyb.util.Code;
import com.wyb.util.Coordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyb on 2017/5/16.
 */
public class GoogleTiles {
    //Web墨卡托经纬度范围
    private static final double maxMLonRange = 180 * 2;
    private static final double maxMLatRange = 85.05113 * 2;

    /**
     * 经纬度转墨卡托
     *
     * @param coordinate 经纬度坐标
     * @return 墨卡托坐标
     */
    public static Coordinate lonLat2WebMercator(Coordinate coordinate) {
        Coordinate mCoordinate = new Coordinate();
        double lon = coordinate.getLon();
        double lat = coordinate.getLat();
        double x = lon * 20037508.3427892 / 180;
        double y = Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180);
        y = y * 20037508.3427892 / 180;
        mCoordinate.setLon(x);
        mCoordinate.setLat(y);
        return mCoordinate;
    }

    /**
     * 墨卡托转经纬度
     *
     * @param mCoordinate 墨卡托坐标
     * @return 经纬度坐标
     */
    public static Coordinate webMercator2LonLat(Coordinate mCoordinate) {
        Coordinate coordinate = new Coordinate();
        double x = mCoordinate.getLon();
        double y = mCoordinate.getLat();
        double lon = x / 20037508.34 * 180;
        double lat = y / 20037508.34 * 180;
        lat = 180 / Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI / 180)) - Math.PI / 2);
        coordinate.setLon(lon);
        coordinate.setLat(lat);
        return coordinate;
    }

    /**
     * 通过经纬度获取对应级别的瓦片
     *
     * @param coordinate 经纬度坐标
     * @param zoom       级别
     * @return 编号
     */
    public static Code coordiante2Code(Coordinate coordinate, final int zoom) {
        Code code = new Code();
        double lon = coordinate.getLon();
        double lat = coordinate.getLat();
        int xTile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int yTile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat)))
                / Math.PI) / 2 * (1 << zoom));
        if (xTile < 0)
            xTile = 0;
        if (xTile >= (1 << zoom))
            xTile = ((1 << zoom) - 1);
        if (yTile < 0)
            yTile = 0;
        if (yTile >= (1 << zoom))
            yTile = ((1 << zoom) - 1);
        code.setRow(xTile);
        code.setColumn(yTile);
        return code;
    }

    /**
     * 通过瓦片编号和级别获取瓦片的最小外包矩形（即瓦片范围）
     *
     * @param code 行列号
     * @param zoom 级别
     * @return 最小外包矩形坐标
     */
    public static Coordinate[] code2Coordinate(Code code, final int zoom) {
        int x = code.getRow();
        int y = code.getColumn();
        Coordinate c1 = new Coordinate();
        c1.setLon(tile2lon(x, zoom));
        c1.setLat(tile2lat(y, zoom));
        Coordinate c2 = new Coordinate();
        c2.setLon(tile2lon(x + 1, zoom));
        c2.setLat(tile2lat(y + 1, zoom));
        return new Coordinate[]{c1, c2};
    }

    /**
     * 通过瓦片编号和级别获取经度
     *
     * @param x 经度
     * @param z 级别
     * @return 经度
     */
    private static double tile2lon(int x, int z) {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    /**
     * 通过瓦片编号和级别获取纬度
     *
     * @param y 纬度
     * @param z 级别
     * @return 纬度
     */
    private static double tile2lat(int y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }

    /**
     * 通过范围获取对应级别的所有瓦片编号
     *
     * @param coordinates 坐标
     * @param level       级别
     * @return 编号集合
     */
    public static List<Code> calculateCodes(Coordinate[] coordinates, int level) {
        List<Code> codeList = new ArrayList<>();
        double xTileSize;
        double yTileSize;
        xTileSize = maxMLonRange / Math.pow(2, level);
        yTileSize = maxMLatRange / Math.pow(2, level);
        double xOrigin = coordinates[0].getLon();
        double yOrigin = coordinates[0].getLat();
        int xCount = (int) Math.ceil(Math.abs(coordinates[0].getLon() - coordinates[1].getLon()) / xTileSize);
        int yCount = (int) Math.ceil(Math.abs(coordinates[0].getLat() - coordinates[1].getLat()) / yTileSize);
        for (int i = 1; i <= xCount; i++) {
            for (int j = 1; j <= yCount; j++) {
                Coordinate coordinate = new Coordinate();
                coordinate.setLon(xOrigin + xTileSize * (i - 1));
                coordinate.setLat(yOrigin + yTileSize * (j - 1));
                codeList.add(coordiante2Code(coordinate, level));
            }
        }
        return codeList.size() > 0 ? codeList : null;
    }

}
