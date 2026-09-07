import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import kiwi.Kiwi;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    private static final double EXIT_DELAY_SECONDS = 1;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Kiwi kiwi;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image kiwiImage = new Image(this.getClass().getResourceAsStream("/images/DaKiwi.png"));

    /** Binds the dialog list so it stays scrolled to the latest message. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Kiwi instance. */
    public void setKiwi(Kiwi kiwi) {
        this.kiwi = kiwi;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Kiwi's reply, then appends them to
     * the dialog container. Clears the user input after processing.
     * Closes the window after {@code bye}, once the goodbye message has been shown.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = kiwi.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getKiwiDialog(response, kiwiImage)
        );
        userInput.clear();

        if (kiwi.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
