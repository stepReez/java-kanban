package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Subtask extends Task{

    private int epicId;



    public Subtask(int epicId, String name, String description, TaskStatus status, LocalDateTime localDateTime, Duration duration) {
        super(name, description, status, localDateTime, duration);
        this.setType(TaskType.SUBTASK);
        this.setEpicId(epicId);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return id == subtask.id && Objects.equals(name, subtask.name) && Objects.equals(description, subtask.description) &&
                status == subtask.status && type == subtask.type && Objects.equals(startTime, subtask.startTime) &&
                Objects.equals(duration, subtask.duration) && Objects.equals(endTime, subtask.endTime) && epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
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
