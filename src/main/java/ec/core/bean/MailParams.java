/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this templateFile file, choose Tools | Templates
 * and open the templateFile in the editor.
 */
package ec.core.bean;

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

/**
 *
 * @author karthikeyan
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MailParams {
//    @XmlAttribute
//    private String loopQuery="SELECT 1 from dual";

    @XmlAttribute
    private boolean commitAndStart = true;
    @XmlAttribute
    private String format = "text/html";
    @XmlAttribute
    private Type type = Type.MAIL;
    @XmlAttribute
    private String templateFile;
    @XmlAttribute
    private String message;
    @XmlAttribute
    private String subject;
    @XmlAttribute
    private String from;
    @XmlAttribute
    private String to;
    @XmlAttribute
    private int waitTime;
    @XmlElementWrapper(name = "GridQueries")
    @XmlElements(
            @XmlElement(name = "SelectQuery"))
    private List<SelectQuery> gridQueries = new ArrayList<SelectQuery>();
    @XmlAttribute
    private String propertyFile="WEB-INF/classes/mail.properties";

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }

//    public List<MailParam> getMailInsructionParams() {
//        return mailInsructionParams;
//    }
//
//    public void setMailInsructionParams(List<MailParam> mailInsructionParams) {
//        this.mailInsructionParams = mailInsructionParams;
//    }
    public boolean isCommitAndStart() {
        return commitAndStart;
    }

    public void setCommitAndStart(boolean commitAndStart) {
        this.commitAndStart = commitAndStart;
    }

    public String getPropertyFile() {
        return propertyFile;
    }

    public void setPropertyFile(String propertyFile) {
        this.propertyFile = propertyFile;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<SelectQuery> getGridQueries() {
        return gridQueries;
    }

    public void setGridQueries(List<SelectQuery> gridQueries) {
        this.gridQueries = gridQueries;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }


    public String toFullString() {
        return "MailParams{" + "commitAndStart=" + commitAndStart + ", format=" + format + ", type=" + type + ", templateFile=" + templateFile + ", message=" + message + ", subject=" + subject + ", from=" + from + ", to=" + to + ", waitTime=" + waitTime + ", gridQueries=" + gridQueries + ", propertyFile=" + propertyFile + '}';
    }

    @Override
    public String toString() {
        return "MailParams{" + "format=" + format + ", subject=" + subject + ", from=" + from + ", to=" + to + '}';
    }
    
}
