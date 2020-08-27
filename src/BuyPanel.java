import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Vector2;

import java.util.ArrayList;
import bagel.map.TiledMap;
import java.util.List;

/**
 * The Buy Panel on top of playing board.
 */
public class BuyPanel {

    private static final String IMAGE_FILE = "res/images/buypanel.png";;
    private static Integer money;

    private static Image image;
    private static Font font;
    private static Font keybindfont;
    private static Font pricefont;

    private static Image tank;
    private Image supertank;
    private Image airplane;

    //Lists for each type of tower that gets added and their corresponding coordinates
    private List<String> arr1 = new ArrayList<>();
    private List<Sprite> arr2 = new ArrayList<>();

    //To use for checking for whether it is a valid point for placing tower
    private final List<Point> polyline;

    //If left mousebutton was pressed
    private static boolean wasPressed = false;

    //If it is a valid coordinate for placing the tower.
    private static boolean isValid = true;

    private TiledMap map;

    /**
     * Creates a panel
     *
     * @param givenpoly The polyline that the slicer must traverse (must have at least 1 point)
     * @param amount the amount of money
     * @param towersTypes list of Strings that contain names of towers bought
     * @param towers list of Sprites with the coordinates of towers bought
     * @param theMap TiledMap
     */
    public BuyPanel(Integer amount, List<String> towersTypes, List<Sprite> towers, List<Point> givenpoly, TiledMap theMap) {
        this.money = amount;
        this.image = new Image(IMAGE_FILE);
        this.map = theMap;
        this.polyline = givenpoly;
        this.font = new Font("res/fonts/DejaVuSans-Bold.ttf", 50);
        this.keybindfont = new Font("res/fonts/DejaVuSans-Bold.ttf", 15);
        this.tank = new Image("res/images/tank.png");
        this.supertank = new Image("res/images/supertank.png");
        this.airplane = new Image("res/images/airsupport.png");
        this.pricefont = new Font("res/fonts/DejaVuSans-Bold.ttf", 20);
        this.arr1 = towersTypes;
        this.arr2 = towers;
    }

    /**
     * @param input
     * Updates the current state of the buy panel.
     * Loads all relevant information and allows player to buy towers
     * @return money so that the amount can always stay updated
     */

    public int update(Input input) {
        image.drawFromTopLeft(0,0);
        font.drawString("$" + Integer.toString(money),1024 - 175, 65, new DrawOptions().setBlendColour(Colour.WHITE));
        keybindfont.drawString("Key binds:", 450, 30, new DrawOptions().setBlendColour(Colour.WHITE));
        keybindfont.drawString("S - Start Wave", 450, 50, new DrawOptions().setBlendColour(Colour.WHITE));
        keybindfont.drawString("L - Increase Timescale", 450, 65, new DrawOptions().setBlendColour(Colour.WHITE));
        keybindfont.drawString("K - Decrease Timescale", 450, 80, new DrawOptions().setBlendColour(Colour.WHITE));

        tank.drawFromTopLeft(25,10);

        supertank.drawFromTopLeft(125,10);
        airplane.drawFromTopLeft(225,10);

        //Going through arrays and drawing each tower and at their corresponding coordinates
//        if (!arr2.isEmpty()) {
//            for (int i = 0; i < arr2.size(); i++) {
//                if (arr1.get(i).equals("tank")) {
//                    tank.draw(arr2.get(i).getCenter().x, arr2.get(i).getCenter().y);
//
//                }
//                if (arr1.get(i).equals("supertank")) {
//                    supertank.draw(arr2.get(i).getCenter().x, arr2.get(i).getCenter().y);
//                }
//                if (arr1.get(i).equals("airplane")) {
//                    airplane.draw(arr2.get(i).getCenter().x, arr2.get(i).getCenter().y);
//                }
//            }
//        }

        if (input.wasPressed(MouseButtons.LEFT)) {
            Point point = new Point(input.getMouseX(), input.getMouseY());
            setIsValid(input.getMousePosition());

            //wasPressed is only set to true if the player has clicked on one of the towers and they have enough money
            //If it is true then the point at which the player wants to place tower to gets added to
            //Point array (arr2) if the point is valid
            if (wasPressed == true && isValid) {
                if (arr1.get(arr1.size() - 1).equals("tank")) {
                    money = money - 250;
                } else if (arr1.get(arr1.size() - 1).equals("supertank")) {
                    money = money - 600;
                } else if (arr1.get(arr1.size() - 1).equals("airplane")) {
                    money = money - 500;
                }
                if (arr1.get(arr1.size() - 1).equals("tank")) {
                    Tank tower = new Tank(point);
                    arr2.add(tower);
                } else if (arr1.get(arr1.size() - 1).equals("supertank")) {
                    SuperTank tower = new SuperTank(point);
                    arr2.add(tower);
                } else if (arr1.get(arr1.size() - 1).equals("airplane")) {
                    Airplane tower = new Airplane(point);
                    arr2.add(tower);
                }
                wasPressed = false;
            } else if ((25 < point.x && point.x < 25 + tank.getWidth()) && money >= 250 && (10 < point.y && point.y < 10 + tank.getHeight())) {
                arr1.add("tank");
                wasPressed = true;
            } else if ((125 < point.x && point.x < 125 + supertank.getWidth()) && money >= 600 && (10 < point.y && point.y < 10 + supertank.getHeight())) {
                arr1.add("supertank");
                wasPressed = true;
            } else if ((225 < point.x && point.x < 225 + supertank.getWidth()) && money >= 500 && (10 < point.y && point.y < 10 + airplane.getHeight())) {
                arr1.add("airplane");
                wasPressed = true;
            }
        }

        //Hovers the images of towers as player chooses which point to place them
        //Will only stop if the point is in a valid area
        if (wasPressed == true && !(arr1.size() == arr2.size())) {
            setIsValid(input.getMousePosition());
            if (arr1.get(arr1.size() - 1).equals("tank") && isValid) {
                tank.draw(input.getMouseX(), input.getMouseY());
            } else if (arr1.get(arr1.size() - 1).equals("supertank") && isValid) {
                supertank.draw(input.getMouseX(), input.getMouseY());
            } else if (arr1.get(arr1.size() - 1).equals("airplane") && isValid) {
                airplane.draw(input.getMouseX(), input.getMouseY());
            }

        }

        //If a player no longer wants to buy a tower
        if (input.wasPressed(MouseButtons.RIGHT)) {
            wasPressed = false;
            if (arr1.size() != arr2.size()) {
                arr1.remove(arr1.size() - 1);
            }
        }

        //Draws prices and sets colors
        if (money >= 600) {
            pricefont.drawString("$250", 30, 90, new DrawOptions().setBlendColour(Colour.GREEN));
            pricefont.drawString("$600", 130, 90, new DrawOptions().setBlendColour(Colour.GREEN));
            pricefont.drawString("$500", 230, 90, new DrawOptions().setBlendColour(Colour.GREEN));
        } else if (money >= 500) {
            pricefont.drawString("$250", 30, 90, new DrawOptions().setBlendColour(Colour.GREEN));
            pricefont.drawString("$600", 130, 90, new DrawOptions().setBlendColour(Colour.RED));
            pricefont.drawString("$500", 230, 90, new DrawOptions().setBlendColour(Colour.GREEN));
        } else if (money >= 250) {
            pricefont.drawString("$250", 30, 90, new DrawOptions().setBlendColour(Colour.GREEN));
            pricefont.drawString("$600", 130, 90, new DrawOptions().setBlendColour(Colour.RED));
            pricefont.drawString("$500", 230, 90, new DrawOptions().setBlendColour(Colour.RED));
        } else {
            pricefont.drawString("$250", 30, 90, new DrawOptions().setBlendColour(Colour.RED));
            pricefont.drawString("$600", 130, 90, new DrawOptions().setBlendColour(Colour.RED));
            pricefont.drawString("$500", 230, 90, new DrawOptions().setBlendColour(Colour.RED));
        }
        return money;
    }

    /**
     * Determines if current point is valid for placement of tower
     * Sets isValid accordingly
     */
    public void setIsValid(Point point) {
        //Check if point is within the area of an already placed tower
        for (int i = 0; i < arr2.size(); i++) {
            if (point == arr2.get(i).getCenter() || (((point.x > arr2.get(i).getCenter().x - (supertank.getWidth() / 2)) && (point.x < arr2.get(i).getCenter().x + (supertank.getWidth() / 2))) && ((point.y > arr2.get(i).getCenter().y - (supertank.getHeight() / 2)) && (point.y < arr2.get(i).getCenter().y + (supertank.getHeight()/2)))) ) {
                isValid = false;
                return;
            }
        }
        //Check if point is within HEIGHT and WIDTH of board, or if the tile has a blocked property
        if (point.x < 0 || point.x > 1024 || point.y < 0 || point.y > 768) {
            isValid = false;

        } else if (point.y > 0 && point.y < image.getHeight()) {
            isValid = false;

        }
        else if (map.hasProperty((int)point.x, (int)point.y, "blocked")) {
            isValid = false;
        } else {
            isValid = true;
        }
    }
    /**
     * Set arr1 and arr2 for use when a new level begins
     */
    public void setLists(List<String> a, List<Sprite> b) {
        this.arr1 = a;
        this.arr2 = b;
    }

    /**
     * Set money amount, for use when adding rewards
     * @param amount to set money to
     */

    public void setMoney(int amount) {
        money = amount;
    }

    public List<Sprite> getArray() {
        return arr2;
    }
    public List<String> getTowerArray() {
        return arr1;
    }


}
