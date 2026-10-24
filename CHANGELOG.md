# Changelog

Todas as alterações relevantes do Park Control serão documentadas neste arquivo.

## [Não lançado]

### Adicionado
- Campo opcional **Chave Pix** na tela de Configurações, permitindo cadastrar
  a chave usada para recebimento via Pix.
- Geração de **QR-Code de pagamento Pix** (padrão BR Code/EMV) ao registrar a
  saída do veículo, com o valor final já calculado (descontos de convênio ou
  manuais aplicados).
- Diálogo de pagamento Pix exibindo o QR-Code, o valor a pagar e a chave Pix
  utilizada, para que o cliente escaneie com o aplicativo do seu banco.

### Observações técnicas
- Novo utilitário `PixPayloadBuilder` (core/utils) responsável por montar o
  payload Pix "Copia e Cola" com cálculo de CRC16.
- Campo `pixKey` adicionado ao modelo `ParkingConfig` e persistido via
  DataStore, seguindo o mesmo padrão de repositório compartilhado já usado
  para os demais valores de configuração.
- Funcionalidade 100% offline, sem necessidade de gateway de pagamento ou
  servidor externo.

