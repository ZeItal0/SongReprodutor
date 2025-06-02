module songsong.playmusics {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.controlsfx.controls;

    opens songsong.playmusics to javafx.fxml;
    exports songsong.playmusics;
}