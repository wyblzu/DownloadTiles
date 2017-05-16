
import com.wyb.tdt.CombineTiles;
import com.wyb.tdt.TDTTiles;
import com.wyb.util.Code;
import com.wyb.util.Coordinate;
import com.wyb.util.DownloadTiles;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException{
        Coordinate c1 = new Coordinate();
        Coordinate c2 = new Coordinate();
        c1.setLon(103);
        c2.setLon(107);
        c1.setLat(30);
        c2.setLat(34);
//        String urlString = "http://t4.tianditu.com/DataServer?T=img_c&x=%d&y=%d&l=%d";
//        List<Code> codeList = TDTTiles.mapCodes(new Coordinate[]{c1, c2}, 13);
//        DownloadTiles.downLoadTile(13, 13, urlString,"e://test", codeList);
        CombineTiles.tilesPath = "e://test//13";
        CombineTiles.combinePNG("e://test//1111.png");
        CombineTiles.createWorldFile("e://test//1111.pgw", 13);
    }
}
