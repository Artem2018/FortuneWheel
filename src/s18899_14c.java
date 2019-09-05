import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;


public class s18899_14c extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private Button start = new Button("Start");
    private Button add = new Button("ADD");
    private ListView<String> llistview = null;
    private ListView<String> rlistview = null;
    private Button lrBut = new Button(" -> "); // ->
    private Button rlBut = new Button(" <- "); // <-
    private Button dlrBut = new Button(" >> "); // ->
    private Button drlBut = new Button(" << "); // <-
    private ArrayList<String> phrases = new ArrayList<>();
    private ListView<String> lastView;
    private Text text;
    private char[] c;
    private Rectangle bg;
    private ArrayList<Text> gwrd = new ArrayList<>();          // list of letters for the guessed word
    private ArrayList<Rectangle> rctngls = new ArrayList<>(); // list of rectangles
    private SimpleIntegerProperty count = new SimpleIntegerProperty();
    private Text score;
    private int countM;
    private int countG;
    private Image wheel;
    private ImageView imageView;
    private Image arrow;
    private ImageView arrowView;
    private TextInputDialog guessed;
    private Button endgame;
    private Alert a;
    private double angle;
    private RotateTransition rotateTransition;
    private ArrayList<Character> rep = new ArrayList<>();
    private RotateTransition rt;
    private double prev;
    private ArrayList<Character> doublelet = new ArrayList<>();
    //Layout 2
    private BorderPane layout2 = new BorderPane();
    private int countdd;
    private static final Font DEFAULT_FONT = new Font("Courier", 36);
    private Scene scene2;

    public void init() {
        ObservableList<String> rwords =
                FXCollections.observableArrayList(
                        "Beginner", "Potato", "Ice", "Tree", "Cream", "Investigation");
        ObservableList<String> lwords =
                FXCollections.observableArrayList();
        llistview = new ListView<>(lwords);
        rlistview = new ListView<>(rwords);
        // listeners for arrow buttons
        EventHandler<ActionEvent> hHandler =
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        ListView<String> fromView;
                        ObservableList<String> fromList,
                                toList;
                        if (event.getSource().equals(rlBut)) {
                            fromView = rlistview;
                            fromList = rwords;
                            toList = lwords;
                        } else if (event.getSource()
                                .equals(lrBut)) {
                            fromView = llistview;
                            fromList = lwords;
                            toList = rwords;
                        } else return;
                        String s = fromView.getSelectionModel()
                                .getSelectedItem();
                        if (s != null) {
                            fromView.getSelectionModel()
                                    .clearSelection();
                            fromList.remove(s);
                            toList.add(s);
                        }
                    }
                };

        EventHandler<ActionEvent> ddHandler = new EventHandler<ActionEvent>() {
            ListView<String> fromView;
            ObservableList<String> fromList,
                    toList;

            @Override
            public void handle(ActionEvent actionEvent) {

                if (actionEvent.getSource().equals(drlBut)) {
                    fromView = rlistview;
                    fromList = rwords;
                    toList = lwords;
                    toList.addAll(fromView.getSelectionModel().getSelectedItems());
                    fromList.removeAll(fromView.getSelectionModel().getSelectedItems());
                } else if (actionEvent.getSource().equals(dlrBut)) {
                    fromView = llistview;
                    fromList = lwords;
                    toList = rwords;
                    toList.addAll(fromView.getSelectionModel().getSelectedItems());
                    fromList.removeAll(fromView.getSelectionModel().getSelectedItems());
                }
                fromView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                fromView.getSelectionModel().selectAll();
            }
        };

        lrBut.setOnAction(hHandler);
        rlBut.setOnAction(hHandler);
        drlBut.setOnAction(ddHandler);
        dlrBut.setOnAction(ddHandler);
    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Fortune wheel");
        BorderPane root = new BorderPane();
        Scene scene1 = new Scene(root, 450, 250);
        GridPane grid = new GridPane();
        grid.setGridLinesVisible(false);
        grid.setPadding(new Insets(5, 20, 10, 20));
        grid.setHgap(10);
        grid.setVgap(10);
        ColumnConstraints col1 =
                new ColumnConstraints(150, 150, Double.MAX_VALUE);
        ColumnConstraints col2 =
                new ColumnConstraints(80);
        ColumnConstraints col3 =
                new ColumnConstraints(150, 150, Double.MAX_VALUE);
        col1.setHgrow(Priority.ALWAYS);
        col3.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2, col3);

        Label lLab = new Label("Phrases for game");
        GridPane.setHalignment(lLab, HPos.CENTER);
        grid.add(lLab, 0, 0);
        llistview.focusedProperty().addListener((o, p, n) -> { // focus on the last used list
            if (n) lastView = llistview;
        });

        Label gLab = new Label("All phrases");
        GridPane.setHalignment(gLab, HPos.CENTER);
        grid.add(gLab, 2, 0);
        rlistview.focusedProperty().addListener((o, p, n) -> {
            if (n) lastView = llistview;
        });

        // input dialog
        TextInputDialog inpD = new TextInputDialog();
        inpD.setTitle("Input");
        inpD.setHeaderText("Please, set new phrase ");
        inpD.setContentText("Phrase:");

        // Add button
        add.setOnAction((ActionEvent event) -> {
            Optional<String> result = inpD.showAndWait();
            if (result.isPresent()) {
                rlistview.getItems().add(result.get());
            }

        });


        // Error dialog
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setResizable(true);
        alert.setTitle("Error");
        alert.setHeaderText("Wrong number of phrases");
        alert.setContentText("The number of phrases should be more or equal  3");


        // Start button
        start.setOnAction((ActionEvent event) -> {

                    gwrd.clear();
                    angle = 360;
                    rctngls.clear();
                    countM = 0;
                    countG = 0;
                    count.set(0);
                    rep.clear();

                    if (llistview.getItems().size() >= 3) {

                        // start new layout
                        stage.setScene(scene2);
                        GridPane upperBox = new GridPane();

                        for (int num = 0; num < llistview.getItems().size(); num++) {
                            phrases.add(llistview.getItems().get(num));                 //put our phrases into the array list
                        }
                        // upper pane
                        int random = new Random().nextInt(phrases.size());     // random index of the selected phrase
                        c = phrases.get(random).toUpperCase().toCharArray(); // divide our phrase to char array
                        for (int i = 0; i < c.length; i++) {
                            System.out.print(c[i]);
                            bg = new Rectangle(30, 50);
                            bg.setFill(c[i] == ' ' ? Color.DARKSEAGREEN : Color.WHITE);
                            bg.setStroke(Color.BLACK);
                            text = new Text(String.valueOf(c[i]).toUpperCase());
                            text.setFont(DEFAULT_FONT);
                            text.setVisible(false);
                            gwrd.add(text);
                            rctngls.add(bg);
                            upperBox.setHgap(5);
                            upperBox.setAlignment(Pos.CENTER);
                            upperBox.add(bg, i, 0);
                            upperBox.add(text, i, 0);
                        }
                        System.out.println();
                        layout2.setTop(upperBox);
                        //
                    } else alert.showAndWait();
                }
        );


        Alert exit = new Alert(Alert.AlertType.CONFIRMATION);
        exit.setTitle("End");
        exit.setHeaderText("Do you want to finish your game?");

        // Main menu button
        Button mainMenu = new Button("Main menu");
        mainMenu.setOnAction(actionEvent -> {
            Optional<ButtonType> result = exit.showAndWait();
            if (result.get() == ButtonType.OK) {
                stage.setScene(scene1);
                // deleting previous modifications
                gwrd.clear();
                angle = 360;
                rctngls.clear();
                countM = 0;
                countG = 0;
                count.set(0);
                rep.clear();
                doublelet.clear();
            }
        });

        // not visible button, fire in case of win or lose
        endgame = new Button();
        endgame.setOnAction(actionEvent -> {
            stage.setScene(scene1);
            // deleting previous modifications
            angle = 360;
            gwrd.clear();
            rctngls.clear();
            countM = 0;
            countG = 0;
            count.set(0);
            rep.clear();
            doublelet.clear();
        });

        // request the letter
        guessed = new TextInputDialog();
        guessed.setTitle("New Letter");
        guessed.setHeaderText("Enter the letter ");
        guessed.setContentText("Letter:");
        guessed.getDialogPane().lookupButton(ButtonType.CANCEL).setVisible(false);
        guessed.initStyle(StageStyle.UNDECORATED);                  // unable the title bar
        guessed.setResizable(true);

        // letter is not correct
        a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText("Not correct");
        a.setContentText("Selected letter is not correct");
        a.setResizable(true);


        //score
        score = new Text();
        score.textProperty().bind(count.asString().concat(" Points")); // concatenate counter with str

        double[] range = {900, 500, 2500, 600, 850, 1250, 550, 800, 650, 700, 500
                , 900, 0, -1, 1500, 700, 950, 650, 500, 0, 750, 1000, 650, -1};

        // Roll button
        Button roll = new Button("Roll");
        roll.setOnAction(actionEvent -> {
            rotate();
            //24 sections
            double result = range[(int) (angle - 360) / 15]; // determine on which value did it stop
            count.set(count.get() + (int) result);           // add the current result to score

            if (result == -1) {                              // case bankrupt
                System.out.println("Bankrupt");
                count.set(0);
            }
            System.out.println("result: " + result);
        });

        // middle pane
        VBox middlePane = new VBox(5);

        double w = 350;
        double h = 350;
        try {
            wheel = new Image(
                    new FileInputStream(
                            "/home/artem/IDEA PROJECTS/Project3_s18899/src/wheel.png"
                    )
            );

            arrow = new Image(
                    new FileInputStream(
                            "/home/artem/IDEA PROJECTS/Project3_s18899/src/arrow.png"
                    )
            );
        } catch (IOException e) {
            System.out.println("File not found");
            System.exit(1);
        }

        // views for images
        arrowView = new ImageView(arrow);
        arrowView.setFitHeight(50);
        arrowView.setFitWidth(50);

        imageView = new ImageView(wheel);
        imageView.setFitHeight(h);
        imageView.setFitWidth(w);

        middlePane.setAlignment(Pos.CENTER);
        middlePane.getChildren().addAll(score, arrowView, imageView);
        //

        // bottom pane
        VBox bottomBox = new VBox(5);
        bottomBox.getChildren().addAll(roll, mainMenu);
        bottomBox.setAlignment(Pos.CENTER);
        //
        // setting the window
        layout2.setCenter(middlePane);
        layout2.setBottom(bottomBox);
        scene2 = new Scene(layout2, 650, 580);

        grid.add(llistview, 0, 1);
        grid.add(rlistview, 2, 1);
        grid.add(start, 0, 2);
        GridPane.setHalignment(start, HPos.CENTER);
        grid.add(add, 2, 2);
        GridPane.setHalignment(add, HPos.CENTER);

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.getChildren().addAll(lrBut, rlBut, drlBut, dlrBut);
        grid.add(vbox, 1, 1);

        root.setCenter(grid);
        GridPane.setVgrow(root, Priority.ALWAYS);
        stage.setScene(scene1);
        stage.show();
    }

    void rotate() {
        rotateTransition = new RotateTransition();
        Random rand = new Random();
        int n = rand.nextInt(360);                  // random value from 0-360
        int spin = 360;                                 // 1 cycle
        rotateTransition.setNode(imageView);
        rotateTransition.setFromAngle(prev);
        rotateTransition.setToAngle(n + spin);
        angle = rotateTransition.getToAngle();
        prev = rotateTransition.getToAngle() - 360;     // previous angle
        System.out.println(rotateTransition.getByAngle() + " " + rotateTransition.getToAngle());
        rotateTransition.setDuration(
                Duration.seconds(3)
        );
        rotateTransition.setCycleCount(1);
        while (rotateTransition.getStatus() != Animation.Status.RUNNING) {
            callGuesser();
            rotateTransition.play();
        }
    }

    void callGuesser() {
        Optional<String> letter = guessed.showAndWait();

        if (letter.isPresent()) {
            String s = guessed.getResult().toUpperCase();
            if (s.length() == 1) {
                boolean isGuessed = false;          // flag to get if a letter is guessed
                char[] res = s.toCharArray();
                countdd = 0;                        // counter of the same letter in the word
                for (int z = 0; z < rep.size(); z++) {
                    for (int l = 0; l < doublelet.size(); l++) {
                        if (res[0] == rep.get(z) && res[0] != doublelet.get(l)) {
                            notGuessed();
                            return;
                        }
                    }
                }

                //wingame message
                Alert win = new Alert(Alert.AlertType.INFORMATION);
                win.setResizable(true);
                win.setHeaderText("Congratulations !!! ");
                try {
                    win.setContentText("You've won this game, your score: " + count.get());
                } catch (Exception e) {
                }
                win.setTitle("WIN");
                //

                for (int i = 0; i < c.length; i++) {
                    // guessed
                    if (res[0] == c[i]) {
                        count.set(count.get() + 200);
                        System.out.println("GUESSED");
                        countdd++;
                        isGuessed = true;
                        rt = new RotateTransition(Duration.seconds(1), rctngls.get(i)); // rotate a rectangle
                        rctngls.get(i).setFill(Color.GREEN);
                        gwrd.get(i).setFill(Color.WHITE);
                        rt.setAxis(Rotate.Y_AXIS);
                        rt.setToAngle(180);

                        int finalI = i;
                        try {
                            rt.setOnFinished(event -> gwrd.get(finalI).setVisible(true)); // set our word visible
                        } catch (IndexOutOfBoundsException e) {
                        }
                        rt.play();


                        countG++;   // counting the correct answers
                        if (countG == c.length && rotateTransition.getStatus() == Animation.Status.STOPPED) {

                            win.showAndWait();
                            endgame.fire();
                        }
                    }
                }

                rep.add(res[0]); // adding to the list our input

                for (int k = 0; k < c.length; k++) {
                    for (int j = 1; j < c.length; j++) {
                        if (c[k] == c[j]) {
                            doublelet.add(c[k]);
                            break;
                        }
                    }
                }

                if (countdd > 1) {
                    System.out.println("Multiple letter opened");
                    doublelet.add(res[0]);
                }
                if (!isGuessed) {
                    notGuessed();
                }
            } else notGuessed();
        }
    }

    void notGuessed() {
        a.showAndWait();
        count.set(count.get() - 1000);
        countM++;
        if (countM == 3) {
            for (int i = 0; i < c.length; i++) {                                // show the answer word
                rt = new RotateTransition(Duration.seconds(1), rctngls.get(i));
                rt.setAxis(Rotate.Y_AXIS);
                rt.setToAngle(180);

                int finalI = i;
                rt.setOnFinished(event -> gwrd.get(finalI).setVisible(true));
                rt.play();
            }

            //endgame message
            Alert lose = new Alert(Alert.AlertType.ERROR);
            lose.setResizable(true);
            lose.setHeaderText("End game");
            lose.setContentText("You've lost this game, your score: " + count.get());
            lose.showAndWait();
            endgame.fire(); // click exit button
        }
    }
}


