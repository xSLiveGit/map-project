package utils;

/**
 * Created by Sergiu on 11/19/2016.
 */
public class Numbers {
    public static boolean isIntegerNumber(String s){
        if(s.isEmpty()) return false;
        for(char c:s.toCharArray()) {
            if(c < '0' || c>'9'){
                return false;
            }
        }
        return true;
    }
}
