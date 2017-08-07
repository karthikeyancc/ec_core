package ec.core;

import ec.core.bean.Param;
import ec.core.servlet.ReadServlet;
import ec.core.util.ECServerUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONGenerator {

    private static final Logger LOGGER = Logger.getLogger(ReadServlet.class.getName());
    private PrintWriter out;
    int limit = -1;
    int start = -1;

    public void process(PrintWriter out, String qu, String[] params, Connection con) throws SQLException, IOException {
        this.out = out;
        //db.query(con, qu, params);
        if (start > -1 && limit > -1 && !(qu.contains(" [Ll][Ii][Mm][Ii][Tt] "))) {
            qu = qu + " LIMIT " + start + " , " + limit;
        }

        try (PreparedStatement stmt = con.prepareStatement(qu)) {
            QueryRunner.setParams(params, stmt);
            ResultSet rs = stmt.executeQuery();
            handle(rs);
        } catch (SQLException sqle) {
            throw sqle;
        }
    }

    public void process(PrintWriter out, String qu, String[] params, Connection con, int limit) throws SQLException, IOException {
        this.limit = limit;
        this.start = -1;
        this.process(out, qu, params, con);
    }

    public void process(PrintWriter out, String qu, String[] params, Connection con, int start, int limit) throws SQLException, IOException {
        this.limit = limit;
        this.start = start;
        this.process(out, qu, params, con);
    }

    private void handle(ResultSet rs) throws SQLException {
        if (limit == 1) {
            firstRecord(rs);
        } else {
            allRecord(rs);
        }
    }

    private void firstRecord(ResultSet rs) throws SQLException {
        if (rs.next()) {
            java.sql.ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();
            for (int c = 1; c <= colCount; c++) {
                try {
                    obj.put(rsmd.getColumnLabel(c), ec.core.QueryRunner.isNumeric(rsmd.getColumnType(c)) ? rs.getLong(c) : rs.getString(c));
                } catch (JSONException ex) {
                    throw new RuntimeException("invalid data for column " + rsmd.getColumnLabel(c));
                }

            }
            out.write(obj.toString());
        }
    }

    private void allRecord(ResultSet rs) throws SQLException {
        java.sql.ResultSetMetaData rsmd = rs.getMetaData();
        JSONArray nameArray = new JSONArray();
        JSONArray typeArray = new JSONArray();
        int colCount = rsmd.getColumnCount();
        boolean[] intarr = new boolean[colCount];
        for (int c = 1; c <= colCount; c++) {
            nameArray.put(rsmd.getColumnLabel(c));
            typeArray.put(rsmd.getColumnTypeName(c).toUpperCase());
            if (ec.core.QueryRunner.isNumeric(rsmd.getColumnType(c))) {
                intarr[c - 1] = true;
            } else {
                intarr[c - 1] = false;
            }
        }
        int i = 0;
        while (rs.next()) {
            if (i == 0) {
                out.write("{\"colnames\":");
                out.println(nameArray.toString());
                out.println(",\"type\":");
                out.println(typeArray.toString());
                out.println(",\"data\":[");
            }
            JSONArray row = new JSONArray();
            for (int c = 1; c <= colCount; c++) {
                if (intarr[c - 1]) {
                    row.put(rs.getLong(c));
                } else {
                    row.put(rs.getString(c));
                }
            }
            if (i > 0) {
                out.append(',');
            }
            out.println(row.toString());
            i++;
        }
        if (i > 0) {
            out.println("],\"rowCount\":" + i + "}");
        }
        this.out = null;
    }

    public String[] findParams(List<Param> params, Map<String, String[]> map, String ecuser) {
        String[] s = new String[params.size()];
        int index = 0;
        for (Param param : params) {
            Object vals = map.get(param.getName());
            String val = param.getValue();
            LOGGER.log(Level.CONFIG, "param = " + param.toString() + " initial value = " + val + "ecuser = " + ecuser + " vals = " + vals);
            if (param.getName().equals(ECServerUtil.USER)) {
                val = ecuser;
                LOGGER.log(Level.CONFIG, " ecuser val = " + val);
            } else if (vals != null) {
                if (vals instanceof String[]) {
                    val = ((String[]) vals)[0];
                } else {
                    val = vals.toString();
                }
            }
            LOGGER.log(Level.CONFIG, " param set val = " + val);
            s[index] = val;
            index++;
        }
        return s;
    }

}
