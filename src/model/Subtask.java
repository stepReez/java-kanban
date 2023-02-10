package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task{

    private int epicId;



    public Subtask(String name, String description, TaskStatus status, LocalDateTime localDateTime, Duration duration) {
        super(name, description, status, localDateTime, duration);
        this.setType(TaskType.SUBTASK);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        return "Subtask{" + "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id='" + getId() + '\'' +
                ", status='" + getStatus() + "'"
                + ", epicId='" + epicId + "'}" +
                ", startTime='" + getStartTime().format(formatter) + '\'' +
                ", duration='" + getDuration().getSeconds() / 3600 + " hours" + '\'' +
                ", endTime='" + getEndTime().format(formatter) + '\'' + "}";
    }
}
