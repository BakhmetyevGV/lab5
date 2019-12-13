public class TestMessage {
    public String packageId;
    public String jsScript;
    public String functionName;
    public Test[] tests;



    public TestMessage(String packageId, String jsScript, String functionName, Test[] tests) {
        this.packageId = packageId;
        this.jsScript = jsScript;
        this.functionName = functionName;
        this.tests = tests;
    }
}
