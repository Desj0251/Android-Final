package intents.ca.edumedia.desj0251.opendoorsfinal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kenata on 2018-01-09.
 */

public class regexValid {

    private Pattern pattern;
    private Matcher matcher;

    private static final String TIME12HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

    public regexValid() {
        pattern = Pattern.compile(TIME12HOURS_PATTERN);
    }

    public boolean validate(final String time){
        matcher = pattern.matcher(time);
        return matcher.matches();
    }
}
