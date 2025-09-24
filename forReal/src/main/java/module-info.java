module za.ac.cput.forreal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires javafx.web; 
    requires java.sql;
    requires jakarta.mail;

    opens za.ac.cput.forreal to javafx.fxml;
    exports za.ac.cput.forreal;
    requires javafx.webEmpty;
}
