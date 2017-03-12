package domain.DTO;

import domain.Task;

/**
 * Created by Sergiu on 1/8/2017.
 */
public class TopTasks {
    private Task task;

    public void setTask(Task task) {
        this.task = task;
    }

    public void setNrOfPostsRegistered(Integer nrOfPostsRegistered) {
        this.nrOfPostsRegistered = nrOfPostsRegistered;
    }

    public Task getTask() {

        return task;
    }

    public Integer getNrOfPostsRegistered() {
        return nrOfPostsRegistered;
    }

    public TopTasks(Task task, Integer nrOfPostsRegistered) {

        this.task = task;
        this.nrOfPostsRegistered = nrOfPostsRegistered;
    }

    private Integer nrOfPostsRegistered;
}
