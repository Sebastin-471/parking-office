package com.parkingoffice;

import com.parkingoffice.repository.HibernateUtil;
import com.parkingoffice.service.AuthService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Initialize Hibernate to ensure DB connection is ready
        HibernateUtil.getSessionFactory();
        
        // Ensure default admin exists
        new AuthService().createDefaultAdminIfNotExists();

        scene = new Scene(loadFXML("login"), 640, 480);
        stage.setScene(scene);
        stage.setTitle("Parking Office System");
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        HibernateUtil.shutdown();
        super.stop();
    }
}
