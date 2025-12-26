# 🎁 Secret Friend API (Amigo Secreto com IA)

Esta é uma API REST robusta desenvolvida com **Spring Boot** para gerir eventos de Amigo Secreto. O sistema automatiza todo o processo, desde o registo de participantes até o sorteio final, integrando **Inteligência Artificial** para sugerir presentes e enviando os resultados por e-mail de forma assíncrona.

## 🔗 Evolução do Projeto

Este projeto representa uma evolução direta da minha implementação anterior de integração com a Groq, que pode ser encontrada em: [java-groq-integration](https://github.com/dvx0exe/java-groq-integration). Enquanto o projeto anterior focava na comunicação básica com a IA, este aplica esse conhecimento num cenário real de negócio com persistência de dados e serviços automatizados.

## ✨ Funcionalidades

* **Gestão de Eventos:** Criação de eventos com nome, e-mail do organizador e data do sorteio.
* **Gestão de Participantes:** Registo de participantes vinculados a um evento, incluindo os seus gostos pessoais para personalização.
* **Sorteio Inteligente:** Lógica circular que garante que ninguém se sorteia a si mesmo, realizada dentro de uma transação segura.
* **Sugestões por IA:** Integração com o modelo `llama-3.1-8b-instant` da Groq para gerar 3 sugestões de presentes baseadas nos interesses de cada sorteado.
* **Notificações por E-mail:** Envio automático do resultado do sorteio e das sugestões da IA para cada participante.
* **Processamento Assíncrono:** Uso de execução em segundo plano para que o envio de e-mails e chamadas de IA não bloqueiem a resposta da API.

## 🛠️ Tecnologias Utilizadas

* **Java 17** e **Spring Boot 3**.
* **Spring Data JPA:** Para persistência em base de dados (MySQL/H2).
* **Groq Cloud API:** Para processamento de linguagem natural e sugestões inteligentes.
* **Java Mail Sender:** Para comunicação via protocolo SMTP.
* **Jackson:** Para manipulação de JSON e controlo de referências cíclicas entre entidades.

## 🚀 Como Executar

1. Clone o repositório.
2. Configure as variáveis de ambiente necessárias (conforme o seu `application.properties.example`).
3. Execute o comando `./mvnw spring-boot:run` ou inicie através da classe `SecretFriendApplication`.

## 📌 Endpoints Principais

* `POST /api/eventos`: Cria um novo evento.
* `POST /api/participantes/{eventoId}`: Adiciona um participante a um evento específico.
* `POST /api/sorteio/{eventoId}`: Realiza o sorteio, consulta a IA e dispara os e-mails.

---
