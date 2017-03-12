package Controller;

import domain.DTO.TopPosts;
import domain.DTO.TopTasks;
import domain.JobDescription;
import domain.Post;
import domain.Task;
import repository.IDataBaseRepository;
import repository.IRepository;
import utils.exceptions.ControllerException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;
import utils.params_loader.IParamsLoader;
import utils.params_loader.JobDescriptionParamsLoader;
import validator.StaticValidator;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Sergiu on 1/7/2017.
 */
public class JobDescriptionController extends AbstractController<JobDescription> {
    private IDataBaseRepository<Post,Integer> postRepository;
    private IDataBaseRepository<Task,Integer> taskRepository;

    public JobDescriptionController(IDataBaseRepository<JobDescription, Integer> repository,
                                    IDataBaseRepository<Post,Integer> postRepository,
                                    IDataBaseRepository<Task,Integer> taskRepository
                                    ) {
        super(repository, new JobDescriptionParamsLoader());
        this.postRepository = postRepository;
        this.taskRepository = taskRepository;
    }

    public JobDescriptionController() {
    }

    public Collection<Post> getAllPostsWithTaskIdRelation(String...obj) throws ControllerException, RepositoryOpException, MapperException, SQLException {
        Integer key = paramsLoader.getId(obj);
        return getAllPostsWithTaskIdRelationCondition(key,true);
    }

    public Collection<Post> getAllPostsWithoutTaskIdRelation(String...obj) throws ControllerException, RepositoryOpException, MapperException, SQLException {
        Integer key = paramsLoader.getId(obj);
        return getAllPostsWithTaskIdRelationCondition(key,false);
    }

    public Collection<Post> getAllPostsWithTaskIdRelationCondition(Integer key,boolean condition) throws RepositoryOpException, MapperException, SQLException {
        HashMap<Integer,Post> col = new HashMap<Integer,Post>();
        Integer oldPage = this.repository.getCurretnPageNumber();
        this.repository.setCurrentPageNumber(0);
        do{
            for(JobDescription jb : repository.getAllFromPage()){
                if(jb.getTaskId().equals(key) == condition){
                    col.put(jb.getPostId(),postRepository.findById(jb.getPostId()));
                }
            }
            if(this.repository.hasNextPage()){
                this.repository.nextPage();
            }
            else break;
        }while(true);
        return col.values();
    }

    public Collection<Task> getAllTaskWithPostIdRelation(String... obj) throws ControllerException, MapperException, RepositoryOpException, SQLException {
        Integer key = paramsLoader.getId(obj);
        return getAllTaskWithPostIdRelationCond(key,true);
    }

    public Collection<Task> getAllTaskWithoutPostIdRelation(String... obj) throws ControllerException, MapperException, RepositoryOpException, SQLException {
        Integer key = paramsLoader.getId(obj);
        return getAllTaskWithPostIdRelationCond(key,false);
    }

    public Collection<Post> getAllPostWithTaskIdRelation(String... obj) throws ControllerException, MapperException, RepositoryOpException, SQLException {
        Integer key = paramsLoader.getId(obj);
        return getAllPostWithTaskIdRelationCond(key,true);
    }

    private Collection<Task> getAllTaskWithPostIdRelationCond(Integer key,boolean condition) throws MapperException, RepositoryOpException, SQLException {
        HashMap<Integer,Task> col = new HashMap<Integer,Task>();
        Integer oldPage = this.repository.getCurretnPageNumber();
        do{
            for(JobDescription jb : repository.getAllFromPage()){
                if(jb.getPostId().equals(key) == condition){
                    col.put(jb.getTaskId(),taskRepository.findById(jb.getTaskId()));
                }
            }
            if(this.repository.hasNextPage()){
                this.repository.nextPage();
            }
            else break;
        }while(true);
        this.repository.setCurrentPageNumber(oldPage);
        return col.values();
    }

    private Collection<Post> getAllPostWithTaskIdRelationCond(Integer key,boolean condition) throws MapperException, RepositoryOpException, SQLException {
        HashMap<Integer,Post> col = new HashMap<Integer,Post>();
        Integer oldPage = this.repository.getCurretnPageNumber();
        do{
            for(JobDescription jb : repository.getAllFromPage()){
                if(jb.getTaskId().equals(key) == condition){
                    col.put(jb.getPostId(),postRepository.findById(jb.getPostId()));
                }
            }
            if(this.repository.hasNextPage()){
                this.repository.nextPage();
            }
            else break;
        }while(true);
        this.repository.setCurrentPageNumber(oldPage);
        return col.values();
    }

    public Collection<Task> getAllTaskWithoutPostId(String... obj) throws ControllerException, RepositoryOpException, MapperException, SQLException {
        Integer idPost =  paramsLoader.getId(obj);
        HashSet<Integer> existTaskId = new HashSet<>();
        Collection<Task> col = new ArrayList<>();

        existTaskId.addAll(getAllTaskWithPostIdRelation(idPost.toString()).stream().map(Task::getId).collect(Collectors.toList()));

        Integer oldPage = this.repository.getCurretnPageNumber();
        this.taskRepository.setCurrentPageNumber(0);
        do{
            col.addAll(this.taskRepository.getAllFromPage().stream().filter(t -> !(existTaskId.contains(t.getId()))).collect(Collectors.toList()));
            if(this.taskRepository.hasNextPage()){
                this.taskRepository.nextPage();
            }
            else break;
        }while (true);
        taskRepository.setCurrentPageNumber(oldPage);
        return col;
    }

    public Collection<Post> getAllPostWithoutTaskId(String... obj) throws ControllerException, MapperException, RepositoryOpException, SQLException {
        Integer idTask = paramsLoader.getId(obj);
        HashSet<Integer> existPostId = new HashSet<>();
        Collection<Post> col = new ArrayList<>();

        existPostId.addAll(getAllPostWithTaskIdRelation(idTask.toString()).stream().map(Post::getId).collect(Collectors.toList()));
        Integer oldPage = this.repository.getCurretnPageNumber();
        this.postRepository.setCurrentPageNumber(0);
        do{
            col.addAll(this.postRepository.getAllFromPage().stream().filter(p -> !(existPostId.contains(p.getId()))).collect(Collectors.toList()));
            if(this.postRepository.hasNextPage()){
                this.postRepository.nextPage();
            }
            else break;
        }while (true);
        postRepository.setCurrentPageNumber(oldPage);
        return col;
    }

  public JobDescription findJobDescription(Integer postId,Integer taskId) throws RepositoryOpException, MapperException, SQLException {
      Integer oldPage = this.repository.getCurretnPageNumber();
      JobDescription jobDescription = null;
      this.repository.setCurrentPageNumber(0);
      boolean conditie = true;
      do{
          for(JobDescription jb : repository.getAllFromPage()){
              if(jb.getTaskId().equals(taskId) && jb.getPostId().equals(postId)){
                  jobDescription = jb;
                  conditie = false;
                  break;//ies din primul fol si cu conditie == false ies din do while
              }
          }
          if(conditie && this.repository.hasNextPage()){
              this.repository.nextPage();
          }
          else break;
      }while(conditie);
      return jobDescription;
  }


  public Collection<Post> getAllPostInRelation() throws RepositoryOpException, MapperException, ControllerException, SQLException {
      HashSet<Post> hash = new HashSet();
      Collection<JobDescription> col = repository.getAll();
      col.forEach(jb -> {
          try {
              hash.add(postRepository.findById(jb.getPostId()));
          } catch (RepositoryOpException | MapperException e) {
              e.printStackTrace();
          }
      });
      return hash;
  }

  public Collection<Task> getAllTaskInRelation() throws RepositoryOpException, MapperException, SQLException {
      HashSet<Task> hash = new HashSet();
      Collection<JobDescription> col = repository.getAll();
      col.forEach(jb -> {
          try {
              hash.add(taskRepository.findById(jb.getTaskId()));
          } catch (RepositoryOpException | MapperException e) {
              e.printStackTrace();
          }
      });
      return hash;
  }

  private boolean existPostInRelation(Post p) throws RepositoryOpException, MapperException, SQLException {
      for(JobDescription jb : this.repository.getAll()){
          if(jb.getPostId().equals(p.getId())){
              return true;
          }
      }
      return false;
  }


    public Collection<TopPosts> getTopPosts(String topS) throws ControllerException, RepositoryOpException, MapperException, SQLException {
        if(!StaticValidator.isIntegerNumber(topS)){
            throw new ControllerException("Ivalid integer number given");
        }
        Integer top = null;
        try {
            top = Integer.parseInt(topS);
        }catch (Exception exc){
            throw new ControllerException("Ivalid integer number given");
        }
        Collection<Post> posts = postRepository.getAll();
        Collection<TopPosts> topC = new ArrayList<>();
        Comparator<TopPosts> comparator = Comparator.comparing(TopPosts::getNrOfTasksRegistered);

        top = Math.min(top,getAllPostInRelation().size());
        posts.forEach((Post p)->{
            try {
                topC.add(new TopPosts(p,getAllTaskWithPostIdRelation(p.getId().toString()).size()));
            } catch (ControllerException | SQLException | RepositoryOpException | MapperException e) {
                e.printStackTrace();
            }
        });
        return topC.stream().sorted(comparator.reversed()).collect(Collectors.toList()).subList(0,top);
    }

  public Collection<TopTasks> getTopTask(String topS) throws ControllerException, RepositoryOpException, MapperException, SQLException {
      if(!StaticValidator.isIntegerNumber(topS)){
          throw new ControllerException("Ivalid integer number given");
      }
      Integer top = null;
      try {
          top = Integer.parseInt(topS);
      }catch (Exception exc){
          throw new ControllerException("Ivalid integer number given");
      }
      Collection<Task> tasks = taskRepository.getAll();
      Collection<TopTasks> topC = new ArrayList<>();
      Comparator<TopTasks> comparator = Comparator.comparing(TopTasks::getNrOfPostsRegistered);

      top = Math.min(top,getAllTaskInRelation().size());
      tasks.forEach((Task t)->{
          try {
              topC.add(new TopTasks(t,getAllPostWithTaskIdRelation(t.getId().toString()).size()));
          } catch (ControllerException | SQLException | RepositoryOpException | MapperException e) {
              e.printStackTrace();
          }
      });
      return topC.stream().sorted(comparator.reversed()).collect(Collectors.toList()).subList(0,top);
  }

  public Collection<TopTasks> getTopTasksPercent(String top) throws ControllerException, MapperException, RepositoryOpException, SQLException {
      Double topS = null;
      try{
          topS = Double.parseDouble(top);
      }
      catch (Exception e){
          throw new ControllerException("Invalid number");
      }

      topS = topS * getAllPostInRelation().size() / 100;
      Integer nr = new Integer(topS.intValue());
      //Integer nr =  this.postRepository.countElements() * percent / 100;
      return getTopTask(nr.toString());
  }

  public Collection<TopPosts> getTopPostsPercent(String top) throws ControllerException, MapperException, RepositoryOpException, SQLException {
      Double topS = null;
      try{
          topS = Double.parseDouble(top);
      }
      catch (Exception e){
          throw new ControllerException("Invalid number");
      }


      topS = topS * getAllPostInRelation().size() / 100;
      Integer nr = new Integer(topS.intValue());
      //Integer nr =  this.postRepository.countElements() * percent / 100;
      return getTopPosts(nr.toString());
  }
}
