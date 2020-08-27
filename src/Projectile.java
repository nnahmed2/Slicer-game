
import bagel.DrawOptions;
import bagel.Image;
import bagel.Input;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.util.Vector2;
import org.lwjgl.system.CallbackI;

import java.util.List;

/**
 * A projectile
 */
public class Projectile extends Sprite {
    private static final int speed = 10;
    private Point target;
    private Vector2 newtarget;

    private static final String projectileImage = "res/images/tank_projectile.png";

    private static Image image;

    private static boolean isReady;

    private static boolean hasHit;
    private Point nextPoint;
    private Point currentPoint;
    private static Image slicer = new Image("res/images/slicer.png");
    private double projectileCount;


//    private boolean hasHit;


    /**
     * Creates a new Sprite (game entity)
     *
     * @param target    the path the projectile will take
     * @param curr a string that shows whether it is a tank or supertank projectile
     */
    public Projectile(Point target, Point curr) {
        super(curr, projectileImage);
        this.target = target;
        this.currentPoint = curr;
        this.image = new Image(projectileImage);
//        imageSrc = String.format("res/images/%s_projectile.png", type);
        hasHit = false;
        isReady = true;
        nextPoint = new Point(0, 0);
    }

    /**
     * Updates the current state of the projectile.
     */

    @Override
    public void update(Input input) {
        Point currentPoint = getCenter();
        // Convert them to vectors to perform some very basic vector math
        checkHasHit();
//        System.out.println(currentPoint);
//        System.out.println(target);
//        System.out.println(nextPoint);
        System.out.println(currentPoint);

        if (target.x != 0 && target.y != 0 && !hasHit) {
            isReady = false;
            Vector2 current = currentPoint.asVector();
            Vector2 distance = target.asVector().sub(current);

            double magnitude = distance.length();
//            System.out.println(distance.normalised());
            if (magnitude < speed * ShadowDefend.getTimescale()) {
                hasHit = false;
            } else {
                hasHit = true;
            }
            nextPoint = (distance.normalised().mul(speed * ShadowDefend.getTimescale()).asPoint());
//            super.move(distance.normalised().mul(speed * ShadowDefend.getTimescale()));
            super.move(nextPoint.asVector());
            super.update(input);
        }


        // Check if we are close to the target point
//        if (magnitude < speed * ShadowDefend.getTimescale()) {
//            // Check if we have reached the end
//            if (targetPointIndex == polyline.size() - 1) {
//                finished = true;
//                return;
//            } else {
//                // Make our focus the next point in the polyline
//                tsargetPointIndex += 1;
//            }
//        }


    }
    public void checkHasHit() {
        if (getRect() == slicer.getBoundingBoxAt(target)) {
            hasHit = true;
        }
    }
    public Point getNextPoint() {
        return nextPoint;
    }

    public void setIsReady(boolean check) {
        if (hasHit) {
            isReady = check;
        }
    }
    public boolean getIsReady() {
        return isReady;
    }

    public void setTarget(Point point) {
        target = point;
    }
    public void increaseCount() {
        projectileCount += ShadowDefend.getTimescale();
    }
    public void setProjectileCount(double value) {
        projectileCount = value;
    }
    public double getProjectileCount() {
        return projectileCount;
    }

//



}