package platformpt3;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Player extends Rectangle {
    
    private Weapon weapon;
    private Direction direction = Direction.right;
    private String imageData;
    
    public enum Direction {
        left, right
    }
    
    public Player(double x, double y, double w, double h, Color fill) {
        super(x, y, w, h);
        Image image = new Image(getClass().getResourceAsStream("playerrightidle.gif"));
        setFill(new ImagePattern(image));
        
    }
    
    public Weapon getWeapon() {
        return weapon;
    }
    
    public void pickUpWeapon(Weapon weapon) {
        this.weapon = weapon;
        this.weapon.setX(getX() + 2);
        this.weapon.setY(getY() + weapon.getHeight() / 2);
        PlatformPt3.levelRoot.getChildren().remove(weapon);
        PlatformPt3.playerRoot.getChildren().add(weapon);
    }
    
    public void releaseWeapon(Weapon weapon) {
        this.weapon = null;
        PlatformPt3.playerRoot.getChildren().remove(weapon);
    }
    
    public void moveX(double x) {
        setX(x);
        
        try {
            this.weapon.setX(x + 2);
        } catch (NullPointerException e) {
        }
        
    }
    
    public void moveY(double y) {
        setY(y);
        
        try {
            this.weapon.setY(y + weapon.getHeight() / 2);
        } catch (NullPointerException e) {
        }
        
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    
    public void setImage(String string) {
        
        imageData = string;
        ImagePattern pattern = new ImagePattern(new Image(getClass().getResourceAsStream(string)));
        setFill(pattern);
        
    }
    
    public String getImage() {
        if (imageData == null) {
            return "";
        }
        return imageData;
    }
    
}
