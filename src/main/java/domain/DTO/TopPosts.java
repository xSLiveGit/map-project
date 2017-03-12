package domain.DTO;

import domain.Post;

/**
 * Created by Sergiu on 1/8/2017.
 */
public class TopPosts {
    private Integer nrOfTasksRegistered;
    private Post post;

    public Post getPost() {
        return post;
    }

    public Integer getNrOfTasksRegistered() {
        return nrOfTasksRegistered;
    }



    public void setPost(Post post) {
        this.post = post;
    }

    public void setNrOfTasksRegistered(Integer nrOfTasksRegistered) {
        this.nrOfTasksRegistered = nrOfTasksRegistered;
    }




    public TopPosts(Post post, Integer nrOfTasksRegistered) {
        this.post = post;
        this.nrOfTasksRegistered = nrOfTasksRegistered;
    }
}
