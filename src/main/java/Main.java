import ui.TelaInicial;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new TelaInicial();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}