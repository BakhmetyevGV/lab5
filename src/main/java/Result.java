public class Result {

    public String testUrl;
    public static Long avgResponseTime;

    public Result(String testUrl, Long avgResponseTime){
        this.testUrl = testUrl;
        this.avgResponseTime = avgResponseTime;
    }
}
