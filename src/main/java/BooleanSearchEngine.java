import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BooleanSearchEngine implements SearchEngine {
    private Memory memory = new Memory();
    private final static File STOPLISTFILE = new File("stop-ru.txt");

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        if (pdfsDir.isDirectory()) {
            for (File file : pdfsDir.listFiles()) {
                PdfDocument doc = new PdfDocument(new PdfReader(file));
                textReap(doc, file);
            }
        }
    }

    @Override

    public List<PageEntry> search(String words) {
        return searchWords(words);
    }

    public void textReap(PdfDocument doc, File file) {
        int lastPage = doc.getNumberOfPages();
        for (int i = 0; i < lastPage; i++) {
            String[] arrStr = PdfTextExtractor
                    .getTextFromPage(doc.getPage(i + 1))
                    .split("\\P{IsAlphabetic}+");
            Map<Object, Long> countMap = Arrays
                    .stream(arrStr)
                    .collect(Collectors.groupingBy(a -> a, Collectors.counting()));
            int finalI = i;
            Map<String, PageEntry> mainMap = countMap
                    .entrySet()
                    .stream()
                    .map(a -> new AbstractMap.SimpleEntry(a.getKey()
                            , new PageEntry(file.getName(), finalI + 1, a.getValue())))
                    .collect(Collectors.toMap(a -> (String) a.getKey(), a -> (PageEntry) a.getValue()));
            memory.addToMemory(mainMap);//запись в
        }
    }

    //сумматор списков для массива строк
    public List<PageEntry> searchWords(String words) {
        List<PageEntry> list = new ArrayList<>();
        List<String> listWordsIn = checkWords(words);
        for (String word : listWordsIn) {
            if (memory.getMainMap().containsKey(word)) {
                list.addAll(memory.getMainMap().get(word));
            }
        }
        if (list.size() > 0) {
            Map<String, Long> longMap = list.stream()
                    .collect(Collectors.groupingBy(PageEntry::generateKey, Collectors.summingLong(PageEntry::getCount)));
            list = longMap.entrySet()
                    .stream()
                    .map(this::convertToPageEntry)
                    .collect(Collectors.toList());
        } else {
            list.add(new PageEntry("Ничего не найдено", 0, 0));
        }
        Collections.sort(list);
        return list;
    }

    //преобразование элемента Map в объект PageEntry
    private PageEntry convertToPageEntry(Map.Entry<String, Long> a) {
        return new PageEntry(a.getKey().split(":")[0],
                Integer.parseInt(a.getKey().split(":")[1]), a.getValue());
    }

    //удаление слов стоп листа из входящих строк
    public List<String> checkWords(String words) {
        String[] arrStr = words.split("\\P{IsAlphabetic}+");
        ArrayList<String> listWords = new ArrayList(Arrays.asList(arrStr));
        List<String> listSrtStop = StopListGenerator.loadFromTxtFile(STOPLISTFILE);
        listWords.removeAll(listSrtStop);
        return listWords;
    }
}
