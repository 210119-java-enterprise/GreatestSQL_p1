package com.revature.GSQL;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import com.revature.Connection.ConnectionFactory;
import com.revature.META.MetaConstructor;
import com.revature.ObjectMapper.*;


/**
 * Class which serves as the public face for Greatest SQL.
 * Contains methods to add a metamodel,
 * add to database, retrieve from database,
 * and make Transactions(commit, savepoint,rollback).
 *
 * Must use the 'getInstance' method to get an instance of the class before using any of its methods.
 */
public class GSQL {
    final private static GSQL gsql = new GSQL();
    private final MetaConstructor construct;
    private final ObjectSaver obj_saver;
    private final ObjectGetter obj_getter;
    private final ObjectRemover obj_remover;
    private final ObjectUpdater obj_updater;
    private final Transactions transaction;
    private final Connection conn;

    private GSQL() {
        super();
        conn        = ConnectionFactory.getInstance().getConnection();
        construct   = MetaConstructor.getInstance();
        obj_saver   = ObjectSaver.getInstance();
        obj_getter  = ObjectGetter.getInstance();
        obj_remover = ObjectRemover.getInstance();
        obj_updater = ObjectUpdater.getInstance();
        transaction = Transactions.getTransaction();
    }

    /**
     * get instance of this singleton class.
     * @return instance of GSQL class.
     */
    public static GSQL getInstance() {
        return gsql;
    }

    /**
     * Add a class to the metamodel Map. Class must be added before GSQL can use the annotations to build/read an obj.
     * @param clazz Class of ob ject to be added to metamodel map.
     * @return boolean indicating whether the operation succeeded.
     */
    public boolean addClass(final Class<?> clazz) {
        construct.addModel(clazz);
        return true;
    }

    /**
     * Updates an Object which already exists in a database. If auto commit is not enabled then user must call 'beginCommit' to save updates to data base.
     * @param obj Object ot be updated.
     * @param update_columns Comma separated string of columns to update.
     * @return boolean to indicate the whether operation succeeded.
     */
    public boolean UpdateObjectInDB(final Object obj,final String update_columns) {
        return obj_updater.updateObject(obj,update_columns,conn);
    }

    /**
     * Remove a given object from the database.
     * @param obj Object to be removed.
     * @return boolean indicating success or failure of operation.
     */
    public boolean removeObjectFromDB(final Object obj) {
        return obj_remover.removeObjectFromDB(obj,conn);
    }

    /**
     * Add an Object to the Database.
     * @param obj Object to add to database.
     * @return boolean to indicated success or failure of operation.
     */
    public boolean addObjectToDB(final Object obj) {
        return obj_saver.saveObject(obj,conn);
    }

    /**
     * Returns a List of all objects from database matching given criteria.
     * @param clazz Class of objects to retrieve.
     * @param columns comma separated string of name of columns to use as identifiers of object in database.
     * @param conditions comma separated string of values for the columns in database to match against.
     * @return Optional containing a List of objects matching the criteria, or an empty optional if no matches are found.
     */
    public Optional<List<Object>> getListObjectFromDB(final Class <?> clazz, final String columns, final String conditions) {
        return obj_getter.getListObjectFromDB(clazz,columns,conditions,"",conn);
    }

    /**
     * Returns a List of all objects from database matching given criteria.
     * @param clazz Class of objects to retrieve.
     * @param columns comma separated string of name of columns to use as identifiers of object in database.
     * @param conditions comma separated string of values for the columns in database to match against.
     * @param operators comma separated string of operators (AND/OR) to be applied to the columns and conditions.
     * @return Optional containing a List of objects matching the criteria, or an empty optional if no matches are found.
     */
    public Optional<List<Object>> getListObjectFromDB(final Class <?> clazz, final String columns, final String conditions,final String operators) {
        return obj_getter.getListObjectFromDB(clazz,columns,conditions,operators,conn);
    }

    /**
     * run a commit.
     */
    public void beginCommit() {
        transaction.Commit(conn);
    }

    /**
     * Rollback to previous commit.
     */
    public void Rollback() {
        transaction.Rollback(conn);
    }

    /**
     * Rollback to savepoint with the given name.
     * @param name name of savepoint to rollback to.
     */
    public void Rollback(final String name) {
        transaction.Rollback(name,conn);
    }

    /**
     * Set a new savepoint with the given name.
     * @param name name to apply to savepoint.
     */
    public void setSavepoint(final String name) {
        transaction.Savepoint(name,conn);
    }

    /**
     * Release a savepoint with the given name.
     * @param name name of savepoint.
     */
    public void ReleaseSavepoint(final String name) {
        transaction.ReleaseSavepoint(name,conn);
    }

    /**
     * Turns on autocommit. This disables manual commits.
     */
    public void enableAutoCommit() {
        transaction.enableAutoCommit(conn);
    }

    /**
     * Start a new transaction command.
     */
    public void setTransaction() {
        transaction.setTransaction(conn);
    }
}
