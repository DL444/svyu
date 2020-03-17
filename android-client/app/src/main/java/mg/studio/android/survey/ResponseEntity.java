package mg.studio.android.survey;

class ResponseEntity {
    public ResponseEntity(int qid, String type, String response) {
        this.qid = qid;
        this.type = type;
        this.response = response;
    }

    public int getQid() {
        return qid;
    }

    public String getType() {
        return type;
    }

    public String getResponse() {
        return response;
    }

    private int qid;
    private String type;
    private String response;
}
