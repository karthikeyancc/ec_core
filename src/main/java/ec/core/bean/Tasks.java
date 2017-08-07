package ec.core.bean;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "Tasks")
@XmlAccessorType(XmlAccessType.FIELD)
public class Tasks implements HasChildren<Task>{
    @XmlElement(name="Task")
    private List<Task> tasks=new ArrayList<Task>();

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public List<Task> getChildElements() {
        return tasks;
    }
    
}

