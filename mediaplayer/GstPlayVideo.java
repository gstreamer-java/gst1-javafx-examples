package mediaplayer;

import java.nio.file.Paths;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GstPlayVideo extends Application {

	
	private Scene scene;	

	private GstMediaPlayer mpMain;

	public static void main(String[] args) {
		System.out.println("STart v08:48+ GstPlayVideo");
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
	
		String video = getParameters().getRaw().get(0);		
		System.out.println("Playing " + Paths.get("", video).toUri());

		mpMain = new GstMediaPlayer(Paths.get("", video).toUri());
	    
	    StackPane ap = new StackPane(mpMain);

		scene = new Scene(ap);
		
        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
        		System.out.println("STOP");
            	Platform.exit();
            }
        }); 
        
		primaryStage.setScene(scene);
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setFullScreen(true);
		primaryStage.show();
		mpMain.play();
	}

}
