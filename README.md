# API REST de Itens de RPG (Spring Boot + JPA + H2)

API CRUD com uma única entidade, **Item**, feita com Java 17, Spring Boot 3, Spring Data JPA, Bean Validation e banco H2.

## Como rodar (CLI)

Pré-requisitos: **JDK 17+** e **Maven 3.6+**.

```bash
mvn clean package        # compila e baixa dependências
mvn spring-boot:run      # inicia a aplicação
```

A API fica em `http://localhost:8081`. Console do banco: `http://localhost:8081/h2-console`
(JDBC URL: `jdbc:h2:file:./data/rpgdb`, usuário `sa`, senha vazia).

## Entidade `Item`

| Campo | Tipo | Regras |
|---|---|---|
| id | Long | Chave primária, gerada automaticamente |
| nome | String | Obrigatório, 2 a 100 caracteres, único |
| raridade | Enum | Obrigatório: COMUM, INCOMUM, RARO, EPICO, LENDARIO |
| preco | Decimal | Obrigatório, maior ou igual a 0 |
| nivelMinimo | Integer | Opcional, de 1 a 100 |

## Endpoints

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| GET | /api/itens | Lista todos (filtro opcional `?nome=`) | 200 |
| GET | /api/itens/{id} | Busca por ID | 200 |
| POST | /api/itens | Cria item | 201 |
| PUT | /api/itens/{id} | Atualiza item completo | 200 |
| DELETE | /api/itens/{id} | Remove item | 204 |

Erros: **400** (validação / JSON inválido), **404** (ID inexistente), **409** (nome duplicado).

## Exemplos (cURL)

**POST** - criar
```bash
curl -i -X POST http://localhost:8081/api/itens -H "Content-Type: application/json" \
  -d '{"nome":"Espada Flamejante","raridade":"EPICO","preco":1500.00,"nivelMinimo":20}'
```
Resposta `201 Created`:
```json
{ "id": 1, "nome": "Espada Flamejante", "raridade": "EPICO", "preco": 1500.00, "nivelMinimo": 20 }
```

**GET** - listar / filtrar / por id
```bash
curl -i http://localhost:8081/api/itens
curl -i "http://localhost:8081/api/itens?nome=espada"
curl -i http://localhost:8081/api/itens/1
```
Resposta `200 OK`: o item (ou lista de itens) em JSON.

**PUT** - atualizar
```bash
curl -i -X PUT http://localhost:8081/api/itens/1 -H "Content-Type: application/json" \
  -d '{"nome":"Espada Flamejante +1","raridade":"LENDARIO","preco":3000.00,"nivelMinimo":25}'
```
Resposta `200 OK` com o item atualizado.

**DELETE**
```bash
curl -i -X DELETE http://localhost:8081/api/itens/1
```
Resposta `204 No Content` (sem corpo).

**Erro 400** (validação)
```bash
curl -i -X POST http://localhost:8081/api/itens -H "Content-Type: application/json" \
  -d '{"nome":"","preco":-5,"nivelMinimo":500}'
```
```json
{
  "timestamp": "2026-10-26T10:00:00",
  "status": 400,
  "mensagem": "Erro de validação",
  "erros": {
    "nome": "O nome é obrigatório",
    "raridade": "A raridade é obrigatória (COMUM, INCOMUM, RARO, EPICO ou LENDARIO)",
    "preco": "O preço não pode ser negativo",
    "nivelMinimo": "O nível mínimo deve ser no máximo 100"
  }
}
```

**Erro 404**: `GET /api/itens/999` -> `{"status":404,"mensagem":"Item com id 999 não encontrado",...}`

**Erro 409**: criar dois itens com o mesmo nome -> `{"status":409,"mensagem":"Já existe um item com o nome 'Espada Flamejante'",...}`

O arquivo `requests.http` tem todas as requisições prontas para a extensão REST Client do VS Code.

## Estrutura

```
model/       Item (entidade + validações) e Raridade (enum)
repository/  ItemRepository (JpaRepository)
controller/  ItemController (endpoints REST)
exception/   Exceções próprias e GlobalExceptionHandler
```
