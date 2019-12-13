public class Request {
    public String testUrl;
    public int count;

    public Request(String testUrl, int count){
        this.testUrl = testUrl;
        this.count = count;
    }

    public String getTestUrl() {
        return testUrl;
    }

    public int getCount() {
        return count;
    }
}
