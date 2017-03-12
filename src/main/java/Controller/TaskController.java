package Controller;

import domain.Post;
import domain.Task;
import repository.IDataBaseRepository;
import repository.IRepository;
import utils.params_loader.TaskParamsLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Sergiu on 1/5/2017.
 */
public class TaskController extends AbstractController<Task> {
    public TaskController(IDataBaseRepository<Task, Integer> repo) {
        super(repo,new TaskParamsLoader());
    }

    public TaskController() {
    }

    public List<Task> filterByPrefixFields(Collection<Task> col, String prefixType, String prefixDuration, String prefixDescription){
        ArrayList<Task> arr = new ArrayList<>(col);
        return arr.stream()
                .filter(el->el.getDescription().toLowerCase().startsWith(prefixDescription))
                .filter(el->el.getType().toLowerCase().startsWith(prefixType))
                .filter(el->el.getDuration().toString().startsWith(prefixDuration))
                .collect(Collectors.toList());
    }
}
