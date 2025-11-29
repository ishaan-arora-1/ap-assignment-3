// File: FleetSimulatorMain.java

import javax.swing.SwingUtilities;

public class FleetSimulatorMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FleetSimulatorGUI gui = new FleetSimulatorGUI();
            gui.setVisible(true);
        });
    }
}

