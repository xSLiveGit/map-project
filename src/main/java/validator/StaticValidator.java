package validator;

/**
 * Created by Sergiu on 1/5/2017.
 */
public class StaticValidator {
    public static boolean isIntegerNumber(String chars)
    {
        for(char c : chars.toCharArray())
        {
            if (c < '0' || c > '9')
                return false;
        }
        return true;
    }

    public static boolean isDoubleNumber(String chars){
        boolean foundPoint = false;
        for(char c : chars.toCharArray())
        {
            if (c < '0' || c > '9'){
                if((c == '.') && !foundPoint){
                    foundPoint = true;
                }
                else{
                    return false;
                }
            }
        }
        return true;
    }
}
