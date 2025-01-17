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
    private List<String> listSrtStop = StopListGenerator.loadFromTxtFile(STOPLISTFILE);

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
        words = words.toLowerCase();//приводим входящую строку к одному регистру
        return searchWords(words);
    }

    public void textReap(PdfDocument doc, File file) {
        int lastPage = doc.getNumberOfPages();
        for (int i = 0; i < lastPage; i++) {
            Map<String, PageEntry> countMap = new HashMap<>();
            String[] arrStr = PdfTextExtractor
                    .getTextFromPage(doc.getPage(i + 1))
                    .split("\\P{IsAlphabetic}+");
            for (String item : arrStr) {
                if (item.isEmpty()) {
                    continue;
                }
                item = item.toLowerCase();
                countMap.put(item, countMap
                        .getOrDefault(item, new PageEntry(file.getName(), i + 1, 0))
                        .addCountAndGetPE());
            }
            memory.addToMemory(countMap);//запись в
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
        if (!list.isEmpty()) {
            Map<String, PageEntry> pageEntryMap = new HashMap<>();
            for (PageEntry item : list) {
                pageEntryMap.merge(item.generateKey(), new PageEntry(item), (a, b) -> a.mergePE(b));
            }
            list = pageEntryMap.entrySet().stream().map((a) -> a.getValue()).collect(Collectors.toList());
        }
        Collections.sort(list);
        return list;
    }

    //удаление слов стоп листа из входящих строк
    public List<String> checkWords(String words) {
        String[] arrStr = words.split("\\P{IsAlphabetic}+");
        List<String> listWords = new ArrayList(Arrays.asList(arrStr));
        listWords.removeAll(listSrtStop);
        return listWords;
    }
}
