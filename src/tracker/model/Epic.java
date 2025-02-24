package tracker.model;

import tracker.constants.TaskType;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(int id, String title, String description) {
        super(id, title, description);
    }


    @Override
    public String toString() {
        return "Epic{" +
                "subtasksIds=" + subtasksIds +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void cleanSubtaskIds() {
        subtasksIds.clear();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }
}
