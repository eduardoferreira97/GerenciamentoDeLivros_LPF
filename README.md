
# Sistema de Gerenciamento de Biblioteca

## Descrição

Este projeto é um sistema de gerenciamento de biblioteca de linha de comando desenvolvido em Java. Ele permite que funcionários e alunos gerenciem os recursos da biblioteca, como livros, usuários e empréstimos. O sistema utiliza um banco de dados SQLite para persistir os dados.

## Funcionalidades

### Gerais
- **Autenticação de Usuário**: Sistema de login seguro para alunos e funcionários.
- **Busca de Livros**: Pesquisa de livros por título, autor ou ISBN.
- **Listagem de Livros**: Visualização de todos os livros cadastrados na biblioteca.

### Funcionalidades de Funcionário
- **Gerenciamento de Livros**:
    - Adicionar novos livros ao acervo.
    - Atualizar a quantidade de exemplares de um livro existente.
    - Remover livros do sistema.
- **Gerenciamento de Usuários**:
    - Cadastrar novos usuários (alunos e funcionários).
    - Buscar usuários por nome, e-mail, CPF ou matrícula.
    - Remover usuários do sistema.
- **Gerenciamento de Empréstimos**:
    - Realizar empréstimos de livros para os usuários.
    - Registrar a devolução de livros.
    - Listar todos os empréstimos que estão atrasados.
    - Visualizar o histórico de empréstimos de um usuário específico.

### Funcionalidades de Aluno
- **Consulta de Livros**:
    - Listar todos os livros disponíveis na biblioteca.
    - Buscar por livros específicos.
- **Minha Conta**:
    - Visualizar o próprio histórico de empréstimos.

## Tecnologias Utilizadas

- **Linguagem**: Java
- **Banco de Dados**: SQLite
- **Gerenciamento de Dependências**: (Adicionar, caso utilize Maven ou Gradle)

## Como Executar o Projeto

1.  **Pré-requisitos**:
    - JDK (Java Development Kit) instalado.
    - (Opcional) Um IDE Java como IntelliJ IDEA, Eclipse ou VS Code.

2.  **Clone o repositório**:
    ```bash
    git clone <URL-do-seu-repositorio>
    ```

3.  **Compile e execute**:
    - Navegue até o diretório `src` do projeto.
    - Compile os arquivos Java:
      ```bash
      javac br/com/poli/biblioteca/Main.java
      ```
    - Execute a classe principal:
      ```bash
      java br.com.poli.biblioteca.Main
      ```
    - Um arquivo de banco de dados `biblioteca.db` será criado no diretório raiz do projeto na primeira execução.

## Estrutura do Projeto

O projeto segue uma arquitetura em camadas para separar as responsabilidades:

-   **`br.com.poli.biblioteca`**: Pacote raiz da aplicação.
    -   **`Main.java`**: Ponto de entrada da aplicação, responsável pela interface com o usuário no console.
-   **`br.com.poli.biblioteca.model`**: Contém as classes de modelo que representam as entidades do sistema.
    -   `Usuario.java`, `Aluno.java`, `Funcionario.java`: Representam os usuários do sistema.
    -   `Livro.java`: Representa os livros da biblioteca.
    -   `Emprestimo.java`: Representa os registros de empréstimo.
    -   `StatusEmprestimo.java`: Enum para os possíveis status de um empréstimo.
-   **`br.com.poli.biblioteca.repository`**: Responsável pela comunicação com o banco de dados (camada de persistência).
    -   `DatabaseSetup.java`: Cria e configura o banco de dados e as tabelas.
    -   `UsuarioRepository.java`, `LivroRepository.java`, `EmprestimoRepository.java`: Implementam as operações de CRUD para cada entidade.
-   **`br.com.poli.biblioteca.service`**: Contém a lógica de negócio da aplicação.
    -   `UsuarioService.java`, `LivroService.java`, `EmprestimoService.java`: Orquestram as operações e regras de negócio.
    -   `LoginService.java`: Gerencia a autenticação dos usuários.

## Autores

-   [Eduardo Ferreira]
-   [Lucas Pereira]
