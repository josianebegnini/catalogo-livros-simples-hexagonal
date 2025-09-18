package domain;
import java.util.List;
import domain.Livro;

public interface LivroRepository {
    void salvar(Livro livro);
    Livro buscarPorId(Long id);
    List<Livro> listarTodos();
}
