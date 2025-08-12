import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Advanced Java 17+ To-Do List App
 * Features:
 * - Add, Remove, Schedule task
 * - Popup notification for scheduled reminders
 * - Search/filter bar
 * - Persistent storage
 * - Mark task done (double-click ✔)
 */
public class ToDoApp extends JFrame {

    // Task with optional time and completion marker
    public static class Task {
        private final String text;
        private final LocalTime time; // may be null
        private final boolean completed;

        public Task(String text, LocalTime time, boolean completed) {
            this.text = text;
            this.time = time;
            this.completed = completed;
        }

        public String toDisplayString() {
            String base = (completed ? "✔ " : "") + text;
            return time != null
                    ? base + " [" + time.format(DateTimeFormatter.ofPattern("HH:mm")) + "]"
                    : base;
        }

        // Save as: completed\ttext\ttime
        public String serialize() {
            return (completed ? "1" : "0") + "\t"
                    + text.replace("\t", "  ") + "\t"
                    + (time != null ? time.toString() : "");
        }

        public static Task deserialize(String s) {
            var parts = s.split("\t", -1);
            String txt = parts.length > 1 ? parts[1] : "";
            LocalTime t = null;
            if (parts.length > 2 && !parts[2].isBlank()) {
                try { t = LocalTime.parse(parts[2]); } catch (Exception ignored) {}
            }
            boolean c = parts.length > 0 && parts[0].equals("1");
            return new Task(txt, t, c);
        }
    }

    private final DefaultListModel<Task> taskListModel = new DefaultListModel<>();
    private final JList<Task> taskList = new JList<>(taskListModel);
    private final JTextField taskField = new JTextField();
    private final JTextField searchField = new JTextField();
    private final JButton addButton = new JButton("➕ Add");
    private final JButton removeButton = new JButton("🗑 Remove");
    private final JButton scheduleButton = new JButton("⏰ Schedule");
    private final Path saveFile = Path.of("tasks.txt");
    private java.util.Timer reminderTimer;

    public ToDoApp() {
        super("📝 Advanced To-Do List");
        setSize(520, 540);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        // === Top: Search Bar
        var searchPanel = new JPanel(new BorderLayout(5, 5));
        searchField.setToolTipText("Search tasks...");
        searchPanel.add(new JLabel("🔍"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);

        // === Center: Tasks
        taskList.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Renderer: show strikethrough for completed
        taskList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.toDisplayString());
            label.setFont(list.getFont());
            if (value.completed) {
                Map<TextAttribute, Object> fontAttrs = new HashMap<>(label.getFont().getAttributes());
                fontAttrs.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                label.setFont(label.getFont().deriveFont(fontAttrs));
                label.setForeground(Color.GRAY);
            }
            if (isSelected) {
                label.setOpaque(true);
                label.setBackground(new Color(0xD7EAF8));
            }
            return label;
        });
        JScrollPane scrollPane = new JScrollPane(taskList);
        add(scrollPane, BorderLayout.CENTER);

        // === Bottom: Input & Buttons (two rows)
        var inputPanel = new JPanel(new BorderLayout(5, 5));
        taskField.setToolTipText("Enter a new task...");
        inputPanel.add(taskField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        var actionPanel = new JPanel();
        actionPanel.add(removeButton);
        actionPanel.add(scheduleButton);

        var southPanel = new JPanel();
        southPanel.setLayout(new GridLayout(2, 1, 5, 5));
        southPanel.add(inputPanel);
        southPanel.add(actionPanel);
        add(southPanel, BorderLayout.SOUTH);

        // === Event listeners
        addButton.addActionListener(e -> addTaskNow());
        taskField.addActionListener(e -> addTaskNow());
        removeButton.addActionListener(e -> removeTask());
        scheduleButton.addActionListener(e -> showScheduleDialog());

        taskList.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) removeTask();
            }
        });

        // Double-click = toggle complete
        taskList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) toggleTaskCompletion();
            }
        });

        // Real-time search
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTasks(); }
            public void removeUpdate(DocumentEvent e) { filterTasks(); }
            public void changedUpdate(DocumentEvent e) { filterTasks(); }
        });

        // === Load tasks & start notifications
        loadTasks();
        startReminderTimer();
        setVisible(true);
    }

    private void addTaskNow() {
        var text = taskField.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a task.");
            return;
        }
        taskListModel.addElement(new Task(text, null, false));
        taskField.setText("");
        saveTasks();
    }

    private void showScheduleDialog() {
        String task = JOptionPane.showInputDialog(this, "Task name:", "Schedule Task", JOptionPane.PLAIN_MESSAGE);
        if (task == null || task.isBlank()) return;

        String timeStr = JOptionPane.showInputDialog(
                this, "Enter time (HH:mm):", "e.g. 18:00", JOptionPane.PLAIN_MESSAGE);
        if (timeStr == null || timeStr.isBlank()) return;
        try {
            LocalTime t = LocalTime.parse(timeStr.trim(), DateTimeFormatter.ofPattern("H:mm"));
            taskListModel.addElement(new Task(task.trim(), t, false));
            saveTasks();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid time format! Use HH:mm (e.g. 18:00)");
        }
    }

    private void removeTask() {
        int idx = taskList.getSelectedIndex();
        if (idx != -1) {
            taskListModel.remove(idx);
            saveTasks();
        } else {
            JOptionPane.showMessageDialog(this, "Select a task to remove.");
        }
    }

    private void toggleTaskCompletion() {
        int idx = taskList.getSelectedIndex();
        if (idx != -1) {
            Task task = taskListModel.get(idx);
            taskListModel.set(idx, new Task(task.text, task.time, !task.completed));
            saveTasks();
        }
    }

    private void filterTasks() {
        var query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            reloadListFromSave();
            return;
        }
        try {
            List<String> lines = Files.exists(saveFile) ? Files.readAllLines(saveFile) : List.of();
            taskListModel.clear();
            lines.stream()
                    .map(Task::deserialize)
                    .filter(t -> t.text.toLowerCase().contains(query))
                    .forEach(taskListModel::addElement);
        } catch (IOException ignored) {}
    }

    private void saveTasks() {
        try (var w = Files.newBufferedWriter(saveFile)) {
            for (int i = 0; i < taskListModel.size(); i++)
                w.write(taskListModel.get(i).serialize() + System.lineSeparator());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving tasks: " + e.getMessage());
        }
    }

    private void loadTasks() {
        if (!Files.exists(saveFile)) return;
        taskListModel.clear();
        try {
            Files.readAllLines(saveFile).stream()
                    .filter(line -> !line.isBlank())
                    .map(Task::deserialize)
                    .forEach(taskListModel::addElement);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading tasks: " + e.getMessage());
        }
    }

    private void reloadListFromSave() {
        loadTasks();
    }

    /** Check every 30s for any tasks with scheduled time == now, and notify if found */
    private void startReminderTimer() {
        if (reminderTimer != null) reminderTimer.cancel();
        reminderTimer = new java.util.Timer(true);
        reminderTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                LocalTime now = LocalTime.now().withSecond(0).withNano(0);
                for (int i = 0; i < taskListModel.size(); i++) {
                    Task t = taskListModel.get(i);
                    if (t.time != null && !t.completed && t.time.equals(now)) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(ToDoApp.this,
                                    "⏰ Reminder: " + t.text + (t.time != null ? " at " + t.time : ""),
                                    "Task Reminder", JOptionPane.INFORMATION_MESSAGE);
                        });
                    }
                }
            }
        }, 0, 30_000); // check every 30 seconds
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoApp::new);
    }
}
