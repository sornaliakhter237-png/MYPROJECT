package dayworkschedule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class Main {
    private JFrame frame;
    private JTable table;
    private TaskTableModel model;
    private List<Task> tasks;

    public Main() {
        tasks = TaskStorage.load();
        model = new TaskTableModel(tasks);
        initUI();
    }

    private void initUI() {
        frame = new JFrame("Day Work Schedule");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);

        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(table);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        JButton exportBtn = new JButton("Export (CSV)");
        JButton importBtn = new JButton("Import (CSV)");
        JComboBox<String> filterBox = new JComboBox<>(new String[] {"All", "Today", "Pending", "Done", "High Priority"});

        topPanel.add(addBtn);
        topPanel.add(editBtn);
        topPanel.add(delBtn);
        topPanel.add(new JLabel("   "));
        topPanel.add(filterBox);
        topPanel.add(new JLabel("   "));
        topPanel.add(exportBtn);
        topPanel.add(importBtn);

        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        delBtn.addActionListener(e -> onDelete());
        exportBtn.addActionListener(e -> onExport());
        importBtn.addActionListener(e -> onImport());
        filterBox.addActionListener(e -> onFilterChanged(filterBox));

        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(sp, BorderLayout.CENTER);

        // Save on close
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                TaskStorage.save(tasks);
            }
        });

        frame.setVisible(true);
    }

    private void onAdd() {
        Task t = new Task();
        TaskDialog dlg = new TaskDialog(frame, t, true);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            tasks.add(t);
            model.fireTableDataChanged();
            TaskStorage.save(tasks);
        }
    }

    private void onEdit() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a task to edit.");
            return;
        }
        Task t = model.getTaskAt(sel);
        TaskDialog dlg = new TaskDialog(frame, t, false);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            model.fireTableRowsUpdated(sel, sel);
            TaskStorage.save(tasks);
        }
    }

    private void onDelete() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a task to delete.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(frame, "Delete selected task?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            tasks.remove(sel);
            model.fireTableDataChanged();
            TaskStorage.save(tasks);
        }
    }

    private void onExport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Title,Note,Date,Time,Priority,Done\n");
        for (Task t : tasks) {
            sb.append(escape(t.getTitle())).append(",")
              .append(escape(t.getNote())).append(",")
              .append(t.getDate() != null ? t.getDate().toString() : "").append(",")
              .append(t.getTime() != null ? t.getTime().toString() : "").append(",")
              .append(t.getPriority().name()).append(",")
              .append(t.isDone() ? "1" : "0").append("\n");
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("tasks_export.csv"));
        int res = fc.showSaveDialog(frame);
        if (res == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileWriter fw = new java.io.FileWriter(fc.getSelectedFile())) {
                fw.write(sb.toString());
                JOptionPane.showMessageDialog(frame, "Exported to " + fc.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Export failed: " + ex.getMessage());
            }
        }
    }

    private void onImport() {
        JFileChooser fc = new JFileChooser();
        int res = fc.showOpenDialog(frame);
        if (res == JFileChooser.APPROVE_OPTION) {
            java.io.File f = fc.getSelectedFile();
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(f))) {
                String line;
                List<Task> imported = new ArrayList<>();
                boolean first = true;
                while ((line = br.readLine()) != null) {
                    if (first) { first = false; continue; } // skip header
                    String[] parts = line.split(",", -1);
                    Task t = new Task();
                    t.setTitle(unescape(parts.length>0?parts[0]:""));
                    t.setNote(unescape(parts.length>1?parts[1]:""));
                    String d = parts.length>2?parts[2]:"";
                    if (!d.isEmpty()) t.setDate(java.time.LocalDate.parse(d));
                    String ti = parts.length>3?parts[3]:"";
                    if (!ti.isEmpty()) t.setTime(java.time.LocalTime.parse(ti));
                    if (parts.length>4) t.setPriority(Task.Priority.valueOf(parts[4]));
                    if (parts.length>5) t.setDone("1".equals(parts[5]));
                    imported.add(t);
                }
                tasks.clear();
                tasks.addAll(imported);
                model.fireTableDataChanged();
                TaskStorage.save(tasks);
                JOptionPane.showMessageDialog(frame, "Import successful.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Import failed: " + ex.getMessage());
            }
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\", "\\").replace(",", "\,").replace("\n", " ");
    }

    private String unescape(String s) {
        if (s == null) return "";
        return s.replace("\,", ",").replace("\\", "\");
    }

    private void onFilterChanged(JComboBox<String> filterBox) {
        String sel = (String) filterBox.getSelectedItem();
        if (sel.equals("All")) {
            model.setTasks(tasks);
        } else if (sel.equals("Today")) {
            java.time.LocalDate now = java.time.LocalDate.now();
            List<Task> f = new ArrayList<>();
            for (Task t : tasks) {
                if (t.getDate() != null && t.getDate().equals(now)) f.add(t);
            }
            model.setTasks(f);
        } else if (sel.equals("Pending")) {
            List<Task> f = new ArrayList<>();
            for (Task t : tasks) if (!t.isDone()) f.add(t);
            model.setTasks(f);
        } else if (sel.equals("Done")) {
            List<Task> f = new ArrayList<>();
            for (Task t : tasks) if (t.isDone()) f.add(t);
            model.setTasks(f);
        } else if (sel.equals("High Priority")) {
            List<Task> f = new ArrayList<>();
            for (Task t : tasks) if (t.getPriority() == Task.Priority.HIGH) f.add(t);
            model.setTasks(f);
        }
    }

    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(() -> new Main());
    }
}
