# 🎁 Secret Friend API (Amigo Secreto com IA)

Esta é uma API REST robusta desenvolvida com **Spring Boot** para gerir eventos de Amigo Secreto. O sistema automatiza todo o processo, desde o registo de participantes até o sorteio final, integrando **Inteligência Artificial** para sugerir presentes e enviando os resultados por e-mail de forma assíncrona.

## 🔗 Evolução do Projeto

* **26 de Dezembro de 2025:** Publicação inicial do projeto focado em persistência de dados e serviços automatizados.
* **11 de Janeiro de 2026:** Evolução significativa com a implementação de **Códigos de Convite**. O sistema passou a utilizar identificadores únicos (UUID) para entrada nos eventos, aumentando a segurança ao deixar de expor os IDs sequenciais do banco de dados.

## ✨ Funcionalidades

* **Gestão de Eventos:** Criação de eventos com nome, e-mail do organizador e data do sorteio.
* **Entrada via Código de Convite (Novo):** Para entrar no evento, o participante deve utilizar um código único gerado automaticamente (ex: "AE7697"), garantindo que o ID do banco de dados não seja exposto.
* **Gestão de Participantes:** Registo de participantes vinculados ao evento exclusivamente através deste código de convite.
* **Sorteio Inteligente:** Lógica circular que garante que ninguém se sorteia a si mesmo, realizada dentro de uma transação segura.
* **Sugestões por IA:** Integração com o modelo `llama-3.1-8b-instant` da Groq para gerar 3 sugestões de presentes baseadas nos interesses de cada sorteado.
* **Notificações por E-mail:** Envio automático do resultado do sorteio e das sugestões da IA para cada participante.
* **Processamento Assíncrono:** Uso de execução em segundo plano para que o envio de e-mails e chamadas de IA não bloqueiem a resposta da API.
* **Infraestrutura Docker:** Orquestração completa do ambiente (API + Base de Dados MySQL) utilizando containers.

## 🛠️ Tecnologias Utilizadas

* **Java 17** e **Spring Boot 3** (ou 4 experimental).
* **Spring Data JPA:** Para persistência em base de dados (MySQL).
* **UUID & @PrePersist:** Para geração dos códigos de convite.
* **Docker & Docker Compose:** Para garantir portabilidade e facilidade no setup.
* **Groq Cloud API:** Para processamento de linguagem natural e sugestões inteligentes.
* **Java Mail Sender:** Para comunicação via protocolo SMTP.
* **Jackson:** Para manipulação de JSON e controlo de referências cíclicas entre entidades.

## 🚀 Como Executar (Via Docker)

Esta é a forma mais simples de rodar o projeto, pois configura automaticamente a base de dados MySQL e a API sem necessidade de instalações complexas.

1. Crie um ficheiro **`.env`** na raiz do projeto com as suas chaves (este ficheiro é ignorado pelo Git):
```env
EMAIL_USER=seu_email@gmail.com
EMAIL_PASS=sua_senha_app
GROQ_KEY=sua_chave_groq

```

2. Execute o comando:

```bash
docker compose up --build

```

A API estará disponível em `http://localhost:8080`.

## 📌 Endpoints Principais

* `POST /api/eventos`: Cria um novo evento e retorna o **Código de Convite**.
* `POST /api/participantes/{codigoConvite}`: Utiliza o código do evento para registar a entrada do participante.
* `POST /api/sorteio/{codigoConvite}`: Realiza o sorteio para o evento identificado pelo código.

---

## 🎥 Demonstração

Veja a API em ação: do cadastro no Postman até o e-mail enviado com sugestões da IA.

![Demonstração do Sorteio](img/demo.gif)