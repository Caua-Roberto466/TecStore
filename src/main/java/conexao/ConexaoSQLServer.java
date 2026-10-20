import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoSQLServer {

    private static final String HOST = obterConfiguracao("DB_HOST", "localhost");
    private static final String PORTA = obterConfiguracao("DB_PORT", "1433");
    private static final String BANCO = obterConfiguracao("DB_NAME", "VendaDB");
    private static final String USUARIO = obterConfiguracao("DB_USER", "sa");
    private static final String SENHA = obterConfiguracao("DB_PASSWORD", "");

    private static final String URL =
            "jdbc:sqlserver://" + HOST + ":" + PORTA + ";databaseName=" + BANCO
                    + ";encrypt=true;trustServerCertificate=true;loginTimeout=10";

    private static String obterConfiguracao(String nome, String padrao) {
        String valor = System.getenv(nome);
        return valor == null || valor.isBlank() ? padrao : valor;
    }

    public static Connection getConnection() throws SQLException {
        if (SENHA.isBlank()) {
            throw new SQLException("Configure a variável de ambiente DB_PASSWORD com a senha do SQL Server.");
        }
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do SQL Server não encontrado no classpath.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
