package pulbot;

import javafx.application.Application;

/** Launches Pulbot while avoiding JavaFX classpath issues. */
public class Launcher {
    /** Starts the JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
