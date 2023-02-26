package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task{

   private List<Integer> subtasks;

    public Epic(String name, String description, LocalDateTime localDateTime) {
        super(name, description, TaskStatus.NEW, localDateTime, Duration.ofHours(0));
        subtasks = new ArrayList<>();
        this.setType(TaskType.EPIC);
    }

    public void addSubtask(int id) {
        subtasks.add(id);
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    public void removeSubtask(int id) {
        for (int i = 0; i < subtasks.size(); i++) {
            if (subtasks.get(i) == id) {
                subtasks.remove(i);
                break;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return id == epic.id && Objects.equals(name, epic.name) && Objects.equals(description, epic.description) &&
                status == epic.status && type == epic.type && Objects.equals(startTime, epic.startTime) &&
                Objects.equals(duration, epic.duration) && Objects.equals(endTime, epic.endTime) &&
                Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        return "Epic{" + "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id='" + getId() + '\'' +
                ", status='" + getStatus() + "'"
                + ", subtasks='" + subtasks + "'" +
                ", startTime='" + getStartTime().format(formatter) + '\'' +
                ", duration='" + getDuration().getSeconds() / 3600 + " hours" + '\'' +
                ", endTime='" + getEndTime().format(formatter) + '\'' + "}";
    }

}
