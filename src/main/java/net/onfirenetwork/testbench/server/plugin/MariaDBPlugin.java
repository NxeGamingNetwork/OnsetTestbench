package net.onfirenetwork.testbench.server.plugin;

import eu.bebendorf.ajorm.SQL;
import eu.bebendorf.ajorm.wrapper.MySQL;
import net.onfirenetwork.testbench.lua.FunctionHandler;
import net.onfirenetwork.testbench.server.ServerPackageEnv;
import org.luaj.vm2.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class MariaDBPlugin implements ServerPlugin {

    private Map<Integer, Handle> connections = new HashMap<>();
    private int nextId = 1;
    private Map<Integer, Result> results = new HashMap<>();
    private int nextResultId = 1;
    private int current = 0;

    public void enable() {

    }

    public void disable() {

    }

    public void enablePackage(String packageName, ServerPackageEnv env) {
        env.add("mariadb_connect", args -> {
            SQL sql = new MySQL(args.tojstring(1), 3306, args.tojstring(4), args.tojstring(2), args.tojstring(3));
            int id = nextId;
            nextId++;
            connections.put(id, new Handle(sql));
            return result(LuaNumber.valueOf(id));
        });
        env.add("mariadb_close", args -> {
            int handleId = args.toint(1);
            Handle handle = connections.get(handleId);
            try {
                handle.sql.getConnection().close();
            } catch (SQLException e) {
            }
            connections.remove(handleId);
            return result();
        });
        env.add("mariadb_prepare", args -> {
            Handle handle = connections.get(args.toint(1));
            LuaTable varargs = args.arg(3).isnil()?new LuaTable():(LuaTable) args.arg(3);
            if(varargs.length() == 1 && varargs.get(1).istable()){
                varargs = (LuaTable) varargs.get(1);
            }
            LuaValue[] values = new LuaValue[varargs.length()];
            for(int i=1; i<=values.length; i++){
                values[i-1] = varargs.get(i);
            }
            PreparedQuery prepared = new PreparedQuery(args.tojstring(2), values);
            String id = "p-"+UUID.randomUUID();
            handle.preparedQueries.put(id, prepared);
            return result(LuaString.valueOf(id));
        });
        FunctionHandler queryHandler = args -> {
            Handle handle = connections.get(args.toint(1));
            String query = args.tojstring(2);
            Object[] values = new Object[0];
            if(query.startsWith("p-")){
                String id = query;
                query = handle.preparedQueries.get(id).query;
                values = handle.preparedQueries.get(id).values;
                handle.preparedQueries.remove(id);
            }
            Result result;
            if(query.startsWith("SELECT") || query.startsWith("SHOW")){
                ResultSet rs = handle.sql.read(query, values);
                result = new Result(rs);
                handle.sql.close(rs);
            }else{
                handle.sql.write(query, values);
                result = new Result();
            }
            int id = nextResultId;
            nextResultId++;
            results.put(id, result);
            current = id;
            LuaFunction callback = args.isnil(3) ? null : (LuaFunction) args.arg(3);
            LuaTable varargs = args.arg(4).isnil()?new LuaTable():(LuaTable) args.arg(4);
            LuaValue[] pass = new LuaValue[varargs.length()];
            for(int i=1; i<=pass.length; i++){
                pass[i-1] = varargs.get(i);
            }
            if(callback != null){
                switch (pass.length){
                    case 1:
                        callback.call(pass[0]);
                        break;
                    case 2:
                        callback.call(pass[0], pass[1]);
                        break;
                    case 3:
                        callback.call(pass[0], pass[1], pass[2]);
                        break;
                    default:
                        callback.call();
                        break;
                }
            }
            current = 0;
            results.remove((Integer) id);
            return result();
        };
        env.add("mariadb_query", queryHandler);
        env.add("mariadb_async_query", queryHandler);
        env.add("mariadb_await_query", args -> {
            Handle handle = connections.get(args.toint(1));
            String query = args.tojstring(2);
            Object[] values = new Object[0];
            if(query.startsWith("p-")){
                String id = query;
                query = handle.preparedQueries.get(id).query;
                values = handle.preparedQueries.get(id).values;
                handle.preparedQueries.remove(id);
            }
            Result result;
            if(query.startsWith("SELECT") || query.startsWith("SHOW")){
                ResultSet rs = handle.sql.read(query, values);
                result = new Result(rs);
                handle.sql.close(rs);
            }else{
                handle.sql.write(query, values);
                result = new Result();
            }
            int id = nextResultId;
            nextResultId++;
            results.put(id, result);
            current = id;
            return result(LuaInteger.valueOf(id));
        });
        env.add("mariadb_set_active_result", args -> {
            current = args.toint(1);
            return result();
        });
        env.add("mariadb_delete_result", args -> {
            int rid = args.toint(1);
            if(current == rid)
                current = 0;
            results.remove((Integer) rid);
            return result();
        });
        env.add("mariadb_get_row_count", args -> {
            if(current == 0)
                return result(LuaInteger.valueOf(0));
            return result(LuaInteger.valueOf(results.get(current).rows.length));
        });
        env.add("mariadb_get_row_count", args -> {
            if(current == 0)
                return result(LuaInteger.valueOf(0));
            return result(LuaInteger.valueOf(results.get(current).rows.length));
        });
        env.add("mariadb_get_field_count", args -> {
            if(current == 0)
                return result(LuaInteger.valueOf(0));
            return result(LuaInteger.valueOf(results.get(current).columnNames.length));
        });
        env.add("mariadb_get_field_name", args -> {
            if(current == 0)
                return result(LuaInteger.valueOf(0));
            return result(LuaString.valueOf(results.get(current).columnNames[args.toint(1)-1]));
        });
        env.add("mariadb_get_value_index", args -> {
            Object value = getValue(args.toint(1), args.toint(2));
            if(value == null)
                return result(LuaValue.NIL);
            return result(LuaString.valueOf((String) value));
        });
        env.add("mariadb_get_value_index_int", args -> {
            Object value = getValue(args.toint(1), args.toint(2));
            if(value == null)
                return result(LuaValue.NIL);
            return result(LuaInteger.valueOf((int) value));
        });
        env.add("mariadb_get_value_index_float", args -> {
            Object value = getValue(args.toint(1), args.toint(2));
            if(value == null)
                return result(LuaValue.NIL);
            return result(LuaNumber.valueOf((float) value));
        });
        env.add("mariadb_get_value_name", args -> {
            Object value = getValue(args.toint(1), args.tojstring(2));
            if(value == null)
                return result(LuaValue.NIL);
            return result(LuaString.valueOf((String) value));
        });
        env.add("mariadb_get_value_name_int", args -> {
            Object value = getValue(args.toint(1), args.tojstring(2));
            if(value == null)
                return result(LuaValue.NIL);
            return result(LuaInteger.valueOf((int) value));
        });
        env.add("mariadb_get_value_name_float", args -> {
            Object value = getValue(args.toint(1), args.tojstring(2));
            if(value == null)
                return result(LuaValue.NIL);
            return result(LuaNumber.valueOf((float) value));
        });
        env.add("mariadb_get_insert_id", args -> {
            Handle handle = connections.get(args.toint(1));
            ResultSet rs = handle.sql.read("SELECT LAST_INSERT_ID();");
            int id = 0;
            try {
                rs.next();
                id = rs.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            handle.sql.close(rs);
            return result(LuaInteger.valueOf(id));
        });
    }

    public void disablePackage(String packageName, ServerPackageEnv env) {

    }

    private Object getValue(int row, String name){
        int index = getIndex(name);
        if(index == 0)
            return null;
        return getValue(row, index);
    }

    private Object getValue(int row, int index){
        return results.get(current).rows[row-1][index-1];
    }

    private int getIndex(String name){
        String[] names = results.get(current).columnNames;
        for(int i=0; i<names.length; i++){
            if(names[i].equals(name)){
                return i+1;
            }
        }
        return 0;
    }

    private class Result {
        String[] columnNames;
        Object[][] rows;
        Result(){
            columnNames = new String[0];
            rows = new Object[0][];
        }
        Result(ResultSet resultSet){
            try {
                ResultSetMetaData metaData = resultSet.getMetaData();
                columnNames = new String[metaData.getColumnCount()];
                for(int i=1; i<=columnNames.length; i++){
                    columnNames[i-1] = metaData.getColumnName(i);
                }
                List<Object[]> rowList = new ArrayList<>();
                while (resultSet.next()){
                    Object[] row = new Object[columnNames.length];
                    for(int i=0; i<row.length; i++){
                        row[i] = resultSet.getObject(i+1);
                    }
                    rowList.add(row);
                }
                rows = rowList.toArray(new Object[0][]);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private class Handle {
        SQL sql;
        Map<String, PreparedQuery> preparedQueries = new HashMap<>();
        Handle(SQL sql){
            this.sql = sql;
        }
    }

    private class PreparedQuery {
        String query;
        Object[] values;
        PreparedQuery(String query, LuaValue... values){
            this.query = query;
            this.values = new Object[values.length];
            for(int i=0; i<values.length; i++){
                if(values[i].isboolean()){
                    this.values[i] = values[i].toboolean();
                }
                if(values[i].isint()){
                    this.values[i] = values[i].toint();
                }
                if(values[i].isnumber()){
                    this.values[i] = values[i].todouble();
                }
                if(values[i].isstring()){
                    this.values[i] = values[i].tojstring();
                }
            }
        }
    }

}
