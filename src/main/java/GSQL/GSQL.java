package GSQL;

import java.sql.Connection;
import java.util.List;

import Meta.MetaConstructor;
import Connection.ConnectionFactory;
import ObjectMapper.ObjectRemover;
import ObjectMapper.ObjectSaver;
import ObjectMapper.ObjectGetter;

public class GSQL {
    private final Connection conn;
    private final MetaConstructor construct;
    private final ObjectSaver obj_saver;
    private final ObjectGetter obj_getter;
    private final ObjectRemover obj_remover;
    final private static GSQL gsql = new GSQL();

    private GSQL() {
        super();
        conn        = ConnectionFactory.getInstance().getConnection();
        construct   = MetaConstructor.getInstance();
        obj_saver   = ObjectSaver.getInstance();
        obj_getter  = ObjectGetter.getInstance();
        obj_remover = ObjectRemover.getInstance();
    }

    public static GSQL getInstance() {
        return gsql;
    }

    public boolean addClass(final Class<?> clazz) {
        construct.addModel(clazz);
        return true;
    }

    public boolean removeObjectFromDB(final Object obj) {
        return obj_remover.removeObjectFromDB(obj,conn);
    }


    public boolean addObjectToDB(final Object obj) {
        return obj_saver.saveObject(obj,conn);
    }

    public List<Object> getListObjectFromDB(final Class <?> clazz, final String columns, final String conditions) {
        return obj_getter.getListObjectFromDB(clazz,columns,conditions,"",conn);
    }

    public List<Object> getListObjectFromDB(final Class <?> clazz, final String columns, final String conditions,final String operators) {
        return obj_getter.getListObjectFromDB(clazz,columns,conditions,operators,conn);
    }


}
