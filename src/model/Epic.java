package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
