package br.com.sweep.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import br.com.sweep.entity.Destinatario;
import br.com.sweep.service.MailService;

@Controller
public class SweepController{

   @Autowired MailService mailService;
   ProcessBuilder builder = new ProcessBuilder();

   @Value("${sweepService.destinatarios}") String destinatarios;
   @Value("${sweepService.dataBasesPath}") String diretorio;
   @Value("${sweepService.gfixPath}") String gfixPath;
   @Value("${sweepService.dbaNome}") String dbaNome;
   @Value("${sweepService.dbaUser}") String dbaUser;
   @Value("${sweepService.dbaSenha}") String dbaSenha;
   static final String COMANDOS_SWEEP = "%s -sweep %s -user %s -pas %s";

   @Scheduled(cron = "0 0 1 * * SUN")
   public void doSweep() {
      mailService.sendEmailInicioSweep(getDestinatarios(), getHoraAtual(), dbaNome);

      try{
         builder.command("bash", "-c", String.format(COMANDOS_SWEEP, gfixPath, dbaNome, dbaUser, dbaSenha));
         builder.directory(new File(diretorio));
         builder.redirectErrorStream(true);

         Process process = builder.start();

         Long tempoInicio = System.currentTimeMillis();

         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
         
         String linhaString;
         Integer linhaInt = 1;
         String historico = "";
         while ((linhaString = reader.readLine()) != null) {
            historico = historico.concat(linhaInt + ". " + reader.readLine() + "<br/>");
            System.out.println(linhaString);
            linhaInt++;
         }

         int exitCode = process.waitFor();
         System.out.println("Command exited with code: " + exitCode);

         if(exitCode == 0){
            mailService.sendEmailConclusaoSweep(getDestinatarios(), getHoraAtual(), getTempoPassado(tempoInicio), dbaNome);

         } else {
            mailService.sendEmailErroSweep(getDestinatarios(), historico, dbaNome);
         }       

      } catch (IOException | InterruptedException e){
         e.printStackTrace();
      }
   }


   private List<Destinatario> getDestinatarios() {
      List<Destinatario> destinatariosList = new ArrayList<>();
      List<String> destinatariosSplit = Arrays.stream(destinatarios.split(";")).collect(Collectors.toList());

      for (String destinatarioSplit : destinatariosSplit){
         destinatarioSplit.replace(";", "");
         String endereco = destinatarioSplit.substring(0, destinatarioSplit.indexOf("-"));
         String nome = destinatarioSplit.substring(destinatarioSplit.indexOf("-") + 1);

         Destinatario destinatario = new Destinatario(endereco, nome);
         destinatariosList.add(destinatario);
      }

      return destinatariosList;
   }
   
   private String getHoraAtual() {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
      LocalDateTime agora = LocalDateTime.now(); 
      
      return formatter.format(agora);
   }

   private String getTempoPassado(Long tempoInicial) {
      Long tempoSweep = System.currentTimeMillis() - tempoInicial;

      Long horas = TimeUnit.MILLISECONDS.toHours(tempoSweep);
      Long minutos = TimeUnit.MILLISECONDS.toMinutes(tempoSweep) - TimeUnit.HOURS.toMinutes(horas);

      String tempoGasto = String.format("%d"+"h " + "%d"+"m", horas, minutos);
      return tempoGasto;
   }

}