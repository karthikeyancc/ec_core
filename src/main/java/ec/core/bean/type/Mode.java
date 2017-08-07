package ec.core.bean.type;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum

public enum Mode {
	@XmlEnumValue("1")ADD(1),
	@XmlEnumValue("2")MODIFY(2),
	@XmlEnumValue("3")DELETE(3),
	@XmlEnumValue("4")VIEW(4);
    private int val=1;
    private Mode(int id)
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
