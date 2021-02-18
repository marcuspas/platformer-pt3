package platformpt3;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Weapon extends Rectangle {

    private WeaponType type;

    public enum WeaponType {
        blaster
    }

    public Weapon(WeaponType type, double w, double h) {

        super(w, h, 16, 10);
        this.type = type;

        Image image = new Image(getClass().getResourceAsStream("gun.png"));
        setFill(new ImagePattern(image));
    }

    public WeaponType getType() {
        return type;
    }

    public void setAngle(double angle) {
        setRotate(angle);
    }

}
