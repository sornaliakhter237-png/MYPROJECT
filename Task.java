package dayworkschedule;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Priority { LOW, MEDIUM, HIGH }

    private String title;
    private String note;
    private LocalDate date;
    private LocalTime time;
    private boolean done;
    private Priority priority;

    public Task() {
        this.title = "";
        this.note = "";
        this.date = null;
        this.time = null;
        this.done = false;
        this.priority = Priority.MEDIUM;
    }

    public Task(String title, String note, LocalDate date, LocalTime time, Priority priority) {
        this.title = title;
        this.note = note;
        this.date = date;
        this.time = time;
        this.done = false;
        this.priority = priority;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
}
