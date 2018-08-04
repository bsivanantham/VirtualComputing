package de.unibi.citec.clf;
/**
 * Hello world!
 *
 */
public class App 
{
    @Override
    public void start(Stage stage) throws Exception {
        
        BorderPane root = new BorderPane();
        
        Scene scene = new Scene(root,640,480);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ui.fxml"));
        scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());

        loader.setControllerFactory(t -> new EditorController(new EditorModel()));
        
        stage.setScene(new Scene(loader.load()));
        
        stage.show();
        
        root.setCenter(new RootLayout());
    }
    
    public static void main(String[] args){
        launch(args);
    }
}
