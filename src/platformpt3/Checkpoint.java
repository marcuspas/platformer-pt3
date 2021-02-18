package platformpt3;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Checkpoint extends Rectangle {

    private boolean checked;

    public Checkpoint(double x, double y, double w, double h, Paint fill) {
        super(x, y, w, h);
        setFill(fill);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
