package br.com.sweep.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import br.com.sweep.entity.Destinatario;

@Service
public class MailService {
    
    private final JavaMailSender javaMailSender;
    public static final String ASSUNTO = "Notificação de Sweep";
    public static final String ASSUNTO_ERRO = "Falha ao executar Sweep";

    @Autowired
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    public void sendEmailInicioSweep(List<Destinatario> destinatarios, String horaInicio, String dbaNome){
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try{
            for(Destinatario destinatario : destinatarios){
                String emailContent = getHtml("templates/mailComecoSweep.html");

                emailContent = emailContent.replace("{nome}", destinatario.getNome());
                emailContent = emailContent.replace("{horaInicio}", horaInicio);
                emailContent = emailContent.replace("{dba}", dbaNome);

                helper.setTo(destinatario.getEndereco());
                helper.setSubject(ASSUNTO);
                helper.setText(emailContent, true);

                javaMailSender.send(message);
            }

        }catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendEmailConclusaoSweep(List<Destinatario> destinatarios, String horaAtual, String tempoPassado, String dbaNome) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try{
            for(Destinatario destinatario : destinatarios){
                String emailContent = getHtml("templates/mailConclusaoSweep.html");

                emailContent = emailContent.replace("{nome}", destinatario.getNome());
                emailContent = emailContent.replace("{horaFinal}", horaAtual);
                emailContent = emailContent.replace("{tempoTotal}", tempoPassado);
                emailContent = emailContent.replace("{dba}", dbaNome);

                helper.setTo(destinatario.getEndereco());
                helper.setSubject(ASSUNTO);
                helper.setText(emailContent, true);

                javaMailSender.send(message);
            }

        }catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendEmailErroSweep(List<Destinatario> destinatarios, String historico, String dbaNome) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try{
            for(Destinatario destinatario : destinatarios){
                String emailContent = getHtml("templates/mailErroSweep.html");

                emailContent = emailContent.replace("{nome}", destinatario.getNome());
                emailContent = emailContent.replace("{historico}", historico);
                emailContent = emailContent.replace("{dba}", dbaNome);

                helper.setTo(destinatario.getEndereco());
                helper.setSubject(ASSUNTO_ERRO);
                helper.setText(emailContent, true);

                javaMailSender.send(message);
            }

        }catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String getHtml(String path){
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(path);

        String htmlContent = new BufferedReader(
            new InputStreamReader(inputStream, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining(System.lineSeparator()));

        return htmlContent;
    }
}
