package app;

import view.MainCLI;

public class Main {

    // Compliant: Instance main method con firma corretta accettata da SonarCloud
    void main(String[] args) {
        MainCLI app = new MainCLI();
        app.avviaApp();
    }
}