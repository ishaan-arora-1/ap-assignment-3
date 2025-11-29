// File: FleetSimulatorGUI.java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FleetSimulatorGUI extends JFrame {
    private List<Vehicle> vehicles;
    private List<Thread> threads;
    
    // UI Components
    private List<VehiclePanel> vehiclePanels; 
    private JLabel totalDistanceLabel;
    private JPanel headerPanel;
    private Timer guiUpdateTimer;

    // Colors
    private final Color COLOR_BG = new Color(245, 245, 250);
    private final Color COLOR_HEADER_OK = new Color(50, 60, 80);
    private final Color COLOR_HEADER_ERROR = new Color(200, 60, 60);

    public FleetSimulatorGUI() {
        // Apply System Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        setTitle("Fleet Highway Simulator (Assignment 3)");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_BG);

        // Initialize lists
        vehicles = new ArrayList<>();
        threads = new ArrayList<>();
        vehiclePanels = new ArrayList<>();

        // --- 1. Create Vehicles ---
        createVehicle("Vehicle-1 (Truck)");
        createVehicle("Vehicle-2 (Car)");
        createVehicle("Vehicle-3 (Bike)");

        // --- 2. Top Header (Shared Counter) ---
        headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        headerPanel.setBackground(COLOR_HEADER_OK);
        
        totalDistanceLabel = new JLabel("Shared Highway Distance: 0 km");
        totalDistanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalDistanceLabel.setForeground(Color.WHITE);
        headerPanel.add(totalDistanceLabel);
        
        add(headerPanel, BorderLayout.NORTH);

        // --- 3. Center Panel (Vehicle List) ---
        JPanel vehicleListPanel = new JPanel();
        vehicleListPanel.setLayout(new BoxLayout(vehicleListPanel, BoxLayout.Y_AXIS));
        vehicleListPanel.setBackground(COLOR_BG);
        vehicleListPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        for (VehiclePanel vp : vehiclePanels) {
            vehicleListPanel.add(vp);
            vehicleListPanel.add(Box.createVerticalStrut(15)); // Gap between cards
        }
        add(new JScrollPane(vehicleListPanel), BorderLayout.CENTER);

        // --- 4. Bottom Panel (Controls) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        JButton btnStart = createStyledButton("Start Simulation");
        JButton btnPause = createStyledButton("Pause");
        JButton btnStop = createStyledButton("Stop");
        JButton btnRefuel = createStyledButton("Refuel All (+10)");

        btnStart.addActionListener(e -> vehicles.forEach(Vehicle::start));
        btnPause.addActionListener(e -> vehicles.forEach(Vehicle::pause));
        btnStop.addActionListener(e -> vehicles.forEach(Vehicle::stop));
        btnRefuel.addActionListener(e -> vehicles.forEach(v -> v.refuel(10)));
        
        // Make Stop button red for visibility
        btnStop.setForeground(Color.RED);

        bottomPanel.add(btnStart);
        bottomPanel.add(btnPause);
        bottomPanel.add(btnStop);
        bottomPanel.add(btnRefuel);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- 5. Start Threads ---
        for (Vehicle v : vehicles) {
            Thread t = new Thread(v);
            threads.add(t);
            t.start();
        }

        // --- 6. GUI Timer ---
        guiUpdateTimer = new Timer(50, e -> updateDisplay());
        guiUpdateTimer.start();
    }

    private void createVehicle(String name) {
        Vehicle v = new Vehicle(name, 15);
        vehicles.add(v);
        VehiclePanel vp = new VehiclePanel(name);
        vehiclePanels.add(vp);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(Color.WHITE);
        return btn;
    }

    private void updateDisplay() {
        totalDistanceLabel.setText("Shared Highway Distance: " + Highway.highwayDistance + " km");

        int sumOfIndividual = 0;
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle v = vehicles.get(i);
            vehiclePanels.get(i).update(v);
            sumOfIndividual += v.getMileage();
        }

        // Visual Alert for Race Condition
        if (sumOfIndividual != Highway.highwayDistance) {
            headerPanel.setBackground(COLOR_HEADER_ERROR);
            totalDistanceLabel.setText("SYNC ERROR: Shared (" + Highway.highwayDistance + 
                                     ") != Sum (" + sumOfIndividual + ")");
        } else {
            headerPanel.setBackground(COLOR_HEADER_OK);
        }
    }

    /**
     * Inner class for Vehicle Cards
     */
    private class VehiclePanel extends JPanel {
        private JLabel nameLbl;
        private JLabel statusLbl;
        private JLabel fuelTextLbl; // NEW: Separate label for fuel text
        private JLabel mileageLbl;
        private JProgressBar fuelBar;

        public VehiclePanel(String name) {
            setLayout(new BorderLayout(15, 0));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 15, 10, 15)
            ));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 85)); 

            // Left: Name
            nameLbl = new JLabel(name);
            nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            nameLbl.setPreferredSize(new Dimension(150, 40)); // Fixed width for alignment
            add(nameLbl, BorderLayout.WEST);

            // Center: Status/Fuel Info + Bar
            JPanel center = new JPanel(new GridLayout(2, 1, 0, 5));
            center.setBackground(Color.WHITE);
            
            // Row 1: Status (Left) and Fuel Text (Right)
            JPanel infoRow = new JPanel(new BorderLayout());
            infoRow.setBackground(Color.WHITE);
            
            statusLbl = new JLabel("Status: Stopped");
            statusLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            fuelTextLbl = new JLabel("Fuel: 15"); // Text is here now
            fuelTextLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            infoRow.add(statusLbl, BorderLayout.WEST);
            infoRow.add(fuelTextLbl, BorderLayout.EAST);
            
            // Row 2: The Bar (No Text)
            fuelBar = new JProgressBar(0, 20);
            fuelBar.setValue(15);
            fuelBar.setStringPainted(false); // REMOVED OVERLAP
            fuelBar.setForeground(new Color(46, 204, 113)); // Green
            fuelBar.setPreferredSize(new Dimension(100, 10)); // Thinner, cleaner bar
            
            center.add(infoRow);
            center.add(fuelBar);
            add(center, BorderLayout.CENTER);

            // Right: Mileage
            mileageLbl = new JLabel("000 km");
            mileageLbl.setFont(new Font("Monospaced", Font.BOLD, 20));
            mileageLbl.setForeground(Color.DARK_GRAY);
            add(mileageLbl, BorderLayout.EAST);
        }

        public void update(Vehicle v) {
            // Update Fuel Text and Bar separately
            fuelTextLbl.setText("Fuel: " + v.getFuel());
            fuelBar.setValue(v.getFuel());
            
            if (v.getFuel() <= 3) {
                fuelBar.setForeground(Color.RED);
                fuelTextLbl.setForeground(Color.RED);
            } else if (v.getFuel() <= 7) {
                fuelBar.setForeground(Color.ORANGE);
                fuelTextLbl.setForeground(Color.DARK_GRAY);
            } else {
                fuelBar.setForeground(new Color(46, 204, 113));
                fuelTextLbl.setForeground(Color.DARK_GRAY);
            }

            // Update Mileage
            mileageLbl.setText(String.format("%03d km", v.getMileage()));

            // Update Status
            String s = v.getStatus();
            statusLbl.setText("Status: " + s);
            if (s.equals("Running")) statusLbl.setForeground(new Color(0, 150, 0));
            else if (s.equals("Out-of-Fuel")) statusLbl.setForeground(Color.RED);
            else statusLbl.setForeground(Color.GRAY);
        }
    }
}