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
    private String seqNo;
    private String type;
    private String dest;
    private Long txnTime;
    private String verkey;
    private Map<String, Object> rawValue;

    public TransactionData(String response, Map<String, Object> responseMap, boolean found, Long reqId, String seqNo, String type, String dest, Long txnTime, String verkey, Map<String, Object> rawValue) {
        this.response = response;
        this.responseMap = responseMap;
        this.found = found;
        this.reqId = reqId;
        this.seqNo = seqNo;
        this.type = type;
        this.dest = dest;
        this.txnTime = txnTime;
        this.verkey = verkey;
        this.rawValue = rawValue;
    }

    private static TransactionData fromGetTxnResponse(String getTxnResponse, Map getTxnResponseMap) throws JsonProcessingException {

        Object jsonGetTxnResult = getTxnResponseMap == null ? null : getTxnResponseMap.get("result");
        Object jsonGetTxnResultData = !(jsonGetTxnResult instanceof Map) ? null : ((Map) jsonGetTxnResult).get("data");

        Object jsonGetTxnResultDataTxn = !(jsonGetTxnResultData instanceof Map) ? null : ((Map) jsonGetTxnResultData).get("txn");
        Object jsonGetTxnResultDataTxnType = !(jsonGetTxnResultData instanceof Map) ? null : ((Map) jsonGetTxnResultData).get("type");
        Object jsonGetTxnResultDataTxnData = !(jsonGetTxnResultDataTxn instanceof Map) ? null : ((Map) jsonGetTxnResultDataTxn).get("data");
        Object jsonGetTxnResultDataTxnDataDest = !(jsonGetTxnResultDataTxnData instanceof Map) ? null : ((Map) jsonGetTxnResultDataTxnData).get("dest");
        Object jsonGetTxnResultDataTxnDataVerkey = !(jsonGetTxnResultDataTxnData instanceof Map) ? null : ((Map) jsonGetTxnResultDataTxnData).get("verkey");
        Object jsonGetTxnResultRaw = !(jsonGetTxnResultDataTxn instanceof Map) ? null : ((Map) jsonGetTxnResultDataTxn).get("raw");
        Object jsonGetTxnResultDataRawValue = !(jsonGetTxnResultDataTxnData instanceof Map) || !(jsonGetTxnResultRaw instanceof String) ? null : ((Map) jsonGetTxnResultDataTxnData).get(jsonGetTxnResultRaw);
        Object jsonGetTxnResultDataTxnMetadata = !(jsonGetTxnResultDataTxn instanceof Map) ? null : ((Map) jsonGetTxnResultDataTxn).get("metadata");
        Object jsonGetTxnResultDataTxnMetadataReqid = !(jsonGetTxnResultDataTxnMetadata instanceof Map) ? null : ((Map) jsonGetTxnResultDataTxnMetadata).get("reqId");
        Object jsonGetTxnResultDataTxnmetadata = !(jsonGetTxnResultData instanceof Map) ? null : ((Map) jsonGetTxnResultData).get("txnMetadata");
        Object jsonGetTxnResultDataTxnmetadataSeqno = !(jsonGetTxnResultDataTxnmetadata instanceof Map) ? null : ((Map) jsonGetTxnResultDataTxnmetadata).get("seqNo");
        Object jsonGetTxnResultDataTxnmetadataTxntime = !(jsonGetTxnResultDataTxnmetadata instanceof Map) ? null : ((Map) jsonGetTxnResultDataTxnmetadata).get("txnTime");

        boolean found = jsonGetTxnResultData != null;
        String type = jsonGetTxnResultDataTxnType instanceof String ? (String) jsonGetTxnResultDataTxnType : null;
        String dest = jsonGetTxnResultDataTxnDataDest instanceof String ? (String) jsonGetTxnResultDataTxnDataDest : null;
        String verkey = jsonGetTxnResultDataTxnDataVerkey instanceof String ? (String) jsonGetTxnResultDataTxnDataVerkey : null;
        Map<String, Object> rawValue = jsonGetTxnResultDataRawValue instanceof Map ? (Map<String, Object>) jsonGetTxnResultDataRawValue : null;
        Long reqId = jsonGetTxnResultDataTxnMetadataReqid instanceof Number ? ((Number) jsonGetTxnResultDataTxnMetadataReqid).longValue() : null;
        String seqNo = jsonGetTxnResultDataTxnmetadataSeqno instanceof String ? (String) jsonGetTxnResultDataTxnmetadataSeqno : null;
        Long txnTime = jsonGetTxnResultDataTxnmetadataTxntime instanceof Number ? ((Number) jsonGetTxnResultDataTxnmetadataTxntime).longValue() : null;

        return new TransactionData(getTxnResponse, getTxnResponseMap, found, reqId, seqNo, type, dest, txnTime, verkey, rawValue);
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
        String seqNo = jsonGetNymResultSeqno instanceof String ? (String) jsonGetNymResultSeqno : null;
        String type = jsonGetNymResultType instanceof String ? (String) jsonGetNymResultType : null;
        String dest = jsonGetNymResultDest instanceof String ? (String) jsonGetNymResultDest : null;
        Long txnTime = jsonGetNymResultTxntime instanceof Number ? ((Number) jsonGetNymResultTxntime).longValue() : null;
        String verkey = jsonGetNymResultDataVerkey instanceof String ? (String) jsonGetNymResultDataVerkey : null;

        return new TransactionData(getNymResponse, getNymResponseMap, found, reqId, seqNo, type, dest, txnTime, verkey, null);
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
        Object jsonGetAttrResultDataRawValue = !(jsonGetAttrResultData instanceof Map) || !(jsonGetAttrResultRaw instanceof String) ? null : ((Map) jsonGetAttrResultData).get(jsonGetAttrResultRaw);

        boolean found = jsonGetAttrResultData != null;
        Long reqId = jsonGetAttrResultReqid instanceof Number ? ((Number) jsonGetAttrResultReqid).longValue() : null;
        String seqNo = jsonGetAttrResultSeqno instanceof String ? (String) jsonGetAttrResultSeqno : null;
        String type = jsonGetAttrResultType instanceof String ? (String) jsonGetAttrResultType : null;
        String dest = jsonGetAttrResultDest instanceof String ? (String) jsonGetAttrResultDest : null;
        Long txnTime = jsonGetAttrResultTxntime instanceof Number ? ((Number) jsonGetAttrResultTxntime).longValue() : null;
        Map<String, Object> rawValue = jsonGetAttrResultDataRawValue instanceof Map ? (Map<String, Object>) jsonGetAttrResultDataRawValue : null;

        return new TransactionData(getAttrRespnse, getAttrResponseMap, found, reqId, seqNo, type, dest, txnTime, null, rawValue);
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
        return this.getRawValue() != null;
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

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
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

    public Map<String, Object> getRawValue() {
        return rawValue;
    }

    public void setRawValue(Map<String, Object> rawValue) {
        this.rawValue = rawValue;
    }

    /*
     * Object methods
     */

    public String toString() {
        return this.response;
    }
}
