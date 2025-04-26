package studio.trc.bukkit.litesignin.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import studio.trc.bukkit.litesignin.database.engine.SQLQuery;

public interface DatabaseEngine
{
    /**
     * Connect to database.
     */
    void connect();
    
    /**
     * Disconnect from database.
     */
    void disconnect();
    
    /**
     * Check connection status.
     */
    void checkConnection() throws SQLException;
    
    /**
     * Execute update syntax.
     * @return How much rows affected.
     */
    int executeUpdate(String sqlSyntax, String... values);
    
    /**
     * Execute multi queries.
     * @return How much rows affected.
     */
    int[] executeMultiQueries(String sqlSyntax, List<Map<Integer, String>> parameters);
    
    /**
     * Execute query syntax.
     * @return The results.
     */
    SQLQuery executeQuery(String sqlSyntax, String... values);
    
    /**
     * Get database connection intance.
     */
    Connection getConnection();
    
    /**
     * Throw SQL exception.
     */
    void throwSQLException(Exception exception, String path, boolean reconnect);
    
    /**
     * Initialization method.
     */
    void initialize();
}
