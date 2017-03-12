package utils.exceptions;

import java.io.FileNotFoundException;

/**
 * Created by Sergiu on 10/26/2016.
 */
public class FileErrorException extends FileNotFoundException {
    public FileErrorException(String msg){
        super(msg);
    }
}
