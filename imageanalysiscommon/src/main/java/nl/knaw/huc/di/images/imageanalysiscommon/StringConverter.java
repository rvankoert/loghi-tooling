package nl.knaw.huc.di.images.imageanalysiscommon;

import com.goebl.simplify.PointExtractor;
import com.goebl.simplify.Simplify;
import com.google.common.base.Strings;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringConverter {
    public static String pointToString(Point point) {
        return Math.round(point.x) + "," + Math.round(point.y);
    }


    public static String pointToString(Rect rectangle) {
        String returnValue = "";
        Point leftTop = new Point(rectangle.x, rectangle.y);
        Point rightTop = new Point(rectangle.x + rectangle.width, rectangle.y);
        Point leftBottom = new Point(rectangle.x, rectangle.y + rectangle.height);
        Point rightBottom = new Point(rectangle.x + rectangle.width, rectangle.y + rectangle.height);
        returnValue += pointToString(leftTop) + " ";
        returnValue += pointToString(rightTop) + " ";
        returnValue += pointToString(rightBottom) + " ";
        returnValue += pointToString(leftBottom);
        return returnValue;
    }

    public static String pointToString(List<Point> points) {
        StringBuilder returnValue = new StringBuilder();
        for (Point point : points) {
            returnValue.append(pointToString(point)).append(" ");
        }
        return returnValue.toString().trim();
    }

    public static ArrayList<Point> stringToPoint(String points) {
        return stringToPoint(points, false);
    }

    public static ArrayList<Point> stringToPoint(String points, boolean fixErrors) {
        ArrayList<Point> returnPoints = new ArrayList<>();
        if (points == null || points.isEmpty()) {
            return returnPoints;
        }

        int length = points.length();
        int start = 0;
        while (start < length) {
            int commaIndex = points.indexOf(',', start);
            int spaceIndex = points.indexOf(' ', commaIndex + 1);
            if (spaceIndex == -1) {
                spaceIndex = length;
            }

            double x = Double.parseDouble(points.substring(start, commaIndex));
            double y = Double.parseDouble(points.substring(commaIndex + 1, spaceIndex));

            if (fixErrors) {
                if (x < 0) {
                    x = 0;
                }
                if (y < 0) {
                    y = 0;
                }
            }

            returnPoints.add(new Point(x, y));
            start = spaceIndex + 1;
        }

        return returnPoints;
    }

    public static double calculateBaselineLength(List<Point> points) {
        Point begin = null;
        Point end = null;
        double length = 0;
        for (Point point : points) {
            end = point;

            if (begin != null) {
                length += distance(begin, end);
            }
            begin = end;
        }

        return length;
    }

    public static double distance(Point seed, Point point) {
        double currentDistance = Math.sqrt(Math.pow((seed.x - point.x), 2.0) + Math.pow((seed.y - point.y), 2.0));
        return currentDistance;
    }

    public static double distanceVertical(Point seed, Point point) {
        return point.y - seed.y;
    }

    public static double distanceHorizontal(Point seed, Point point) {
        return point.x - seed.x;
    }


    public static List<Point> expandPointList(List<Point> compressed) {
        List<Point> expanded = new ArrayList<>();
        Point lastPoint = null;
        for (Point point : compressed) {
            if (lastPoint == null) {
                lastPoint = point.clone();
                continue;
            }
            int startX = (int) lastPoint.x;
            int startY = (int) lastPoint.y;
            int lastX = (int) point.x;
            int lastY = (int) point.y;
//            int distance = lastX - startX;
            double distance = distance(lastPoint, point);
            double distanceHorizontal = distanceHorizontal(lastPoint, point);
            double distanceVertical = distanceVertical(lastPoint, point);
            expanded.add(lastPoint);
            for (int i = 1; i <= distance; i++) {
                Point newPoint = new Point(startX + distanceHorizontal * ((double) i / distance), startY + distanceVertical * ((double) i / distance));
                expanded.add(newPoint);
            }
//            for (int i = startX; i < point.x; i++) {
//                int y = startY + (i - startX) * (lastY - startY) / distance;
//                expanded.add(new Point(i, y));
//            }
            lastPoint = point.clone();
        }
        return expanded;
    }

    private static PointExtractor<Point> getPointExtractor() {
        PointExtractor<Point> pointPointExtractor = new PointExtractor<Point>() {
            @Override
            public double getX(Point point) {
                return point.x;
            }

            @Override
            public double getY(Point point) {
                return point.y;
            }

        };
        return pointPointExtractor;
    }

    public static synchronized List<Point> simplifyPolygon(List<Point> points) {
        return simplifyPolygon(points, 5);
    }

    public static synchronized List<Point> simplifyPolygon(List<Point> points, double tolerance) {
        if (points.size() <= 2) {
            return points;
        }
        Simplify<Point> simplify = new Simplify<Point>(new Point[0], getPointExtractor());

        Point[] allPoints = points.toArray(new Point[0]);
        boolean highQuality = true; // Douglas-Peucker, false for Radial-Distance

        // run simplification process
        Point[] lessPoints = simplify.simplify(allPoints, tolerance, highQuality);
        return Arrays.asList(lessPoints);
    }

    public static String boundingBoxToPoints(Rect boundingBox) {
        List<Point> points = new ArrayList<>();
        points.add(new Point(boundingBox.x, boundingBox.y));
        points.add(new Point(boundingBox.x + boundingBox.width, boundingBox.y));
        points.add(new Point(boundingBox.x + boundingBox.width, boundingBox.y + boundingBox.height));
        points.add(new Point(boundingBox.x, boundingBox.y + boundingBox.height));
        return StringConverter.pointToString(points);
    }
}
