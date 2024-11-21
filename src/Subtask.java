public class Subtask extends Task {
    int epicId;

    public Subtask(int epicId, int id, String title, String description, Status status) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
