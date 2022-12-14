import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{

   List<Integer> subtasks;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subtasks = new ArrayList<>();
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
        return "Epic{" + "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id='" + getId() + '\'' +
                ", status='" + getStatus() + "'"
                + ", subtasks='" + subtasks + "'";
    }

}
