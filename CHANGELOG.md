# Changelog

Todas as alterações relevantes do Park Control serão documentadas neste arquivo.

## [Não lançado]

## [1.1.2] - 03-11-2026

### Corrigido
- Removido o fundo escuro indevido do card **Primeiros 30 Minutos** na tela
  de Configurações, aplicando `colorScheme.surface` para manter a mesma cor
  de fundo dos demais cards da tela.
- Padronizada a mensagem de sucesso exibida ao salvar o cadastro de
  estacionamento: agora utiliza um Toast com ícone à esquerda e animação de
  subida.
- Padronizada a mensagem de sucesso exibida ao salvar a tela de
  Configurações: agora utiliza um Toast com ícone à esquerda e animação de
  subida.
- Padronizadas as mensagens de sucesso na tela de Clientes: agora utilizam um Toast com
  ícone à esquerda e animação de subida.
- Padronizadas as mensagens de sucesso na tela de cadastro de veículo do cliente: agora utilizam um Toast com ícone
  à esquerda e animação de subida(o salvamento comum não exibia nenhuma
  mensagem de sucesso).
- Padronizadas as mensagens de sucesso na tela de Convênios: agora utilizam um Toast com ícone à esquerda e
  animação de subida, no lugar do Snackbar exibido anteriormente após navegar
  de volta para a lista.
- Ajustado o tamanho da fonte do placeholder do campo **Complemento** na tela
  de cadastro de veículo no estacionamento, utilizando o estilo
  `bodySmall` para melhor adequação ao espaço do campo.
- Ajustado o tamanho da fonte do placeholder do campo **Complemento** na tela
  de cadastro de convênio, utilizando o estilo `bodySmall` para melhor
  adequação ao espaço do campo.

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

