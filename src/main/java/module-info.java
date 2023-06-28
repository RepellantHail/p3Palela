module com.example.p3proyectoparalela {
    requires javafx.controls;
    requires javafx.fxml;
            
            requires com.dlsc.formsfx;
                        
    opens com.example.p3proyectoparalela to javafx.fxml;
    exports com.example.p3proyectoparalela;
}