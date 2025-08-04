# Calculadora WIT Challenge

Este projeto implementa um microserviço para realizar operações matemáticas (soma, subtração, multiplicação e divisão) através de APIs REST com comunicação assíncrona através de Kafka. Toda a infraestrutura está hospedade no Docker.

## Requisitos
- Docker
- Java 17
- Maven
- Postman (opcional)

## Como construir o projeto

1 - Clone o repositório:
```bash
git clone https://github.com/SCFrancisco2221857/WITChallenge.git
cd WITChallenge
```
2 - Compilar os módulos:
```bash
mvn clean install
```
3- Iniciar o Docker:
```bash
docker-compose up -d
```

## Endpoints da API
- GET /api/calculation/sum?a=10&b=5
- GET /api/calculation/subtract?a=10&b=5
- GET /api/calculation/multiply?a=10&b=5
- GET /api/calculation/divide?a=10&b=5

## Correr testes
Para correr os testes, execute o seguinte comando:
```bash
mvn test
```
## Estrutura do projeto
O projeto é dividido em três módulos principais:
- **/commom**: Contem classes comuns e utilitários compartilhados entre os outros módulos, tais como, os models CalculationRequest e CalculationResponse e a enum Operation. Tem ainda as configurações do kafka.
- **/rest**: Contem um controller com todos os endpoins mencionados em cima. Contem dois services um que é o OperationService onde tem a lógica que negócio , ali é onde os pedidos são enviados para o tópico em kafka e onde aguarda no máximo até 5 segundos para receber um sinal do outro serviço deste módulo que se chama CalculationResponse. Este último módulo tem como principal objetivo consumir as mensagens naquele tópico e quando recebe a resposta manda o signal para o operationservice que aguarda os tais 5 segundos .
- **/calculator**: Contem somente um service. Este service e um kafkaconsumer sempre que e feito um pedido ele consome faz os calculos e publica o resultado .

## Autor
- Francisco Cordeiro 
- www.linkedin.com/in/francisco-cordeiro-a2b069259