# Changelog

Todas as alterações relevantes do Park Control serão documentadas neste arquivo.

## [Não lançado]

## [1.1.1] - 02-11-2026

### Adicionado
- Validação obrigatória dos campos **Preço** e **Valor da hora** na tela de
  Configurações, impedindo o salvamento de valores vazios ou inválidos.
- Snackbar de sucesso exibida ao salvar as configurações de estacionamento.

### Corrigido
- Suprimido aviso falso-positivo de dependência duplicada do `composeBom`.

### Documentação
- Atualizado `REFACTORING_ARCHITECTURE.md` para refletir o estado atual do
  código (persistência com Room, features de convênios, mensalistas,
  estacionamento, home e sobre, navegação e fluxo de pagamento Pix) e
  corrigida a formatação da tabela de Clean Architecture Grid.

## [1.1.0] - 24-10-2026

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

