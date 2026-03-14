# 🎁 Secret Friend IA (Amigo Secreto Fullstack)

[![CI - Maven Test](https://github.com/dvx0exe/secret-friend-api/actions/workflows/maven-ci.yml/badge.svg)](https://github.com/dvx0exe/secret-friend-api/actions/workflows/maven-ci.yml)
[![Swagger UI](https://img.shields.io/badge/docs-swagger-85EA2D?logo=swagger)](https://api-amigo-secreto.onrender.com/swagger-ui/index.html)

Esta é uma solução **Fullstack** robusta desenvolvida com **Java 17 e Spring Boot** para gerir eventos de Amigo Secreto. O sistema automatiza todo o processo, desde o registro de participantes até o sorteio final, integrando **Inteligência Artificial** para sugerir presentes e enviando os resultados por e-mail de forma assíncrona.

---

## 🔗 Evolução do Projeto

* **23 de Dezembro de 2025:** Publicação inicial focada em persistência de dados e serviços básicos.
* **11 de Janeiro de 2026:** Implementação de **UUIDs** para convites seguros e proteção de IDs.
* **17 de Janeiro de 2026:** Camada de segurança com **Spring Security** e **OAuth2** (Login com Google).
* **22 de Janeiro de 2026:** Interface **SPA (Vanilla JS)** artesanal e deploy na infraestrutura **Render**.
* **11 de Março de 2026:** Implementação de **Testes Unitários** e lógica de **Resiliência de IA**.

---

## ✨ Funcionalidades

* **Dashboard SPA:** Gerenciamento dinâmico de eventos (Organizador vs. Participante).
* **Autenticação OAuth2:** Acesso administrativo blindado via Login com Google.
* **Sorteio com IA Resiliente:** Integração com a API da Groq (`llama-3.1`). Falhas na IA não interrompem o fluxo de sorteio (Isolamento de Erros).
* **Processamento Assíncrono:** Threads em background para disparos de e-mail e chamadas de IA.

---

## 🧪 Qualidade e Testes (Unit Testing)

O projeto utiliza **JUnit 5** e **Mockito** para garantir a integridade das regras:

* **Validação de Regras:** Garante o mínimo de 2 participantes e impede que alguém tire a si mesmo.
* **Teste de Resiliência:** Simula falhas na API da Groq para validar que o sistema continua operando mesmo com instabilidades externas.
* **Mocking:** Isolamento total de dependências para testes rápidos e independentes.

---

## 🛠️ Tecnologias Utilizadas

### **Backend**
* **Java 17** & **Spring Boot 3.x**
* **Spring Security & OAuth2 Client**
* **PostgreSQL** & **Spring Data JPA**
* **Groq Cloud API** (IA Generativa - Llama 3.1)

### **Frontend & DevOps**
* **Vanilla JavaScript (SPA)** & **CSS3 Moderno**
* **Docker & Docker Compose**

---

## 🚀 Como Executar (Via Docker)

1. Crie um arquivo **`.env`** na raiz com suas credenciais:
```env
EMAIL_USER=seu_email@gmail.com
EMAIL_PASS=sua_senha_app
GROQ_KEY=sua_chave_groq
GOOGLE_CLIENT_ID=seu_client_id_google
GOOGLE_CLIENT_SECRET=seu_client_secret_google


2. Inicie o ambiente:
```bash
docker compose up --build

```


A API estará disponível em `http://localhost:8080`.

---

## 📌 Endpoints Principais

### **1. Eventos (Gestão)**

**`POST /api/eventos`**
* **Auth**: Requer Login Google (OAuth2).
* **Ação**: Cria um novo evento.
* **Status**: 201 Created.

**`GET /api/eventos/meus`**
* **Auth**: Requer Login Google (OAuth2).
* **Ação**: Lista todos os eventos criados pelo usuário logado.



### **2. Participantes (Acesso Público)**
**`POST /api/participantes/entrar?codigo={UUID}`**
* **Ação**: Registra um novo participante no evento correspondente ao código.
* **Body**: {"nome": "...", "email": "...", "gostosPessoais": "..."}.
* **Status**: 200 OK.




### **3. Sorteio (Ação Final)**

* **`POST /api/sorteio/{codigoConvite}`**
* **Auth**: Apenas o Criador do Evento (OAuth2).
* **Ação**: Realiza o embaralhamento, define os pares, consulta a IA para dicas e dispara os e-mails.

---

## 🎥 Demonstração

* **API em ação:** do cadastro no Postman até o e-mail enviado com sugestões da IA.
![Demonstração do Sorteio](img/demo.gif)

* **Spring Security e OAuth2**
![](img/OAuth2.jpeg)

* **Documentação Swagger:** Disponível em **[https://api-amigo-secreto.onrender.com/swagger-ui/index.html](https://api-amigo-secreto.onrender.com/swagger-ui/index.html)**.
  ![](img/dockerizacao.jpeg)

---

## ⚠️ Notas de Desenvolvimento (Beta)

* **Compatibilidade**: Atualmente otimizado para **Google Chrome**.
* **Pendência Técnica**: Identificamos instabilidades em dispositivos **iOS (iPhone/Safari)** relacionadas ao fluxo de login.
* **Roadmap**: As funções de exclusão administrativa e remoção de participantes estão sendo ajustadas para garantir a integridade total do banco de dados.
