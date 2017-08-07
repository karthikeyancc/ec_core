package ec.core.bean;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "Queries")
@XmlAccessorType(XmlAccessType.FIELD)
public class Queries implements HasChildren<SelectQuery>{
    @XmlElement(name="SelectQuery")
    private List<SelectQuery> queries=new ArrayList<SelectQuery>();

    public List<SelectQuery> getQueries() {
        return queries;
    }

    public void setQueries(List<SelectQuery> queries) {
        this.queries = queries;
    }
    public List<SelectQuery> getChildElements(){
        return queries;
    }
}

