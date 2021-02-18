package platformpt3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.shape.Shape;

public class PlatformPt3 extends Application {

    public static Pane levelRoot = new Pane();
    public static Pane mainRoot = new Pane();
    public static Pane playerRoot = new Pane();
    public static Pane GUI = new Pane();
    private Scene scene = new Scene(mainRoot, 1240, 680);

    private Player player = new Player(0, 0, 20, 20, Color.RED);
    public static ArrayList<Platform> platforms = new ArrayList<>();
    private ArrayList<Door> doors = new ArrayList<>();
    private ArrayList<Weapon> weapons = new ArrayList<>();
    private HashMap<KeyCode, Boolean> keys = new HashMap<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Bullet> bossBullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Checkpoint> checkpoints = new ArrayList<>();
    private String[] currentLevel;
    private HashMap<Door, Boolean> doorLocks = new HashMap<>();

    private double velX, xVelTerminal = 1;
    private double velY, velYI, jumpTime, accY = 0.25;

    private boolean canJump = true;
    private boolean xScroll;
    private boolean yScroll;

    private double resetX = 0, resetY = 0, startX = 0, startY = 0;
    private double mouseX, mouseY;
    private double playerAngle;
    private Point2D point1, point2, point3;
    private long count = 0;

    private Timeline mainTimeline = new Timeline();

    private Boss boss;
    private Platform trapOne, trapTwo, trapThree;
    private boolean flagTrap = true;
    private double trapVelX = 1;
    private boolean bossCreation = true;

    private Rectangle back, start, exit, title;
    private Stage stage1 = new Stage();

    @Override
    public void start(Stage stage) throws Exception {

        stage1.setScene(scene);
        scene.setFill(Color.BLACK);
        stage1.show();

        KeyFrame mainGameLoop = new KeyFrame(
                Duration.seconds(0.005), (event) -> {
            xMove();
            yMove();
            translateRoot();
            calculatePlayerAimAngle();
            enterDoor();
            traverseBullets();
            traversePlatforms();
            weaponControl();
            if (currentLevel == LevelData.LEVELBOSS) {
                if (bossCreation) {
                    createBoss();
                }
                bossControl();
                enemyControl();
            }
            enemyControl();
            checkpointControl();
            playerImageControl();
        });

        mainTimeline.getKeyFrames().add(mainGameLoop);

    }

    @Override
    public void init() {

        scene.setRoot(GUI);

        back = new Rectangle(0, 0, 1240, 680);
        Image background = new Image(getClass().getResourceAsStream("giphy.gif"));
        back.setFill(new ImagePattern(background));

        start = new Rectangle(550, 350, 150, 75);
        exit = new Rectangle(550, 450, 150, 75);
        title = new Rectangle(220, 100, 800, 100);

        Image titleButton = new Image(getClass().getResourceAsStream("Bagel-Bandits.png"));
        title.setFill(new ImagePattern(titleButton));
        Image startButton = new Image(getClass().getResourceAsStream("PlayButton.jpg"));
        start.setFill(new ImagePattern(startButton));
        Image exitButton = new Image(getClass().getResourceAsStream("ExitButton.jpg"));
        exit.setFill(new ImagePattern(exitButton));

        start.setOnMouseClicked(e -> {
            scene.setRoot(mainRoot);
            play();
        });

        exit.setOnMouseClicked(e -> stage1.close());

        GUI.getChildren().addAll(back, start, exit, title);

    }

    private void play() {
        mainRoot.getChildren().addAll(levelRoot, playerRoot);
        playerRoot.getChildren().add(player);
        scene.setCursor(Cursor.CROSSHAIR);
        inputControl();
        loadLevel(LevelData.START);
        for (Door door : doors) {
            if (door.getLevel() == LevelData.LEVEL2 || door.getLevel() == LevelData.LEVEL3 || door.getLevel() == LevelData.LEVELBOSS) {
               //doorLocks.put(door, false);
            }
        }

        mainTimeline.setCycleCount(Timeline.INDEFINITE);
        mainTimeline.play();
    }

    private void createBoss() {
        boss = new Boss(500, 260, 100, 100, 100, 1, 1, Color.PURPLE);
        levelRoot.getChildren().add(boss);
        bossCreation = false;
    }

    private void bossControl() {
        count++;

        boss.setX(boss.getX() - boss.getVelX());
        if (count % 400 == 0) {
            bullets.add(new Bullet(Bullet.BulletType.enemy, boss.getX(), boss.getY() + boss.getHeight(), 14, Color.LIME, 90, 1, 1));
            levelRoot.getChildren().add(bullets.get(bullets.size() - 1));
        }

        if (boss.getX() <= 100 || boss.getX() >= 850) {
            boss.setVelX(boss.getVelX() * -1);
        }
        if (boss.getHealth() >= 75) {
            if (count % 800 == 0) {
                bossPhaseOne();
            }
        }
        if (boss.getHealth() < 75 && boss.getHealth() > 50) {
            if (count % 800 == 0) {
                bossPhaseOne();
            }
            bossPhaseTwo();
        }
        if (boss.getHealth() < 50) {
            traverseBossBullets();
            if (count % 1000 == 0) {
                bossPhaseThreeCreate();
            } else if (count % 500 == 0) {
                bossPhaseThreeCreateTwo();
            }
        }

    }

    private void bossPhaseOne() {
        Random rand = new Random();
        int place = rand.nextInt(6) + 1;
        switch (place) {
            case 1:
                enemies.add(new Enemy(Enemy.EnemyType.right, 40, 560, 20, 20, 1, 5, Color.PURPLE));
                levelRoot.getChildren().add(enemies.get(enemies.size() - 1));
                break;

            case 2:
                enemies.add(new Enemy(Enemy.EnemyType.right, 40, 660, 20, 20, 1, 5, Color.PURPLE));
                levelRoot.getChildren().add(enemies.get(enemies.size() - 1));
                break;

            case 3:
                enemies.add(new Enemy(Enemy.EnemyType.spider, rand.nextInt((440 - 120) + 1) + 120, 300, 20, 20, 1, 2, Color.TURQUOISE));
                levelRoot.getChildren().add(enemies.get(enemies.size() - 1));
                break;

            case 4:
                enemies.add(new Enemy(Enemy.EnemyType.spider, rand.nextInt((940 - 560) + 1) + 560, 300, 20, 20, 1, 2, Color.TURQUOISE));
                levelRoot.getChildren().add(enemies.get(enemies.size() - 1));
                break;

            case 5:
                enemies.add(new Enemy(Enemy.EnemyType.left, 980, 560, 20, 20, 1, 5, Color.PURPLE));
                levelRoot.getChildren().add(enemies.get(enemies.size() - 1));
                break;

            case 6:
                enemies.add(new Enemy(Enemy.EnemyType.left, 980, 660, 20, 20, 1, 5, Color.PURPLE));
                levelRoot.getChildren().add(enemies.get(enemies.size() - 1));
                break;
        }
    }

    private void bossPhaseTwo() {
        boolean[] sides = new boolean[4];
        if (flagTrap) {
            trapOne = new Platform(Platform.PlatformType.death, 80, 660, 20, 20, Color.PINK, sides, 100000);
            trapTwo = new Platform(Platform.PlatformType.death, 80, 640, 20, 20, Color.PINK, sides, 100000);
            trapThree = new Platform(Platform.PlatformType.death, 80, 620, 20, 20, Color.PINK, sides, 100000);
            platforms.add(trapOne);
            platforms.add(trapTwo);
            platforms.add(trapThree);
            levelRoot.getChildren().addAll(trapOne, trapTwo, trapThree);
            flagTrap = false;
        } else {
            if (trapOne.getX() >= 960 || trapOne.getX() <= 60) {
                trapVelX = trapVelX * -1;
            }

            trapOne.setX(trapOne.getX() + trapVelX);
            trapTwo.setX(trapTwo.getX() + trapVelX);
            trapThree.setX(trapThree.getX() + trapVelX);
        }
    }

    private void bossPhaseThreeCreate() {
        bossBullets.add(new Bullet(Bullet.BulletType.enemy, 100, 60, 16, Color.LIME, 90, 1, 1));
        levelRoot.getChildren().addAll(bossBullets.get(bossBullets.size() - 1));
        bossBullets.add(new Bullet(Bullet.BulletType.enemy, 300, 60, 16, Color.LIME, 90, 1, 1));
        levelRoot.getChildren().addAll(bossBullets.get(bossBullets.size() - 1));
        bossBullets.add(new Bullet(Bullet.BulletType.enemy, 500, 60, 16, Color.LIME, 90, 1, 1));
        levelRoot.getChildren().addAll(bossBullets.get(bossBullets.size() - 1));
        bossBullets.add(new Bullet(Bullet.BulletType.enemy, 700, 60, 16, Color.LIME, 90, 1, 1));
        levelRoot.getChildren().addAll(bossBullets.get(bossBullets.size() - 1));
        bossBullets.add(new Bullet(Bullet.BulletType.enemy, 900, 60, 16, Color.LIME, 90, 1, 1));
        levelRoot.getChildren().addAll(bossBullets.get(bossBullets.size() - 1));
        levelRoot.getChildren().addAll(bossBullets.get(bossBullets.size() - 1));
    }

    private void bossPhaseThreeCreateTwo() {
        bossBullets.add(new Bullet(Bullet.BulletType.enemy, 200, 60, 16, Color.LIME, 90, 1, 1));
        levelRoot.getChildren().addAll(bossBullets.get(bossBullets.size() - 1));
        bossBullets.add(new Bullet(Bullet.BulletType.enemy, 400, 60, 16, Color.LIME, 90, 1, 1));
        levelRoot.getChildren().addAll(bossBullets.get(bossBullets.size() - 1));
        bossBullets.add(new Bullet(Bullet.BulletType.enemy, 600, 60, 16, Color.LIME, 90, 1, 1));
        levelRoot.getChildren().addAll(bossBullets.get(bossBullets.size() - 1));
        bossBullets.add(new Bullet(Bullet.BulletType.enemy, 800, 60, 16, Color.LIME, 90, 1, 1));
        levelRoot.getChildren().addAll(bossBullets.get(bossBullets.size() - 1));
        bossBullets.add(new Bullet(Bullet.BulletType.enemy, 1000, 60, 16, Color.LIME, 90, 1, 1));
        levelRoot.getChildren().addAll(bossBullets.get(bossBullets.size() - 1));
        levelRoot.getChildren().addAll(bossBullets.get(bossBullets.size() - 1));
    }

    private void traverseBossBullets() {
        for (Bullet bossBullet : bossBullets) {
            bossBullet.moveBullet();
            for (Platform platform : platforms) {
                if (bossBullet.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    killBullet(bossBullet);
                    break;
                }
            }
            if (bossBullet.getBoundsInParent().intersects(player.getBoundsInParent()) && bossBullet.getType() == Bullet.BulletType.enemy) {
                killPlayer();
            }
        }
    }

    private void inputControl() {
        scene.setOnKeyPressed((e) -> keys.put(e.getCode(), true));
        scene.setOnKeyReleased((e) -> keys.put(e.getCode(), false));

        scene.setOnMouseMoved((event) -> {
            mouseX = event.getSceneX() - mainRoot.getTranslateX();
            mouseY = event.getSceneY() - mainRoot.getTranslateY();
        });

        scene.setOnMousePressed((event) -> createBullet(playerAngle, Bullet.BulletType.player));
    }

    private boolean isPressed(KeyCode key) {
        return keys.getOrDefault(key, false);
    }

    private boolean isOpen(Door door) {
        return doorLocks.getOrDefault(door, true);
    }

    private void xMove() {
        if (isPressed(KeyCode.A)) {
            if (velX > -xVelTerminal) {
                velX -= 0.01;
            }
        } else {
            if (velX < 0) {
                velX += 0.02;
            }
        }

        if (isPressed(KeyCode.D)) {
            if (velX < xVelTerminal) {
                velX += 0.01;
            }
        } else {
            if (velX > 0) {
                velX -= 0.02;
            }
        }

        if (!(isPressed(KeyCode.A) || isPressed(KeyCode.D)) && Math.abs(velX) < 0.02) {
            velX = 0;
        }

        if (intersectsLeft(player) && velX > 0) {
            velX = 0;
            if (!intersectsTop(player)) {
                player.moveX(player.getX() - player.getX() % 20);
            }
        }

        if (intersectsRight(player) && velX < -0.01) {
            velX = 0;

            if (!intersectsTop(player)) {
                player.moveX((player.getX() - player.getX() % 20) + 20);
            }

        }

        player.moveX(player.getX() + velX);
    }

    private void yMove() {
        if ((isPressed(KeyCode.SPACE)) || isPressed(KeyCode.W)) {
            if (canJump) {
                velYI = -1.3;
                accY = 0.25;
                jumpTime = 0.04;
                player.moveY(player.getY() - 1);
            }
        } else if (velY < -0.7) {
            accY = 0.4;
        }

        double temp;

        try {
            temp = getIntersectedPlatform(Platform.PlatformType.oneway).getY();
        } catch (NullPointerException e) {
            temp = 1000000;
        }

        if (intersectsTop(player) && velY > 0 && player.getY() + 19 < temp) {
            velY = 0;
            velYI = 0;
            jumpTime = 0;
            player.moveY(player.getY() - player.getY() % 20);
            canJump = true;
        } else {
            player.moveY(player.getY() + velY);
            jumpTime += 0.04;
            if (velY < 1.5) {
                velY = velYI + accY * jumpTime;
            }
            canJump = false;
        }

        if (intersectsBottom(player) && velY < 0) {
            jumpTime = jumpTime + (-2 * velY) / accY;
            player.moveY((player.getY() - player.getY() % 20) + 21);
        }
        if ((player.getY() > 700 - mainRoot.getTranslateY() || player.getY() > 1600) && currentLevel != LevelData.LEVEL3) {
            killPlayer();
        }

    }

    private void enterDoor() {
        for (int i = 0; i < doors.size(); i++) {
            if (player.getBoundsInParent().intersects(doors.get(i).getBoundsInParent()) && isPressed(KeyCode.ENTER) && isOpen(doors.get(i))) {
                if (doors.get(i).getLevel() == LevelData.START) {
                    if (currentLevel == LevelData.LEVEL1) {
                        for (Door door : doors) {
                            if (door.getLevel() == LevelData.LEVEL2) {
                                doorLocks.put(door, true);
                            }
                        }
                    }
                }
                if (doors.get(i).getLevel() == LevelData.START) {
                    if (currentLevel == LevelData.LEVEL2) {
                        for (Door door : doors) {
                            if (door.getLevel() == LevelData.LEVEL3) {
                                doorLocks.put(door, true);
                            }
                        }
                    }
                }
                if (doors.get(i).getLevel() == LevelData.START) {
                    if (currentLevel == LevelData.LEVEL3) {
                        for (Door door : doors) {
                            if (door.getLevel() == LevelData.LEVELBOSS) {
                                doorLocks.put(door, true);
                            }
                        }
                    }
                }
                startX = 0;
                startY = 0;
                resetX = 0;
                resetY = 0;
                String[] load = doors.get(i).getLevel();
                clearLevel();
                loadLevel(load);
            }
        }
    }

    private Platform bulletIntersectsPlatform(Bullet bullet) {
        for (Platform platform : platforms) {
            if (bullet.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                return platform;
            }
        }
        return null;
    }

    private Weapon onWeapon() {
        for (Weapon weapon : weapons) {
            if (player.getBoundsInParent().intersects(weapon.getBoundsInParent())) {
                return weapon;
            }
        }
        return null;
    }

    private boolean intersectsTop(Shape shape) {
        for (Platform platform : platforms) {
            if (shape.getBoundsInParent().intersects(platform.getTop().getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

    private boolean intersectsBottom(Shape shape) {
        for (Platform platform : platforms) {
            if (shape.getBoundsInParent().intersects(platform.getBottom().getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

    private boolean intersectsLeft(Shape shape) {
        for (Platform platform : platforms) {
            if (shape.getBoundsInParent().intersects(platform.getLeft().getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

    private boolean intersectsRight(Shape shape) {
        for (Platform platform : platforms) {
            if (shape.getBoundsInParent().intersects(platform.getRight().getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

    private Platform getIntersectedPlatform(Platform.PlatformType type) {
        for (Platform platform : platforms) {
            if (player.getBoundsInParent().intersects(platform.getBoundsInParent()) && platform.getType() == type) {
                return platform;
            }
        }
        return null;
    }

    private void changePlatform(Platform platform) {
        for (Platform platform1 : platforms) {
            if (platform1.getY() == platform.getY() - 20 && platform1.getX() == platform.getX()) {
                platform1.setSides(2, true);
            }
            if (platform1.getY() == platform.getY() + 20 && platform1.getX() == platform.getX()) {
                platform1.setSides(0, true);
            }
            if (platform1.getY() == platform.getY() && platform1.getX() == platform.getX() - 20) {
                platform1.setSides(1, true);
            }
            if (platform1.getY() == platform.getY() && platform1.getX() == platform.getX() + 20) {
                platform1.setSides(3, true);
            }
        }
    }

    private void translateRoot() {
        if (xScroll) {
            if (player.getX() > scene.getWidth() * 3 / 5 - mainRoot.getTranslateX()) {
                mainRoot.setTranslateX(scene.getWidth() * 3 / 5 - player.getX());
            }

            if (player.getX() < scene.getWidth() * 2 / 5 - mainRoot.getTranslateX()) {
                mainRoot.setTranslateX(scene.getWidth() * 2 / 5 - player.getX());
            }
        }
        if (yScroll) {
            if (player.getY() > scene.getHeight() * 4 / 5 - mainRoot.getTranslateY()) {
                mainRoot.setTranslateY(scene.getHeight() * 4 / 5 - player.getY());
            }

            if (player.getY() < scene.getHeight() * 2 / 5 - mainRoot.getTranslateY() && mainRoot.getTranslateY() <= 0) {
                mainRoot.setTranslateY(scene.getHeight() * 2 / 5 - player.getY());
            }
        }
    }

    private void createBullet(double angle, Bullet.BulletType type) {
        if (player.getWeapon() != null) {
            if (player.getWeapon().getType() == Weapon.WeaponType.blaster) {
                bullets.add(new Bullet(type, player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, 8, Color.BLUE, angle, 2, 1));
                levelRoot.getChildren().add(bullets.get(bullets.size() - 1));
                bullets.get(bullets.size() - 1).toBack();
            }
        }
    }

    private void killBullet(Bullet bullet) {
        levelRoot.getChildren().remove(bullet);
        bullets.remove(bullet);
    }

    private void traversePlatforms() {
        Platform remove = null;
        for (Platform platform : platforms) {
            if (platform.getHealth() <= 0) {
                levelRoot.getChildren().remove(platform);
                remove = platform;
                changePlatform(platform);
                break;
            }
            if (platform.getType() == Platform.PlatformType.death && player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                killPlayer();
                break;
            }
            if (platform.getType() == Platform.PlatformType.falling && player.getBoundsInParent().intersects(platform.getTop().getBoundsInParent())) {
                platform.getTimelineDisappear().play();
            }
        }
        platforms.remove(remove);
    }

    private void traverseBullets() {
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).moveBullet();
            if (bulletIntersectsEnemy(bullets.get(i)) != null && bullets.get(i).getType() == Bullet.BulletType.player) {
                bulletIntersectsEnemy(bullets.get(i)).setHealth(bulletIntersectsEnemy(bullets.get(i)).getHealth() - bullets.get(i).getDamage());
                killBullet(bullets.get(i));
                break;
            }
            if (bulletIntersectsPlatform(bullets.get(i)) != null) {
                bulletIntersectsPlatform(bullets.get(i)).setHealth(bulletIntersectsPlatform(bullets.get(i)).getHealth() - bullets.get(i).getDamage());
                killBullet(bullets.get(i));
                break;
            }
            if (bullets.get(i).getBoundsInParent().intersects(player.getBoundsInParent()) && bullets.get(i).getType() == Bullet.BulletType.enemy) {
                killPlayer();
                break;
            }
            if (currentLevel == LevelData.LEVELBOSS) {
                if (bullets.get(i).getBoundsInParent().intersects(boss.getBoundsInParent()) && bullets.get(i).getType() == Bullet.BulletType.player) {
                    boss.setHealth(boss.getHealth() - bullets.get(i).getDamage());
                    killBullet(bullets.get(i));
                    break;
                }
            }
        }
    }

    private void calculatePlayerAimAngle() {
        point1 = new Point2D(player.getX() + player.getWidth() / 2, 0);
        point2 = new Point2D(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2);
        point3 = new Point2D(mouseX, mouseY);

        if (point3.getX() > point2.getX()) {
            playerAngle = point2.angle(point1, point3);
        } else {
            playerAngle = 360 - point2.angle(point1, point3);
        }
        playerAngle -= 90;

        try {
            player.getWeapon().setAngle(playerAngle);
        } catch (NullPointerException e) {
        }

    }

    private void weaponControl() {
        if (onWeapon() != null && isPressed(KeyCode.E)) {
            player.pickUpWeapon(onWeapon());
            weapons.remove(onWeapon());
        }
    }

    private void enemyControl() {
        count++;
        for (Enemy enemy : enemies) {

            switch (enemy.getType()) {
                case basic:
                case aiming:
                    if (enemy.getType() == Enemy.EnemyType.basic || enemy.getType() == Enemy.EnemyType.aiming) {
                        if (enemyMovementDetectionRight(enemy) && enemyMovementDetectionLeft(enemy)) {
                            enemy.setX(enemy.getX() - enemy.getVelX());
                            if (intersectsTop(enemy) && (intersectsLeft(enemy) || intersectsRight(enemy))) {
                                enemy.setVelX(enemy.getVelX() * -1);
                                switch (enemy.getType()) {
                                    case basic:
                                        switch (enemy.getImage()) {
                                            case "lizardLeft.gif":
                                                enemy.setImage("lizardRight.gif");
                                                break;
                                            case "lizardRight.gif":
                                                enemy.setImage("lizardLeft.gif");
                                                break;
                                        }
                                        break;
                                    case aiming:
                                        switch (enemy.getImage()) {
                                            case "movingshooterleft.gif":
                                                enemy.setImage("movingshooterright.gif");
                                                break;
                                            case "movingshooterright.gif":
                                                enemy.setImage("movingshooterleft.gif");
                                                break;
                                        }
                                        break;
                                }
                            }
                        } else {
                            enemy.setVelX(enemy.getVelX() * -1);
                            enemy.setX(enemy.getX() - enemy.getVelX());
                        }
                    }
                    break;

                case spider:
                    if (intersectsTop(enemy)) {
                        enemy.setY(enemy.getY() - 2);
                        enemy.setType(Enemy.EnemyType.basic);
                    }
                    if (enemy.getSpiderCheck() == true) {
                        enemy.setY(enemy.getY() + enemy.getVelX());
                    }
                    if (enemy.getX() <= player.getX() + 100) {
                        enemy.setSpiderCheck(true);
                    }
                    break;
            }

            if (enemy.getBoundsInParent().intersects(player.getBoundsInParent())) {
                killPlayer();
                break;
            }

            if (enemy.getHealth() <= 0) {
                killEnemy(enemy);
                break;
            }

            if (count % 400 == 0) {
                switch (enemy.getType()) {
                    case left:
                        bullets.add(new Bullet(Bullet.BulletType.enemy, enemy.getX(), enemy.getY() + enemy.getHeight() / 2, 8, Color.LIME, 180, 1, 1));
                        levelRoot.getChildren().add(bullets.get(bullets.size() - 1));
                        break;

                    case right:
                        bullets.add(new Bullet(Bullet.BulletType.enemy, enemy.getX() + enemy.getWidth(), enemy.getY() + enemy.getHeight() / 2, 8, Color.LIME, 0, 1, 1));
                        levelRoot.getChildren().add(bullets.get(bullets.size() - 1));
                        break;

                    case up:
                        bullets.add(new Bullet(Bullet.BulletType.enemy, enemy.getX() + enemy.getWidth() / 2, enemy.getY(), 8, Color.LIME, 270, 1, 1));
                        levelRoot.getChildren().add(bullets.get(bullets.size() - 1));
                        break;

                    case down:
                        bullets.add(new Bullet(Bullet.BulletType.enemy, enemy.getX() + enemy.getWidth() / 2, enemy.getY() + enemy.getHeight(), 8, Color.LIME, 90, 1, 1));
                        levelRoot.getChildren().add(bullets.get(bullets.size() - 1));
                        break;

                    case triple:
                        bullets.add(new Bullet(Bullet.BulletType.enemy, enemy.getX() + enemy.getWidth() / 2, enemy.getY() + enemy.getHeight(), 6, Color.LIME, 90, 1, 1));
                        levelRoot.getChildren().add(bullets.get(bullets.size() - 1));
                        bullets.add(new Bullet(Bullet.BulletType.enemy, enemy.getX() + enemy.getWidth() / 2, enemy.getY() + enemy.getHeight(), 6, Color.LIME, 115, 1, 1));
                        levelRoot.getChildren().add(bullets.get(bullets.size() - 1));
                        bullets.add(new Bullet(Bullet.BulletType.enemy, enemy.getX() + enemy.getWidth() / 2, enemy.getY() + enemy.getHeight(), 6, Color.LIME, 65, 1, 1));
                        levelRoot.getChildren().add(bullets.get(bullets.size() - 1));
                        break;

                    case aiming:
                        bullets.add(new Bullet(Bullet.BulletType.enemy, enemy.getX() + enemy.getWidth() / 2, enemy.getY() + enemy.getHeight() / 2, 8, Color.LIME, calculateEnemyAimAngle(enemy), 1.5, 1));
                        levelRoot.getChildren().add(bullets.get(bullets.size() - 1));
                        break;
                }

            }
        }

    }

    private void checkpointControl() {
        for (Checkpoint checkpoint : checkpoints) {
            if (player.getBoundsInParent().intersects(checkpoint.getBoundsInParent()) && !checkpoint.isChecked()) {
                resetX = checkpoint.getX();
                resetY = checkpoint.getY();
                checkpoint.setChecked(true);
            }
        }
    }

    private void playerImageControl() {
        if (velX < 0) {
            player.setDirection(Player.Direction.left);
        }
        if (velX > 0) {
            player.setDirection(Player.Direction.right);
        }

        if (player.getDirection() == Player.Direction.left) {
            if (isPressed(KeyCode.A) && !player.getImage().equals("playerleftidle.gif")) {
                player.setImage("playerleftidle.gif");
            }

        }
        if (player.getDirection() == Player.Direction.right) {
            if (isPressed(KeyCode.D) && !player.getImage().equals("playerrightidle.gif")) {
                player.setImage("playerrightidle.gif");
            }
        }

    }

    private double calculateEnemyAimAngle(Enemy enemy) {
        double angle;
        point1 = new Point2D(enemy.getX() + enemy.getWidth() / 2, 0);
        point2 = new Point2D(enemy.getX() + enemy.getWidth() / 2, enemy.getY() + enemy.getHeight() / 2);
        point3 = new Point2D(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2);

        if (point3.getX() > point2.getX()) {
            angle = point2.angle(point1, point3);
        } else {
            angle = 360 - point2.angle(point1, point3);
        }
        angle -= 90;
        return angle;
    }

    private Enemy bulletIntersectsEnemy(Bullet bullet) {
        for (Enemy enemy : enemies) {
            if (bullet.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                return enemy;
            }
        }
        return null;
    }

    private boolean enemyMovementDetectionRight(Enemy enemy) {
        for (Platform platform : platforms) {
            if (enemy.getBoundsInParent().contains(platform.getX() + 20, platform.getY() - 20)) {
                return true;
            }

        }
        return false;
    }

    private boolean enemyMovementDetectionLeft(Enemy enemy) {
        for (Platform platform : platforms) {
            if (enemy.getBoundsInParent().contains(platform.getX() - 1, platform.getY() - 20)) {
                return true;
            }
        }
        return false;
    }

    private void killEnemy(Enemy enemy) {
        enemies.remove(enemy);
        levelRoot.getChildren().removeAll(enemy);
    }

    private void killPlayer() {
        levelRoot.getChildren().clear();
        platforms.clear();
        enemies.clear();
        weapons.clear();
        bullets.clear();
        doors.clear();
        checkpoints.clear();
        if (resetX == 0 && resetY == 0) {
            player.moveX(startX);
            player.moveY(startY);
        } else {
            player.moveX(resetX);
            player.moveY(resetY);
        }
        bossCreation = true;
        loadLevel(currentLevel);
    }

    private void loadLevel(String[] level) {
        currentLevel = level;
        player.releaseWeapon(player.getWeapon());
        currentLevel = level;
        boolean[] sides;
        if (level == LevelData.LEVEL1 || level == LevelData.LEVEL2 || level == LevelData.VIEW) {
            xScroll = true;
            yScroll = false;
        } else if (level == LevelData.LEVEL3) {
            xScroll = false;
            yScroll = true;
        } else if (level == LevelData.LEVELBOSS) {
            xScroll = false;
            yScroll = false;
            mainRoot.setTranslateX(100);
            Weapon weapon = new Weapon(Weapon.WeaponType.blaster, 0, 0);
            player.pickUpWeapon(weapon);
        }

        for (int i = 0; i < level.length; i++) {
            for (int j = 0; j < level[i].length(); j++) {
                sides = new boolean[4];
                switch (level[i].substring(j, j + 1)) {

                    case "a":

                        if (i != 0 && i != level.length - 1) {
                            sides[0] = !(level[i - 1].substring(j, j + 1).equals("a") || level[i - 1].substring(j, j + 1).equals("g") || level[i - 1].substring(j, j + 1).equals("h"));
                            sides[2] = !(level[i + 1].substring(j, j + 1).equals("a") || level[i + 1].substring(j, j + 1).equals("g") || level[i + 1].substring(j, j + 1).equals("h"));
                        }

                        if (j != 0 && j != level[i].length() - 1) {
                            sides[1] = !(level[i].substring(j + 1, j + 2).equals("a") || level[i].substring(j + 1, j + 2).equals("g") || level[i].substring(j + 1, j + 2).equals("h"));
                            sides[3] = !(level[i].substring(j - 1, j).equals("a") || level[i].substring(j - 1, j).equals("g") || level[i].substring(j - 1, j).equals("h"));
                        }

                        platforms.add(new Platform(Platform.PlatformType.standard, j * 20, i * 20, 20, 20, Color.RED, sides, 100000));
                        levelRoot.getChildren().add(platforms.get(platforms.size() - 1));
                        break;

                    case "s":
                        platforms.add(new Platform(Platform.PlatformType.death, j * 20, i * 20, 20, 20, Color.PINK, sides, 100000));
                        levelRoot.getChildren().add(platforms.get(platforms.size() - 1));
                        break;

                    case "d":
                        sides[0] = true;
                        platforms.add(new Platform(Platform.PlatformType.oneway, j * 20, i * 20, 20, 7, Color.AQUA, sides, 100000));
                        levelRoot.getChildren().add(platforms.get(platforms.size() - 1));
                        break;

                    case "f":

                        if (i != 0 && i != level.length - 1) {
                            sides[0] = !(level[i - 1].substring(j, j + 1).equals("a") || level[i - 1].substring(j, j + 1).equals("g") || level[i - 1].substring(j, j + 1).equals("h"));
                            sides[2] = !(level[i + 1].substring(j, j + 1).equals("a") || level[i + 1].substring(j, j + 1).equals("g") || level[i + 1].substring(j, j + 1).equals("h"));
                        }

                        if (j != 0 && j != level[i].length() - 1) {
                            sides[1] = !(level[i].substring(j + 1, j + 2).equals("a") || level[i].substring(j + 1, j + 2).equals("g") || level[i].substring(j + 1, j + 2).equals("h"));
                            sides[3] = !(level[i].substring(j - 1, j).equals("a") || level[i].substring(j - 1, j).equals("g") || level[i].substring(j - 1, j).equals("h"));
                        }

                        platforms.add(new Platform(Platform.PlatformType.falling, j * 20, i * 20, 20, 20, Color.GREEN, sides, 100000));
                        levelRoot.getChildren().add(platforms.get(platforms.size() - 1));
                        break;

                    case "g":

                        if (i != 0 && i != level.length - 1) {
                            sides[0] = !(level[i - 1].substring(j, j + 1).equals("a") || level[i - 1].substring(j, j + 1).equals("g") || level[i - 1].substring(j, j + 1).equals("h"));
                            sides[2] = !(level[i + 1].substring(j, j + 1).equals("a") || level[i + 1].substring(j, j + 1).equals("g") || level[i + 1].substring(j, j + 1).equals("h"));
                        }

                        if (j != 0 && j != level[i].length() - 1) {
                            sides[1] = !(level[i].substring(j + 1, j + 2).equals("a") || level[i].substring(j + 1, j + 2).equals("g") || level[i].substring(j + 1, j + 2).equals("h"));
                            sides[3] = !(level[i].substring(j - 1, j).equals("a") || level[i].substring(j - 1, j).equals("g") || level[i].substring(j - 1, j).equals("h"));
                        }

                        platforms.add(new Platform(Platform.PlatformType.breakable, j * 20, i * 20, 20, 20, Color.BLUE, sides, 1));
                        levelRoot.getChildren().add(platforms.get(platforms.size() - 1));
                        break;

                    case "h":
                        Rectangle rectangle = new Rectangle(j * 20, i * 20, 20, 20);
                        rectangle.setFill(Color.rgb(2, 42, 61));
                        levelRoot.getChildren().add(rectangle);
                        break;

                    case "q":
                        enemies.add(new Enemy(Enemy.EnemyType.basic, j * 20, i * 20, 20, 20, -1, 3, Color.BLUEVIOLET));
                        levelRoot.getChildren().add(enemies.get(enemies.size() - 1));
                        break;

                    case "w":
                        enemies.add(new Enemy(Enemy.EnemyType.spider, j * 20, i * 20, 20, 20, 2, 1, Color.TURQUOISE));
                        levelRoot.getChildren().add(enemies.get(enemies.size() - 1));
                        break;

                    case "e":
                        enemies.add(new Enemy(Enemy.EnemyType.up, j * 20, i * 20, 20, 20, 1, 5, Color.PURPLE));
                        levelRoot.getChildren().add(enemies.get(enemies.size() - 1));
                        break;

                    case "r":
                        enemies.add(new Enemy(Enemy.EnemyType.down, j * 20, i * 20, 20, 20, 1, 5, Color.PURPLE));
                        levelRoot.getChildren().add(enemies.get(enemies.size() - 1));
                        break;

                    case "t":
                        enemies.add(new Enemy(Enemy.EnemyType.left, j * 20, i * 20, 20, 20, 1, 5, Color.PURPLE));
                        levelRoot.getChildren().add(enemies.get(enemies.size() - 1));
                        break;

                    case "y":
                        enemies.add(new Enemy(Enemy.EnemyType.right, j * 20, i * 20, 20, 20, 1, 5, Color.PURPLE));
                        levelRoot.getChildren().add(enemies.get(enemies.size() - 1));
                        break;

                    case "i":
                        enemies.add(new Enemy(Enemy.EnemyType.triple, j * 20, i * 20, 20, 20, 2, 5, Color.DODGERBLUE));
                        levelRoot.getChildren().add(enemies.get(enemies.size() - 1));
                        break;

                    case "o":
                        enemies.add(new Enemy(Enemy.EnemyType.aiming, j * 20, i * 20, 20, 20, 0.5, 5, Color.CHOCOLATE));
                        levelRoot.getChildren().add(enemies.get(enemies.size() - 1));
                        break;

                    case "x":
                        if (startX == 0 && startY == 0) {
                            player.moveX(j * 20);
                            player.moveY(i * 20);
                            startX = j * 20;
                            startY = i * 20;
                        }
                        break;

                    case "c":
                        Weapon weapon = new Weapon(Weapon.WeaponType.blaster, j * 20 + 5, i * 20 + 5);
                        levelRoot.getChildren().add(weapon);
                        weapons.add(weapon);
                        break;

                    case "v":
                        checkpoints.add(new Checkpoint((j * 20) + 9, i * 20, 2, 20, Color.BROWN));
                        levelRoot.getChildren().add(checkpoints.get(checkpoints.size() - 1));
                        break;

                    case "1":
                        doors.add(new Door(LevelData.LEVEL1, j * 20, i * 20, 20, 20, Color.BROWN));
                        levelRoot.getChildren().add(doors.get(doors.size() - 1));
                        doors.get(doors.size() - 1).toBack();
                        break;

                    case "2":
                        doors.add(new Door(LevelData.LEVEL2, j * 20, i * 20, 20, 20, Color.BROWN));
                        levelRoot.getChildren().add(doors.get(doors.size() - 1));
                        doors.get(doors.size() - 1).toBack();
                        break;

                    case "3":
                        doors.add(new Door(LevelData.LEVEL3, j * 20, i * 20, 20, 20, Color.BROWN));
                        levelRoot.getChildren().add(doors.get(doors.size() - 1));
                        doors.get(doors.size() - 1).toBack();
                        break;

                    case "4":
                        doors.add(new Door(LevelData.LEVELBOSS, j * 20, i * 20, 20, 20, Color.BROWN));
                        levelRoot.getChildren().add(doors.get(doors.size() - 1));
                        doors.get(doors.size() - 1).toBack();
                        break;

                    case "#":
                        doors.add(new Door(LevelData.START, j * 20, i * 20, 20, 20, Color.BROWN));
                        levelRoot.getChildren().add(doors.get(doors.size() - 1));
                        doors.get(doors.size() - 1).toBack();
                        break;

                }
            }
        }
    }

    private void clearLevel() {
        levelRoot.getChildren().clear();
        platforms.clear();
        doors.clear();
        weapons.clear();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
