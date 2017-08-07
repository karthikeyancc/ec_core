package ec.core.bean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ec.core.bean.type.CallType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SubTask {
    @XmlAttribute
    private int id;
    @XmlAttribute
    private int taskId;
    @XmlAttribute
    private CallType stConditionType;

    private String stCondition;
    @XmlAttribute
    private boolean quitOnInvalidCondition;
    @XmlAttribute
    private String quitMessage;
    @XmlElementWrapper(name="SubTaskConditionParams")
    @XmlElements( @XmlElement(name="Param"))
    private List<Param> subTaskConditionParams =new ArrayList<Param>() ;
    @XmlAttribute
    private CallType stLoopType;
    @XmlAttribute
    private String stLoop;
    @XmlElementWrapper(name="SubTaskLoopParams")
    @XmlElements( @XmlElement(name="Param"))
    private List<Param> subTaskLoopParams=new ArrayList<Param>();
    @XmlElement(name="Instruction")
    private List<Instruction> instructions=new ArrayList<Instruction>();

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

    public CallType getStConditionType() {
        return stConditionType;
    }

    public void setStConditionType(CallType stConditionType) {
        this.stConditionType = stConditionType;
    }

    public String getStCondition() {
        return stCondition;
    }

    public void setStCondition(String stCondition) {
        this.stCondition = stCondition;
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
    
    public List<Param> getSubTaskConditionParams() {
        return subTaskConditionParams;
    }

    public void setSubTaskConditionParams(List<Param> stConditionParams) {
        this.subTaskConditionParams = stConditionParams;
    }

    public CallType getStLoopType() {
        return stLoopType;
    }

    public void setStLoopType(CallType stLoopType) {
        this.stLoopType = stLoopType;
    }

    public String getStLoop() {
        return stLoop;
    }

    public void setStLoop(String stLoop) {
        this.stLoop = stLoop;
    }

    public List<Param> getSubTaskLoopParams() {
        return subTaskLoopParams;
    }

    public void setSubTaskLoopParams(List<Param> stLoopParams) {
        this.subTaskLoopParams = stLoopParams;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instruction> operations) {
        this.instructions = operations;
    }
    public String toString (){
        return "\n\tsubtask id : "+id+" taskid: "+taskId+" stConditionType : "+stConditionType+" stCondition : "+stCondition+"stLoopType: "+stLoopType+
        "stCallType : "+stLoop+"subTaskConditionParams: "+subTaskConditionParams+"subTaskLoopParams: "+subTaskLoopParams+"\n\tinstructions : "+(instructions==null?"null":instructions.toString());
    }
   
}
