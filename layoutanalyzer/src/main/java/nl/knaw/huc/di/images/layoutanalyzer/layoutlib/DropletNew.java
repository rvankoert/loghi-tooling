package nl.knaw.huc.di.images.layoutanalyzer.layoutlib;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class DropletNew {
    private ArrayList<Point> pointList;

    DropletNew(Mat inputMat, ArrayList<Point> targetPath) {
        this.pointList = new ArrayList<>();

        pointList.add(targetPath.get(0));

        double x = pointList.get(0).x;
        double y = pointList.get(0).y;

        long cost = 0;

        int i=0;
        while (x < targetPath.get(targetPath.size()-1).x) {

            double nextY = y;
            byte[] data = new byte[1];
            inputMat.get((int)Math.round(x+1), (int)Math.round(nextY), data);
            int bestYValue = data[0] & 0xFF;

            if (y-1>=0){
                inputMat.get((int)Math.round(x+1), (int)Math.round(y-1), data);
                int yValue = data[0] & 0xFF;
                if (yValue== bestYValue && targetPath.get(i).y<y){
                    nextY = y-1;
                }
                if (yValue> bestYValue){
                    nextY = y-1;
                    bestYValue = yValue;
                }
            }

            if (y+1<=inputMat.height()-1){
                inputMat.get((int)Math.round(x+1), (int)Math.round(y+1), data);
                int yValue = data[0] & 0xFF;
                if (yValue== bestYValue && targetPath.get(i).y>y){
                    nextY = y+1;
                    bestYValue = yValue;
                }
                if (yValue> bestYValue){
                    nextY = y+1;
                }
            }

            Point point = new Point(x, nextY);
            pointList.add(point);
            y=nextY;
            x++;
            i++;
        }
    }


    private void generatePath(Point seedPoint, Mat inputMat) {
        List<Point> newList = new ArrayList<>();
        newList.add(seedPoint);
        long cost = getCost(newList, inputMat);


    }

    private long getCost(List<Point> points, Mat inputMat) {
        long cost = 0;
        for (Point point : points) {
            byte[] data = new byte[1];
            inputMat.get((int) point.x, (int) point.y, data);
            cost += data[0] & 0xFF;
        }

        return cost;
    }


    public ArrayList<Point> getPointList() {
        return pointList;
    }

}
