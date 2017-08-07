package ec.core.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import ec.core.bean.type.Mode;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Task implements Serializable,HasId{
    @XmlAttribute
    private String id;
    @XmlAttribute
    private int processid;
    @XmlAttribute
    private String description;
    @XmlElement(name="SubTask")
    private List<SubTask> subTasks=new ArrayList<SubTask>();
    @XmlAttribute
    private String quitMessage;
    @XmlAttribute
    private String reloadURL;
    @XmlAttribute
    private String forwardURL;

    @XmlAttribute
    private String responseParams;
    
    @XmlAttribute
    private boolean async;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getProcessid() {
        return processid;
    }

    public void setProcessid(int processid) {
        this.processid = processid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public String getQuitMessage() {
        return quitMessage;
    }

    public String getReloadURL() {
        return reloadURL;
    }

    public void setReloadURL(String reloadURL) {
        this.reloadURL = reloadURL;
    }
    
    public void setQuitMessage(String QuitMessage) {
        this.quitMessage = QuitMessage;
    }


    public String getForwardURL() {
        return forwardURL;
    }

    public void setForwardURL(String forwardURL) {
        this.forwardURL = forwardURL;
    }
    public String getResponseParams() {
        return responseParams;
    }

    public void setResponseString(String appendQueryString) {
        this.responseParams = appendQueryString;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    @Override
    public String toString() {
        return "Task{" + "id=" + id + ", processid=" + processid + ", description=" + description + ", subTasks=" + subTasks + ", quitMessage=" + quitMessage + ", reloadURL=" + reloadURL + ", forwardURL=" + forwardURL + ", responseParams=" + responseParams + ", async=" + async + '}';
    }
}




