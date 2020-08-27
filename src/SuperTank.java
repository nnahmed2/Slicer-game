import bagel.Image;
import bagel.Input;
import bagel.util.Point;
import bagel.util.Vector2;
import java.util.List;

/**
 * A super tank.
 */
public class SuperTank extends Sprite {

    private static final String imageFile = "res/images/supertank.png";
    private static final String projectileImage = "res/images/supertank_projectile.png";
    private static final int damage = 1;
    private static final int impactRadius = 100;
    private static final int projectileCool = 1000;
    private static final String towerType = "supertank";
    private static Point point;
    private static Image image;


    /**
     * Creates a new Sprite (game entity)
     *
     * @param point the point on the map at which the tank is placed
     */
    public SuperTank(Point point) {
        super(point, imageFile);
        this.point = point;

    }

    @Override
    public void update(Input input) {
        super.update(input);

//        image.draw(point.x, point.y);
    }

    @Override
    public void move(Vector2 dx) {
        super.move(dx);
    }

    public String getType() {
        return towerType;
    }

}