package domain;

import javafx.util.Pair;

/**
 * Created by Sergiu on 1/5/2017.
 */
public class JobDescription implements HasID<Integer>{
    private Integer id;

    public Integer getPostId() {
        return postId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    private Integer postId;
    private Integer taskId;

    public JobDescription(Integer id,Integer postId,Integer taskId){
        this.id = id;
        this.postId = postId;
        this.taskId = taskId;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id.hashCode() & postId.hashCode() & taskId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj){
            return false;
        }
        if(obj instanceof JobDescription){
            JobDescription jb = (JobDescription)obj;
            return (id.hashCode() == jb.id.hashCode()) && (postId.hashCode() == jb.taskId.hashCode()) && (taskId.hashCode() == jb.taskId.hashCode());
        }
        return false;
    }

    @Override
    public String toString() {
        return this.id.toString() + " " + this.postId.toString() + " " + this.taskId.toString();
    }
}
