package app;

import adapters.application.EmailService;
import domain.Livro;
import domain.LivroRepository;
import java.util.List;

public class LivroService {
    private final LivroRepository repository;
    private final EmailService emailService;


    public LivroService(LivroRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    public void cadastrarLivro(Livro livro) {
        repository.salvar(livro);
        emailService.enviarEmail("josianebegnini@gmail.com", "Novo livro cadastrado", "Livro: " + livro.getTitulo());
    }

    public Livro buscarLivroPorId(Long id) {
        return repository.buscarPorId(id);
    }

    public List<Livro> listarTodosLivros() {
        return repository.listarTodos();
    }
}

