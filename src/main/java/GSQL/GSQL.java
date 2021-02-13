package GSQL;

import java.sql.Connection;
import Meta.MetaConstructor;
import Connection.ConnectionFactory;
import ObjectMapper.ObjectMapper;
import ObjectMapper.ObjectSaver;
import ObjectMapper.ObjectGetter;

public class GSQL {
    private Connection conn;
    private MetaConstructor construct;
    private ObjectSaver obj_saver;
    private ObjectGetter obj_getter;
    final private static GSQL gsql = new GSQL();

    private GSQL() {
        super();
        conn       = ConnectionFactory.getInstance().getConnection();
        construct  = MetaConstructor.getInstance();
        obj_saver  = ObjectSaver.getInstance();
        obj_getter = ObjectGetter.getInstance();
    }

    public static GSQL getInstance() {
        return gsql;
    }

    public boolean addClass(final Class<?> clazz) {
        construct.addModel(clazz);
        return true;
    }

    public boolean addObjectToDB(final Object obj) {
        return obj_saver.saveObject(obj,conn);
    }

    public Object getObjectFromDB(final Class <?> clazz, final String columns, final String conditions) {
        return obj_getter.getObjectFromDB(clazz,columns,conditions,"",conn);
    }

    public Object getObjectFromDB(final Class <?> clazz, final String columns, final String conditions,final String operators) {
        return obj_getter.getObjectFromDB(clazz,columns,conditions,operators,conn);
    }


}
