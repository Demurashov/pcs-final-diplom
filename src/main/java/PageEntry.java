public class PageEntry implements Comparable<PageEntry> {
    private String pdfName;
    private int page;
    private long count;

    public PageEntry(String pdfName, int page, long count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    public long getCount() {
        return count;
    }

    public int getPage() {
        return page;
    }

    public String getPdfName() {
        return pdfName;
    }

    //создание уникального ключа для удобства работы с объектом в stream API
    public String generateKey() {
        return pdfName + ":" + page;
    }

    @Override
    public String toString() {
        return "| pdfName: " + pdfName + "| page: " + page + "| count: " + count + " |";
    }

    @Override
    public int compareTo(PageEntry o) {
        return (int) -(count - o.getCount());
    }
}
