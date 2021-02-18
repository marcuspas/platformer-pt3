package platformpt3;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public final class Enemy extends Rectangle {

    private int health;
    private boolean spiderCheck;
    private double velX;
    private EnemyType type;
    private String imageData;

    public enum EnemyType {
        basic, spider, up, down, left, right, triple, aiming
    }

    Enemy(EnemyType type, double x, double y, int w, int h, double velX, int health, Color color) {
        super(w, h, color);
        setX(x);
        setY(y);
        this.type = type;
        this.velX = velX;
        this.health = health;

        switch (type) {
            case basic:
                setImage("lizardRight.gif");
                break;
            case spider:
                setImage("lizardLeft.gif");
                break;
            case aiming:
                setImage("movingshooterleft.gif");
                break;
            case up:
                setImage("enemyup.gif");
                break;
            case down:
                setImage("enemydown.gif");
                break;
            case left:
                setImage("enemyleft.gif");
                break;
            case right:
                setImage("enemyright.gif");
                break;
            case triple:
                setImage("enemydown.gif");
                break;
        }
    }

    public double getVelX() {
        return velX;
    }

    public void setVelX(double velX) {
        this.velX = velX;
    }

    public EnemyType getType() {
        return type;
    }

    public void setType(EnemyType type) {
        this.type = type;
    }

    public boolean getSpiderCheck() {
        return spiderCheck;
    }

    public void setSpiderCheck(boolean spiderCheck) {
        this.spiderCheck = spiderCheck;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setImage(String string) {
        imageData = string;
        Image image = new Image(getClass().getResourceAsStream(string));
        setFill(new ImagePattern(image));
    }

    public String getImage() {
        if (imageData == null) {
            return "";
        }
        return imageData;
    }

}
