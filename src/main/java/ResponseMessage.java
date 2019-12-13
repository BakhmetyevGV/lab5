import java.util.Arrays;

public class ResponseMessage {
    public String packageId;
    public boolean success;
    public TestResult[] results;

    public ResponseMessage(){
    }

    public ResponseMessage(String packageId, TestResult[] results){
        this.packageId = packageId;
        this.results = results;

        success = Arrays.stream(results)
                .map(r -> r.success)
                .reduce((a,b) -> a && b)
                .get();
    }
}
