package platformpt3;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Boss extends Rectangle {

    private int health;
    private double velX, velY;

    Boss(double x, double y, int w, int l, int health, double velX, double velY, Color color) {
        super(x, y, w, l);
        this.velX = velX;
        this.velY = velY;
        this.health = health;
        Image image = new Image(getClass().getResourceAsStream("boss.gif"));
        setFill(new ImagePattern(image));
        
    }

    public double getVelX() {
        return velX;
    }

    public void setVelX(double velX) {
        this.velX = velX;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    
}
}