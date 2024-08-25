import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileConversionApp extends JFrame {
    private final DefaultListModel<File> fileListModel;
    private final JList<File> fileList;
    private final JComboBox<String> conversionTypeComboBox;
    private final JProgressBar progressBar;
    private final JLabel statusBar;
    private final JButton startButton;
    private final JButton cancelButton;
    private ExecutorService executorService;

    public FileConversionApp() {
        // Set up the JFrame
        setTitle("File Conversion App");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // File List
        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(fileList);

        // Conversion Type ComboBox
        String[] conversionTypes = {"PDF to DOCX", "Image Resize"};
        conversionTypeComboBox = new JComboBox<>(conversionTypes);

        // Progress Bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        // Status Bar
        statusBar = new JLabel("Select files and choose a conversion type to begin.");

        // Buttons
        JButton addButton = new JButton("Add Files");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addFiles();
            }
        });

        startButton = new JButton("Start Conversion");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startConversion();
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelConversion();
            }
        });

        // Layout Setup
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(1, 4));
        controlPanel.add(addButton);
        controlPanel.add(conversionTypeComboBox);
        controlPanel.add(startButton);
        controlPanel.add(cancelButton);

        // Adding components to the JFrame
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.NORTH);
        add(progressBar, BorderLayout.SOUTH);
        add(statusBar, BorderLayout.PAGE_END);
    }

    private void addFiles() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF & Images", "pdf", "jpg", "png"));
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            for (File file : files) {
                fileListModel.addElement(file);
            }
            statusBar.setText("Added " + files.length + " files for conversion.");
        }
    }

    private void startConversion() {
        if (fileListModel.getSize() == 0) {
            JOptionPane.showMessageDialog(this, "No files selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        startButton.setEnabled(false);
        cancelButton.setEnabled(true);
        progressBar.setValue(0);
        executorService = Executors.newFixedThreadPool(4);

        for (int i = 0; i < fileListModel.getSize(); i++) {
            File file = fileListModel.getElementAt(i);
            String conversionType = (String) conversionTypeComboBox.getSelectedItem();
            FileConversionTask task = new FileConversionTask(file, conversionType);
            executorService.submit(task);
        }

        executorService.shutdown();
    }

    private void cancelConversion() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
            statusBar.setText("Conversion canceled.");
            startButton.setEnabled(true);
            cancelButton.setEnabled(false);
        }
    }

    private class FileConversionTask extends SwingWorker<Void, Integer> {
        private final File file;
        private final String conversionType;

        public FileConversionTask(File file, String conversionType) {
            this.file = file;
            this.conversionType = conversionType;
        }

        @Override
        protected Void doInBackground() {
            int progress = 0;
            while (progress < 100) {
                try {
                    Thread.sleep(100); // Simulate time-consuming conversion
                    progress += 10; // Increment progress
                    publish(progress); // Send progress updates to the process method
                } catch (InterruptedException e) {
                    return null; // Handle interruption
                }
            }

            // Simulate conversion completion and save the converted file
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Converted File");
                fileChooser.setSelectedFile(new File(file.getName().replaceAll("\\.[^.]*$", "") + "-converted." + getExtension()));

                int userSelection = fileChooser.showSaveDialog(FileConversionApp.this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File savedFile = fileChooser.getSelectedFile();
                    saveDummyFile(savedFile);
                } else {
                    cancel(true); // User canceled the save operation
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void process(List<Integer> chunks) {
            int latestProgress = chunks.get(chunks.size() - 1); // Get the most recent progress
            progressBar.setValue(latestProgress); // Update the progress bar
            statusBar.setText("Converting: " + file.getName() + " (" + conversionType + ") " + latestProgress + "%");
        }

        @Override
        protected void done() {
            if (!isCancelled()) {
                statusBar.setText("Completed: " + file.getName() + " (" + conversionType + ")");
            } else {
                statusBar.setText("Canceled: " + file.getName());
            }

            if (allTasksCompleted()) { // Check if all files are done
                startButton.setEnabled(true);
                cancelButton.setEnabled(false);
                JOptionPane.showMessageDialog(FileConversionApp.this, "All conversions are completed!");
            }
        }

        private boolean allTasksCompleted() {
            for (int i = 0; i < fileList.getModel().getSize(); i++) {
                if (!executorService.isTerminated()) {
                    return false;
                }
            }
            return true;
        }

        private String getExtension() {
            switch (conversionType) {
                case "PDF to DOCX":
                    return "docx";
                case "Image Resize":
                    return "jpg"; // Assuming image resize conversion
                default:
                    return "txt"; // Fallback extension
            }
        }

        private void saveDummyFile(File file) {
            try {
                if (file.createNewFile()) {
                    statusBar.setText("File saved: " + file.getAbsolutePath());
                } else {
                    statusBar.setText("Failed to save the file");
                }
            } catch (Exception e) {
                e.printStackTrace();
                statusBar.setText("Error saving the file");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FileConversionApp app = new FileConversionApp();
                app.setVisible(true);
            }
        });
    }
}
