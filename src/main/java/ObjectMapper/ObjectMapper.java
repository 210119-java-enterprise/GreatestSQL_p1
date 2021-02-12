package ObjectMapper;

import java.util.Collections;

public abstract class ObjectMapper {

    protected String getArgs(final int length) {
        return String.join(",", Collections.nCopies(length,"?")) + ",? ";
    }

}
