package utils.generical;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Sergiu on 11/30/2016.
 */
public class ListGenericalHelper {

    public ListGenericalHelper() {
    }

    public static<E> List<E> filterGeneric(Iterable<E> inputList, Predicate<E> predicate, Comparator<E> comparator){
        List newList = new ArrayList();
        inputList.forEach(a->{
            if(predicate.test(a))
                newList.add(a);
        });
        newList.sort(comparator);
        return newList;
    }

    public static<E> List<E> filterGeneric(Iterable<E> inputList, Predicate<E> predicate){
        List newList = new ArrayList();
        inputList.forEach(a->{
            if(predicate.test(a))
                newList.add(a);
        });
        return newList;
    }
}
