package ec.core.bean;

import java.io.Serializable;
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
public class SelectQuery implements Serializable, HasId {

    @XmlAttribute
    private String id;
    @XmlAttribute
    private String operation;
    @XmlAttribute
    private String baseQuery;
    @XmlElementWrapper(name = "OperationParams")
    @XmlElements(
            @XmlElement(name = "Param"))
    private List<Param> operationParams = new ArrayList<Param>();

    private boolean whereExists;

    @XmlAttribute
    private String dispFields;

    private String displaytemplate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isWhereExists() {
        return whereExists;
    }

    public void setWhereExists(boolean whereExists) {
        this.whereExists = whereExists;
    }

    public String getDispFields() {
        return dispFields;
    }

    public void setDispFields(String dispFields) {
        this.dispFields = dispFields;
    }

    public String getDisplaytemplate() {
        return displaytemplate;
    }

    public void setDisplaytemplate(String displaytemplate) {
        this.displaytemplate = displaytemplate;
    }

    public String getBaseQuery() {
        return baseQuery;
    }

    public void setBaseQuery(String baseQuery) {
        this.baseQuery = baseQuery;
    }

    @Override
    public String toString() {
        return "SelectQuery{" + "id=" + id + ", operation=" + operation + ", baseQuery=" + baseQuery + ", operationParams=" + operationParams + ", whereExists=" + whereExists + ", dispFields=" + dispFields + ", displaytemplate=" + displaytemplate + '}';
    }

}
