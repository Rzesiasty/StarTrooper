package model;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class GameViewManager {

    private AnchorPane gamePane;
    private Scene gameScene;
    private Stage gameStage;

    private final static int GAME_WIDTH = 600;
    private final static int GAME_HEIGHT = 800;

    private Stage menuStage;
    private ImageView ship;

    private boolean isLeftKeyPressed;
    private boolean isRightKeyPressed;
    private boolean isUpKeyPressed;
    private boolean isDownKeyPressed;

    private int angle;
    private AnimationTimer gameTimer;

    private GridPane gridPane1;
    private GridPane gridPane2;
    private final static String BACKGROUND_IMAGE = "view/resources/darkPurple.png";

    private final static String BROWN_METEOR = "view/resources/meteorBrown_med3.png";
    private final static String GREY_METEOR = "view/resources/meteorGrey_med1.png";

    private List<ImageView> brownMeteors;
    private List<ImageView> greyMeteors;
    private Random randomPositionGenerator;

    private ImageView star;
    private SmallInfoLabel pointsLabel;
    private List<ImageView> playerLifes;
    private int playerLife;
    private int points;
    private List<Integer> pointsList;
    private int rankingPoints;

    private final static int SHIP_RADIUS = 30;
    private final static int STAR_RADIUS = 12;
    private final static int ENEMY_RADIUS = 27;
    private final static int METEOR_RADIUS = 20;
    private final static int LASER_RADIUS = 12;

    private final static String GOLD_STAR_IMAGE = "view/resources/star_gold.png";

    private final static String LASER_PLAYER_SHOOT_IMAGE = "view/resources/shipchooser/fire01.png";
    private ImageView laserImage;
    private List<Node> laser = new ArrayList<>();

    private Duration firingInterval = Duration.millis(1000);
    private Timeline firing = new Timeline(
            new KeyFrame(Duration.ZERO, event -> fire()),
            new KeyFrame(firingInterval));

    private final static String BLACK_ENEMIES_IMAGE = "view/resources/enemies/enemyBlack1.png";
    private final static String BLUE_ENEMIES_IMAGE = "view/resources/enemies/enemyBlue2.png";
    private final static String GREEN_ENEMIES_IMAGE = "view/resources/enemies/enemyGreen3.png";
    private final static String RED_ENEMIES_IMAGE = "view/resources/enemies/enemyRed4.png";
    private List<ImageView> blackEnemies;
    private List<ImageView> blueEnemies;
    private List<ImageView> greenEnemies;
    private List<ImageView> redEnemies;

    private File savedRankingList = new File("ranking.list");
    private List<Integer> rankingList;

    public GameViewManager() {
        initializeStage();
        createKeyListeners();
        randomPositionGenerator = new Random();
    }

    private void initializeStage() {
        gamePane = new AnchorPane();
        gameScene = new Scene(gamePane, GAME_WIDTH, GAME_HEIGHT);
        gameStage = new Stage();
        gameStage.setScene(gameScene);
    }

    private void fire() {
        laserImage = new ImageView(LASER_PLAYER_SHOOT_IMAGE);
        Node newLaser = laserImage;
        newLaser.relocate(ship.getLayoutX() + 48, ship.getLayoutY() - 20);
        laser.add(newLaser);
        gamePane.getChildren().add(newLaser);
    }


    private void createKeyListeners() {
        gameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                isLeftKeyPressed = true;
            } else if (event.getCode() == KeyCode.RIGHT) {
                isRightKeyPressed = true;
            } else if (event.getCode() == KeyCode.UP) {
                isUpKeyPressed = true;
            } else if (event.getCode() == KeyCode.DOWN) {
                isDownKeyPressed = true;
            } else if (event.getCode() == KeyCode.SPACE && firing.getStatus() != Animation.Status.RUNNING) {
                firing.playFromStart();
//                firing.setDelay(firingInterval);
//                firing.setCycleCount(Animation.INDEFINITE);

            }
        });

        gameScene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                isLeftKeyPressed = false;
            } else if (event.getCode() == KeyCode.RIGHT) {
                isRightKeyPressed = false;
            } else if (event.getCode() == KeyCode.UP) {
                isUpKeyPressed = false;
            } else if (event.getCode() == KeyCode.DOWN) {
                isDownKeyPressed = false;
            } else if (event.getCode() == KeyCode.SPACE) {
                firing.stop();
            }
        });
    }

    public void createNewGame(Stage menuStage, SHIP shipChoosen) {
        this.menuStage = menuStage;
        this.menuStage.hide();
        createBackground();
        createShip(shipChoosen);
        createGameElements(shipChoosen);
        createGameLoop();
        gameStage.show();
    }

    private void createGameElements(SHIP shipChossen) {
        playerLife = 2;
        star = new ImageView(GOLD_STAR_IMAGE);
        setElementsOnPosition(star);
        gamePane.getChildren().add(star);
        pointsLabel = new SmallInfoLabel("Points : 00");
        pointsLabel.setLayoutX(420);
        pointsLabel.setLayoutY(60);
        gamePane.getChildren().add(pointsLabel);
        playerLifes = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            playerLifes.add(new ImageView(shipChossen.getUrlLife()));
            playerLifes.get(i).setLayoutX(380 + (playerLifes.size() * 50));
            playerLifes.get(i).setLayoutY(120);
            gamePane.getChildren().add(playerLifes.get(i));
        }

        brownMeteors = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            brownMeteors.add(new ImageView(BROWN_METEOR));
            setElementsOnPosition(brownMeteors.get(i));
            gamePane.getChildren().add(brownMeteors.get(i));
        }

        greyMeteors = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            greyMeteors.add(new ImageView(GREY_METEOR));
            setElementsOnPosition(greyMeteors.get(i));
            gamePane.getChildren().add(greyMeteors.get(i));
        }

        blackEnemies = new ArrayList<>();
        blackEnemies.add(new ImageView(BLACK_ENEMIES_IMAGE));
        for (ImageView black : blackEnemies) {
            setElementsOnPosition(black);
            gamePane.getChildren().add(black);
        }
        blueEnemies = new ArrayList<>();
        blueEnemies.add(new ImageView(BLUE_ENEMIES_IMAGE));
        for (ImageView blue : blueEnemies) {
            setElementsOnPosition(blue);
            gamePane.getChildren().add(blue);
        }
        greenEnemies = new ArrayList<>();
        greenEnemies.add(new ImageView(GREEN_ENEMIES_IMAGE));
        for (ImageView green : greenEnemies) {
            gamePane.getChildren().add(green);
            initializeTransitionGreenEnemies();
        }

        redEnemies = new ArrayList<>();
        redEnemies.add(new ImageView(RED_ENEMIES_IMAGE));
        for (ImageView red : redEnemies) {
            gamePane.getChildren().add(red);
            initializeTransitionRedEnemies();
        }
    }


    private void moveGameElements() {
        star.setLayoutY(star.getLayoutY() + 5);

        for (int i = 0; i < laser.size(); i++) {
            if (laser.get(i).getLayoutY() < GAME_HEIGHT) {
                laser.get(i).setLayoutY(laser.get(i).getLayoutY() - 7);
            } else {
                laser.remove(i);
            }
        }

        for (int i = 0; i < brownMeteors.size(); i++) {
            brownMeteors.get(i).setLayoutY(brownMeteors.get(i).getLayoutY() + 7);
            brownMeteors.get(i).setRotate(brownMeteors.get(i).getRotate() + 4);
        }

        for (int i = 0; i < greyMeteors.size(); i++) {
            greyMeteors.get(i).setLayoutY(greyMeteors.get(i).getLayoutY() + 7);
            greyMeteors.get(i).setRotate(greyMeteors.get(i).getRotate() + 4);
        }

        for (ImageView black : blackEnemies) {
            black.setLayoutY(black.getLayoutY() + 4);
            black.setLayoutX(ship.getLayoutX());
        }

        for (ImageView blue : blueEnemies) {
            blue.setLayoutY(blue.getLayoutY() + 15);
            if (blue.getLayoutY() > 500) {
                if (blue.getLayoutX() > ship.getLayoutX()) {
                    blue.setLayoutX(blue.getLayoutX() - 5);
                } else {
                    blue.setLayoutX(blue.getLayoutX() + 5);
                }
            }
        }
        for (ImageView green : greenEnemies) {
            green.setLayoutY(green.getLayoutY() + 1);
        }
        for (int i = 0; i < redEnemies.size(); i++) {
            redEnemies.get(i).setLayoutY(redEnemies.get(i).getLayoutY() + 2);

        }
    }

    private void checkIfElementsAreBehindTheSceneAndRelocate() {

        if (star.getLayoutY() > 1200) {
            setElementsOnPosition(star);
        }

        for (int i = 0; i < brownMeteors.size(); i++) {
            if (brownMeteors.get(i).getLayoutY() > 900) {
                setElementsOnPosition(brownMeteors.get(i));
            }
        }

        for (int i = 0; i < greyMeteors.size(); i++) {
            if (greyMeteors.get(i).getLayoutY() > 900) {
                setElementsOnPosition(greyMeteors.get(i));
            }
        }
        for (ImageView black : blackEnemies) {
            if (black.getLayoutY() > 900) {
                setElementsOnPosition(black);
            }
        }
        for (ImageView blue : blueEnemies) {
            if (blue.getLayoutY() > 900) {
                setElementsOnPosition(blue);
            }
        }
        for (ImageView green : greenEnemies) {
            if (green.getLayoutY() > 900) {
                green.relocate(Math.random(), -300);
            }
        }
        for (ImageView red : redEnemies) {
            if (red.getLayoutY() > 900) {
                red.relocate(0, -300);
            }
        }
    }

    private void setElementsOnPosition(ImageView image) {
        image.setLayoutX(randomPositionGenerator.nextInt(370));
        image.setLayoutY(-(randomPositionGenerator.nextInt(3200) + 600));
    }

    private void createShip(SHIP shipChoosen) {
        ship = new ImageView(shipChoosen.getUrlShip());
        ship.setLayoutX(GAME_WIDTH / 2 - 50);
        ship.setLayoutY(GAME_HEIGHT - 90);
        gamePane.getChildren().add(ship);

    }

    private void createGameLoop() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                moveBackground();
                checkIfElementsAreBehindTheSceneAndRelocate();
                moveGameElements();
                checkIfElementsCollide();
                movesShip();
            }
        };
        gameTimer.start();
    }

    private void movesShip() {

        if (isLeftKeyPressed && !isRightKeyPressed) {
            if (angle > -30) {
                angle -= 5;
            }
            ship.setRotate(angle);
            if (ship.getLayoutX() > -10) {
                ship.setLayoutX(ship.getLayoutX() - 5);
            }
        }

        if (isRightKeyPressed && !isLeftKeyPressed) {
            if (angle < 30) {
                angle += 5;
            }
            ship.setRotate(angle);
            if (ship.getLayoutX() < 495) {
                ship.setLayoutX(ship.getLayoutX() + 5);
            }
        }
        if (!isLeftKeyPressed && !isRightKeyPressed) {
            if (angle < 0) {
                angle += 5;
            } else if (angle > 0) {
                angle -= 5;
            }
            ship.setRotate(angle);
        }
        if (isLeftKeyPressed && isRightKeyPressed) {
            if (angle < 0) {
                angle += 5;
            } else if (angle > 0) {
                angle -= 5;
            }
            ship.setRotate(angle);
        }
        if (isUpKeyPressed && !isDownKeyPressed) {
            if (ship.getLayoutY() > 10) {
                ship.setLayoutY(ship.getLayoutY() - 5);
            }
        }
        if (isDownKeyPressed && !isUpKeyPressed) {
            if (ship.getLayoutY() < 700) {
                ship.setLayoutY(ship.getLayoutY() + 5);
            }
        }

    }

    private void createBackground() {
        gridPane1 = new GridPane();
        gridPane2 = new GridPane();

        for (int i = 0; i < 12; i++) {
            ImageView backgroundImage1 = new ImageView(BACKGROUND_IMAGE);
            ImageView backgroundImage2 = new ImageView(BACKGROUND_IMAGE);

            GridPane.setConstraints(backgroundImage1, i % 3, i / 3);
            GridPane.setConstraints(backgroundImage2, i % 3, i / 3);
            gridPane1.getChildren().add(backgroundImage1);
            gridPane2.getChildren().add(backgroundImage2);
        }
        gridPane2.setLayoutY(-1024);
        gamePane.getChildren().add(gridPane1);
        gamePane.getChildren().add(gridPane2);
    }


    private void moveBackground() {
        gridPane1.setLayoutY(gridPane1.getLayoutY() + 0.5);
        gridPane2.setLayoutY(gridPane2.getLayoutY() + 0.5);

        if (gridPane1.getLayoutY() >= 1024) {
            gridPane1.setLayoutY(-1024);
        }
        if (gridPane2.getLayoutY() >= 1024) {
            gridPane2.setLayoutY(-1024);
        }
    }

    private void addPoints(int point) {
        points += point;
        pointsList = new ArrayList<>();
        pointsList.add(points);
        for (int i : pointsList) {
            int sum = IntStream.of(i).sum();
            String textToSet = "POINTS : ";
            if (sum < 10) {
                textToSet = textToSet + "0";
            }
            pointsLabel.setText(textToSet + sum);
        }
    }

    private void checkIfElementsCollide() {

        if (SHIP_RADIUS + STAR_RADIUS > calculateDistance(ship.getLayoutX() + 49, star.getLayoutX() + 15,
                ship.getLayoutY() + 37, star.getLayoutY() + 15)) {
            setElementsOnPosition(star);
            addPoints(3);
        }

        for (int i = 0; i < brownMeteors.size(); i++) {
            if (SHIP_RADIUS + METEOR_RADIUS > calculateDistance(ship.getLayoutX() + 49, brownMeteors.get(i).getLayoutX() + 20,
                    ship.getLayoutY() + 37, brownMeteors.get(i).getLayoutY() + 20)) {
                removeLife();
                setElementsOnPosition(brownMeteors.get(i));

            }
        }

        for (int i = 0; i < greyMeteors.size(); i++) {
            if (SHIP_RADIUS + METEOR_RADIUS > calculateDistance(ship.getLayoutX() + 49, greyMeteors.get(i).getLayoutX() + 20,
                    ship.getLayoutY() + 37, greyMeteors.get(i).getLayoutY() + 20)) {
                setElementsOnPosition(greyMeteors.get(i));
                removeLife();
            }
        }

        for (int i = 0; i < brownMeteors.size(); i++) {
            for (int k = 0; k < laser.size(); k++) {
                if (METEOR_RADIUS + LASER_RADIUS > calculateDistance(brownMeteors.get(i).getLayoutX() + 20, laser.get(k).getLayoutX() + 12,
                        brownMeteors.get(i).getLayoutY() + 20, laser.get(k).getLayoutY() + 12)) {
                    addPoints(1);
                    setElementsOnPosition(brownMeteors.get(i));
                    gamePane.getChildren().remove(laser.get(k));
                    laser.remove(k);
                }
            }
        }

        for (int i = 0; i < greyMeteors.size(); i++) {
            for (int k = 0; k < laser.size(); k++) {
                if (METEOR_RADIUS + LASER_RADIUS > calculateDistance(greyMeteors.get(i).getLayoutX() + 20, laser.get(k).getLayoutX() + 12,
                        greyMeteors.get(i).getLayoutY() + 20, laser.get(k).getLayoutY() + 12)) {
                    addPoints(1);
                    setElementsOnPosition(greyMeteors.get(i));
                    gamePane.getChildren().remove(laser.get(k));
                    laser.remove(k);

                }
            }
        }

        for (int i = 0; i < blackEnemies.size(); i++) {
            for (int k = 0; k < laser.size(); k++) {
                if (ENEMY_RADIUS + LASER_RADIUS > calculateDistance(blackEnemies.get(i).getLayoutX() + 49, laser.get(k).getLayoutX() + 12,
                        blackEnemies.get(i).getLayoutY() + 37, laser.get(k).getLayoutY() + 12)) {
                    addPoints(2);
                    setElementsOnPosition(blackEnemies.get(i));
                    gamePane.getChildren().remove(laser.get(k));
                    laser.remove(k);

                }
            }
        }
        for (int i = 0; i < blackEnemies.size(); i++) {
            if (ENEMY_RADIUS + SHIP_RADIUS > calculateDistance(blackEnemies.get(i).getLayoutX() + 49, ship.getLayoutX() + 49,
                    blackEnemies.get(i).getLayoutY() + 37, ship.getLayoutY() + 37)) {
                setElementsOnPosition(blackEnemies.get(i));
                removeLife();
            }
        }


        for (int i = 0; i < blueEnemies.size(); i++) {
            for (int k = 0; k < laser.size(); k++) {
                if (ENEMY_RADIUS + LASER_RADIUS > calculateDistance(blueEnemies.get(i).getLayoutX() + 49, laser.get(k).getLayoutX() + 12,
                        blueEnemies.get(i).getLayoutY() + 37, laser.get(k).getLayoutY() + 12)) {
                    addPoints(2);
                    setElementsOnPosition(blueEnemies.get(i));
                    gamePane.getChildren().remove(laser.get(k));
                    laser.remove(k);

                }
            }
        }
        for (int i = 0; i < blueEnemies.size(); i++) {
            if (ENEMY_RADIUS + SHIP_RADIUS > calculateDistance(blueEnemies.get(i).getLayoutX() + 52, ship.getLayoutX() + 49,
                    blueEnemies.get(i).getLayoutY() + 40, ship.getLayoutY() + 37)) {
                setElementsOnPosition(blueEnemies.get(i));
                removeLife();
            }
        }


        for (int i = 0; i < laser.size(); i++) {
            for (int k = 0; k < greenEnemies.size(); k++) {
                if (laser.get(i).getBoundsInParent().intersects(greenEnemies.get(k).getBoundsInParent())) {
                    addPoints(2);
                    greenEnemies.get(k).relocate(Math.random(), -300);
                    gamePane.getChildren().remove(laser.get(i));
                    laser.remove(i);
                }
            }
        }
        for (int i = 0; i < greenEnemies.size(); i++) {
            if (greenEnemies.get(i).getBoundsInParent().intersects(ship.getBoundsInParent())) {
                greenEnemies.get(i).relocate(Math.random(), -300);
                removeLife();
            }
        }

        for (int i = 0; i < laser.size(); i++) {
            for (int k = 0; k < redEnemies.size(); k++) {
                if (laser.get(i).getBoundsInParent().intersects(redEnemies.get(k).getBoundsInParent())) {
                    addPoints(2);
                    redEnemies.get(k).relocate(0, -300);
                    gamePane.getChildren().remove(laser.get(i));
                    laser.remove(i);
                }
            }
        }
        for (int i = 0; i < redEnemies.size(); i++) {
            if (ship.getBoundsInParent().intersects(redEnemies.get(i).getBoundsInParent())) {
                redEnemies.get(i).relocate(0, -100);
                removeLife();
            }
        }
    }

    private double calculateDistance(double x1, double x2, double y1, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private void removeLife() {

        gamePane.getChildren().remove(playerLifes.get(playerLife));
        playerLife--;
        if (playerLife < 0) {
            saveList();
            gameStage.close();
            gameTimer.stop();
            menuStage.show();
        }
    }

    public void saveList() {
        try {
            FileOutputStream fos = new FileOutputStream(savedRankingList, true);
            DataOutputStream oos = new DataOutputStream(fos);
            oos.writeInt(points);
            oos.close();
        } catch (Exception e) {
            System.out.println("Exception" + e);
        }
    }

    public List<Integer> loadList() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savedRankingList));
            Object readList = ois.readObject();
            if (readList instanceof ArrayList) {
                rankingList.addAll((ArrayList) readList);
            }
            ois.close();
        } catch (Exception e) {
            System.out.println("Exception" + e);
        }
        System.out.println(rankingList);
        return rankingList;
    }


    public void initializeTransitionGreenEnemies() {
        for (ImageView green : greenEnemies) {
            Path path = new Path();
            path.getElements().add(new MoveTo(250, -100));
            ArcTo arcTo = new ArcTo();
            arcTo.setX(150);
            arcTo.setY(150);
            arcTo.setRadiusX(50);
            arcTo.setRadiusY(50);
            path.getElements().add(arcTo);
            path.getElements().add(new ClosePath());

            PathTransition transition = new PathTransition();
            transition.setNode(green);
            transition.setDuration(Duration.seconds(2));
            transition.setPath(path);
            transition.setCycleCount(PathTransition.INDEFINITE);
            transition.play();
        }
    }

    public void initializeTransitionRedEnemies() {
        for (ImageView red : redEnemies) {
            Path path = new Path();

            MoveTo moveTo = new MoveTo();
            moveTo.setX(30);
            moveTo.setY(50);

            LineTo line = new LineTo();
            line.setX(570);
            line.setY(50);

            LineTo lineBack = new LineTo(30, 50);

            path.getElements().add(moveTo);
            path.getElements().add(line);
            path.getElements().add(lineBack);

            PathTransition transition = new PathTransition();
            transition.setNode(red);
            transition.setDuration(Duration.seconds(4));
            transition.setPath(path);
            transition.setCycleCount(PathTransition.INDEFINITE);
            transition.play();
        }
    }
}

