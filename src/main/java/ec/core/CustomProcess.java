package ec.core;

import java.sql.Connection;
import java.util.Map;

import org.json.JSONObject;

public interface CustomProcess {
    void processData(Connection con,JSONObject jsondata,int loopindex,Map<String,Object>map) throws ECException;
}
