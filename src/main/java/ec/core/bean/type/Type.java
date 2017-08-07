package ec.core.bean.type;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum Type {

    @XmlEnumValue("")
    NONE(0),
    @XmlEnumValue("JS_EXPRESSION")
    JS_EXPRESSION(1),
    @XmlEnumValue("JAVA_EXPRESSION")
    JAVA_EXPRESSION(2),
    @XmlEnumValue("SQL")
    SQL(3),
    @XmlEnumValue("SQL_RECORD")
    SQL_RECORD(9),
    @XmlEnumValue("INSERT")
    INSERT(4),
    @XmlEnumValue("UPD_DEL")
    UPD_DEL(5),
    @XmlEnumValue("SEL_INS")
    SEL_INS(6),
    @XmlEnumValue("MAIL")
    MAIL(7),
    @XmlEnumValue("HTTP")
    HTTP(8),
    @XmlEnumValue("UPLOAD_FILE")
    UPLOAD_FILE(9),
    @XmlEnumValue("DOWNLOAD_FILE")
    DOWNLOAD_FILE(10);
    private int val = 0;

    private Type(int id) {
        this.val = id;
    }

    /**
     * Gives the id for this Link Status instance.
     *
     * @return
     */
    public int val() {
        return this.val;
    }

}
