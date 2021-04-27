package sample;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

public class Controller implements Initializable {
    PictureGeneration pictureGeneration;

    static int SIZE;

    public static BufferedImage bufferedImage;
    public static BufferedImage image128;
    public static BufferedImage image256;
    public static BufferedImage image512;

    static {
        try {
            bufferedImage = ImageIO.read(new File("monalisa.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Text okText;

    public ImageView imageView;
    static int PopulationSIZE;
    static int mutationRate;
    static int MosaicSIZE;

    public MenuButton selectMenu;
    public Button uploadButton;

    public Slider populationSlider;
    public Slider mutationSlider;
    public Slider mosaicSizeSlider;

    public Button startButton;
    public Button pauseButton;
    public Button stopButton;
    public Button saveButton;

    public Button button128;
    public Button button256;
    public Button button512;

    public Text generationValue;
    public Text similarityValue;
    public Text populationValue;
    public Text mutationValue;
    public Text mosaicSize;

    @Override
    //initializing everything at the start of the program
    public void initialize(URL location, ResourceBundle resources) {
        Tooltip t1 = new Tooltip(); //setting tooltip for mosaic size
        t1.setStyle("-fx-font-size: 12.0");
        t1.setText("Mosaic Size: the lower the value is,\n" +
                "the more mosaic element will fit in the\n" +
                "picture, which would worsen the speed of generation");
        mosaicSizeSlider.setTooltip(t1);

        Tooltip t2 = new Tooltip(); //setting tooltip for mutation value
        t2.setStyle("-fx-font-size: 12.0");
        t2.setText("Mutation Value: the higher the value is,\n" +
                "the higher probability of each of the chromosomes is");
        mutationSlider.setTooltip(t2);

        Tooltip t3 = new Tooltip(); //setting tooltip for population size
        t3.setStyle("-fx-font-size: 12.0");
        t3.setText("Population Value: the higher the value is,\n" +
                "the more picture there will be in a generation,\n " +
                "which would worsen the speed of generation");
        populationSlider.setTooltip(t3);

        Tooltip t4 = new Tooltip(); //setting tooltip for pause button
        t4.setStyle("-fx-font-size: 12.0");
        t4.setText("To save the outcome, pause the generation");
        pauseButton.setTooltip(t4);

        SIZE = 128; //setting current size

        PopulationSIZE = 20; //setting initial population size
        populationSlider.setValue(PopulationSIZE);
        mosaicSize.setText(Integer.toString(MosaicSIZE));
        populationValue.setText(Integer.toString(PopulationSIZE));

        mutationRate = 20; //setting initial mutation value
        mutationSlider.setValue(mutationRate);
        mutationValue.setText(Integer.toString(mutationRate));

        MosaicSIZE = 16; //setting initial mosaic size
        mosaicSizeSlider.setValue(MosaicSIZE);
        mosaicSize.setText(Integer.toString(MosaicSIZE));

        try { //setting up the main image
            setupTheMainImage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //creating a new generation
        pictureGeneration = new PictureGeneration(SIZE, MosaicSIZE, PopulationSIZE, mutationRate, image128, image128, this);
        pictureGeneration.generation(); //setting up the generation
    }

    void setupTheMainImage() throws IOException { //setting up the main image
        /*
        creating images of three different sizes so that when a user changes the size
        a suitable picture will appear
         */
        image128 = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        Graphics g = image128.createGraphics();
        g.drawImage(this.bufferedImage, 0, 0, 128, 128, null);
        g.dispose();

        image256 = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
        Graphics g1 = image256.createGraphics();
        g1.drawImage(this.bufferedImage, 0, 0, 256, 256, null);
        g1.dispose();

        image512 = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
        Graphics g2 = image512.createGraphics();
        g2.drawImage(this.bufferedImage, 0, 0, 512, 512, null);
        g2.dispose();

        if (SIZE == 128) setImage(image128); //setting the suitable image
        if (SIZE == 256) setImage(image256);
        if (SIZE == 512) setImage(image512);
    }

    //handling input data for population number
    public void handlePopulation(MouseEvent mouseEvent) throws IOException {
        PopulationSIZE = (int)populationSlider.getValue();
        populationSlider.setValue(PopulationSIZE);
        populationValue.setText(Integer.toString(PopulationSIZE));

        pictureGeneration.populationSize = PopulationSIZE;

        setupTheMainImage();
        resetPictureGeneration();
        pictureGeneration.generation();
    }

    //handling input data for mutation value
    public void handleMutation(MouseEvent mouseEvent) throws IOException {
        mutationRate = (int)mutationSlider.getValue();
        mutationSlider.setValue(mutationRate);
        mutationValue.setText(Integer.toString(mutationRate));

        pictureGeneration.mutationRate = mutationRate;
    }

    //handling input data for mosaic size
    public void handleMosaicSize(MouseEvent mouseEvent) throws IOException {
        MosaicSIZE = ((int)mosaicSizeSlider.getValue() / 2) * 2;
        mosaicSizeSlider.setValue(MosaicSIZE);
        mosaicSize.setText(Integer.toString(MosaicSIZE));

        pictureGeneration.mosaicSize = MosaicSIZE;

        setupTheMainImage();
        resetPictureGeneration();
        pictureGeneration.generation();
    }

    //when a user sets the size to 128, reset everything
    public void handle128(MouseEvent mouseEvent) throws IOException {
        if (!button128.isDisabled()){
            SIZE = 128;

            button128.setDisable(true);
            button256.setDisable(false);
            button512.setDisable(false);

            setupTheMainImage();
            resetPictureGeneration();
            pictureGeneration.generation();
        }
    }

    //when a user sets the size to 256, reset everything
    public void handle256(MouseEvent mouseEvent) throws IOException {
        if (!button256.isDisabled()){
            SIZE = 256;
            button256.setDisable(true);
            button128.setDisable(false);
            button512.setDisable(false);

            setupTheMainImage();
            resetPictureGeneration();
            pictureGeneration.generation();
        }
    }

    //when a user sets the size to 512, reset everything
    public void handle512(MouseEvent mouseEvent) throws IOException {
        if (!button512.isDisabled()){
            SIZE = 512;
            button512.setDisable(true);
            button256.setDisable(false);
            button128.setDisable(false);

            setupTheMainImage();
            resetPictureGeneration();
            pictureGeneration.generation();
        }
    }

    //when a user presses the start button, start processing
    public void handleStart(MouseEvent mouseEvent) throws IOException {
        if (!startButton.isDisabled()){
            if (!pauseButton.isDisabled()) {
                setupTheMainImage();
                resetPictureGeneration();
                pictureGeneration.generation();
            }

            pauseButton.setDisable(false);
            stopButton.setDisable(false);
            startButton.setDisable(true);
            saveButton.setDisable(true);

            pictureGeneration.setActive(true);

            disableEverything(true);

            //creating a thread to generate pictures
            Task<Void> task = new Task<Void>() {
                @Override protected Void call() throws Exception {
                    while(pictureGeneration.isActive()){
                        pictureGeneration.processing();
                    }
                    return null;
                }
            };

            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
        }
    }

    //when a user presses the pause button, start processing
    public void handlePause(MouseEvent mouseEvent) {
        if (!pauseButton.isDisabled()){
            stopButton.setDisable(false);
            pauseButton.setDisable(true);
            startButton.setDisable(false);
            saveButton.setDisable(false);

            pictureGeneration.setActive(false);
        }
    }

    //when a user presses the ыещз button, we stop and reset everything
    public void handleStop(MouseEvent mouseEvent) throws IOException {
        if (!stopButton.isDisabled()){
            stopButton.setDisable(true);
            pauseButton.setDisable(true);
            startButton.setDisable(false);
            saveButton.setDisable(true);

            generationValue.setText("0");
            similarityValue.setText("0");

            setupTheMainImage();

            disableEverything(false);
            pictureGeneration.setActive(false);
            resetPictureGeneration();

            if (SIZE == 128) button128.setDisable(true);
            if (SIZE == 256) button256.setDisable(true);
            if (SIZE == 512) button512.setDisable(true);
        }
    }

    public void setImage(BufferedImage nextImage) throws IOException {
        Image image = SwingFXUtils.toFXImage(nextImage, null );

        imageView.setFitHeight(280);
        imageView.setFitWidth(280);
        imageView.setImage(image);
    }

    public void setSimilarity(double similarity){
        similarity*=10000;
        similarityValue.setText(((double)((int)similarity)/100) + "%");
    }

    void resetPictureGeneration() throws IOException {
        if (SIZE == 128) pictureGeneration.reset(image128);
        if (SIZE == 256) pictureGeneration.reset(image256);
        if (SIZE == 512) pictureGeneration.reset(image512);

        pictureGeneration.generation();
    }

    //when we choose mona lisa
    public void handleMonaLisa(ActionEvent actionEvent) throws IOException {
        bufferedImage = ImageIO.read(new File("monalisa.jpg"));
        setupTheMainImage();
    }

    //when we choose the girl with a pearl earring
    public void handleGirl(ActionEvent actionEvent) throws IOException {
        bufferedImage = ImageIO.read(new File("GirlWithAPearlEarring.jpg"));
        setupTheMainImage();
    }

    //when we choose the starry night
    public void handleTheStarryNight(ActionEvent actionEvent) throws IOException {
        bufferedImage = ImageIO.read(new File("TheStarryNight.jpg"));
        setupTheMainImage();
    }

    //when we choose the scream
    public void handleTheScream(ActionEvent actionEvent) throws IOException {
        bufferedImage = ImageIO.read(new File("TheScream.jpg"));
        setupTheMainImage();
    }

    //when we choose miley cyrus
    public void handleMileyCyrus(ActionEvent actionEvent) throws IOException {
        bufferedImage = ImageIO.read(new File("mileycyrus.jpg"));
        setupTheMainImage();
    }

    //when we choose hungry cat
    public void handleHungryCat(ActionEvent actionEvent) throws IOException {
        bufferedImage = ImageIO.read(new File("hungrycat.jpg"));
        setupTheMainImage();
    }

    //turning off the buttons
    void disableEverything(boolean b){
        button128.setDisable(b);
        button512.setDisable(b);
        button256.setDisable(b);

        selectMenu.setDisable(b);
        uploadButton.setDisable(b);

        populationSlider.setDisable(b);
        mosaicSizeSlider.setDisable(b);
        //mutationSlider.setDisable(b);

        if (b) okText.setFill(awtColorToJavaFX(Color.GRAY));
        else okText.setFill(awtColorToJavaFX(new Color(244, 244, 244)));
    }

    private javafx.scene.paint.Color awtColorToJavaFX(Color c) {
        return javafx.scene.paint.Color.rgb(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() / 255.0);
    }

    //when we want to upload a picture, the window should open
    public void handleUpload(MouseEvent mouseEvent) throws IOException {
        uploadButton.setDisable(true);

        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.jpeg", "*.png", "*.");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            bufferedImage = ImageIO.read(file);
            setupTheMainImage();
        }

        uploadButton.setDisable(false);
    }

    //when we want to save a picture, the window should open
    public void handleSaveButton(MouseEvent mouseEvent) throws IOException {
        saveButton.setDisable(true);

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Choose location To Save The Picture");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.jpeg", "*.png", "*.tiff");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("outcome.jpg");

        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            if (SIZE == 128) ImageIO.write(image128, "jpg", file);
            if (SIZE == 256) ImageIO.write(image256, "jpg", file);
            if (SIZE == 512) ImageIO.write(image512, "jpg", file);
        }
    }
}
