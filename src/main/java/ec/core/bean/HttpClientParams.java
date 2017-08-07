/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this message file, choose Tools | Templates
 * and open the message in the editor.
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
public class HttpClientParams {
//    @XmlAttribute
//    private String loopQuery="SELECT 1 from dual";

    @XmlAttribute
    private boolean commitAndStart = true;
    @XmlAttribute
    private Type type = Type.HTTP;
    @XmlAttribute
    private String message;
    @XmlAttribute
    private String url;
    @XmlAttribute
    private String method;
    @XmlAttribute
    private String headerFields;
    @XmlElement(name = "UsernameParam", type = Param.class)
    private Param usernameParam;
    @XmlElement(name = "PasswordParam", type = Param.class)
    private Param passwordParam;
    @XmlElementWrapper(name = "Headers")
    @XmlElements(
            @XmlElement(name = "Param", type = Param.class))
    private List<Param> headerParams = new ArrayList<Param>();
    @XmlElementWrapper(name = "UrlParams")
    @XmlElements(
            @XmlElement(name = "Param", type = Param.class))
    private List<Param> urlParams = new ArrayList<Param>();
    @XmlElementWrapper(name = "MessageParams")
    @XmlElements(
            @XmlElement(name = "Param", type = Param.class))
    private List<Param> messageParams = new ArrayList<Param>();

    public boolean isCommitAndStart() {
        return commitAndStart;
    }

    public void setCommitAndStart(boolean commitAndStart) {
        this.commitAndStart = commitAndStart;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getHeaderFields() {
        return headerFields;
    }

    public void setHeaderFields(String headerFields) {
        this.headerFields = headerFields;
    }

    public Param getUsernameParam() {
        return usernameParam;
    }

    public void setUsernameParam(Param usernameParam) {
        this.usernameParam = usernameParam;
    }

    public Param getPasswordParam() {
        return passwordParam;
    }

    public void setPasswordParam(Param passwordParam) {
        this.passwordParam = passwordParam;
    }

    public List<Param> getHeaderParams() {
        return headerParams;
    }

    public void setHeaderParams(List<Param> headerParams) {
        this.headerParams = headerParams;
    }

    public List<Param> getUrlParams() {
        return urlParams;
    }

    public void setUrlParams(List<Param> urlParams) {
        this.urlParams = urlParams;
    }

    public List<Param> getMessageParams() {
        return messageParams;
    }

    public void setMessageParams(List<Param> messageParams) {
        this.messageParams = messageParams;
    }

    @Override
    public String toString() {
        return "HttpClientParams{" + "commitAndStart=" + commitAndStart + ", type=" + type + ", message=" + message + ", url=" + url + ", method=" + method + ", headerFields=" + headerFields + ", usernameParam=" + usernameParam + ", passwordParam=" + passwordParam + ", headerParams=" + headerParams + ", urlParams=" + urlParams + ", messageParams=" + messageParams + '}';
    }

}
