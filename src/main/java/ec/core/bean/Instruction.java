package ec.core.bean;

import ec.core.bean.type.CallType;
import ec.core.bean.type.Type;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Instruction {

    @XmlAttribute
    private int id;
    @XmlAttribute
    private int taskId;
    @XmlAttribute
    private int subTaskId;
    @XmlAttribute
    private Type type = Type.INSERT;

    private String operation;
    @XmlElementWrapper(name = "OperationParams")
    @XmlElements(
            @XmlElement(name = "Param"))
    private List<Param> operationParams = new ArrayList<Param>();
    @XmlAttribute
    private CallType opConditionType = CallType.SQL;

    @XmlAttribute
    private String opCondition;
    @XmlAttribute
    private boolean quitOnInvalidCondition;
    @XmlAttribute
    private String quitMessage;
    @XmlElementWrapper(name = "InstructionConditionParams")
    @XmlElements(
            @XmlElement(name = "Param"))
    private List<Param> operationConditionParams = new ArrayList<Param>();
    @XmlAttribute
    private CallType opLoopType;
    @XmlAttribute
    private String opLoop;
    @XmlElementWrapper(name = "InstructionLoopParams")
    @XmlElements(
            @XmlElement(name = "Param"))
    private List<Param> operationLoopParams = new ArrayList<Param>();
    @XmlAttribute
    private String parentFieldRef;
    @XmlAttribute
    private String parentFieldVal;
    @XmlAttribute
    private boolean quitOnException;
    @XmlAttribute
    private boolean saveResult;
    @XmlAttribute
    private String saveResultAs;
    @XmlElement(name = "MailParams")
    private MailParams mailParams;
    @XmlElement(name = "HttpClientParams")
    private HttpClientParams httpClientParams;
    @XmlElement(name = "UploadFileParams")
    private UploadFileParams uploadFileParams;
    @XmlElement(name = "DownloadFileParams")
    private DownloadFileParams downloadFileParams;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(int subTaskId) {
        this.subTaskId = subTaskId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type oper) {
        this.type = oper;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operand) {
        this.operation = operand;
    }

    public List<Param> getOperationParams() {
        return operationParams;
    }

    public void setOperationParams(List<Param> operandParams) {
        this.operationParams = operandParams;
    }

    public CallType getOpConditionType() {
        return opConditionType;
    }

    public void setOpConditionType(CallType opConditionType) {
        this.opConditionType = opConditionType;
    }

    public String getOpCondition() {
        return opCondition;
    }

    public void setOpCondition(String opCondition) {
        this.opCondition = opCondition;
    }

    public boolean isQuitOnInvalidCondition() {
        return quitOnInvalidCondition;
    }

    public void setQuitOnInvalidCondition(boolean quitOnInvalidCondition) {
        this.quitOnInvalidCondition = quitOnInvalidCondition;
    }

    public String getQuitMessage() {
        return quitMessage;
    }

    public void setQuitMessage(String quitMessage) {
        this.quitMessage = quitMessage;
    }

    public List<Param> getOperationConditionParams() {
        return operationConditionParams;
    }

    public void setOperationConditionParams(List<Param> opConditionParams) {
        this.operationConditionParams = opConditionParams;
    }

    public CallType getOpLoopType() {
        return opLoopType;
    }

    public void setOpLoopType(CallType opLoopType) {
        this.opLoopType = opLoopType;
    }

    public String getOpLoop() {
        return opLoop;
    }

    public void setOpLoop(String opLoop) {
        this.opLoop = opLoop;
    }

    public List<Param> getOperationLoopParams() {
        return operationLoopParams;
    }

    public void setOperationLoopParams(List<Param> opLoopParams) {
        this.operationLoopParams = opLoopParams;
    }

    public String getParentFieldRef() {
        return parentFieldRef;
    }

    public void setParentFieldRef(String parentFieldRef) {
        this.parentFieldRef = parentFieldRef;
    }

    public String getParentFieldVal() {
        return parentFieldVal;
    }

    public void setParentFieldVal(String parentFieldVal) {
        this.parentFieldVal = parentFieldVal;
    }

    public boolean isQuitOnException() {
        return quitOnException;
    }

    public void setQuitOnException(boolean quitOnException) {
        this.quitOnException = quitOnException;
    }

    public boolean isSaveResult() {
        return saveResult;
    }

    public void setSaveResult(boolean saveResult) {
        this.saveResult = saveResult;
    }

    public MailParams getMailParams() {
        return mailParams;
    }

    public void setMailParams(MailParams mailParams) {
        this.mailParams = mailParams;
    }

    public HttpClientParams getHttpClientParams() {
        return httpClientParams;
    }

    public void setHttpClientParams(HttpClientParams httpClientParams) {
        this.httpClientParams = httpClientParams;
    }

    public UploadFileParams getUploadFileParams() {
        return uploadFileParams;
    }

    public void setUploadFileParams(UploadFileParams uploadFileParams) {
        this.uploadFileParams = uploadFileParams;
    }

    public DownloadFileParams getDownloadFileParams() {
        return downloadFileParams;
    }

    public void setDownloadFileParams(DownloadFileParams downloadFileParams) {
        this.downloadFileParams = downloadFileParams;
    }

    public String getSaveResultAs() {
        return saveResultAs;
    }

    public void setSaveResultAs(String saveResultAs) {
        this.saveResultAs = saveResultAs;
    }

    @Override
    public String toString() {
        return "Instruction{" + "id=" + id + ", taskId=" + taskId + ", subTaskId=" + subTaskId + ", type=" + type + ", operation=" + operation + ", operationParams=" + operationParams + ", opConditionType=" + opConditionType + ", opCondition=" + opCondition + ", quitOnInvalidCondition=" + quitOnInvalidCondition + ", quitMessage=" + quitMessage + ", operationConditionParams=" + operationConditionParams + ", opLoopType=" + opLoopType + ", opLoop=" + opLoop + ", operationLoopParams=" + operationLoopParams + ", parentFieldRef=" + parentFieldRef + ", parentFieldVal=" + parentFieldVal + ", quitOnException=" + quitOnException + ", saveResult=" + saveResult + ", saveResultAs=" + saveResultAs + ", mailParams=" + mailParams + ", httpClientParams=" + httpClientParams + ", uploadFileParams=" + uploadFileParams + ", downloadFileParams=" + downloadFileParams + '}';
    }


}
