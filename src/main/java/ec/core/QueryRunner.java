/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.core;

import ec.core.util.ECServerUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author karthikeyan
 */
public class QueryRunner {

    public static boolean isNumeric(int i) {
        return i == Types.BIGINT || i == Types.BIT || i == Types.DECIMAL || i == Types.DOUBLE
                || i == Types.FLOAT || i == Types.INTEGER || i == Types.NUMERIC || i == Types.REAL
                || i == Types.ROWID || i == Types.SMALLINT || i == Types.TINYINT;
    }

    public void query(Connection con, String operation, String[] findParams, JSONObject obj,ScriptEngine engine) throws SQLException, JSONException, ScriptException {
        try (PreparedStatement stmt = con.prepareStatement(operation)) {
            setParams(findParams, stmt);
            ResultSet rs = stmt.executeQuery();
            java.sql.ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                getRecordJSON(rsmd, obj, rs,engine);
            }
        } catch (SQLException sqle) {
            throw sqle;
        }

    }

    public void getRecordJSON(ResultSetMetaData rsmd, JSONObject obj, ResultSet rs,ScriptEngine engine) throws JSONException, SQLException, ScriptException {
        int colCount = rsmd.getColumnCount();
        for (int c = 1; c <= colCount; c++) {
            obj.put(rsmd.getColumnLabel(c), isNumeric(rsmd.getColumnType(c)) ? rs.getLong(c) : rs.getString(c));
            if(engine!=null){
                engine.eval("data['"+rsmd.getColumnLabel(c)+"']="+(isNumeric(rsmd.getColumnType(c)) ? rs.getLong(c) : "'"+rs.getString(c)+"'"));
            }
        }
    }

    public String query(Connection con, String operation, String[] findParams) throws SQLException, JSONException {
        String ret = null;
        try (PreparedStatement stmt = con.prepareStatement(operation)) {
            setParams(findParams, stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ret = rs.getString(1);
            }
        } catch (SQLException sqle) {
            throw sqle;
        }
        return ret;

    }

    public String insert(Connection con, String operation, String[] findParams) throws SQLException {
        String ret = null;
        try (PreparedStatement stmt = con.prepareStatement(operation, PreparedStatement.RETURN_GENERATED_KEYS)) {
            setParams(findParams, stmt);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                ret = rs.getString(1);
            }
        } catch (SQLException sqle) {
            throw sqle;
        }
        return ret;
    }

    public void update(Connection con, String operation, String[] findParams) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement(operation)) {
            setParams(findParams, stmt);
            stmt.executeUpdate();
        } catch (SQLException sqle) {
            throw sqle;
        }
    }

    public static void setParams(String[] findParams, final PreparedStatement stmt) throws SQLException {
        int i = 1;
        for (String param : findParams) {
            if(param!=null&& (!param.equals("null"))){
                stmt.setString(i, param);
            }else{
                stmt.setNull(i, Types.NULL);
            }
            i++;
        }
    }

}
