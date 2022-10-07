package nl.knaw.huc.di.images.layoutanalyzer.layoutlib;

import nl.knaw.huc.di.images.layoutanalyzer.LayoutConfiguration;
import org.opencv.core.Point;

import java.util.ArrayList;

public class Droplet {
    private boolean searchingUp=true;
    private boolean goingUp = false;
    private boolean goingDown = false;
    private boolean goingLeft = false;
    private boolean goingRight = false;
    private byte[] data;
    private int width;
    private int x;
    private int y;
    private int xStart;
    private int xStop;
    private int yStart;
    private int awayCounter = 0;
    private int lastStraightPosition = 0;
    private int upperboundary;
    private int lowerboundary;
    private ArrayList<org.opencv.core.Point> pointList;
    private int nextMoveCounter = 0;

    Droplet(byte[] data, int width, int xStart, int xStop, int yStart, int upperboundary, int lowerboundary) {
        this.data = data;
        this.width = width;
        this.xStart = xStart;
        this.xStop = xStop;
        this.yStart = yStart;

        this.x = xStart;
        this.y = yStart;
        this.upperboundary = upperboundary;
        this.lowerboundary = lowerboundary;
        this.pointList = new ArrayList<>();

        org.opencv.core.Point point = new org.opencv.core.Point(x, y);
        pointList.add(point);

        while (x < xStop && data[y * width + x + 1] != 0) {
            x++;
            point = new org.opencv.core.Point(x, y);
            pointList.add(point);
            goingRight = true;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ArrayList<org.opencv.core.Point> getPointList() {
        return pointList;
    }

    public boolean canGoRight(int xStart, int xStop, int yStart, byte[] data, int width, int x, int y) {

        return (!goingLeft || y != yStart) && x < xStop && x >= xStart && data[(y) * width + x + 1] == 0;
    }

    public void goRight() {
        x++;
        Point point = new Point(x, y);
        pointList.add(point);
        this.goingRight = true;
        this.goingDown = false;
        this.goingLeft = false;
        this.goingUp = false;
        awayCounter++;
        nextMoveCounter=0;
        if (y == yStart) {
            if (x > pointList.get(lastStraightPosition).x) {
                awayCounter = 0;
                lastStraightPosition = pointList.size() - 1;
                searchingUp = true;
            }
        }
    }

    public boolean canGoUp(int xStart, int xStop, int yStart, byte[] data, int width, int x, int y) {
        return y - 1 != upperboundary && y - 1 >= 0 && data[(y - 1) * width + x] == 0;
    }

    public void goUp() {
        y--;
        Point point = new Point(x, y);
        pointList.add(point);
        this.goingRight = false;
        this.goingDown = false;
        this.goingLeft = false;
        this.goingUp = true;
        awayCounter++;
        nextMoveCounter=0;
    }

    public boolean canGoDown(int xStart, int xStop, int yStart, byte[] data, int width, int x, int y) {
        return y + 1 != lowerboundary && (y < yStart || !searchingUp) && data[(y + 1) * width + x] == 0;
    }

    public void goDown() {
        y++;
        Point point = new Point(x, y);
        pointList.add(point);
        this.goingRight = false;
        this.goingDown = true;
        this.goingLeft = false;
        this.goingUp = false;
        awayCounter++;
        nextMoveCounter=0;
    }

    public boolean canGoLeft(int xStart, int xStop, int yStart, byte[] data, int width, int x, int y) {
        return x > xStart &&
                data[(y) * width + x - 1] == 0;
    }

    public void goLeft() {
        x--;
        Point point = new Point(x, y);
        pointList.add(point);
        this.goingRight = false;
        this.goingDown = false;
        this.goingLeft = true;
        this.goingUp = false;
        awayCounter++;
        nextMoveCounter=0;
    }

    private void nextMove() {
        nextMoveCounter++;
        if (nextMoveCounter == 5) {
            awayCounter = 99999;
        }
        //search above
        if (searchingUp) {

            if (goingRight) {
                goingRight = false;
                goingUp = true;
                return;
            }
            if (goingUp) {
                goingUp = false;
                goingLeft = true;
                return;
            }
            if (goingLeft) {
                goingLeft = false;
                goingDown = true;
                return;
            }
            if (goingDown) {
                goingDown = false;
                goingRight = true;
                return;
            }
        } else {
            // search below
            if (goingDown) {
                goingDown = false;
                goingLeft= true;
                return;
            }
            if (goingLeft) {
                goingLeft = false;
                goingUp = true;
                return;
            }
            if (goingUp) {
                goingUp = false;
                goingRight = true;
                return;
            }
            if (goingRight) {
                goingRight = false;
                goingDown= true;
                return;
            }

        }
    }

    public void move() {
        if (awayCounter > LayoutConfiguration.getDropletMaxAway()) {
            awayCounter = 0;
            while (pointList.size() - 1 > lastStraightPosition) {
                pointList.remove(pointList.size() - 1);
            }
            //cutting
            if (searchingUp){
                searchingUp = false;
                x = (int) pointList.get(lastStraightPosition).x;
                y = yStart;
                return;
            }
            x = (int) pointList.get(lastStraightPosition).x;
            y = yStart;
            searchingUp = true;
            goRight();
            return;
        }

        if (y == yStart) {
            if ((pointList.size() > 0 && x < pointList.get(lastStraightPosition).x && !goingLeft )) {
                awayCounter = 99999;
                return;
            }
            if (canGoRight(xStart, xStop, yStart, data, width, x, y)) {
                this.goRight();
                searchingUp = true;
                return;
            } else {
                if (searchingUp) {
                    if (canGoUp(xStart, xStop, yStart, data, width, x, y)) {
                        goUp();
                        return;
                    }
                    // try reversing if it's possible
                    if (canGoLeft(xStart, xStop, yStart, data, width, x, y)) {
                        goLeft();
                        return;
                    }
                }else{
                    if (canGoDown(xStart, xStop, yStart, data, width, x, y)) {
                        goDown();
                        return;
                    }
                    // try reversing if it's possible
                    if (canGoLeft(xStart, xStop, yStart, data, width, x, y)) {
                        goLeft();
                        return;
                    }
                }
                awayCounter = 99999;
                return;
            }
        }

        if (searchingUp) {
            if (goingLeft) {
                //going up & top pixel is available
                if (canGoUp(xStart, xStop, yStart, data, width, x, y)) {
                    goUp();
                    return;
                } else {
                    nextMove();
                    return;
                }
            }
            if (goingUp) {
                //going up & right pixel is available
                if (canGoRight(xStart, xStop, yStart, data, width, x, y)) {
                    goRight();
                    return;
                } else {
                    nextMove();
                    return;
                }
            }

            if (goingDown) {
                //going down & left pixel is available
                if (canGoLeft(xStart, xStop, yStart, data, width, x, y)) {
                    goLeft();
                    return;
                } else {
                    nextMove();
                    return;
                }

            }

            if (goingRight) {
                if (canGoDown(xStart, xStop, yStart, data, width, x, y)) {
                    goDown();
                    return;
                } else {
                    nextMove();
                    return;
                }

            }
        }else{
            if (goingLeft) {
                //going up & top pixel is available
                if (canGoDown(xStart, xStop, yStart, data, width, x, y)) {
                    goDown();
                    return;
                } else {
                    nextMove();
                    return;
                }
            }
            if (goingUp) {
                //going up & right pixel is available
                if (canGoLeft(xStart, xStop, yStart, data, width, x, y)) {
                    goLeft();
                    return;
                } else {
                    nextMove();
                    return;
                }
            }

            if (goingDown) {
                //going down & left pixel is available
                if (canGoRight(xStart, xStop, yStart, data, width, x, y)) {
                    goRight();
                    return;
                } else {
                    nextMove();
                    return;
                }

            }

            if (goingRight) {
                if (canGoUp(xStart, xStop, yStart, data, width, x, y)) {
                    goUp();
                    return;
                } else {
                    nextMove();
                    return;
                }
            }
        }
    }
}
