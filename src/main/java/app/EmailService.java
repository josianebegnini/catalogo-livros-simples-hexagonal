package app;

public interface EmailService {
    void enviarEmail(String destinatario, String assunto, String corpo);
}
