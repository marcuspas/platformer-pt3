package platformpt3;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Door extends Rectangle {

    private String[] level;

    public Door(String[] level, double x, double y, double width, double height, Paint fill) {
        super(x, y, width, height);
        setFill(fill);
        this.level = level;

        Image image = new Image(getClass().getResourceAsStream("door.gif"));
        setFill(new ImagePattern(image));
    }

    public String[] getLevel() {
        return level;
    }

}
