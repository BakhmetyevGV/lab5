public class Result {

    public String testUrl;
    public Long avgResponseTime;

    public Result(String testUrl, Long avgResponseTime){
        this.testUrl = testUrl;
        this.avgResponseTime = avgResponseTime;
    }
}
