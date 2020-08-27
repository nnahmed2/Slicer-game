import bagel.DrawOptions;
import bagel.Image;
import bagel.Input;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Vector2;
import java.util.List;
import bagel.Font;


/**
 * The Status Panel on bottom of playing board.
 */
public class StatusPanel {

    private static final String IMAGE_FILE = "res/images/statuspanel.png";
    ;
    private static int waveNumber;
    private static double timeScale;
    private static String status;
    private static int lives;
    private static Image image;

    //Font for all information
    private static Font font;


    /**
     * Creates new status panel
     *
     * @param wN           current wavenumber
     * @param tS           current timescale
     * @param statusUpdate current status
     * @param livesLeft    amount of lives left
     */
    public StatusPanel(int wN, double tS, String statusUpdate, int livesLeft) {
        this.waveNumber = wN;
        this.timeScale = tS;
        this.status = statusUpdate;
        this.lives = livesLeft;
        this.image = new Image(IMAGE_FILE);
        this.font = new Font("res/fonts/DejaVuSans-Bold.ttf", 15);

    }

    /**
     * Updates status panel with all relevant information
     *
     * @param input
     */
    public void update(Input input) {
        image.drawFromTopLeft(0, 768 - image.getHeight());
        font.drawString("Wave: " + waveNumber, 5, 760, new DrawOptions().setBlendColour(Colour.WHITE));
        font.drawString("Status: " + status, 450, 760, new DrawOptions().setBlendColour(Colour.WHITE));
        font.drawString("Lives: " + lives, 950, 760, new DrawOptions().setBlendColour(Colour.WHITE));

        if (timeScale <= 1) {
            font.drawString("Time Scale: " + timeScale, 200, 760, new DrawOptions().setBlendColour(Colour.WHITE));
        } else {
            font.drawString("Time Scale: " + timeScale, 200, 760, new DrawOptions().setBlendColour(Colour.GREEN));
        }
    }
}
