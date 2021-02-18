package platformpt3;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class Bullet extends Circle {

    private int damage, hitCount;
    private double speed, angle;
    private BulletType type;

    public enum BulletType {
        player, enemy
    }

    public Bullet(BulletType type, double centerX, double centerY, double radius, Paint fill, double angle, double speed, int damage) {
        super(centerX, centerY, radius, fill);
        this.damage = damage;
        this.angle = angle;
        this.speed = speed;
        this.type = type;
        setRotate(angle + 90);
        Image image = new Image(getClass().getResourceAsStream("fire-ball.gif"));
        setFill(new ImagePattern(image));
    }

    public void moveBullet() {
        setTranslateX(getTranslateX() + speed * Math.cos(Math.toRadians(angle)));
        setTranslateY(getTranslateY() + speed * Math.sin(Math.toRadians(angle)));
    }

    public int getDamage() {
        return damage;
    }

    public int getHitCount() {
        return hitCount;
    }

    public BulletType getType() {
        return type;
    }

}
