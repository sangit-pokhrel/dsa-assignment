import javax.swing.*; // Import Swing components for the GUI
import java.awt.*;    // Import AWT components for layout management
import java.awt.event.ActionEvent; // Import ActionEvent for handling button actions
import java.awt.event.ActionListener; // Import ActionListener for handling button clicks
import java.io.File; // Import File for file handling
import java.util.List; // Import List for managing multiple files
import java.util.ArrayList; // Import ArrayList for storing files
import java.util.concurrent.ExecutionException; // Import ExecutionException for handling thread exceptions

public class FileConversionApp {

    // GUI components
    private JFrame frame; // Main window of the application
    private JButton selectFilesButton; // Button to select files for conversion
    private JButton startConversionButton; // Button to start the conversion process
    private JButton cancelButton; // Button to cancel the conversion
    private JProgressBar overallProgressBar; // Progress bar for overall conversion progress
    private JTextArea statusArea; // Text area to display status messages
    private JFileChooser fileChooser; // File chooser for selecting multiple files

    private List<File> filesToConvert; // List to hold files selected for conversion
    private FileConversionTask conversionTask; // Task to manage the file conversion process

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileConversionApp().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("File Conversion Tool"); // Create the main application window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure the application exits on close
        frame.setSize(600, 400); // Set the size of the main window

        // Create and configure the panel for file selection and control buttons
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(0, 1)); // Use a vertical layout for buttons and progress bars

        // Select Files Button
        selectFilesButton = new JButton("Select Files"); // Create a button for selecting files
        selectFilesButton.addActionListener(new SelectFilesButtonListener()); // Add action listener to handle button clicks
        controlPanel.add(selectFilesButton); // Add the button to the control panel

        // Start Conversion Button
        startConversionButton = new JButton("Start Conversion"); // Create a button to start the conversion
        startConversionButton.addActionListener(new StartConversionButtonListener()); // Add action listener
        controlPanel.add(startConversionButton); // Add the button to the control panel

        // Cancel Button
        cancelButton = new JButton("Cancel"); // Create a button to cancel the conversion
        cancelButton.addActionListener(new CancelButtonListener()); // Add action listener
        controlPanel.add(cancelButton); // Add the button to the control panel

        // Overall Progress Bar
        overallProgressBar = new JProgressBar(); // Create a progress bar for overall progress
        overallProgressBar.setStringPainted(true); // Show percentage on the progress bar
        controlPanel.add(overallProgressBar); // Add the progress bar to the control panel

        // Status Area
        statusArea = new JTextArea(10, 50); // Create a text area for displaying status messages
        statusArea.setEditable(false); // Make the text area read-only
        JScrollPane statusScrollPane = new JScrollPane(statusArea); // Add a scroll pane for the text area
        controlPanel.add(statusScrollPane); // Add the scroll pane to the control panel

        // Add the control panel to the main frame
        frame.getContentPane().add(controlPanel, BorderLayout.CENTER); // Add control panel to the center of the frame

        // Initialize the file chooser
        fileChooser = new JFileChooser(); // Create a file chooser for selecting files
        fileChooser.setMultiSelectionEnabled(true); // Enable multi-selection

        // Make the frame visible
        frame.setVisible(true); // Show the main window
    }

    private class SelectFilesButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Show the file chooser dialog and retrieve selected files
            int returnValue = fileChooser.showOpenDialog(frame); // Display file chooser dialog
            if (returnValue == JFileChooser.APPROVE_OPTION) { // Check if files were selected
                File[] selectedFiles = fileChooser.getSelectedFiles(); // Get the selected files
                filesToConvert = new ArrayList<>(List.of(selectedFiles)); // Store the selected files
                statusArea.append("Selected files:\n"); // Update status area
                for (File file : filesToConvert) {
                    statusArea.append(file.getName() + "\n"); // Display selected file names
                }
            }
        }
    }

    private class StartConversionButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (filesToConvert == null || filesToConvert.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No files selected for conversion.", "Error", JOptionPane.ERROR_MESSAGE); // Show error if no files selected
                return;
            }
            // Initialize and start the file conversion task
            conversionTask = new FileConversionTask(filesToConvert);
            conversionTask.execute(); // Start the asynchronous task
        }
    }

    private class CancelButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (conversionTask != null) {
                conversionTask.cancel(true); // Cancel the ongoing task
                statusArea.append("Conversion cancelled.\n"); // Update status area
            }
        }
    }

    // SwingWorker class for performing file conversions asynchronously
    private class FileConversionTask extends SwingWorker<Void, String> {
        private final List<File> files; // List of files to be converted

        public FileConversionTask(List<File> files) {
            this.files = files; // Initialize with files to be converted
        }

        @Override
        protected Void doInBackground() throws Exception {
            int totalFiles = files.size(); // Total number of files
            int fileCount = 0; // Counter for processed files

            // Iterate over each file and perform conversion
            for (File file : files) {
                if (isCancelled()) break; // Check if the task is cancelled

                // Simulate file conversion
                for (int i = 0; i <= 100; i += 10) {
                    if (isCancelled()) break; // Check if the task is cancelled
                    Thread.sleep(100); // Simulate processing time
                    publish(String.format("Converting %s: %d%% complete", file.getName(), i)); // Publish progress update
                }
                fileCount++; // Increment file counter
                setProgress((int) (fileCount / (float) totalFiles * 100)); // Update overall progress
            }
            return null; // Return null as there's no result to return
        }

        @Override
        protected void process(List<String> chunks) {
            for (String status : chunks) {
                statusArea.append(status + "\n"); // Update status area with progress
            }
        }

        @Override
        protected void done() {
            try {
                get(); // Retrieve result of the task (if any exception occurred)
                statusArea.append("All files converted successfully.\n"); // Notify completion
            } catch (InterruptedException | ExecutionException e) {
                statusArea.append("Conversion failed: " + e.getMessage() + "\n"); // Handle exceptions
            }
        }
    }
}
