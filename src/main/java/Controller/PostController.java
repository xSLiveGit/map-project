package Controller;

import domain.Post;
import repository.IDataBaseRepository;
import utils.params_loader.PostParamsLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Sergiu on 1/5/2017.
 */
public class PostController extends AbstractController<Post>
{
    public PostController(IDataBaseRepository<Post, Integer> repo) {
        super(repo,new PostParamsLoader());
    }

    public PostController() {
    }

    List<Post> filterByPrefixFieldsFromCurrentPage(String name, String type, String salary){
        return filterByPrefixFields(repository.getAllFromPage(),name,type,salary);
    }

    public List<Post> filterByPrefixFields(Collection<Post> col, String prefixName, String prefixType, String prefixSalary){
        ArrayList<Post> arr = new ArrayList<>(col);
        return arr.stream()
                .filter(el->el.getName().toLowerCase().startsWith(prefixName))
                .filter(el->el.getType().toLowerCase().startsWith(prefixType))
                .filter(el->el.getSalary().toString().startsWith(prefixSalary))
                .collect(Collectors.toList());
    }

}

