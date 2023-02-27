import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Memory {
    private Map<String, List> mainMap = new HashMap<>();//

    public void addToMemory(Map<String, PageEntry> countMap) {
        countMap.forEach(this::addList);
    }

    private void addList(String a, PageEntry b) {
        mainMap.put(a, addAndGetExistList(b, mainMap.getOrDefault(a, new ArrayList())));

    }

    public static List<PageEntry> addAndGetExistList(PageEntry pageEntry, List<PageEntry> list) {
        list.add(pageEntry);
        return list;
    }

    public Map<String, List> getMainMap() {
        return mainMap;
    }

    //отладочный метод
    public void printMainMap() {
        mainMap.forEach((a, b) -> {
            System.out.println(a + ":");
            b.forEach(c -> System.out.println(c.toString()));
        });
    }


}

