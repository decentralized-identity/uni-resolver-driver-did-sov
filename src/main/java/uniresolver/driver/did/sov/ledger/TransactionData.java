package uniresolver.driver.did.sov.ledger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class TransactionData {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String response;
    private Map<String, Object> responseMap;

    private boolean found;
    private Long reqId;
    private Long seqNo;
    private String type;
    private String dest;
    private Long txnTime;
    private String verkey;
    private String rawKey;
    private Map<String, Object> rawValue;

    public TransactionData(String response, Map<String, Object> responseMap, boolean found, Long reqId, Long seqNo, String type, String dest, Long txnTime, String verkey, String rawKey, Map<String, Object> rawValue) {
        this.response = response;
        this.responseMap = responseMap;
        this.found = found;
        this.reqId = reqId;
        this.seqNo = seqNo;
        this.type = type;
        this.dest = dest;
        this.txnTime = txnTime;
        this.verkey = verkey;
        this.rawKey = rawKey;
        this.rawValue = rawValue;
    }

    private static TransactionData fromGetTxnResponse(String getTxnResponse, Map getTxnResponseMap) throws JsonProcessingException {

        Object jsonGetTxnResult = getTxnResponseMap == null ? null : getTxnResponseMap.get("result");
        Object jsonGetTxnResultData = !(jsonGetTxnResult instanceof Map) ? null : ((Map) jsonGetTxnResult).get("data");

        Object jsonGetTxnResultReqid = !(jsonGetTxnResult instanceof Map) ? null : ((Map) jsonGetTxnResult).get("reqId");
        Object jsonGetTxnResultSeqno = !(jsonGetTxnResult instanceof Map) ? null : ((Map) jsonGetTxnResult).get("seqNo");
        Object jsonGetTxnResultDataTxn = !(jsonGetTxnResultData instanceof Map) ? null : ((Map) jsonGetTxnResultData).get("txn");
        Object jsonGetTxnResultDataTxnType = !(jsonGetTxnResultDataTxn instanceof Map) ? null : ((Map) jsonGetTxnResultDataTxn).get("type");
        Object jsonGetTxnResultDataTxnData = !(jsonGetTxnResultDataTxn instanceof Map) ? null : ((Map) jsonGetTxnResultDataTxn).get("data");
        Object jsonGetTxnResultDataTxnDataDest = !(jsonGetTxnResultDataTxnData instanceof Map) ? null : ((Map) jsonGetTxnResultDataTxnData).get("dest");
        Object jsonGetTxnResultDataTxnDataVerkey = !(jsonGetTxnResultDataTxnData instanceof Map) ? null : ((Map) jsonGetTxnResultDataTxnData).get("verkey");
        Object jsonGetTxnResultDataTxnDataRawString = !(jsonGetTxnResultDataTxnData instanceof Map) ? null : ((Map) jsonGetTxnResultDataTxnData).get("raw");
        Object jsonGetTxnResultDataTxnDataRawObject = !(jsonGetTxnResultDataTxnDataRawString instanceof String) ? null : objectMapper.readValue((String) jsonGetTxnResultDataTxnDataRawString, Map.class);
        Object jsonGetTxnResultDataTxnDataRawKey = !(jsonGetTxnResultDataTxnDataRawObject instanceof Map) ? null : ((Map.Entry) ((Map) jsonGetTxnResultDataTxnDataRawObject).entrySet().iterator().next()).getKey();
        Object jsonGetTxnResultDataTxnDataRawValue = !(jsonGetTxnResultDataTxnDataRawObject instanceof Map) ? null : ((Map.Entry) ((Map) jsonGetTxnResultDataTxnDataRawObject).entrySet().iterator().next()).getValue();
        Object jsonGetTxnResultDataTxnmetadata = !(jsonGetTxnResultData instanceof Map) ? null : ((Map) jsonGetTxnResultData).get("txnMetadata");
        Object jsonGetTxnResultDataTxnmetadataTxntime = !(jsonGetTxnResultDataTxnmetadata instanceof Map) ? null : ((Map) jsonGetTxnResultDataTxnmetadata).get("txnTime");

        boolean found = jsonGetTxnResultData != null;
        Long reqId = jsonGetTxnResultReqid instanceof Number ? ((Number) jsonGetTxnResultReqid).longValue() : null;
        Long seqNo = jsonGetTxnResultSeqno instanceof Number ? ((Number) jsonGetTxnResultSeqno).longValue() : null;
        String type = jsonGetTxnResultDataTxnType instanceof String ? (String) jsonGetTxnResultDataTxnType : null;
        String dest = jsonGetTxnResultDataTxnDataDest instanceof String ? (String) jsonGetTxnResultDataTxnDataDest : null;
        String verkey = jsonGetTxnResultDataTxnDataVerkey instanceof String ? (String) jsonGetTxnResultDataTxnDataVerkey : null;
        String rawKey = jsonGetTxnResultDataTxnDataRawKey instanceof String ? (String) jsonGetTxnResultDataTxnDataRawKey : null;
        Map<String, Object> rawValue = jsonGetTxnResultDataTxnDataRawValue instanceof Map ? (Map<String, Object>) jsonGetTxnResultDataTxnDataRawValue : null;
        Long txnTime = jsonGetTxnResultDataTxnmetadataTxntime instanceof Number ? ((Number) jsonGetTxnResultDataTxnmetadataTxntime).longValue() : null;

        return new TransactionData(getTxnResponse, getTxnResponseMap, found, reqId, seqNo, type, dest, txnTime, verkey, rawKey, rawValue);
    }

    private static TransactionData fromGetNymResponse(String getNymResponse, Map getNymResponseMap) throws JsonProcessingException {

        Object jsonGetNymResult = getNymResponseMap == null ? null : getNymResponseMap.get("result");
        Object jsonGetNymResultDataString = !(jsonGetNymResult instanceof Map) ? null : ((Map) jsonGetNymResult).get("data");
        Object jsonGetNymResultData = !(jsonGetNymResultDataString instanceof String) ? null : objectMapper.readValue((String) jsonGetNymResultDataString, Map.class);

        Object jsonGetNymResultReqid = !(jsonGetNymResult instanceof Map) ? null : ((Map) jsonGetNymResult).get("reqId");
        Object jsonGetNymResultSeqno = !(jsonGetNymResult instanceof Map) ? null : ((Map) jsonGetNymResult).get("seqNo");
        Object jsonGetNymResultType = !(jsonGetNymResult instanceof Map) ? null : ((Map) jsonGetNymResult).get("type");
        Object jsonGetNymResultDest = !(jsonGetNymResult instanceof Map) ? null : ((Map) jsonGetNymResult).get("dest");
        Object jsonGetNymResultTxntime = !(jsonGetNymResult instanceof Map) ? null : ((Map) jsonGetNymResult).get("txnTime");
        Object jsonGetNymResultDataVerkey = !(jsonGetNymResultData instanceof Map) ? null : ((Map) jsonGetNymResultData).get("verkey");

        boolean found = jsonGetNymResultData != null;
        Long reqId = jsonGetNymResultReqid instanceof Number ? ((Number) jsonGetNymResultReqid).longValue() : null;
        Long seqNo = jsonGetNymResultSeqno instanceof Number ? ((Number) jsonGetNymResultSeqno).longValue() : null;
        String type = jsonGetNymResultType instanceof String ? (String) jsonGetNymResultType : null;
        String dest = jsonGetNymResultDest instanceof String ? (String) jsonGetNymResultDest : null;
        Long txnTime = jsonGetNymResultTxntime instanceof Number ? ((Number) jsonGetNymResultTxntime).longValue() : null;
        String verkey = jsonGetNymResultDataVerkey instanceof String ? (String) jsonGetNymResultDataVerkey : null;

        return new TransactionData(getNymResponse, getNymResponseMap, found, reqId, seqNo, type, dest, txnTime, verkey, null, null);
    }

    private static TransactionData fromGetAttrResponse(String getAttrRespnse, Map getAttrResponseMap) throws JsonProcessingException {

        Object jsonGetAttrResult = getAttrResponseMap == null ? null : getAttrResponseMap.get("result");
        Object jsonGetAttrResultDataString = !(jsonGetAttrResult instanceof Map) ? null : ((Map) jsonGetAttrResult).get("data");
        Object jsonGetAttrResultData = !(jsonGetAttrResultDataString instanceof String) ? null : objectMapper.readValue((String) jsonGetAttrResultDataString, Map.class);

        Object jsonGetAttrResultReqid = !(jsonGetAttrResult instanceof Map) ? null : ((Map) jsonGetAttrResult).get("reqId");
        Object jsonGetAttrResultSeqno = !(jsonGetAttrResult instanceof Map) ? null : ((Map) jsonGetAttrResult).get("seqNo");
        Object jsonGetAttrResultType = !(jsonGetAttrResult instanceof Map) ? null : ((Map) jsonGetAttrResult).get("type");
        Object jsonGetAttrResultDest = !(jsonGetAttrResult instanceof Map) ? null : ((Map) jsonGetAttrResult).get("dest");
        Object jsonGetAttrResultTxntime = !(jsonGetAttrResult instanceof Map) ? null : ((Map) jsonGetAttrResult).get("txnTime");
        Object jsonGetAttrResultRaw = !(jsonGetAttrResult instanceof Map) ? null : ((Map) jsonGetAttrResult).get("raw");
        Object jsonGetAttrResultRawKey = jsonGetAttrResultRaw;
        Object jsonGetAttrResultDataRawValue = !(jsonGetAttrResultData instanceof Map) || !(jsonGetAttrResultRaw instanceof String) ? null : ((Map) jsonGetAttrResultData).get(jsonGetAttrResultRaw);

        boolean found = jsonGetAttrResultData != null;
        Long reqId = jsonGetAttrResultReqid instanceof Number ? ((Number) jsonGetAttrResultReqid).longValue() : null;
        Long seqNo = jsonGetAttrResultSeqno instanceof Number ? ((Number) jsonGetAttrResultSeqno).longValue() : null;
        String type = jsonGetAttrResultType instanceof String ? (String) jsonGetAttrResultType : null;
        String dest = jsonGetAttrResultDest instanceof String ? (String) jsonGetAttrResultDest : null;
        Long txnTime = jsonGetAttrResultTxntime instanceof Number ? ((Number) jsonGetAttrResultTxntime).longValue() : null;
        String rawKey = jsonGetAttrResultRawKey instanceof String ? (String) jsonGetAttrResultRawKey : null;
        Map<String, Object> rawValue = jsonGetAttrResultDataRawValue instanceof Map ? (Map<String, Object>) jsonGetAttrResultDataRawValue : null;

        return new TransactionData(getAttrRespnse, getAttrResponseMap, found, reqId, seqNo, type, dest, txnTime, null, rawKey, rawValue);
  }

    public static TransactionData fromGetTxnResponse(String getTxnResponse) {
        try {
            Map<String, Object> getTxnResponseMap = objectMapper.readValue(getTxnResponse, Map.class);
            return fromGetTxnResponse(getTxnResponse, getTxnResponseMap);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Cannot parse transaction " + getTxnResponse + ": " + ex.getMessage(), ex);
        }
    }

    public static TransactionData fromTxnResponseMap(Map<String, Object> getTxnResponseMap) {
        try {
            String getTxnResponse = objectMapper.writeValueAsString(getTxnResponseMap);
            return fromGetTxnResponse(getTxnResponse, getTxnResponseMap);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Cannot write transaction " + getTxnResponseMap + ": " + ex.getMessage(), ex);
        }
    }

    public static TransactionData fromGetNymResponse(String getNymResponse) {
        try {
            Map<String, Object> getTxnResponseMap = objectMapper.readValue(getNymResponse, Map.class);
            return fromGetNymResponse(getNymResponse, getTxnResponseMap);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Cannot parse transaction " + getNymResponse + ": " + ex.getMessage(), ex);
        }
    }

    public static TransactionData fromGetNymResponse(Map<String, Object> getNymResponseMap) {
        try {
            String getTxnResponse = objectMapper.writeValueAsString(getNymResponseMap);
            return fromGetNymResponse(getTxnResponse, getNymResponseMap);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Cannot write transaction " + getNymResponseMap + ": " + ex.getMessage(), ex);
        }
    }

    public static TransactionData fromGetAttrResponse(String getAttrResponse) {
        try {
            Map<String, Object> getTxnResponseMap = objectMapper.readValue(getAttrResponse, Map.class);
            return fromGetAttrResponse(getAttrResponse, getTxnResponseMap);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Cannot parse transaction " + getAttrResponse + ": " + ex.getMessage(), ex);
        }
    }

    public static TransactionData fromGetAttrResponse(Map<String, Object> getAttrResponseMap) {
        try {
            String getTxnResponse = objectMapper.writeValueAsString(getAttrResponseMap);
            return fromGetAttrResponse(getTxnResponse, getAttrResponseMap);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Cannot write transaction " + getAttrResponseMap + ": " + ex.getMessage(), ex);
        }
    }

    public boolean isNym() {
        return "1".equals(this.getType());
    }

    public boolean isAttrib() {
        return "100".equals(this.getType());
    }

    public boolean isDIDRelated() {
        return this.isNym() || this.isAttrib();
    }

    public boolean isCompleteForNym() {
        return this.getDest() != null && this.getVerkey() != null;
    }

    public boolean isCompleteForAttrib() {
        return this.getRawKey() != null;
    }

    /*
     * Getters and setters
     */

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Map<String, Object> getResponseMap() {
        return responseMap;
    }

    public void setResponseMap(Map<String, Object> responseMap) {
        this.responseMap = responseMap;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public Long getReqId() {
        return reqId;
    }

    public void setReqId(Long reqId) {
        this.reqId = reqId;
    }

    public Long getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Long seqNo) {
        this.seqNo = seqNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public Long getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(Long txnTime) {
        this.txnTime = txnTime;
    }

    public String getVerkey() {
        return verkey;
    }

    public void setVerkey(String verkey) {
        this.verkey = verkey;
    }

    public String getRawKey() {
        return rawKey;
    }

    public void setRawKey(String rawKey) {
        this.rawKey = rawKey;
    }

    public Map<String, Object> getRawValue() {
        return rawValue;
    }

    public void setRawValue(Map<String, Object> rawValue) {
        this.rawValue = rawValue;
    }

    /*
     * Object methods
     */

    @Override
    public String toString() {
        return "TransactionData{" +
                "found=" + found +
                ", reqId=" + reqId +
                ", seqNo='" + seqNo + '\'' +
                ", type='" + type + '\'' +
                ", dest='" + dest + '\'' +
                ", txnTime=" + txnTime +
                ", verkey='" + verkey + '\'' +
                ", rawKey='" + rawKey + '\'' +
                ", rawValue=" + rawValue +
                '}';
    }
}
