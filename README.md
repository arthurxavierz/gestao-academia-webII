# Gestão Academia

Sistema web para gestão de academia desenvolvido na disciplina de Web II (IFTM).

Controle de planos, modalidades, instrutores, alunos, matrículas, mensalidades, pagamentos e check-in, com acesso separado para administrador, recepção e aluno.

## Tecnologias

- Java 21
- Spring Boot 3.5.6 (Web, Data JPA, Security, Validation)
- Thymeleaf
- PostgreSQL
- JUnit 5, Mockito e H2 para os testes

## Como rodar

1. Criar o banco no PostgreSQL:
   ```sql
   CREATE DATABASE gestao_academia;
   ```
2. Ajustar usuário e senha do banco em `src/main/resources/application.properties`.
3. Rodar a aplicação:
   ```
   ./mvnw spring-boot:run
   ```
4. Acessar `http://localhost:8080`.

## Testes

Os testes usam o perfil `test` com banco H2 em memória:
```
./mvnw test
```

## Organização

O andamento das sprints está no quadro do Trello da disciplina.
