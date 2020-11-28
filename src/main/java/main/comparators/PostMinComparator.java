package main.comparators;

import java.util.Comparator;
import java.util.HashMap;

public class PostMinComparator implements Comparator<HashMap<String, Object>> {

    private String param;

    public PostMinComparator(String param) {
        this.param = param;
    }

    @Override
    public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
        if (Integer.parseInt(String.valueOf(o1.get(param))) > Integer.parseInt(String.valueOf(o2.get(param))))
        {
            return 1;
        }
        else if (Integer.parseInt(String.valueOf(o1.get(param))) < Integer.parseInt(String.valueOf(o2.get(param))))
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}
