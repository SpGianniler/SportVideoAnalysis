package gr.ihu.ict.sportvideoanalysis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Label loginMessageLabel;

    @FXML
    private Button loginButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField enterPasswordField;
    @FXML
    private ImageView brandingImageView;
    @FXML
    private ImageView lockImageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        File brandingFile = new File("src/main/resources/images/dp_chibi.png");
        Image brandingImage = new Image(brandingFile.toURI().toString());
        brandingImageView.setImage(brandingImage);

        File lockFile = new File("src/main/resources/images/lock.png");
        Image lockImage = new Image(lockFile.toURI().toString());
        lockImageView.setImage(lockImage);
    }

    public void loginButtonOnAction(){
        if (!usernameTextField.getText().isBlank() || !enterPasswordField.getText().isBlank()){
            if (validateLogin()){
                loginMessageLabel.setText("logging you in...");
                createVideoScreen();
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.close();
            }else{loginMessageLabel.setText("Wrong username and/or password! \nPlease try again!");}
        }else {
            loginMessageLabel.setText("Please enter username and/or password");
        }
    }

    public void cancelButtonOnAction(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public boolean validateLogin(){
        return usernameTextField.getText().equals("admin") && enterPasswordField.getText().equals("admin");
    }

    public void createVideoScreen(){
        try{
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("videoScreenView.fxml")));
            Stage videoScreenStage = new Stage();
            videoScreenStage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);

            videoScreenStage.setScene(scene);
            scene.setFill(Color.TRANSPARENT);
            videoScreenStage.show();
        }catch(Exception e){
            Main.handleException(e);
            e.getCause();
        }
    }
}
