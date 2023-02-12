public class PageEntry implements Comparable<PageEntry> {
    private String pdfName;
    private int page;
    private int count;

    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public int getPage() {
        return page;
    }

    public String getPdfName() {
        return pdfName;
    }

    public PageEntry addCountAndGetPE() {
        count++;
        return this;
    }

    @Override
    public String toString() {
        return "| pdfName: " + pdfName + "| page: " + page + "| count: " + count+" |";
    }

    @Override
    public int compareTo(PageEntry o) {
        return -(count-o.getCount());
    }
}
