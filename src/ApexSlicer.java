
import bagel.Input;
import bagel.util.Point;
import bagel.util.Vector2;
import java.util.List;

/**
 * An apex slicer.
 */
public class ApexSlicer extends Sprite {

    private static final String image = "res/images/apexslicer.png";
    private static final double speed = 0.75;
    private static final int reward = 150;
    private static final double health = 25;
    private static int penalty = 16;

    private static int spawnNumber;
    private static int waveNumber;

    private boolean isEliminated;


    private final List<Point> polyline;
    private int targetPointIndex;
    private boolean finished;
    private static final String slicerType = "apexslicer";

    private Point nextPoint;


    /**
     * Creates a new Slicer
     *
     * @param polyline The polyline that the slicer must traverse (must have at least 1 point)
     */
    public ApexSlicer(List<Point> polyline, int sN, int wN) {
        super(polyline.get(0), image);
        this.polyline = polyline;
        this.targetPointIndex = 1;
        this.finished = false;
        this.isEliminated = false;
        this.spawnNumber = sN;
        this.waveNumber = wN;
        this.nextPoint = new Point(0, 0);

    }

    /**
     * Updates the current state of the slicer. The slicer moves towards its next target point in
     * the polyline at its specified movement rate.
     */
    @Override
    public void update(Input input) {
        if (finished) {
            return;
        }
        // Obtain where we currently are, and where we want to be
        Point currentPoint = getCenter();
        Point targetPoint = polyline.get(targetPointIndex);

        // Convert them to vectors to perform some very basic vector math
        Vector2 target = targetPoint.asVector();
        Vector2 current = currentPoint.asVector();
        Vector2 distance = target.sub(current);

        // Distance we are (in pixels) away from our target point
        double magnitude = distance.length();

        // Check if we are close to the target point
        if (magnitude < speed * ShadowDefend.getTimescale()) {
            // Check if we have reached the end
            if (targetPointIndex == polyline.size() - 1) {
                finished = true;
                return;
            } else {
                // Make our focus the next point in the polyline
                targetPointIndex += 1;
            }
        }
        // Move towards the target point
        // We do this by getting a unit vector in the direction of our target, and multiplying it
        // by the speed of the slicer (accounting for the timescale)
        nextPoint = distance.normalised().mul(speed * ShadowDefend.getTimescale()).asPoint();
        super.move(nextPoint.asVector());
        // Update current rotation angle to face target point
        setAngle(Math.atan2(targetPoint.y - currentPoint.y, targetPoint.x - currentPoint.x));
        super.update(input);
    }

    public boolean isEliminated() {
        return isEliminated;
    }

    //If slicer gets eliminated, get the polyline for rest of path so the new spawned slicers can begin from there
    public List<Point> getNewPolyline() {
        List<Point> newpolyline = null;
        for (int i = polyline.size() - 1; i > targetPointIndex; i--) {
            newpolyline.add(0, polyline.get(i));
        }
        return newpolyline;
    }
    public Point getNext() {
        Point point = getNextPoint(nextPoint.asVector());
        return point;
    }
    public int getSpawnNumber() {
        return spawnNumber;
    }

    public int getWaveNumber() {
        return waveNumber;
    }
    public String getType() {
        return slicerType;
    }

    public int getReward() {
        return reward;
    }

    public int getTargetPointIndex() {
        return targetPointIndex;
    }
    public int getPenalty() {
        return penalty;
    }

    public boolean isFinished() {
        return finished;
    }
}
