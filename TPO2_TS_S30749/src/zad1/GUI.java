package zad1;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GUI extends JFrame {

    private JLabel Title;
    private JPanel MainPanel;
    private JLabel WeatherLabel;
    private JLabel Rate1Label;
    private JLabel Rate2Label;
    private JPanel TextBoxPanel;
    private JTextField countryTextField;
    private JTextField cityTextField;
    private JTextField currencyTextField;
    private JButton submitButton;
    private JPanel wikiPanel;
    private JLabel TextFieldLabel;
    private JFormattedTextField formattedTextField1;
    private JLabel Image;
    private Service service;

    public GUI(Service s, String weatherJson, Double rate1, Double rate2) {
        setContentPane(MainPanel);
        setTitle("GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
        this.service = s;

        AddLabels(weatherJson, rate1, rate2);
        WikiPanel(service.getCity());
        addSubmitListener(weatherJson, rate1, rate2);
    }

    public void AddLabels(String weatherJson, Double rate1, Double rate2) {
        Title.setText(String.format("Showing data for: %s, %s, %s", service.getKraj(), service.getCity(), service.getWaluta()));
        Title.setFont(new Font("Serif", Font.PLAIN, 30));
        String formattedWeather = service.formatWeather(weatherJson).replace("\n", "<br>");
        WeatherLabel.setText("<html>" + formattedWeather + "</html>");
        WeatherLabel.setMinimumSize(new Dimension(300, 200));
        Rate1Label.setText("Exchange rate: 1 " + service.getCodeFromCuntry(service.getKraj()) + " = " + rate1 + " " + service.getWaluta());
        Rate2Label.setText("NBP rate: 1 "+ service.getCodeFromCuntry(service.getKraj()) +"=" + rate2 + " PLN");
        Image.setMinimumSize(new Dimension(100, 100));
        Image.setIcon(new ImageIcon("lib/polandmountain.png"));
    }



    public void WikiPanel(String city) {
        wikiPanel.removeAll();

        JFXPanel jfxPanel = new JFXPanel();
        wikiPanel.setLayout(new BorderLayout());
        wikiPanel.add(jfxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();
            webEngine.load("https://en.wikipedia.org/wiki/" + city.replace(" ", "_"));

            Scene scene = new Scene(webView);
            jfxPanel.setScene(scene);
        });

        wikiPanel.revalidate();
        wikiPanel.repaint();
    }

    private void addSubmitListener(String weatherJson, Double rate1, Double rate2) {
        submitButton.addActionListener(e -> {

            String kraj = countryTextField.getText().trim().isEmpty() ? service.getKraj() : countryTextField.getText().trim();
            String miasto = cityTextField.getText().trim().isEmpty() ? service.getCity() : cityTextField.getText().trim();
            String waluta = currencyTextField.getText().trim().isEmpty() ? service.getWaluta() : currencyTextField.getText().trim();

            service.setKraj(kraj);
            service.setCity(miasto);
            service.setWaluta(waluta);

            String newWeatherJson = weatherJson;
            Double newRate1 = rate1;
            Double newRate2 = rate2;

            try {
                newWeatherJson = service.getWeather(miasto);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Błąd pobierania pogody: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }

            try {
                newRate1 = service.getRateFor(waluta);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Błąd pobierania kursu waluty: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }

            try {
                newRate2 = service.getNBPRate();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Błąd pobierania kursu NBP: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }

            AddLabels(newWeatherJson, newRate1, newRate2);
            WikiPanel(miasto);
        });
    }

}
