package platformpt3;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Platform extends Rectangle {

    private Rectangle top, bottom, left, right;
    private int health;
    private boolean[] sides;
    private PlatformType type;
    private Timeline timelineDisappear = new Timeline();
    private Timeline timelineReappear = new Timeline();
    private int count;

    public enum PlatformType {
        standard, oneway, falling, death, breakable
    }

    public Platform(PlatformType type, double x, double y, double w, double h, Color fill, boolean[] sides, int health) {

        super(x, y, w, h);
        setFill(fill);
        this.health = health;
        this.sides = sides;
        this.type = type;

        Image image = null;

        switch (type) {
            case death:
                image = new Image(getClass().getResourceAsStream("lava.gif"));
                setFill(new ImagePattern(image));
                break;
            case standard:
                if(!sides[0] && !sides[1] && !sides[2] && !sides[3]){
                    setFill(Color.rgb(2, 42, 61));
                }
                else if (sides[0] && !sides[1] && !sides[2] && !sides[3]) {
                    image = new Image(getClass().getResourceAsStream("floorplatform.png"));
                    setFill(new ImagePattern(image));
                }
                else if (!sides[0] && !sides[1] && !sides[2] && sides[3]) {
                    image = new Image(getClass().getResourceAsStream("leftplatform.png"));
                    setFill(new ImagePattern(image));
                }
                else if (!sides[0] && sides[1] && !sides[2] && !sides[3]) {
                    image = new Image(getClass().getResourceAsStream("rightplatform.png"));
                    setFill(new ImagePattern(image));
                }
                else if (!sides[0] && !sides[1] && sides[2] && !sides[3]) {
                    image = new Image(getClass().getResourceAsStream("bottomplatform.png"));
                    setFill(new ImagePattern(image));
                }
                else{
                    image = new Image(getClass().getResourceAsStream("cornerplatform.png"));
                    setFill(new ImagePattern(image));
                }
                break;
            case oneway:
                image = new Image(getClass().getResourceAsStream("onewayplatform.png"));
                setFill(new ImagePattern(image));
                break;
            case falling:
                image = new Image(getClass().getResourceAsStream("fallingplatform.png"));
                setFill(new ImagePattern(image));
                break;
            case breakable:
                image = new Image(getClass().getResourceAsStream("breakableplatform.png"));
                setFill(new ImagePattern(image));
                break;
        }

        KeyFrame keyframeDisappear = new KeyFrame(
                Duration.seconds(0.01), (event) -> {
            setOpacity(getOpacity() - 0.03);
            if (getOpacity() < 0.02) {
                PlatformPt3.platforms.remove(this);
                timelineDisappear.stop();
                timelineReappear.play();
            }
        });

        KeyFrame keyframeReappear = new KeyFrame(
                Duration.seconds(1), (event) -> {
            count++;
            if (count == 2) {
                PlatformPt3.platforms.add(this);
                setOpacity(1);
                count = 0;
            }
        });

        timelineDisappear.getKeyFrames().add(keyframeDisappear);
        timelineDisappear.setCycleCount(Timeline.INDEFINITE);
        timelineReappear.getKeyFrames().add(keyframeReappear);
        timelineReappear.setCycleCount(2);

        if (sides[0]) {
            this.top = new Rectangle(x + 1, y, w - 2, 1);
        } else {
            this.top = new Rectangle(0, 0, 0, 0);
        }

        if (sides[1]) {
            this.right = new Rectangle(x + w - 1, y + 1, 1, h - 2);
        } else {
            this.right = new Rectangle(0, 0, 0, 0);
        }

        if (sides[2]) {
            this.bottom = new Rectangle(x + 1, y + h - 1, w - 2, 1);
        } else {
            this.bottom = new Rectangle(0, 0, 0, 0);
        }

        if (sides[3]) {
            this.left = new Rectangle(x, y + 1, 1, h - 2);
        } else {
            this.left = new Rectangle(0, 0, 0, 0);
        }

    }

    public Rectangle getTop() {
        return top;
    }

    public Rectangle getBottom() {
        return bottom;
    }

    public Rectangle getLeft() {
        return left;
    }

    public Rectangle getRight() {
        return right;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public PlatformType getType() {
        return type;
    }

    public Timeline getTimelineDisappear() {
        return timelineDisappear;
    }

    public Timeline getTimelineReappear() {
        return timelineReappear;
    }

    public void setSides(int index, boolean side) {
        if (side) {
            switch (index) {
                case 0:
                    this.top = new Rectangle(getX() + 1, getY(), getWidth() - 2, 1);
                    break;
                case 1:
                    this.right = new Rectangle(getX() + getWidth() - 1, getY() + 1, 1, getHeight() - 2);
                    break;
                case 2:
                    this.bottom = new Rectangle(getX() + 1, getY() + getHeight() - 1, getWidth() - 2, 1);
                    break;
                case 3:
                    this.left = new Rectangle(getX(), getY() + 1, 1, getHeight() - 2);
                    break;
            }
        }
    }
}
