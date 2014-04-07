package com.tunnelmanager.server.database;


import com.tunnelmanager.utils.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class Database
 * exec select queries to database
 *
 * @author Pierre-Olivier on 07/03/14.
 */
public class Database {
    /**
     * Single connection instance
     */
    private static Connection connect = null;

    /**
     * Table prefix
     */
    private static String tablePrefix;

    /**
     * List of opened statements, the method clean closes all statements of the current instance
     */
    private List<Statement> statements;

    /**
     * List of opened resultSets, the method clean closes all resultSets of the current instance
     */
    private List<ResultSet> resultSets;

    /**
     * Default constructor
     */
    public Database() {
        super();

        this.statements = new ArrayList<>();
        this.resultSets = new ArrayList<>();
    }

    /**
     * Private method for mysql connection
     */
    private void connect() throws ClassNotFoundException, SQLException, IOException {
        if(Database.connect == null || Database.connect.isClosed()) {
            // Load config file
            Properties prop = new Properties();
            InputStream input = new FileInputStream("config.properties");
            prop.load(input);

            String host = prop.getProperty("database_host");
            String database = prop.getProperty("database_name");
            String user = prop.getProperty("database_user");
            String password = prop.getProperty("database_password");

            Database.tablePrefix = prop.getProperty("database_prefix");

            Class.forName("com.mysql.jdbc.Driver");
            Database.connect = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?user=" + user + "&password=" + password);
        }
    }

    /**
     * mysql query
     * @param query SELECT mysql query
     * @return list of results
     */
    public ResultSet query(String query) throws SQLException, ClassNotFoundException, IOException {
        connect();

        Statement statement;
        ResultSet resultSet;

        query = query.replaceAll("prefix_", Database.tablePrefix);

        Log.e(query);

        statement = Database.connect.createStatement();
        resultSet = statement.executeQuery(query);

        this.statements.add(statement);
        this.resultSets.add(resultSet);

        return resultSet;
    }

    /**
     * mysql exec
     * @param exec INSERT/UPDATE
     * @return number of exec
     */
    public int exec(String exec) throws SQLException, ClassNotFoundException, IOException {
        connect();

        Statement statement;

        exec = exec.replaceAll("prefix_", Database.tablePrefix);

        Log.e(exec);

        statement = Database.connect.createStatement();

        int nbUpdated = statement.executeUpdate(exec);

        this.statements.add(statement);

        return nbUpdated;
    }

    /**
     * close all Statement and ResultSet of the current instance
     */
    public void clean() {
        try {
            for(ResultSet resultSet : this.resultSets) {
                if(!resultSet.isClosed())
                    resultSet.close();
            }

            for(Statement statement : this.statements) {
                if(!statement.isClosed())
                    statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * if user forgot to clean
     */
    @Override
    protected void finalize() throws Throwable {
        clean();

        super.finalize();
    }
}
