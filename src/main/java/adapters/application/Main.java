package adapters.application;

import adapters.in.LivroController;
import adapters.out.EmailSender;
import adapters.out.LivroRepositoryH2;
import app.LivroService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        try {
            // Conectar ao banco H2 em memória
            Connection connection = DriverManager.getConnection("jdbc:h2:mem:livros", "sa", "");

            // Criar tabela livros
            Statement stmt = connection.createStatement();
            stmt.execute("CREATE TABLE livros (" +
                    "id BIGINT PRIMARY KEY, " +
                    "titulo VARCHAR(255), " +
                    "autor VARCHAR(255), " +
                    "anoPublicacao INT)");

            // Instanciar os componentes da arquitetura hexagonal
            LivroRepositoryH2 repository = new LivroRepositoryH2(connection);
            EmailSender emailSender = new EmailSender();
            LivroService service = new LivroService(repository, emailSender);
            LivroController controller = new LivroController(service);

            // Iniciar o servidor REST
            controller.startServer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
