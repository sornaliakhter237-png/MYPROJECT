package dayworkschedule;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskTableModel extends AbstractTableModel {
    private final String[] cols = {"Done", "Title", "Note", "Date", "Time", "Priority"};
    private List<Task> tasks;
    private DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

    public TaskTableModel(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        fireTableDataChanged();
    }

    public Task getTaskAt(int row) {
        return tasks.get(row);
    }

    @Override
    public int getRowCount() {
        return tasks.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int col) {
        return cols[col];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) return Boolean.class;
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Task t = tasks.get(rowIndex);
        switch (columnIndex) {
            case 0: return t.isDone();
            case 1: return t.getTitle();
            case 2: return t.getNote();
            case 3: return t.getDate() != null ? dateFmt.format(t.getDate()) : "";
            case 4: return t.getTime() != null ? timeFmt.format(t.getTime()) : "";
            case 5: return t.getPriority().name();
            default: return "";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0; // allow toggling Done directly
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Task t = tasks.get(rowIndex);
        if (columnIndex == 0 && aValue instanceof Boolean) {
            t.setDone((Boolean) aValue);
            TaskStorage.save(tasks);
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }
}
