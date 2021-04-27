package sample;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PictureGeneration{
    Controller ct;

    public static int SIZE;
    public  static int mosaicSize;
    public static int populationSize;
    public static int mutationRate;
    BufferedImage mainImage;

    private static boolean active = true;
    int generation = 0;

    //having thee generations of pictures each filtered out
    static ArrayList<Picture> myRedPictures = new ArrayList<>(populationSize);
    static ArrayList<Picture> myGreenPictures = new ArrayList<>(populationSize);
    static ArrayList<Picture> myBluePictures = new ArrayList<>(populationSize);

    public static BufferedImage nextImage;

    //constructor
    public PictureGeneration(int SIZE, int mosaicSize, int populationSize, int mutationRate, BufferedImage image, BufferedImage mainImage, Controller ct){
        nextImage = image;
        this.mainImage = mainImage;
        this.mosaicSize = mosaicSize;
        this.SIZE = SIZE;
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.ct = ct;
    }

    BufferedImage redPicture;
    BufferedImage greenPicture;
    BufferedImage bluePicture;

    //resetting everything
    public void reset(BufferedImage image) throws IOException {
        nextImage = image;
        ct.setImage(image);
        myRedPictures = new ArrayList<>(populationSize);
        myGreenPictures = new ArrayList<>(populationSize);
        myBluePictures = new ArrayList<>(populationSize);
        generation = 0;
        SIZE = Controller.SIZE;
        mosaicSize = Controller.MosaicSIZE;
    }

    //here i create he first generation to work with after
    public void generation(){
        redPicture = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
        greenPicture = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
        bluePicture = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < SIZE; i++){
            for (int j = 0 ; j < SIZE; j++){
                Color color = new Color(nextImage.getRGB(i, j));

                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();

                Color red = new Color(r, 0, 0);
                redPicture.setRGB(i, j, red.getRGB());

                Color green = new Color(0, g, 0);
                greenPicture.setRGB(i, j, green.getRGB());

                Color blue = new Color(0, 0, b);
                bluePicture.setRGB(i, j, blue.getRGB());
            }
        }

        for (int i = 0; i < populationSize; i++){
            myRedPictures.add(new Picture(SIZE, mosaicSize, mutationRate));
            myRedPictures.get(i).generatePicture("red");

            myGreenPictures.add(new Picture(SIZE, mosaicSize, mutationRate));
            myGreenPictures.get(i).generatePicture("green");

            myBluePictures.add(new Picture(SIZE, mosaicSize, mutationRate));
            myBluePictures.get(i).generatePicture("blue");
        }

        generation++;
    }

    //here i start processing
    void processing() throws IOException {

        //setting the similarity value for each picture
        for (int i = 0; i < populationSize; i++) {
            myRedPictures.get(i).similarity = similarity(myRedPictures.get(i).image("red"), redPicture);
            myGreenPictures.get(i).similarity = similarity(myGreenPictures.get(i).image("green"), greenPicture);
            myBluePictures.get(i).similarity = similarity(myBluePictures.get(i).image("blue"), bluePicture);
        }

        //sorting by similarity values
        myRedPictures.sort(Collections.reverseOrder());
        myBluePictures.sort(Collections.reverseOrder());
        myGreenPictures.sort(Collections.reverseOrder());

        //creating children by crossing over
        ArrayList<Picture> redChildren = crossover(myRedPictures);
        ArrayList<Picture> greenChildren = crossover(myGreenPictures);
        ArrayList<Picture> blueChildren = crossover(myBluePictures);

        //creating the new three populations from the children
        ArrayList<Picture> newRedPopulation = newPopulation(redChildren, "red");
        ArrayList<Picture> newGreenPopulation = newPopulation(greenChildren, "green");
        ArrayList<Picture> newBluePopulation = newPopulation(blueChildren, "blue");

        //choosing the best pictures from each generation to output
        BufferedImage nextRedImage = newRedPopulation.get(0).image("red");
        BufferedImage nextGreenImage = newGreenPopulation.get(0).image("green");
        BufferedImage nextBlueImage = newBluePopulation.get(0).image("blue");

        //combining the best pictures into one
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Color redColor = new Color(nextRedImage.getRGB(i, j));
                Color greenColor = new Color(nextGreenImage.getRGB(i, j));
                Color blueColor = new Color(nextBlueImage.getRGB(i, j));

                Color nextColor = new Color(redColor.getRed(), greenColor.getGreen(), blueColor.getBlue());

                nextImage.setRGB(i, j, nextColor.getRGB());
            }
        }

        //replace the previous generations by the new ones
        myRedPictures = new ArrayList<>(newRedPopulation);
        myGreenPictures = new ArrayList<>(newGreenPopulation);
        myBluePictures = new ArrayList<>(newBluePopulation);

        //outputting data
        ct.generationValue.setText(Integer.toString(generation));
        ct.setImage(nextImage);

        mainImage = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics g = mainImage.createGraphics();
        g.drawImage(ct.bufferedImage, 0, 0, SIZE, SIZE, null);
        g.dispose();

        ct.setSimilarity(similarity(nextImage, mainImage));
        generation++;
    }

    //checking similarity (the fitness function)
    private double similarity(BufferedImage image1, BufferedImage image2){
        double total = 0;

        for (int i = 0; i < SIZE; i++){
            for (int j = 0; j < SIZE; j++){
                Color color1 = new Color(image1.getRGB(i, j));
                Color color2 = new Color(image2.getRGB(i, j));

                int sum1 = color1.getRed() + color1.getGreen() + color1.getBlue();
                int sum2 = color2.getRed() + color2.getGreen() + color2.getBlue();

                double diff = (double)(Math.abs(sum1 - sum2))/765;
                total += 1 - diff;
            }
        }

        total/=(SIZE*SIZE);
        return total;
    }

    //doing the crossover
    private ArrayList<Picture> crossover(ArrayList<Picture> bestPictures){
        ArrayList<Picture> children = new ArrayList<>();

        int childrenAmount = 0;

        //creating children from 3 best pictures

        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                if (childrenAmount == populationSize) return children;
                else {
                    if (i != j){
                        Picture child = child(bestPictures.get(i), bestPictures.get(j));
                        children.add(child);

                        childrenAmount++;
                    }
                }
            }
        }

        return children;
    }

    //giving a birth to new pictures
    private Picture child(Picture picture1, Picture picture2){

        //in this function each mosaic element of a child gets
        //X% of color from parent #1 nad 100-X% of color from parent #2

        Random rand = new Random();
        Picture child = new Picture(SIZE, mosaicSize, mutationRate);

        ArrayList<Crystal> Crystals1 = picture1.myCrystals;
        ArrayList<Crystal> Crystals2 = picture2.myCrystals;

        for (int i = 0; i < Crystals1.size(); i++){
            Crystal cr = new Crystal();
            cr.X = Crystals1.get(i).X;
            cr.Y = Crystals1.get(i).Y;

            double rand1 = rand.nextDouble();
            int r = (int)(Crystals1.get(i).color.getRed()*rand1 + Crystals2.get(i).color.getRed()*(1-rand1));

            double rand2 = rand.nextDouble();
            int g = (int)(Crystals1.get(i).color.getGreen()*rand2 + Crystals2.get(i).color.getGreen()*(1-rand2));

            double rand3 = rand.nextDouble();
            int b = (int)(Crystals1.get(i).color.getBlue()*rand3 + Crystals2.get(i).color.getBlue()*(1-rand3));

            cr.color = new Color(r, g, b);

            child.myCrystals.add(cr);
        }

        return child;
    }

    //creating a new population by copying the children and mutating them,
    //so they are kind of siblings
    private ArrayList<Picture> newPopulation(ArrayList<Picture> children, String code){
        ArrayList<Picture> newPopulation = new ArrayList<>();

        for (int i = 0; i < 6; i++){
            Picture curr = children.get(i);
            newPopulation.add(curr);
        }

        for (int i = 0; ; i++){
            if (i == children.size() - 1) i = 0;

            Picture next1 = children.get(i);
            next1.mutate(code);
            newPopulation.add(next1);
            if (newPopulation.size() == populationSize) break;

            Picture next2 = children.get(i);
            next2.mutate(code);
            newPopulation.add(next2);
            if (newPopulation.size() == populationSize) break;
        }

        return newPopulation;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public boolean isActive(){
        return active;
    }
}
