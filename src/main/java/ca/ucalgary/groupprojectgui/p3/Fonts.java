package ca.ucalgary.groupprojectgui.p3;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.Objects;

public class Fonts {
    static {

        String[] fontsToLoad = {
                "/ca/ucalgary/groupprojectgui/p3/fonts/Orbitron/Orbitron-Bold.ttf",
                "/ca/ucalgary/groupprojectgui/p3/fonts/Orbitron/Orbitron-Medium.ttf",
                "/ca/ucalgary/groupprojectgui/p3/fonts/Rajdhani/Rajdhani-Regular.ttf",
                "/ca/ucalgary/groupprojectgui/p3/fonts/Rajdhani/Rajdhani-Bold.ttf",
                "/ca/ucalgary/groupprojectgui/p3/fonts/Rajdhani/Rajdhani-semiBold.ttf",
        };

        for (String path : fontsToLoad) {
            try {
                Font font = Font.loadFont(
                        Objects.requireNonNull(Fonts.class.getResourceAsStream(path)),
                        12
                );
            } catch (Exception e) {
                System.err.println("Error loading " + path + ": " + e.getMessage());
            }
        }

    }

    public static Font orbitron(double size) {
        return Font.font("Orbitron", size);
    }

    public static Font orbitron(FontWeight weight, double size) {
        return Font.font("Orbitron", weight, size);
    }

    // Rajdhani font getters
    public static Font rajdhaniRegular(double size) {
        return Font.font("Rajdhani", FontWeight.NORMAL, size);
    }

    public static Font rajdhaniMedium(double size) {
        return Font.font("Rajdhani", FontWeight.MEDIUM, size);
    }

    public static Font rajdhaniSemiBold(double size) {
        return Font.font("Rajdhani", FontWeight.SEMI_BOLD, size);
    }

    public static Font rajdhaniBold(double size) {
        return Font.font("Rajdhani", FontWeight.BOLD, size);
    }
    public static Font rajdhani(FontWeight weight, double size) {
        return Font.font("Rajdhani", weight, size);
    }
}