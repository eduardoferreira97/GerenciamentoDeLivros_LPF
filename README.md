# 📚 Sistema de Gerenciamento de Biblioteca (Web)

![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)
![JavaScript](https://img.shields.io/badge/JavaScript-ES6%2B-yellow.svg)
![Node.js](https://img.shields.io/badge/Node.js-18.x-green.svg)
![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow.svg)

## 📖 Sobre o Projeto

Este projeto é uma **aplicação web** para o gerenciamento de bibliotecas, desenvolvido como parte de um trabalho acadêmico. A aplicação, com uma interface gráfica simples e intuitiva, permite o controle completo sobre o catálogo de livros, o cadastro de usuários e a gestão de empréstimos e devoluções.

O backend foi construído utilizando **Node.js puro**, empregando o módulo `http` nativo para criar um servidor web do zero. Essa abordagem foi escolhida para um entendimento profundo do ciclo de vida de requisições e respostas HTTP, sem as abstrações de frameworks.

## ✨ Conceitos Aplicados

### 🏛️ Programação Orientada a Objetos (POO)

A arquitetura do backend é fundamentada em POO para representar as entidades do mundo real como objetos de software.
* **Encapsulamento:** Cada classe (`Livro`, `Usuario`, `Emprestimo`) agrupa seus dados e comportamentos.
* **Herança:** A classe `Usuario` serve como base para `Aluno` e `Funcionario`.
* **Abstração:** Classes de serviço orquestram a lógica de negócio e a interação com o banco de dados.

### ⚙️ Lógica de Programação Funcional (LPF)

Para a manipulação de coleções de dados em JavaScript, foram aplicados conceitos da programação funcional.
* **Funções de Ordem Superior (Higher-Order Functions):** Utilizamos amplamente funções como `.map()`, `.filter()` e `.reduce()` para operar sobre arrays de dados.
    * `.filter()`: Para buscar livros de um autor específico.
    * `.map()`: Para transformar dados do banco para um formato adequado para a interface.

## 🚀 Funcionalidades Principais

* **Gerenciamento de Livros:** Interface web para cadastrar, editar, buscar e listar livros.
* **Gerenciamento de Usuários:** Páginas para cadastro e listagem de alunos e funcionários.
* **Sistema de Empréstimo e Devolução:** Lógica para garantir a consistência do estoque.
* **Interface Gráfica:** Todas as funcionalidades são acessíveis através de uma interface web amigável feita com HTML e CSS.

## 📊 Diagramas do Sistema

*(Os diagramas conceituais permanecem os mesmos, pois a lógica e a estrutura dos dados não mudaram.)*

### Diagrama de Classes (UML)
*(Substitua o link abaixo pelo URL da imagem do seu diagrama de classes no GitHub ou Imgur)*
![Diagrama de Classes](URL_DA_IMAGEM_AQUI)

### Diagrama Entidade-Relacionamento (DER)
*(Substitua o link abaixo pelo URL da imagem do seu diagrama de banco de dados)*
![Diagrama de Banco de Dados](URL_DA_IMAGEM_AQUI)

## 🛠️ Tecnologias Utilizadas

* **Backend:** Node.js (utilizando o módulo `http` nativo)
* **Frontend:** HTML5, CSS3, JavaScript (Vanilla)
* **Banco de Dados:** SQLite3

## ⚙️ Instalação e Execução

A vantagem de usar SQLite é que você não precisa instalar um servidor de banco de dados!

**Pré-requisitos:**
* Node.js v18+ (que já inclui o npm)
* Git

**Passos:**

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/eduardoferreira97/GerenciamentoDeLivros_LPF.git](https://github.com/eduardoferreira97/GerenciamentoDeLivros_LPF)
    cd GerenciamentoDeLivros_LPF
    ```

2.  **Instale as dependências:**
    ```bash
    npm install
    ```
    *(Isso irá instalar o driver do `sqlite3` e outras dependências que você possa precisar)*

3.  **Execute a aplicação:**
    ```bash
    node server.js
    ```

4.  **Acesse no navegador:**
    * O servidor será iniciado. Abra seu navegador e acesse: `http://localhost:3000`
    * O banco de dados (`database.sqlite`) será criado e configurado automaticamente na primeira vez que o servidor for executado.

## 📁 Estrutura do Projeto
