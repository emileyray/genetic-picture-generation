package sample;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Picture implements Comparable<Picture>{
    int SIZE;
    int CRYSTAL_SIZE;
    int mutationRate;
    BufferedImage image;
    double similarity;

    ArrayList<Crystal> myCrystals = new ArrayList<>();

    //constructor
    public Picture(int SIZE, int CRYSTAL_SIZE, int mutationRate){
        image = new BufferedImage(SIZE, SIZE,BufferedImage.TYPE_INT_ARGB);

        this.CRYSTAL_SIZE = CRYSTAL_SIZE;
        this.SIZE = SIZE;
        this.mutationRate = mutationRate;

        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);

        g2d.fillRect(0, 0, SIZE,SIZE);
    }

    //creating a picture: red, green or blue
    public void generatePicture(String code){
        Random rand = new Random();
        for (int i = 0; i < SIZE+CRYSTAL_SIZE; i+=CRYSTAL_SIZE){
            for (int j = 0; j < SIZE+CRYSTAL_SIZE; j+=CRYSTAL_SIZE){
                Crystal nextCrystal = new Crystal();
                nextCrystal.color = new Color((code.equalsIgnoreCase("red") ? 1 : 0) * rand.nextInt(256),
                        (code.equalsIgnoreCase("green") ? 1 : 0)* rand.nextInt(256),
                        (code.equalsIgnoreCase("blue") ? 1 : 0) *rand.nextInt(256));
                if (j%(CRYSTAL_SIZE*2) == 0) nextCrystal.X = i;
                else nextCrystal.X = i + CRYSTAL_SIZE/2;
                nextCrystal.Y = j;
                myCrystals.add(nextCrystal);
            }
        }
    }

    //mutating a picture by changing a color of an element
    public void mutate(String code){
        for(Crystal c: myCrystals) {
            Random rand = new Random();
            int cont = rand.nextInt(100) + 1;

            //the chances of mutation are defined by the user
            //so we have a condition below

            if (cont <= mutationRate) {
                int r = c.color.getRed();
                int g = c.color.getGreen();
                int b = c.color.getBlue();

                r += -5 + rand.nextInt(11);
                if (r < 0) r = 0;
                if (r > 255) r = 255;
                g += -5 + rand.nextInt(11);
                if (g < 0) g = 0;
                if (g > 255) g = 255;
                b += -5 + rand.nextInt(11);
                if (b < 0) b = 0;
                if (b > 255) b = 255;

                c.color = new Color((code.equalsIgnoreCase("red") ? 1 : 0) * r,
                        (code.equalsIgnoreCase("green") ? 1 : 0) * g,
                        (code.equalsIgnoreCase("blue") ? 1 : 0) * b);
            }
        }
    }

    //convert the data into image
    public BufferedImage image(String code){
        BufferedImage image;

        image = new BufferedImage(SIZE, SIZE,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);

        for (Crystal c: myCrystals) {
            int r = c.color.getRed();
            int g = c.color.getGreen();
            int b = c.color.getBlue();

            Polygon p1 = upperLeftPolygon(c.X, c.Y);
            g2d.setColor(new Color((code.equalsIgnoreCase("red") ? 1 : 0) * Math.min(r + 20, 255),
                    (code.equalsIgnoreCase("green") ? 1 : 0) * Math.min(g + 20, 255),
                    (code.equalsIgnoreCase("blue") ? 1 : 0) * Math.min(b + 20, 255)));
            g2d.fill(p1);
            Polygon p2 = upperRightPolygon(c.X, c.Y);
            g2d.setColor(new Color((code.equalsIgnoreCase("red") ? 1 : 0) * Math.min(r + 2, 255),
                    (code.equalsIgnoreCase("green") ? 1 : 0) * Math.min(g + 2, 255),
                    (code.equalsIgnoreCase("blue") ? 1 : 0) * Math.min(b + 2, 255)));
            g2d.fill(p2);
            Polygon p3 = lowerLeftPolygon(c.X, c.Y);
            g2d.setColor(new Color((code.equalsIgnoreCase("red") ? 1 : 0) * Math.max(r - 2, 0),
                    (code.equalsIgnoreCase("green") ? 1 : 0) * Math.max(g - 2, 0),
                    (code.equalsIgnoreCase("blue") ? 1 : 0) * Math.max(b - 2, 0)));
            g2d.fill(p3);
            Polygon p4 = lowerRightPolygon(c.X, c.Y);
            g2d.setColor(new Color((code.equalsIgnoreCase("red") ? 1 : 0) * Math.max(r - 20, 0),
                    (code.equalsIgnoreCase("green") ? 1 : 0) * Math.max(g - 20, 0),
                    (code.equalsIgnoreCase("blue") ? 1 : 0) * Math.max(b - 20, 0)));
            g2d.fill(p4);
        }

        return image;
    }

    //for construction of a mosaic element
    Polygon upperLeftPolygon(int X, int Y){
        Polygon p = new Polygon();
        p.addPoint(X-CRYSTAL_SIZE/2, Y);
        p.addPoint(X, Y);
        p.addPoint(X, Y+CRYSTAL_SIZE);
        return p;
    }

    //for construction of a mosaic element
    Polygon upperRightPolygon(int X, int Y){
        Polygon p = new Polygon();
        p.addPoint(X+CRYSTAL_SIZE/2, Y);
        p.addPoint(X, Y);
        p.addPoint(X, Y+CRYSTAL_SIZE);
        return p;
    }

    //for construction of a mosaic element
    Polygon lowerLeftPolygon(int X, int Y){
        Polygon p = new Polygon();
        p.addPoint(X-CRYSTAL_SIZE/2, Y);
        p.addPoint(X, Y);
        p.addPoint(X, Y-CRYSTAL_SIZE);
        return p;
    }

    //for construction of a mosaic element
    Polygon lowerRightPolygon(int X, int Y){
        Polygon p = new Polygon();
        p.addPoint(X+CRYSTAL_SIZE/2, Y);
        p.addPoint(X, Y);
        p.addPoint(X, Y-CRYSTAL_SIZE);
        return p;
    }

    //making the pictures comparable by their similarity
    @Override
    public int compareTo(Picture o) {
        int result;
        if (this.similarity > o.similarity) result = 1;
        else {
            if (this.similarity < o.similarity) result = -1;
            else result = 0;
        }
        return result;
    }
}

//the crystal class
class Crystal{
    Color color;
    int X;
    int Y;
}
