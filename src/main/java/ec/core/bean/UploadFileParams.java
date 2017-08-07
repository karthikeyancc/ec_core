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
public class UploadFileParams {

    @XmlAttribute
    private boolean commitAndStart = true;
    @XmlAttribute
    private String field;
    @XmlAttribute
    private Type type = Type.MAIL;
    @XmlAttribute
    private String destFile;
    @XmlElementWrapper(name = "DestFileNameParams")
    @XmlElements(
            @XmlElement(name = "Param"))
    private List<Param> destFileNameParams = new ArrayList<Param>();

    public boolean isCommitAndStart() {
        return commitAndStart;
    }

    public void setCommitAndStart(boolean commitAndStart) {
        this.commitAndStart = commitAndStart;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getDestFile() {
        return destFile;
    }

    public void setDestFile(String destFile) {
        this.destFile = destFile;
    }

    public List<Param> getDestFileNameParams() {
        return destFileNameParams;
    }

    public void setDestFileNameParams(List<Param> destFileNameParams) {
        this.destFileNameParams = destFileNameParams;
    }

    @Override
    public String toString() {
        return "UploadFileParams{" + "commitAndStart=" + commitAndStart + ", field=" + field + ", type=" + type + ", destFile=" + destFile + ", destFileNameParams=" + destFileNameParams + '}';
    }
    
}
