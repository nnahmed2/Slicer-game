import bagel.Image;
import bagel.Input;
import bagel.util.Point;
import bagel.util.Vector2;
import java.util.List;

/**
 * Airplane
 */
public class Airplane extends Sprite {

    private static final String imageFile = "res/images/airsupport.png";
    private static final int damage = 1;

    private static final String towerType = "airplane";

    private static Point point;
    private static Image image;


    /**
     * Creates a new Sprite (game entity)
     *
     * @param point the point on the map at which the tank is placed
     */
    public Airplane(Point point) {
        super(point, imageFile);
        this.point = point;

    }

    @Override
    public void update(Input input) {
//        image.draw(point.x, point.y);
        super.update(input);

    }

    @Override
    public void move(Vector2 dx) {
        super.move(dx);
    }

    public String getType() {
        return towerType;
    }

}