package dayworkschedule;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class TaskDialog extends JDialog {
    private JTextField titleField;
    private JTextField noteField;
    private JTextField dateField; // yyyy-mm-dd
    private JTextField timeField; // HH:mm
    private JComboBox<String> priorityBox;
    private JCheckBox doneBox;
    private boolean saved = false;
    private Task task;

    public TaskDialog(Frame owner, Task task, boolean isNew) {
        super(owner, isNew ? "New Task" : "Edit Task", true);
        this.task = task;
        initUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        titleField = new JTextField(task.getTitle(), 30);
        noteField = new JTextField(task.getNote(), 30);
        dateField = new JTextField(task.getDate() != null ? task.getDate().toString() : "", 12);
        timeField = new JTextField(task.getTime() != null ? task.getTime().toString() : "", 6);
        priorityBox = new JComboBox<>(new String[] {"LOW","MEDIUM","HIGH"});
        priorityBox.setSelectedItem(task.getPriority().name());
        doneBox = new JCheckBox("Done", task.isDone());

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.WEST;
        p.add(new JLabel("Title:"), c);
        c.gridx = 1; p.add(titleField, c);

        c.gridx = 0; c.gridy++;
        p.add(new JLabel("Note:"), c);
        c.gridx = 1; p.add(noteField, c);

        c.gridx = 0; c.gridy++;
        p.add(new JLabel("Date (yyyy-MM-dd):"), c);
        c.gridx = 1; p.add(dateField, c);

        c.gridx = 0; c.gridy++;
        p.add(new JLabel("Time (HH:mm):"), c);
        c.gridx = 1; p.add(timeField, c);

        c.gridx = 0; c.gridy++;
        p.add(new JLabel("Priority:"), c);
        c.gridx = 1; p.add(priorityBox, c);

        c.gridx = 0; c.gridy++;
        p.add(new JLabel("Done:"), c);
        c.gridx = 1; p.add(doneBox, c);

        JPanel buttons = new JPanel();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        buttons.add(saveBtn);
        buttons.add(cancelBtn);

        saveBtn.addActionListener(ev -> {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title cannot be empty", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            task.setTitle(title);
            task.setNote(noteField.getText().trim());
            String d = dateField.getText().trim();
            if (d.isEmpty()) {
                task.setDate(null);
            } else {
                try {
                    task.setDate(LocalDate.parse(d));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Date format invalid. Use yyyy-MM-dd", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            String t = timeField.getText().trim();
            if (t.isEmpty()) {
                task.setTime(null);
            } else {
                try {
                    task.setTime(LocalTime.parse(t));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Time format invalid. Use HH:mm", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            task.setPriority(Task.Priority.valueOf((String)priorityBox.getSelectedItem()));
            task.setDone(doneBox.isSelected());
            saved = true;
            dispose();
        });
        cancelBtn.addActionListener(ev -> dispose());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(p, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    public boolean isSaved() { return saved; }
}
