package songsong.playmusics;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlayerController {

    @FXML private Button PlayandPause, previous, later, repeat, Closed, Minimized;
    @FXML private ListView<String> MusicList;
    @FXML private Slider slider;
    @FXML private MediaView mediaView;
    @FXML private Text timeSong;
    @FXML private Text Title;  // <-- aqui adiciona o campo Title

    private List<File> songs = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private int songIndex = 0;

    private Image playIcon;
    private Image pauseIcon;
    private ImageView playPauseImageView;

    @FXML
    public void initialize() {
        try {
            URL musicURL = getClass().getResource("/music");
            if (musicURL == null) return;
            File musicDir = new File(musicURL.toURI());
            if (musicDir.exists() && musicDir.isDirectory()) {
                for (File file : musicDir.listFiles()) {
                    if (file.getName().endsWith(".mp3")) {
                        songs.add(file);
                        MusicList.getItems().add(file.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        playIcon = new Image(getClass().getResourceAsStream("/icons/play.png"));
        pauseIcon = new Image(getClass().getResourceAsStream("/icons/pause.png"));
        playPauseImageView = (ImageView) PlayandPause.getGraphic();

        if (!songs.isEmpty()) {
            playSong(songIndex);
        }

        PlayandPause.setOnAction(e -> {
            if (mediaPlayer == null) return;
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playPauseImageView.setImage(playIcon);
            } else {
                mediaPlayer.play();
                playPauseImageView.setImage(pauseIcon);
            }
        });

        previous.setOnAction(e -> {
            songIndex = (songIndex - 1 + songs.size()) % songs.size();
            playSong(songIndex);
        });

        later.setOnAction(e -> {
            songIndex = (songIndex + 1) % songs.size();
            playSong(songIndex);
        });

        repeat.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(mediaPlayer.getStartTime());
                mediaPlayer.play();
                playPauseImageView.setImage(pauseIcon);
            }
        });

        MusicList.setOnMouseClicked(e -> {
            int selectedIndex = MusicList.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                songIndex = selectedIndex;
                playSong(songIndex);
            }
        });

        Closed.setOnAction(e -> {
            Stage stage = (Stage) Closed.getScene().getWindow();
            stage.close();
        });

        Minimized.setOnAction(e -> {
            Stage stage = (Stage) Minimized.getScene().getWindow();
            stage.setIconified(true);
        });
    }

    private void playSong(int index) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        File currentSong = songs.get(index);
        Media media = new Media(currentSong.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        // Atualiza o título da música aqui
        Title.setText(currentSong.getName());

        mediaPlayer.play();
        playPauseImageView.setImage(pauseIcon);

        mediaPlayer.setOnReady(() -> {
            slider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            updateTimer(mediaPlayer.getCurrentTime(), mediaPlayer.getTotalDuration());
        });

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!slider.isValueChanging()) {
                slider.setValue(newTime.toSeconds());
            }
            updateTimer(newTime, mediaPlayer.getTotalDuration());
        });

        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (slider.isValueChanging()) {
                mediaPlayer.seek(Duration.seconds(newVal.doubleValue()));
            }
        });
    }

    private void updateTimer(Duration current, Duration total) {
        int currentSeconds = (int) current.toSeconds();
        int totalSeconds = (int) total.toSeconds();

        String currentTimeStr = String.format("%02d:%02d", currentSeconds / 60, currentSeconds % 60);
        String totalTimeStr = totalSeconds > 0 ? String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60) : "00:00";

        timeSong.setText(currentTimeStr + " / " + totalTimeStr);
    }
}
