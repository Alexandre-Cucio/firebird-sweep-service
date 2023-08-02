# Sweep Service
Serviço em Java Spring para realização automatica do Sweep de bancos **Firebird**

## Requisitos
* O Banco e a Aplicação de Sweep devem estar rodando no mesmo ambiente **Linux**
* Setar como variaveis de ambiente:
    - *DATABASES_PATH*: Localização do banco de dados Firebird
    - *GFIX_PATH*: Localização do arquivo gfix para realização do Sweep
    - *DBA_NOME*: Nome do banco do arquvo '.fdb' com a extenção
    - *DBA_USER*: Usuário do banco de Dados
    - *DBA_SENHA*: Senha do banco de dados
    - *DESTINATARIOS_NOTIFICACAO_SWEEP*: Destinatarios dos e-mails de notificações. EX: *{endereço1}**-**{vocativo1}**;**{endereço2}**-**{vocativo2}*
    - *MAIL_ENDERECO*: Endereço da conta de envio dos e-mails
    - *MAIL_SENHA*: Senha da conta de envio dos e-mails
* Alterar *@Scheduled* do metodo *doSweep()*
    - Como padrão é configurado para todo **domingo** as **1:00AM**, mas caso tenha de ter alterado é só alterar o cron de acordo