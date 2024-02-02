package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableChangeOwner;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.antdb.parser.AntDBLexer;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGLexer;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

/**
 * PG alter语法解析
 *
 * @author YAOZJ
 * @version v1.0
 * @date 2024/1/30 9:48
 */
public class PGAlterTest extends TestCase {

    public void testPgAlterTableChangeOwner() {
        String sql = "alter table alter_table_test\n" + "    owner to test_alter";
        SQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) parser.parseStatement();
        String stmtStr = null;
        try {
            stmtStr = stmt.toString();
        } catch (ClassCastException e) {
            Assert.fail(e.getMessage());
        }

        System.out.println(stmtStr);
        assert stmt.getTableName().equals("alter_table_test");
        assert stmt.getItems().size() == 1;
        assert stmt.getItems().get(0) instanceof SQLAlterTableChangeOwner;
        assert (((SQLAlterTableChangeOwner) stmt.getItems().get(0)).getOwner().getSimpleName()).equals("test_alter");
    }

    public void testAntDBAlterTableChangeOwner() {
        String sql = "/*pg*/alter table alter_table_test owner to test_alter";
//        String sql = "alter/*pg*/ table alter_table_test owner to test_alter";

        AntDBLexer antDBLexer = new AntDBLexer(sql, SQLParserFeature.KeepComments);
        SQLStatementParser parser = new PGSQLStatementParser(new PGExprParser(antDBLexer));
        antDBLexer.nextToken();
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) parser.parseStatement();
        try {
            stmt.toString();
        } catch (ClassCastException e) {
            Assert.fail(e.getMessage());
        }

        System.out.println(SQLUtils.toPGString(stmt));
    }

    public void testAntDBSelectGrammar() {
        String sql = "/*pg*/ select * from dual";
//        String sql = "/*pg*/ select * from (select distinct a from test_table)";
//        String sql = " select * /*pg*/from dual";

        AntDBLexer antDBLexer = new AntDBLexer(sql, SQLParserFeature.KeepComments);
        SQLStatementParser parser = new PGSQLStatementParser(new PGExprParser(antDBLexer));
        antDBLexer.nextToken();
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatement();
        try {
            stmt.toString();
        } catch (ClassCastException e) {
            Assert.fail(e.getMessage());
        }

        System.out.println(SQLUtils.toPGString(stmt));
    }

}
