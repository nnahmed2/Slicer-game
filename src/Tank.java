
import bagel.DrawOptions;
import bagel.Image;
import bagel.Input;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.util.Vector2;
import org.lwjgl.system.CallbackI;

import java.util.List;


import java.util.ArrayList;


/**
 * A regular tank.
 */
public class Tank extends Sprite {

    private static final String imageFile = "res/images/tank.png";
    private static final String projectileImage = "res/images/tank_projectile.png";
    private static final int damage = 1;
    private static final int impactRadius = 10000;
    private static final int projectileCool = 1;
    private static final String towerType = "tank";
    private static Point point;
    private static final double FPS = 60;
    private double frameCount;

    private List<Point> targetPoints = new ArrayList<>();

    private boolean projectileReady = true;
    private boolean getNextTarget = true;

    private static Point targetPoint;

    private double angle;
    private static Image image;



    /**
     * Creates a new Sprite (game entity)
     *
     * @param point the point on the map at which the tank is placed
     */
    public Tank(Point point) {
        super(point, imageFile);
        this.image = new Image(imageFile);
        this.point = point;
        this.targetPoint = point;
        this.angle = 0;
    }

    @Override
    public void update(Input input) {
        frameCount += ShadowDefend.getTimescale();
        if (targetPoint.x != 0 && targetPoint.y != 0) {
            angle = Math.atan2(targetPoint.y - point.y, targetPoint.x - point.x) + 45;
            projectileReady = true;
            getNextTarget = false;
        }

        image.draw(getCenter().x, getCenter().y, new DrawOptions().setRotation(angle));

        if ((frameCount / FPS > (projectileCool / ShadowDefend.getTimescale()))) {
            projectileReady = true;
            frameCount = 0;
        } else {
            projectileReady = false;
        }

    }

    public Point getPoint() {
        return point;
    }

    public void setTarget(Point point) {
        targetPoint = point;
    }

    public boolean isProjectileReady() {
        return projectileReady;
    }


    public List<Point> findPath() {
        List<Point> path = new ArrayList<>();
        return path;
    }

    public int getImpactRadius() {
        return impactRadius;
    }

    public boolean isReadyForNextTarget() {
        return getNextTarget;
    }
    public void setGetNextTarget(boolean update) {
        getNextTarget = update;
    }
    public String getType() {
        return towerType;
    }
    public int getProjectileCool() {
        return projectileCool;
    }

}