package ec.core.bean.type;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum CallType {
	@XmlEnumValue("")NONE(0),
	@XmlEnumValue("JS_EXPRESSION")JS_EXPRESSION(1),
	@XmlEnumValue("JAVA_EXPRESSION")JAVA_EXPRESSION(2),
	@XmlEnumValue("SQL")SQL(3);
    private int val=0;
    private CallType(int id)
    {
        this.val = id;
    }

    /**
     * Gives the id for this Link Status instance.
     *
     * @return
     */
    public int val()
    {
        return this.val;
    }


}

